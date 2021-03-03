package space.pxls.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.structs.TemplateState;
import space.pxls.ui.Screens.CanvasScreen;

public class Template implements Renderer {
    private String url;
    private float width;
    private float height;
    private int _width;
    private int _height;
    private float scale;
    private FrameBuffer buffer;
    private CanvasScreen parent;
    private boolean _initial = false;

    public Template(CanvasScreen _parent) {
        parent = _parent;
        _width = 0;
        url = "";
        //load(x, y, 211, "https://i.imgur.com/Pr8tuTT.png");
        TemplateState templateState = Pxls.getGameState().getSafeTemplateState();
        if (Pxls.getPrefsHelper().getRememberTemplate() && templateState.URL.length() > 0) {
            _initial = true;
            load(templateState.offsetX, templateState.offsetY, templateState.totalWidth, templateState.opacity, templateState.URL);
        }
    }

    public void load(int _x, int _y, final float _tw, float _opacity, String _url) {
        Pxls.getGameState().getSafeTemplateState().offsetX = _x;
        Pxls.getGameState().getSafeTemplateState().offsetY = _y;
        Pxls.getGameState().getSafeTemplateState().totalWidth = _tw;
        Pxls.getGameState().getSafeTemplateState().opacity = _opacity;
        Pxls.getGameState().getSafeTemplateState().URL = _url;
//        Pxls.gameState.getSafeTemplateState().enabled = false;

        if (url.equals(_url)) {
            if (_tw != -1) {
                scale = _tw / _width;
                width = _width * scale;
                height = _height * scale;
            }
            Pxls.getGameState().getSafeTemplateState().enabled = true;
            return;
        }
        // fetch the new temaplate image
        url = _url;
        if (url.isEmpty()) {
            Pxls.getGameState().getSafeTemplateState().enabled = false;
            return;
        }
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(url);
        req.setHeader("User-Agent", Pxls.getUserAgent());
        req.setHeader("Accept", "image/png, image/jpg, image/jpeg");
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String contentType = httpResponse.getHeader("Content-Type");
                if (!(contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/jpg") || contentType.equalsIgnoreCase("image/jpeg"))) {
                    PxlsGame.i.alert("The requested template image is not a supported format.");
                    Pxls.getGameState().getSafeTemplateState().enabled = false;
                    Pxls.getGameState().getSafeTemplateState().URL = "";
                    return;
                }
                final Pixmap p = PxlsGame.i.imageHelper.getPixmapForIS(httpResponse.getResultAsStream());
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Pixmap.Format format = Pixmap.Format.RGBA8888;
                        if (contentType.equalsIgnoreCase("image/jpg") || contentType.equalsIgnoreCase("image/jpeg")) {
                            format = Pixmap.Format.RGB888;
                        }
                        if (_width != 0) {
                            buffer.dispose();
                        }
                        _width = p.getWidth();
                        _height = p.getHeight();
                        buffer = new FrameBuffer(format, _width, _height, false);
                        buffer.getColorBufferTexture().draw(p, 0, 0);
                        buffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        p.dispose();
                        scale = 1;
                        if (_tw != -1) {
                            scale = _tw / _width;
                        }
                        width = _width * scale;
                        height = _height * scale;
                        if (_initial) {
                            _initial = false;
                        } else {
                            Pxls.getGameState().getSafeTemplateState().enabled = true;
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                System.err.println("Failed to fetch Template image");
                Pxls.getGameState().getSafeTemplateState().enabled = false;
            }

            @Override
            public void cancelled() {
                System.out.println("template fetch webreq was cancelled");
                Pxls.getGameState().getSafeTemplateState().enabled = false;
            }
        });
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        TemplateState ts = Pxls.getGameState().getSafeTemplateState();
        if (!ts.enabled) {
            return;
        }
        if (buffer == null) return;
        Vector2 pos;
        if (ts.moveMode) {
            pos = new Vector2(ts.movingOffsetX, parent.boardInfo.height - ts.movingOffsetY - height);
        } else {
            pos = new Vector2(ts.offsetX, parent.boardInfo.height - ts.offsetY - height);
        }
        Vector2 size = new Vector2(width, height).scl(zoom);
        Vector2 corner = screenCenter.mulAdd(pos, zoom);
        parent.batch.setColor(new Color(1, 1, 1, ts.opacity));
        parent.batch.draw(buffer.getColorBufferTexture(), corner.x, corner.y, size.x, size.y);
        parent.batch.setColor(Color.WHITE);
    }

    public String makePxlsURL() {
        TemplateState ts = Pxls.getGameState().getSafeTemplateState();
        return String.format("%s/#template=%s&ox=%s&oy=%s&oo=%s&tw=%s&x=%s&y=%s&scale=%s", Pxls.getDomain(), ts.URL, ts.offsetX, ts.offsetY, ts.opacity, ts.totalWidth, parent.panX(), parent.panY(true), parent.panZoom());
    }

    public void pixel(int x, int y, int color) {}

    @Override
    public CanvasScreen getCanvasScreen() {
        return parent;
    }

    @Override
    public void setCanvasScreen(CanvasScreen screen) {
        this.parent = screen;
    }
}
