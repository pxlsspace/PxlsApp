package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.google.gson.JsonObject;
import space.pxls.Account;
import space.pxls.Pxls;
import space.pxls.PxlsClient;
import space.pxls.PxlsGame;

import java.util.Date;

public class CanvasScreen extends ScreenAdapter implements PxlsClient.UpdateCallback {
    public final LoadScreen.BoardInfo boardInfo;
    private FrameBuffer canvasBuffer;
    private float zoom = 1;
    private Vector2 center = new Vector2();
    public SpriteBatch batch;

    private Vector2 focalPoint;
    private float initialZoom;

    private Stage stage = new Stage(new ExtendViewport(640, 0));

    private Container<UserBar> topContainer;
    private Container<WidgetGroup> bottomContainer;
    private Container<PixelLookupOverlay> lookupContainer;
    private UndoPopup undoPopup;

    private PixelBar paletteBar;
    private LoginBar login;
    private UserBar userBar;

    private PxlsClient client;

    public Template template;

    public CanvasScreen(FrameBuffer buffer, LoadScreen.BoardInfo info) {
        boardInfo = info;

        batch = new SpriteBatch();
        canvasBuffer = buffer;

        center.set(info.width / 2, info.height / 2);

        paletteBar = new PixelBar(info.palette);
        login = new LoginBar();
        template = new Template(this);

        bottomContainer = new Container<WidgetGroup>(login).fill();
        bottomContainer.background(Pxls.skin.getDrawable("background"));

        topContainer = new Container<UserBar>(null).fill();
        topContainer.background(Pxls.skin.getDrawable("background"));

        lookupContainer = new Container<PixelLookupOverlay>(null).fill();
        lookupContainer.background(Pxls.skin.getDrawable("background"));

        undoPopup = new UndoPopup();
        undoPopup.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof UndoPopup.UndoEvent) {
                    client.undo();
                    return true;
                }
                return false;
            }
        });

        Table table = new Table();
        table.add(topContainer).fillX().expandX().row();
        table.add(lookupContainer).fillX().expandX().row();
        Stack stack = new Stack();
        stack.add(login.popup);
        stack.add(undoPopup);
        table.add(stack).expandX().expandY().center().bottom().row();
        table.add(bottomContainer).fillX().expandX();
        table.setFillParent(true);
        stage.addActor(table);

        client = new PxlsClient(this);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new GestureDetector(new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (paletteBar.getCurrentColor() >= 0) {
                    Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                    placePixel((int) pos.x, (int) pos.y);
                    return true;
                }
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                showLookup((int) pos.x, (int) pos.y);
                return true;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                int hw = Gdx.graphics.getWidth() / 2;
                int hh = Gdx.graphics.getHeight() / 2;
                if (focalPoint == null) {
                    Vector2 ps = new Vector2(x, y);
                    ps.y = Gdx.graphics.getHeight() - ps.y;
                    focalPoint = ps.sub(hw, hh).scl(1 / zoom).add(center);
                } else {
                    Vector2 ps = new Vector2(x, y);
                    ps.y = Gdx.graphics.getHeight() - ps.y;
                    center = focalPoint.cpy().sub(ps.sub(hw, hh).scl(1 / zoom));
                }
                center.x = MathUtils.clamp(center.x, 0, boardInfo.width);
                center.y = MathUtils.clamp(center.y, 0, boardInfo.height);
                return true;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                focalPoint = null;
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                return false;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                int hw = Gdx.graphics.getWidth() / 2;
                int hh = Gdx.graphics.getHeight() / 2;
                if (focalPoint == null) {
                    initialZoom = zoom;
                    Vector2 ps = getPinchCenter(initialPointer1, initialPointer2);
                    ps.y = Gdx.graphics.getHeight() - ps.y;
                    focalPoint = ps.sub(hw, hh).scl(1 / zoom).add(center);
                } else {
                    zoom = initialZoom * (pointer1.dst(pointer2) / initialPointer1.dst(initialPointer2));
                    Vector2 ps = getPinchCenter(pointer1, pointer2);
                    ps.y = Gdx.graphics.getHeight() - ps.y;
                    center = focalPoint.cpy().sub(ps.sub(hw, hh).scl(1 / zoom));
                }
                zoom = MathUtils.clamp(zoom, 0.5f, 200f);
                center.x = MathUtils.clamp(center.x, 0, boardInfo.width);
                center.y = MathUtils.clamp(center.y, 0, boardInfo.height);
                return true;
            }

            @Override
            public void pinchStop() {
                focalPoint = null;
            }
        }) {
            @Override
            public boolean scrolled(int amount) {
                Vector2 delta = new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() - Gdx.input.getY()) - Gdx.graphics.getHeight() / 2);

                float oldZoom = zoom;
                if (amount > 0) {
                    zoom /= 1.2f;
                } else {
                    zoom *= 1.2f;
                }
                zoom = MathUtils.clamp(zoom, 1, 75);

                center.x += delta.x / oldZoom;
                center.y += delta.y / oldZoom;

                center.x -= delta.x / zoom;
                center.y -= delta.y / zoom;
                return true;
            }
        }));
    }

    private void showLookup(int x, int y) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(Pxls.domain + "/lookup?x=" + x + "&y=" + y);
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String resp = httpResponse.getResultAsString();
                
                JsonObject data = Pxls.gson.fromJson(resp, JsonObject.class);

                PixelLookupOverlay plo = new PixelLookupOverlay(data.get("x").getAsInt(), data.get("y").getAsInt(), data.get("username").getAsString(), data.get("time").getAsLong(), data.get("pixel_count").getAsInt(), data.get("id").getAsInt(), client.loggedIn);
                lookupContainer.setActor(plo);
            }

            @Override
            public void failed(Throwable t) {
                //PxlsGame.i.alert("pixel not set!!!!");
                lookupContainer.removeActor(lookupContainer.getActor());
            }

            @Override
            public void cancelled() {

            }
        });
    }

    private void placePixel(int x, int y) {
        if (paletteBar.getCurrentColor() >= 0) {
            client.placePixel(x, y, paletteBar.getCurrentColor());
            paletteBar.changeColor(-1);
        }
    }

    @Override
    public void resize(final int width, final int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    private Vector2 screenToBoardSpace(Vector2 vec) {
        int hw = Gdx.graphics.getWidth() / 2;
        int hh = Gdx.graphics.getHeight() / 2;
        Vector2 thing = new Vector2(vec.x, Gdx.graphics.getHeight() - vec.y).sub(hw, hh).scl(1 / zoom).add(center);
        return new Vector2(thing.x, boardInfo.height - thing.y);
    }

    private Vector2 getPinchCenter(Vector2 p1, Vector2 p2) {
        return p1.cpy().add(p2).scl(0.5f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        Vector2 screenCenter = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).scl(0.5f);
        Vector2 canvasSize = new Vector2(boardInfo.width, boardInfo.height).scl(zoom);
        Vector2 canvasCorner = screenCenter.mulAdd(center, -zoom);
        batch.draw(canvasBuffer.getColorBufferTexture(), canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
        
        template.render(zoom, screenCenter);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    public void reconnect() {
        client.disconnect();
        client = new PxlsClient(this);
        topContainer.setActor(null);
    }

    @Override
    public void pixel(final int x, final int y, final int color) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                String colorString = boardInfo.palette.get(color);
                Color c = Color.valueOf(colorString);

                canvasBuffer.begin();
                batch.getProjectionMatrix().setToOrtho2D(0, 0, boardInfo.width, boardInfo.height);
                batch.begin();
                Pxls.skin.newDrawable("pixel", c).draw(batch, x, y, 1, 1);
                batch.end();
                canvasBuffer.end();
            }
        });
    }

    @Override
    public void users(int users) {

    }

    @Override
    public void updateAccount(Account account) {
        if (account != null && bottomContainer.getActor() == login) {
            bottomContainer.setActor(paletteBar);
        }

        if (account != null) {
            userBar = new UserBar(account.getName());
            userBar.addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {
                    if (event instanceof UserBar.LogoutEvent) {
                        logout();
                        return true;
                    }
                    return false;
                }
            });

            topContainer.setActor(userBar);
            if (account.isBanned()) {
                bottomContainer.setActor(new BannedBar(account.getBanExpiry(), account.getBanReason()));
            }
        } else {
            topContainer.setActor(null);
        }
    }

    @Override
    public void cooldown(float seconds) {
        paletteBar.updateCooldown(seconds);
    }

    @Override
    public void canUndo(float seconds) {
        undoPopup.popUp(seconds);
    }

    @Override
    public void runCaptcha() {
        PxlsGame.i.captchaRunner.doCaptcha(boardInfo.captchaKey, new PxlsGame.CaptchaCallback() {

            @Override
            public void done(String token) {
                client.finishCaptcha(token);
            }
        });
    }

    public void logout() {
        PxlsGame.i.logOut();
        bottomContainer.setActor(login);
    }
}
