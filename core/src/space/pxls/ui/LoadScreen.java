package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Align;
import com.google.gson.Gson;
import de.tomgrill.gdxdialogs.core.dialogs.GDXProgressDialog;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

import java.util.ArrayList;
import java.util.List;

public class LoadScreen extends ScreenAdapter {
    @Override
    public void show() {
        super.show();

        final Gson gson = new Gson();

        System.out.println("Sending board info request...");

        Net.HttpRequest infoReq = new Net.HttpRequest(Net.HttpMethods.GET);
        infoReq.setUrl(Pxls.domain + "/info");

        Gdx.net.sendHttpRequest(infoReq, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println("Parsing board info...");

                final BoardInfo info = gson.fromJson(httpResponse.getResultAsString(), BoardInfo.class);

                System.out.println("Initializing fancy color map (s = " + info.palette.size() + ")");
                final int[] fancyColors = new int[info.palette.size()];
                List<String> palette = info.palette;

                System.out.println("Looping...");
                for (int i = 0; i < palette.size(); i++) {
                    String s = palette.get(i);
                    System.out.println("i = " + i + ", s = " + s);
                    Color color = Color.valueOf(s);
                    System.out.println(color);
                    fancyColors[i] = Color.rgb888(color);
                    System.out.println(fancyColors[i]);
                }
                System.out.println("Done!");

                System.out.println("Creating board data request");
                Net.HttpRequest boardDataReq = new Net.HttpRequest(Net.HttpMethods.GET);
                boardDataReq.setUrl(Pxls.domain + "/boarddata");

                System.out.println("Requesting board data...");

                Gdx.net.sendHttpRequest(boardDataReq, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        byte[] board = httpResponse.getResult();

                        System.out.println("Wrangling bytes...");

                        final byte[] data = wrangleBytes(board, info, fancyColors);

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Uploading to GPU...");
                                FrameBuffer canvasTexture = new FrameBuffer(Pixmap.Format.RGBA8888, info.width, info.height, false);

                                System.out.println("Switching states...");
                                PxlsGame.i.setScreen(new CanvasScreen(new Canvas(data, info)));
                                PxlsGame.i.handleView(PxlsGame.i.startupURI);
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable t) {
                        t.printStackTrace();
                        System.out.println("Failed fetching board data");
                        PxlsGame.i.alert("Failed to load the game. Press OK to try again", new PxlsGame.ButtonCallback() {
                            @Override
                            public void clicked() {
                                PxlsGame.i.setScreen(new LoadScreen());
                            }
                        });
                    }

                    @Override
                    public void cancelled() {

                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                PxlsGame.i.alert("Connection failed. Press OK to try again", new PxlsGame.ButtonCallback() {
                    @Override
                    public void clicked() {
                        PxlsGame.i.setScreen(new LoadScreen());
                    }
                });
            }

            @Override
            public void cancelled() {

            }
        });
    }

    private byte[] wrangleBytes(byte[] board, BoardInfo info, int[] fancyColors) {
        // please optimize this jvm thanks

        byte[] data = new byte[info.width * info.height * 4];
        for (int i = 0; i < board.length; i++) {
            byte color = board[i];

            if (color == -1) {
                // transparent!
                data[i * 4] = (byte)0;
                data[i * 4 + 1] = (byte)0;
                data[i * 4 + 2] = (byte)0;
                data[i * 4 + 3] = (byte)0;
            } else {
                int col = fancyColors[color];
                data[i * 4] = (byte) (col >> 16 & 0xFF);
                data[i * 4 + 1] = (byte) (col >> 8 & 0xFF);
                data[i * 4 + 2] = (byte) (col & 0xFF);
                data[i * 4 + 3] = (byte)0xFF;
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
        Pxls.batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
        Pxls.batch.begin();
        BitmapFont font = Pxls.skin.getFont("default");
        font.setColor(1, 1, 1, 1);
        font.draw(Pxls.batch, "Loading...", w / 2, h / 2 + font.getCapHeight() / 2, 0, Align.center, false);
        Pxls.batch.end();
    }

    static class BoardInfo {
        int width;
        int height;
        List<String> palette;
        String captchaKey;
        int maxStacked;
    }
}
