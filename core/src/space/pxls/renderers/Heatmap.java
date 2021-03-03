package space.pxls.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

import java.util.Timer;
import java.util.TimerTask;

import space.pxls.Pxls;
import space.pxls.ui.Screens.CanvasScreen;

public class Heatmap implements Renderer {
    public final Color fillStyle = Color.valueOf("#CD5C5C");
    private boolean loadedHeatmap = false;
    private boolean loadingHeatmap = false;
    private CanvasScreen parent;
    private FrameBuffer heatmapBuffer;
    private Texture backdropTexture;
    private Texture heatmapTexture;
    private byte[] heatmapData;
    private Timer heatmapTickTimer;
    private boolean updatingTexture = false;

    public Heatmap(CanvasScreen canvasScreen) {
        this.parent = canvasScreen;
        final Pixmap p = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0f, 0f, 0f, 0f));
        p.fillRectangle(0, 0, p.getWidth(), p.getHeight());

        heatmapTexture = new Texture(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        heatmapTexture.draw(p, 0, 0);
        heatmapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        backdropTexture = new Texture(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        backdropTexture.draw(p, 0, 0);
        backdropTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        p.dispose();

        heatmapTickTimer = new Timer();
        heatmapTickTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!loadedHeatmap) return;
                if (heatmapData == null) return;
                for (int i = 0; i < heatmapData.length; i++) {
                    if (heatmapData[i] != 0) heatmapData[i] -= 4;
                }
                updateTexture();
            }
        }, 0, 10800*1000 / 256); //3 hours (in millis) / 256 (number of possible states)
    }

    public void pixel(final int x, final int y, final int color) {
        if (heatmapData == null) return;
        heatmapData[x + parent.boardInfo.width * y] = (byte)0xff;
        updateTexture();
    }

    public void loadHeatmap() {
        loadHeatmap(false);
    }
    public void loadHeatmap(boolean reload) {
        if (parent == null) throw new IllegalStateException("Parent is not set");
        if (parent.canvas == null) throw new IllegalStateException("Canvas is not initialized on parent");
        if (loadedHeatmap && !reload) return;
        if (loadingHeatmap) return;
        loadingHeatmap = true;

        Net.HttpRequest heatmapReq = new Net.HttpRequest(Net.HttpMethods.GET);
        heatmapReq.setUrl(Pxls.getDomain() + "/heatmap?r=" + ((int) Math.floor(Math.random() * 10000)));
        heatmapReq.setHeader("User-Agent", Pxls.getUserAgent());
        Gdx.net.sendHttpRequest(heatmapReq, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                heatmapData = httpResponse.getResult();

                loadingHeatmap = false;
                loadedHeatmap = true;
                updateTexture();
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                System.err.println("Failed to fetch heatmap data");
                loadedHeatmap = false;
            }

            @Override
            public void cancelled() {
                System.err.println("Heatmap data request was cancelled");
                loadedHeatmap = false;
            }
        });
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        if (!Pxls.getPrefsHelper().getHeatmapEnabled()) return;
        if (loadingHeatmap) {return;}
        if (!loadedHeatmap) {
            loadHeatmap();
            return;
        }
        if (parent == null) throw new IllegalStateException("Parent is null");
        if (parent.batch == null) throw new IllegalStateException("Parent's batch is null");
        if (heatmapTexture == null) throw new IllegalStateException("heatmapTexture is null");
        if (backdropTexture == null) throw new IllegalStateException("backdropTexture is null");

        parent.batch.draw(backdropTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
        parent.batch.draw(heatmapTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
    }

    public void updateTexture() {
        if (!loadedHeatmap) return;
        if (updatingTexture) return;
        updatingTexture = true;

        byte[] toRender = new byte[parent.boardInfo.width * parent.boardInfo.height * 4];
        byte[] backdropPixels = new byte[parent.boardInfo.width * parent.boardInfo.height * 4];
        byte backdropOpacity = (byte)(Pxls.getGameState().getSafeHeatmapState().opacity * 0xFF);
        int color = Color.rgb888(fillStyle);
        for (int i = 0; i < heatmapData.length; i++) {
            if (heatmapData[i] != 0) {
                toRender[i * 4] = (byte) (color >> 16 & 0xFF);
                toRender[i * 4 + 1] = (byte) (color >> 8 & 0xFF);
                toRender[i * 4 + 2] = (byte) (color & 0xFF);
                toRender[i * 4 + 3] = (byte)(heatmapData[i] & 255);
            }

            backdropPixels[i * 4] = (byte) 0x00;
            backdropPixels[i * 4 + 1] = (byte) 0x00;
            backdropPixels[i * 4 + 2] = (byte) 0x00;
            backdropPixels[i * 4 + 3] = backdropOpacity;
        }

        final Pixmap heatmapPixmap = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        heatmapPixmap.getPixels().put(toRender).position(0);

        final Pixmap backdropPixmap = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        backdropPixmap.getPixels().put(backdropPixels).position(0);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                heatmapTexture.draw(heatmapPixmap, 0, 0);
                heatmapPixmap.dispose();

                backdropTexture.draw(backdropPixmap, 0, 0);
                backdropPixmap.dispose();

                updatingTexture = false;
            }
        });
    }

    public boolean getLoadedHeatmap() {
        return this.loadedHeatmap;
    }

    public byte[] convertHeatmapResponse() {
        return new byte[] {};
    }

    @Override
    public CanvasScreen getCanvasScreen() {
        return parent;
    }

    @Override
    public void setCanvasScreen(CanvasScreen screen) {
        this.parent = screen;
    }
}
