package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.google.gson.JsonObject;

import space.pxls.Account;
import space.pxls.Pxls;
import space.pxls.PxlsClient;
import space.pxls.PxlsGame;
import space.pxls.renderers.Canvas;
import space.pxls.renderers.GridOverlay;
import space.pxls.renderers.Heatmap;
import space.pxls.renderers.Template;
import space.pxls.renderers.Virginmap;
import space.pxls.structs.TemplateState;

public class CanvasScreen extends ScreenAdapter implements PxlsClient.UpdateCallback {
    public final LoadScreen.BoardInfo boardInfo;
    private float zoom = 1;
    private Vector2 center = new Vector2();
    public SpriteBatch batch;

    private Vector2 focalPoint;
    private float initialZoom;

    private Stage stage = new Stage(new ExtendViewport(640, 0));

    private Container<WidgetGroup> topContainer;
    private Container<WidgetGroup> bottomContainer;
    private Container<PixelLookupOverlay> lookupContainer;
    private Container<StackOverlay> stackOverlayContainer;
    private UndoPopup undoPopup;
    private Cell centerPopupCell;
    public StackOverlay stackOverlay;

    private PixelBar paletteBar;
    private LoginBar login;
    private UserBar userBar;
    private UserCountOverlay userCountOverlay;

    private PxlsClient client;

    public Template template;
    public Heatmap heatmap;
    public Canvas canvas;
    private GridOverlay gridOverlay;
    public Virginmap virginmap;

    private TemplateMoveModeHelper templateMoveModeHelper;

    public CanvasScreen(Canvas canvas) {
        Pxls.gameState = Pxls.prefsHelper.GetSavedGameState();
        System.out.println("Initializing CanvasScreen with " + Pxls.gameState);
        boardInfo = canvas.info;

        batch = new SpriteBatch();

        if (Pxls.gameState.canvasState.panX == -1 || Pxls.gameState.canvasState.panY == -1) {
            center.set(canvas.info.width / 2, canvas.info.height / 2);
        } else {
            center.set(Pxls.gameState.canvasState.panX, Pxls.gameState.canvasState.panY);
        }
        zoom = Pxls.gameState.canvasState.zoom;

        paletteBar = new PixelBar(canvas.info.palette);
        login = new LoginBar();
        this.canvas = canvas.setParent(this);
        template = new Template(this);
        heatmap = new Heatmap(this);
        if (Pxls.prefsHelper.getHeatmapEnabled()) {
            heatmap.loadHeatmap();
        }

        virginmap = new Virginmap(this);
        if (Pxls.prefsHelper.getVirginmapEnabled()) {
            virginmap.loadMap();
        }
        gridOverlay = new GridOverlay(this);

        templateMoveModeHelper = new TemplateMoveModeHelper();

        bottomContainer = new Container<WidgetGroup>(login).fill();
        bottomContainer.background(Pxls.skin.getDrawable("background"));

        topContainer = new Container<WidgetGroup>(null).fill();
        topContainer.background(Pxls.skin.getDrawable("background"));

        lookupContainer = new Container<PixelLookupOverlay>(null).fill();
        lookupContainer.background(Pxls.skin.getDrawable("background"));

        stackOverlayContainer = new Container<StackOverlay>(null).fill();

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

        stackOverlay = new StackOverlay(canvas.info.maxStacked, canvas.info.maxStacked);
        stackOverlay.empty();
        stackOverlayContainer.removeActor(stackOverlayContainer.getActor());

        userCountOverlay = new UserCountOverlay();

        Table table = new Table();
        table.add(topContainer).fillX().expandX().colspan(3).row();
        table.add(lookupContainer).fillX().expandX().colspan(3).row();
        Stack centerPopup = new Stack();
        centerPopup.add(login.popup);
        centerPopup.add(undoPopup);

        table.add(stackOverlayContainer).expandY().bottom().left();
        table.add(centerPopup).center().bottom().expandX().padRight(stackOverlayContainer.getWidth());
        table.add(userCountOverlay).expandX().bottom().right();
        table.row();

        table.add(bottomContainer).fillX().expandX().colspan(3);
        table.setFillParent(true);
        stage.addActor(table);

        client = new PxlsClient(this);
    }

