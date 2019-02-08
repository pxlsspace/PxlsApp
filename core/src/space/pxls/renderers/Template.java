package space.pxls.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.structs.TemplateState;
import space.pxls.ui.CanvasScreen;

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
        TemplateState templateState = Pxls.gameState.getSafeTemplateState();
        if (Pxls.prefsHelper.getRememberTemplate() && templateState.URL.length() > 0) {
            _initial = true;
            load(templateState.offsetX, templateState.offsetY, templateState.totalWidth, templateState.opacity, templateState.URL);
        }
    }

    public void load(int _x, int _y, final float _tw, float _opacity, String _url) {
        Pxls.gameState.getSafeTemplateState().offsetX = _x;
        Pxls.gameState.getSafeTemplateState().offsetY = _y;
        Pxls.gameState.getSafeTemplateState().totalWidth = _tw;
        Pxls.gameState.getSafeTemplateState().opacity = _opacity;
        Pxls.gameState.getSafeTemplateState().URL = _url;
//        Pxls.gameState.getSafeTemplateState().enabled = false;

        if (url.equals(_url)) {
            if (_tw != -1) {
                scale = _tw / _width;
                width = _width * scale;
                height = _height * scale;
            }
            return;
        }
        // fetch the new temaplate image
        url = _url;
        if (url.isEmpty()) {
            return;
        }
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(url);
        req.setHeader("User-Agent", Pxls.getUA());
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                byte[] data = httpResponse.getResult();
                final Pixmap p = new Pixmap(data, 0, data.length);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (_width != 0) {
                            buffer.dispose();
                        }
                        _width = p.getWidth();
                        _height = p.getHeight();
                        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, _width, _height, false);
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
                            Pxls.gameState.getSafeTemplateState().enabled = true;
                        }
                    }
                });
            }
            
            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                System.err.println("Failed to fetch Template image");
                Pxls.gameState.getSafeTemplateState().enabled = false;
            }
            
            @Override
            public void cancelled() {
                System.out.println("template fetch webreq was cancelled");
                Pxls.gameState.getSafeTemplateState().enabled = false;
            }
        });
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        TemplateState ts = Pxls.gameState.getSafeTemplateState();
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
        TemplateState ts = Pxls.gameState.getSafeTemplateState();
        return String.format("%s/#template=%s&ox=%s&oy=%s&oo=%s&tw=%s", Pxls.domain, ts.URL, ts.offsetX, ts.offsetX, ts.opacity, ts.totalWidth);
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
