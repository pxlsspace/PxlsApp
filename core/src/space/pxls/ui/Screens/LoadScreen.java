package space.pxls.ui.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Align;
import com.google.gson.Gson;

import java.util.List;

import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.renderers.Canvas;

public class LoadScreen extends ScreenAdapter {
    @Override
    public void show() {
        super.show();

        final Gson gson = new Gson();

        Net.HttpRequest infoReq = new Net.HttpRequest(Net.HttpMethods.GET);
        infoReq.setUrl(Pxls.getDomain() + "/info");
        infoReq.setHeader("User-Agent", Pxls.getUserAgent());

        Gdx.net.sendHttpRequest(infoReq, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final BoardInfo info = gson.fromJson(httpResponse.getResultAsString(), BoardInfo.class);

                final int[] fancyColors = new int[info.palette.size()];
                List<PaletteEntry> palette = info.palette;

                for (int i = 0; i < palette.size(); i++) {
                    String s = palette.get(i).value;
                    Color color = Color.valueOf(s);
                    fancyColors[i] = Color.rgb888(color);
                }

                Net.HttpRequest boardDataReq = new Net.HttpRequest(Net.HttpMethods.GET);
                boardDataReq.setUrl(Pxls.getDomain() + "/boarddata");
                boardDataReq.setHeader("User-Agent", Pxls.getUserAgent());

                Gdx.net.sendHttpRequest(boardDataReq, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        final byte[] board = httpResponse.getResult();
                        final byte[] data = wrangleBytes(board, info, fancyColors);
                        Gdx.app.postRunnable(() -> {
                            new FrameBuffer(Pixmap.Format.RGBA8888, info.width, info.height, false);
                            PxlsGame.i.setScreen(new CanvasScreen(new Canvas(data, info)));
                            PxlsGame.i.handleView(PxlsGame.i.startupURI);
                        });
                    }

                    @Override
                    public void failed(Throwable t) {
                        t.printStackTrace();
                        PxlsGame.i.alert("Failed to load the game. Press OK to try again", () -> PxlsGame.i.setScreen(new LoadScreen()));
                    }

                    @Override
                    public void cancelled() {
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                PxlsGame.i.alert("Connection failed. Press OK to try again", () -> PxlsGame.i.setScreen(new LoadScreen()));
            }

            @Override
            public void cancelled() {
            }
        });
    }

    private byte[] wrangleBytes(byte[] board, BoardInfo info, int[] fancyColors) {
        byte[] data = new byte[info.width * info.height * 4];
        for (int i = 0; i < board.length; i++) {
            byte color = board[i];

            if (color == -1) {
                // Transparency
                data[i * 4] = (byte) 0;
                data[i * 4 + 1] = (byte) 0;
                data[i * 4 + 2] = (byte) 0;
                data[i * 4 + 3] = (byte) 0;
            } else {
                int col = fancyColors[color];
                data[i * 4] = (byte) (col >> 16 & 0xFF);
                data[i * 4 + 1] = (byte) (col >> 8 & 0xFF);
                data[i * 4 + 2] = (byte) (col & 0xFF);
                data[i * 4 + 3] = (byte) 0xFF;
            }
        }
        return data;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = 1000;
        float h = w * Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        SpriteBatch batch = Pxls.getBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
        batch.begin();
        BitmapFont font = Pxls.getSkin().getFont("default");
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Loading...", w / 2, h / 2 + font.getCapHeight() / 2, 0, Align.center, false);
        batch.end();
    }

    public static class BoardInfo {
        public int width;
        public int height;
        public List<PaletteEntry> palette;
        public String captchaKey;
        public int maxStacked;
    }

    public static class PaletteEntry {
        public String name;
        public String value;
    }
}
