package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class Template {
    private String url;
    private int x;
    private int y;
    private float width;
    private float height;
    private int _width;
    private int _height;
    private float scale;
    private float opacity;
    private boolean use;
    private FrameBuffer buffer;
    private CanvasScreen parent;
    public Template(CanvasScreen _parent) {
        parent = _parent;
        _width = 0;
        url = "";
        x = 0;
        y = 0;
        use = false;
        opacity = 0.5f;
        //load(x, y, 211, "https://i.imgur.com/Pr8tuTT.png");
    }

    public void load(int _x, int _y, final float _tw, float _opacity, String _url) {
        if (Pxls.prefsHelper.getRememberTemplate()) { //These should be serialized as something that gets called to `load`, so we'll just store these params.
            Pxls.gameState.templateState.offsetX = _x;
            Pxls.gameState.templateState.offsetY = _y;
            Pxls.gameState.templateState.totalWidth = _tw;
            Pxls.gameState.templateState.opacity = _opacity;
            Pxls.gameState.templateState.URL = _url;
        }
        x = _x;
        y = _y;
        opacity = _opacity;
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
        use = false;
        //PxlsGame.i.alert(Integer.toString(buffer.getWidth()));
        //if (buffer.getWidth() > 0) {
        //    buffer.dispose();
        //}
        if (url.isEmpty()) {
            return;
        }
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(url);
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
                        use = true;
                    }
                });
            }
            
            @Override
            public void failed(Throwable t) {
                use = false;
            }
            
            @Override
            public void cancelled() {
                use = false;
            }
        });
    }

    public void render(float zoom, Vector2 screenCenter) {
        if (!use) {
            return;
        }
        Vector2 pos = new Vector2(x, parent.boardInfo.height - y - height);
        Vector2 size = new Vector2(width, height).scl(zoom);
        Vector2 corner = screenCenter.mulAdd(pos, zoom);
        parent.batch.setColor(new Color(1, 1, 1, opacity));
        parent.batch.draw(buffer.getColorBufferTexture(), corner.x, corner.y, size.x, size.y);
        parent.batch.setColor(Color.WHITE);
        
    }
}