    public void menuClosed() {
        if (Pxls.prefsHelper.getHeatmapEnabled() && heatmap != null) {
            heatmap.updateTexture();
        }
        if (Pxls.prefsHelper.getVirginmapEnabled() && virginmap != null) {
            virginmap.updateTexture();
        }

        if (userCountOverlay != null && userCountOverlay.hasReceivedCount()) userCountOverlay.setVisible(!Pxls.prefsHelper.getHideUserCount());
        if (Pxls.gameState.getSafeTemplateState().moveMode) {
            if (!Pxls.prefsHelper.getHasSeenMoveModeTutorial()) {
                PxlsGame.i.alert("Pan/zoom/etc as you normally would.\n\nIf you double tap a pixel, the top left corner of the template will move to where you tapped.\n\nUse the nudge buttons for precision after jumping", new PxlsGame.ButtonCallback() {
                    @Override
                    public void clicked() {
                        Pxls.prefsHelper.setHasSeenMoveModeTutorial(true);
                    }
                });
            }
            Pxls.gameState.getSafeTemplateState().stageForMoving();
            topContainer.setActor(templateMoveModeHelper.moveModeControls);
        } else {
            topContainer.setActor(userBar);
        }
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (!templateMoveModeHelper.pointerDown[pointer]) ++templateMoveModeHelper.numPointersDown;
                templateMoveModeHelper.pointerDown[pointer] = true;
                return false;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                if (templateMoveModeHelper.pointerDown[pointer]) --templateMoveModeHelper.numPointersDown;
                templateMoveModeHelper.pointerDown[pointer] = false;
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                if (Pxls.gameState.getSafeCanvasState().locked) return true;

                Vector2 delta = new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() - Gdx.input.getY()) - Gdx.graphics.getHeight() / 2);
                int max = 75;

                float oldZoom = zoom;
                if (amount > 0) {
                    zoom /= 1.2f;
                } else {
                    zoom *= 1.2f;
                }
                zoom = MathUtils.clamp(zoom, 1, Pxls.prefsHelper.getAllowGreaterZoom() ? max * 10 : max);

                center.x += delta.x / oldZoom;
                center.y += delta.y / oldZoom;

                center.x -= delta.x / zoom;
                center.y -= delta.y / zoom;

                Pxls.gameState.canvasState.zoom = zoom;
                Pxls.gameState.canvasState.panX = (int)center.x;
                Pxls.gameState.canvasState.panY = (int)center.y;
                Pxls.prefsHelper.SaveGameState(Pxls.gameState);
                return true;
            }
        }, new GestureDetector(new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (Pxls.gameState.getSafeTemplateState().moveMode && count == 2) {
                    Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                    Pxls.gameState.getSafeTemplateState().setOffsetX((int)pos.x);
                    Pxls.gameState.getSafeTemplateState().setOffsetY((int)pos.y);
                    return true;
                } else if (paletteBar.getCurrentColor() >= 0) {
                    Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                    placePixel((int) pos.x, (int) pos.y, Pxls.prefsHelper.getKeepColorSelected());
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
                if (Pxls.gameState.getSafeCanvasState().locked) return true;

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
                Pxls.gameState.canvasState.panX = (int)center.x;
                Pxls.gameState.canvasState.panY = (int)center.y;
                Pxls.prefsHelper.SaveGameState(Pxls.gameState);
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
                if (Pxls.gameState.getSafeCanvasState().locked) return true;

                int hw = Gdx.graphics.getWidth() / 2;
                int hh = Gdx.graphics.getHeight() / 2;
                float max = 200f;
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
                zoom = MathUtils.clamp(zoom, 0.5f, Pxls.prefsHelper.getAllowGreaterZoom() ? max * 10 : max);
                center.x = MathUtils.clamp(center.x, 0, boardInfo.width);
                center.y = MathUtils.clamp(center.y, 0, boardInfo.height);
                Pxls.gameState.canvasState.zoom = zoom;
                Pxls.gameState.canvasState.panX = (int)center.x;
                Pxls.gameState.canvasState.panY = (int)center.y;
                Pxls.prefsHelper.SaveGameState(Pxls.gameState);
                return true;
            }

            @Override
            public void pinchStop() {
                focalPoint = null;
            }
        })));
    }

    private void showLookup(int x, int y) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(Pxls.domain + "/lookup?x=" + x + "&y=" + y);
        req.setHeader("User-Agent", Pxls.getUA());
        req.setHeader("Cookie", String.format("pxls-token=%s", Pxls.prefsHelper.getToken()));
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String resp = httpResponse.getResultAsString();
                
                JsonObject data = Pxls.gson.fromJson(resp, JsonObject.class);

                PixelLookupOverlay plo = new PixelLookupOverlay(
                    data.get("x").getAsInt(),
                    data.get("y").getAsInt(),
                    data.get("username").getAsString(),
                    data.get("time").getAsLong(),
                    data.get("pixel_count").getAsInt(),
                    data.get("pixel_count_alltime").getAsInt(),
                    data.get("id").getAsInt(),
                    client.loggedIn
                );
                lookupContainer.setActor(plo);
            }

            @Override
            public void failed(Throwable t) {
                lookupContainer.removeActor(lookupContainer.getActor());
            }

            @Override
            public void cancelled() {

            }
        });
    }

    private void placePixel(int x, int y, boolean keepSelected) {
        if (paletteBar.getCurrentColor() >= 0) {
            client.placePixel(x, y, paletteBar.getCurrentColor());
            if (!keepSelected) {
                paletteBar.changeColor(-1);
            }
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
        try {
            super.render(delta);
            Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();
            Vector2 screenCenter = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).scl(0.5f);
            Vector2 canvasSize = new Vector2(boardInfo.width, boardInfo.height).scl(zoom);
            Vector2 canvasCorner = screenCenter.mulAdd(center, -zoom);
            canvas.render(zoom, screenCenter, canvasSize, canvasCorner);
            heatmap.render(zoom, screenCenter, canvasSize, canvasCorner);
            virginmap.render(zoom, screenCenter, canvasSize, canvasCorner);
            template.render(zoom, screenCenter);
            gridOverlay.render(zoom, screenCenter, canvasSize, canvasCorner);
            batch.end();

            stage.act(delta);
            stage.draw();
        } catch (java.lang.StringIndexOutOfBoundsException sioobe) {
            sioobe.printStackTrace();
            System.err.println("got a StringIndexOutOfBounds?");
        }
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
                canvas.pixel(x, y, color);
                heatmap.pixel(x, y, color);
                virginmap.pixel(x, y, color);
            }
        });
    }

    @Override
    public void users(int users) {
        if (userCountOverlay == null) return;
        userCountOverlay.setCount(users);
    }

    @Override
    public void updateAccount(final Account account) {
        if (account != null && bottomContainer.getActor() == login) {
            bottomContainer.setActor(paletteBar);
        }

        if (account != null) {
            final CanvasScreen self = this;
            userBar = new UserBar(account.getName());
            userBar.addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {
                    if (event instanceof UserBar.LogoutEvent) {
                        logout(false);
                        return true;
                    }
                    if (event instanceof UserBar.MenuOpenRequestedEvent) {
                        PxlsGame.i.setScreen(new MenuScreen(self, account));
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
        if (bottomContainer.getActor() != login) {
            stackOverlayContainer.setActor(stackOverlay);
            stackOverlay.updateCooldown(seconds);
        } else {
            stackOverlayContainer.removeActor(stackOverlayContainer.getActor());
        }
        centerPopupCell.padRight(stackOverlay.getWidth());
    }

    @Override
    public void canUndo(float seconds) {
        undoPopup.popUp(seconds);
    }

    @Override
    public void stack(int count, String cause) {
        if (bottomContainer.getActor() != login) {
            stackOverlayContainer.setActor(stackOverlay);
            stackOverlay.updateStack(count, cause);
        } else {
            stackOverlayContainer.removeActor(stackOverlayContainer.getActor());
        }
        centerPopupCell.padRight(stackOverlay.getWidth());
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

    public void moveTo(int x, int y, int scale) {
        System.out.printf("should jump to %s %s @%s%n", x, y, scale);
        center.x = x;
        center.y = y;
        zoom = scale;
    }

    public void logout() {
        logout(true);
    }
    public void logout(boolean skipConfirm) {
        if (!skipConfirm) {
            PxlsGame.i.confirm("Are you sure you want to log out?", new PxlsGame.ConfirmCallback() {
                @Override
                public void done(boolean confirmed) {
                    if (confirmed) {
                        doLogout();
                    }
                }
            });
        } else {
            doLogout();
        }
    }

    private void doLogout() {
        PxlsGame.i.logOut();
        stackOverlayContainer.removeActor(stackOverlayContainer.getActor());
        bottomContainer.setActor(login);
    }

    public Template getTemplate() {
        return template;
    }

    private class TemplateMoveModeHelper {
        private int numPointersDown = 0;
        private boolean[] pointerDown = new boolean[] {false, false, false, false, false, false, false, false, false, false};

        public Table moveModeControls;
        public TemplateMoveModeHelper() {
            PxlsButton btnCancel = new PxlsButton(" Cancel ").setFontScale(0.2f).red();
            PxlsButton btnConfirm = new PxlsButton(" Confirm ").setFontScale(0.2f).blue();

            PxlsButton btnUp = new PxlsButton(" up ").setFontScale(0.2f);
            PxlsButton btnDown = new PxlsButton(" down ").setFontScale(0.2f);
            PxlsButton btnLeft = new PxlsButton(" left ").setFontScale(0.2f);
            PxlsButton btnRight = new PxlsButton(" right ").setFontScale(0.2f);

            btnUp.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.gameState.getSafeTemplateState().movingOffsetY -= 1;
                }
            });

            btnDown.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.gameState.getSafeTemplateState().movingOffsetY += 1;
                }
            });

            btnLeft.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.gameState.getSafeTemplateState().movingOffsetX -= 1;
                }
            });

            btnRight.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.gameState.getSafeTemplateState().movingOffsetX += 1;
                }
            });

            btnCancel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    PxlsGame.i.confirm("Are you sure you want to cancel template movement?", new PxlsGame.ConfirmCallback() {
                        @Override
                        public void done(boolean confirmed) {
                            if (confirmed) {
                                Pxls.gameState.getSafeTemplateState().finalizeMove(true);
                                topContainer.setActor(userBar);
                            }
                        }
                    });
                }
            });

            btnConfirm.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    PxlsGame.i.confirm("Is this where you want to move the template to?", new PxlsGame.ConfirmCallback() {
                        @Override
                        public void done(boolean confirmed) {
                            if (confirmed) {
                                Pxls.gameState.getSafeTemplateState().finalizeMove(false);
                                topContainer.setActor(userBar);
                                Pxls.prefsHelper.SaveGameState(Pxls.gameState);
                            }
                        }
                    });
                }
            });

            moveModeControls = new Table(Pxls.skin);
            moveModeControls.add(btnCancel).padRight(8).padTop(4).padBottom(4).center();
            moveModeControls.add(new Container()).padRight(8).padTop(4).padBottom(4).center();
            moveModeControls.add(btnConfirm).padRight(16).padTop(4).padBottom(4).center().row();
            moveModeControls.add(new SolidContainer()).growX().height(2).pad(2,0,2,0).colspan(3).row();

            moveModeControls.add(new PxlsLabel("Nudge:").setFontScaleChain(0.3f)).pad(0,4,0,4).center().colspan(3).row();

            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(btnUp).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX().row();

            moveModeControls.add(btnLeft).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(btnRight).pad(4, 4, 4, 4).fillX().row();

            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(btnDown).pad(4, 4, 4, 4).fillX();
            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX().row();
        }
    }
}
