package space.pxls.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import space.pxls.Pxls;
import space.pxls.ui.Screens.CanvasScreen;

public class Virginmap implements Renderer {
    public final Color fillStyle = Color.valueOf("#00FF00");
    private CanvasScreen parent;
    private boolean loaded = false;
    private boolean loading = false;
    private boolean updating = false;
    private byte[] mapData;
    private Texture mapTexture;

    public Virginmap(CanvasScreen screen) {
        this.parent = screen;

        final Pixmap p = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0f, 0f, 0f, 0f));
        p.fill();

        mapTexture = new Texture(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        mapTexture.draw(p, 0, 0);
        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        p.dispose();
    }

    public void pixel(final int x, final int y, final int color) {
        if (mapData == null) return;
        mapData[x + parent.boardInfo.width * y] = (byte)0x00;
        updateTexture();
    }

    public void loadMap() {
        loadMap(false);
    }
    public void loadMap(boolean reload) {
        if (parent == null) throw new IllegalStateException("Parent is not set");
        if (parent.canvas == null) throw new IllegalStateException("Canvas is not initialized on parent");
        if (loaded && !reload) return;
        if (loading) return;
        loading = true;
        Net.HttpRequest virginmapReq = new Net.HttpRequest(Net.HttpMethods.GET);
        virginmapReq.setUrl(Pxls.domain + "/virginmap?r=" + ((int) Math.floor(Math.random() * 10000)));
        virginmapReq.setHeader("User-Agent", Pxls.getUA());
        Gdx.net.sendHttpRequest(virginmapReq, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                mapData = httpResponse.getResult();

                loading = false;
                loaded = true;
                updateTexture();
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                System.err.println("Failed to fetch virginmap data");
                loaded = false;
            }

            @Override
            public void cancelled() {
                System.err.println("Virginmap data request was cancelled");
                loaded = false;
            }
        });
    }

    public void updateTexture() {
        if (!loaded) return;
        if (updating) return;
        updating = true;

        byte[] toRender = new byte[parent.boardInfo.width * parent.boardInfo.height * 4];
        byte opacity = (byte)(Pxls.gameState.getSafeVirginmapState().opacity * 0xFF);

        for (int i = 0; i < mapData.length; i++) {
            toRender[i * 4] = (byte) 0x00;
            toRender[i * 4 + 1] = (byte) (mapData[i] != 0 ? 0xff : 0x00);
            toRender[i * 4 + 2] = (byte) 0x00;
            toRender[i * 4 + 3] = opacity;
        }

        final Pixmap mapPixmap = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        mapPixmap.getPixels().put(toRender).position(0);


        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                mapTexture.draw(mapPixmap, 0, 0);
                mapPixmap.dispose();

                updating = false;
            }
        });
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        if (!Pxls.prefsHelper.getVirginmapEnabled()) return;
        if (loading) {return;}
        if (!loaded) {
            loadMap();
            return;
        }
        if (parent == null) throw new IllegalStateException("Parent is null");
        if (parent.batch == null) throw new IllegalStateException("Parent's batch is null");
        if (mapTexture== null) throw new IllegalStateException("mapTexture is null");

        parent.batch.draw(mapTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
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
