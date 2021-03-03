package space.pxls.ui.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import space.pxls.Lookup;
import space.pxls.OrientationHelper;
import space.pxls.Pxls;
import space.pxls.PxlsClient;
import space.pxls.PxlsGame;
import space.pxls.data.Pixel;
import space.pxls.renderers.Canvas;
import space.pxls.renderers.GridOverlay;
import space.pxls.renderers.Heatmap;
import space.pxls.renderers.Template;
import space.pxls.renderers.Virginmap;
import space.pxls.structs.PxlsGameState;
import space.pxls.ui.BannedBar;
import space.pxls.ui.Components.PxlsButton;
import space.pxls.ui.Components.PxlsSlider;
import space.pxls.ui.Components.SolidContainer;
import space.pxls.ui.Components.TTFLabel;
import space.pxls.ui.LoginBar;
import space.pxls.ui.Overlays.CooldownOverlay;
import space.pxls.ui.Overlays.PixelLookupOverlay;
import space.pxls.ui.Overlays.StackOverlay;
import space.pxls.ui.Overlays.UserCountOverlay;
import space.pxls.ui.PixelBar;
import space.pxls.ui.UndoPopup;
import space.pxls.ui.events.MenuOpenRequested;
import space.pxls.data.User;

public class CanvasScreen extends ScreenAdapter implements PxlsClient.UpdateCallback {
    public final LoadScreen.BoardInfo boardInfo;
    private final Stage stage = new Stage(new ExtendViewport(640, 0));
    private final Container<WidgetGroup> bottomContainer;
    private final Container<PixelLookupOverlay> lookupContainer;
    private final Container<StackOverlay> stackOverlayContainer;
    private final UndoPopup undoPopup;
    private final Cell topCell;
    private final PixelBar paletteBar;
    private final LoginBar login = new LoginBar();
    private final UserCountOverlay userCountOverlay;
    private final GridOverlay gridOverlay;
    private final Table pixcountAndCooldownTable;
    private final Container<Label> secondaryCooldownContainer;
    private final Cell secondaryCooldownContainerCell;
    private final AuthedBar authedBar;
    private final TemplateMoveModeHelper templateMoveModeHelper;
    public SpriteBatch batch = new SpriteBatch();
    public StackOverlay stackOverlay;
    public Template template;
    public Heatmap heatmap;
    public Canvas canvas;
    public Virginmap virginmap;
    private float zoom;
    private Vector2 center = new Vector2();
    private Vector2 focalPoint;
    private float initialZoom;
    private PxlsClient client;
    private User user;

    public CanvasScreen(Canvas canvas) {
        CooldownOverlay.getInstance().resetLabel();
        final CanvasScreen self = this;
        Pxls.setGameState(Pxls.getPrefsHelper().GetSavedGameState());
        boardInfo = canvas.info;

        PxlsGameState gameState = Pxls.getGameState();

        if (gameState.canvasState.panX == -1 || gameState.canvasState.panY == -1) {
            center.set(canvas.info.width / 2f, canvas.info.height / 2f);
        } else {
            center.set(gameState.canvasState.panX, gameState.canvasState.panY);
        }
        zoom = gameState.canvasState.zoom;

        paletteBar = new PixelBar(canvas.info.palette);
        this.canvas = canvas.setParent(this);
        template = new Template(this);
        heatmap = new Heatmap(this);
        if (Pxls.getPrefsHelper().getHeatmapEnabled()) {
            heatmap.loadHeatmap();
        }
        virginmap = new Virginmap(this);
        if (Pxls.getPrefsHelper().getVirginmapEnabled()) {
            virginmap.loadMap();
        }
        gridOverlay = new GridOverlay(this);

        secondaryCooldownContainer = new Container<>(new TTFLabel(""));
        secondaryCooldownContainer.padLeft(4).padRight(16);

        templateMoveModeHelper = new TemplateMoveModeHelper();

        bottomContainer = new Container<WidgetGroup>(login).fill();
        bottomContainer.background(Pxls.getSkin().getDrawable("background"));

        lookupContainer = new Container<PixelLookupOverlay>(null).fill();
        lookupContainer.background(Pxls.getSkin().getDrawable("background"));

        stackOverlayContainer = new Container<StackOverlay>(null).fill();

        undoPopup = new UndoPopup();
        undoPopup.addListener(event -> {
            if (event instanceof UndoPopup.UndoEvent) {
                client.getPacketHandler().undoPixel();
                return true;
            }
            return false;
        });

        stackOverlay = new StackOverlay(canvas.info.maxStacked, canvas.info.maxStacked);
        stackOverlay.empty();
        if (stackOverlayContainer.getActor() != null)
            stackOverlayContainer.removeActor(stackOverlayContainer.getActor());

        userCountOverlay = new UserCountOverlay();

        authedBar = new AuthedBar();
        authedBar.addListener(event -> {
            System.out.printf("Got event %s%n", event.getClass().getSimpleName());
            if (event instanceof MenuOpenRequested) {
                PxlsGame.i.setScreen(new MenuScreen(self, user));
                return true;
            }
            return false;
        });

        Table mainUITable = new Table();
        topCell = mainUITable.add(authedBar).fillX().expandX().colspan(3);
        mainUITable.row();

        mainUITable.add(lookupContainer).fillX().expandX().colspan(3).row();
        Stack centerPopup = new Stack();
        centerPopup.add(login.popup);
        centerPopup.add(undoPopup);

        pixcountAndCooldownTable = new Table();
        pixcountAndCooldownTable.setBackground(new NinePatchDrawable(Pxls.getSkin().getPatch("rounded.topRight")));
        secondaryCooldownContainerCell = pixcountAndCooldownTable.add(secondaryCooldownContainer).fill().space(0).pad(0);
        pixcountAndCooldownTable.add(stackOverlayContainer);
        // Hide stack and cooldown by default, set visible when logged in
        pixcountAndCooldownTable.setVisible(false);

        mainUITable.add(pixcountAndCooldownTable).bottom().left();
        mainUITable.add(centerPopup).center().bottom().expandX().expandY();
        mainUITable.add(userCountOverlay).bottom().right();
        mainUITable.row();

        mainUITable.add(bottomContainer).fillX().expandX().colspan(3);
        mainUITable.setFillParent(true);
        stage.addActor(mainUITable);
        client = new PxlsClient(this);
    }

    public void menuClosed() {
        if (Pxls.getPrefsHelper().getHeatmapEnabled() && heatmap != null) {
            heatmap.updateTexture();
        }
        if (Pxls.getPrefsHelper().getVirginmapEnabled() && virginmap != null) {
            virginmap.updateTexture();
        }
        if (authedBar != null) {
            authedBar.setLockImageVisible(Pxls.getGameState().getSafeCanvasState().locked);
        }

        if (userCountOverlay != null && userCountOverlay.hasReceivedCount())
            userCountOverlay.setVisible(!Pxls.getPrefsHelper().getHideUserCount());
        if (Pxls.getGameState().getSafeTemplateState().moveMode) {
            if (!Pxls.getPrefsHelper().getHasSeenMoveModeTutorial()) {
                PxlsGame.i.alert(Pxls.getMoveModeTutorial(), () -> Pxls.getPrefsHelper().setHasSeenMoveModeTutorial(true));
            }
            templateMoveModeHelper.moveStart();
            Pxls.getGameState().getSafeTemplateState().stageForMoving();
            topCell.setActor(templateMoveModeHelper.moveModeControls);
        } else {
            topCell.setActor(authedBar);
        }

        updateCooldownActors();
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
                templateMoveModeHelper.pointerDown[pointer] = true;
                return false;
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
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
            public boolean scrolled(float amountX, float amountY) {
                System.out.println("amountX: " + amountX);
                System.out.println("amountY: " + amountY);
                if (Pxls.getGameState().getSafeCanvasState().locked) return true;

                Vector2 delta = new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth() / 2f, (Gdx.graphics.getHeight() - Gdx.input.getY()) - Gdx.graphics.getHeight() / 2f);
                int max = 75;

                float oldZoom = zoom;
                if (amountX > 0 && amountY > 0) {
                    zoom /= 1.2f;
                } else {
                    zoom *= 1.2f;
                }
                zoom = MathUtils.clamp(zoom, 1, Pxls.getPrefsHelper().getAllowGreaterZoom() ? max * 10 : max);

                center.x += delta.x / oldZoom;
                center.y += delta.y / oldZoom;

                center.x -= delta.x / zoom;
                center.y -= delta.y / zoom;

                Pxls.getGameState().canvasState.zoom = zoom;
                Pxls.getGameState().canvasState.panX = (int) center.x;
                Pxls.getGameState().canvasState.panY = (int) center.y;
                Pxls.getPrefsHelper().SaveGameState(Pxls.getGameState());
                return true;
            }
        }, new GestureDetector(new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (Pxls.getGameState().getSafeTemplateState().moveMode && count == 2) {
                    Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                    Pxls.getGameState().getSafeTemplateState().setOffsetX((int) pos.x);
                    Pxls.getGameState().getSafeTemplateState().setOffsetY((int) pos.y);
                    return true;
                } else if (paletteBar.getCurrentColor() >= 0 && !Pxls.getGameState().getSafeTemplateState().moveMode && y > authedBar.getHeight() * 2) {
                    Vector2 pos = screenToBoardSpace(new Vector2(x, y));
                    placePixel((int) pos.x, (int) pos.y, Pxls.getPrefsHelper().getKeepColorSelected());
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
                if (Pxls.getGameState().getSafeCanvasState().locked) return true;

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
                Pxls.getGameState().canvasState.panX = (int) center.x;
                Pxls.getGameState().canvasState.panY = (int) center.y;
                Pxls.getPrefsHelper().SaveGameState(Pxls.getGameState());
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
                if (Pxls.getGameState().getSafeCanvasState().locked) return true;

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
                zoom = MathUtils.clamp(zoom, 0.5f, Pxls.getPrefsHelper().getAllowGreaterZoom() ? max * 10 : max);
                center.x = MathUtils.clamp(center.x, 0, boardInfo.width);
                center.y = MathUtils.clamp(center.y, 0, boardInfo.height);
                Pxls.getGameState().canvasState.zoom = zoom;
                Pxls.getGameState().canvasState.panX = (int) center.x;
                Pxls.getGameState().canvasState.panY = (int) center.y;
                Pxls.getPrefsHelper().SaveGameState(Pxls.getGameState());
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
        req.setUrl(Pxls.getDomain() + "/lookup?x=" + x + "&y=" + y);
        req.setHeader("User-Agent", Pxls.getUserAgent());
        req.setHeader("Cookie", String.format("pxls-token=%s", Pxls.getPrefsHelper().getToken()));
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String resp = httpResponse.getResultAsString();

                Lookup lookup = Pxls.getGson().fromJson(resp, Lookup.class);

                PixelLookupOverlay plo = new PixelLookupOverlay(lookup, client.loggedIn);
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
        if (!stackOverlay.onCooldown() && paletteBar.getCurrentColor() >= 0) {
            client.getPacketHandler().placePixel(new Pixel(x, y, paletteBar.getCurrentColor()));
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

    public void updateCooldownActors() {
        if (paletteBar != null && secondaryCooldownContainer != null && secondaryCooldownContainerCell != null) {
            Label cdLabel = CooldownOverlay.getInstance().getCooldownLabel();
            boolean keepSelected = Pxls.getPrefsHelper().getKeepColorSelected();
            boolean isOnCD = CooldownOverlay.getInstance().getCooldownExpiry() - System.currentTimeMillis() > 0;

            paletteBar.getCooldownContainer().setActor(keepSelected ? null : cdLabel);
            secondaryCooldownContainer.setActor(keepSelected ? cdLabel : null);
            if (keepSelected) {
                secondaryCooldownContainerCell.setActor(isOnCD ? secondaryCooldownContainer : null);
            } else {
                secondaryCooldownContainerCell.setActor(null);
            }
        }
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
            template.render(zoom, screenCenter, canvasSize, canvasCorner);
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
        client.getPacketHandler().getSocket().close();
        client = new PxlsClient(this);
        authedBar.setUsername(null);
    }

    @Override
    public void pixel(final int x, final int y, final int color) {
        Gdx.app.postRunnable(() -> {
            canvas.pixel(x, y, color);
            heatmap.pixel(x, y, color);
            virginmap.pixel(x, y, color);
        });
    }

    @Override
    public void users(int users) {
        if (userCountOverlay == null) return;
        userCountOverlay.setCount(users);
    }

    @Override
    public void updateUser(final User user) {
        if (user != null && bottomContainer.getActor() == login) {
            bottomContainer.setActor(paletteBar);
        }

        this.user = user;

        if (user != null) {
            authedBar.setUsername(user.getSanitizedUsername());
            pixcountAndCooldownTable.setVisible(true);
            if (user.isBanned()) {
                bottomContainer.setActor(new BannedBar(user.getBanExpiry(), user.getBanReason()));
            }
        } else {
            authedBar.setUsername(null);
        }
    }

    @Override
    public void cooldown(float seconds) {
        CooldownOverlay.getInstance().updateCooldown(seconds);
        if (bottomContainer.getActor() != login) {
            stackOverlayContainer.setActor(stackOverlay);
            stackOverlay.updateCooldown(seconds);
            updateCooldownActors();
        } else {
            stackOverlayContainer.removeActor(stackOverlayContainer.getActor());
        }
        //centerPopupCell.padRight(stackOverlay.getWidth());
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
        //centerPopupCell.padRight(stackOverlay.getWidth());
    }

    @Override
    public void runCaptcha() {
        PxlsGame.i.captchaRunner.doCaptcha(boardInfo.captchaKey, token -> client.getPacketHandler().finishCaptcha(token));
    }

    @Override
    public void showFailedCaptcha() {
        final GDXButtonDialog gdxButtonDialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage("Failed captcha verification");
        gdxButtonDialog.addButton(":(");
        gdxButtonDialog.setClickListener(button -> gdxButtonDialog.dismiss());
        gdxButtonDialog.build().show();
    }

    @Override
    public void showAlert(String message) {
        final GDXButtonDialog gdxButtonDialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("OK");
        gdxButtonDialog.setClickListener(button -> gdxButtonDialog.dismiss());
        gdxButtonDialog.build().show();
    }

    public void moveTo(int x, int y, float scale) {
        center.x = x;
        center.y = boardInfo.height - y; //(0, 0) is bottom left corner. y needs to be translated relative to board height in this case.
        zoom = scale;
    }

    public void logout(boolean skipConfirm) {
        if (!skipConfirm) {
            PxlsGame.i.confirm("Are you sure you want to log out?", confirmed -> {
                if (confirmed) doLogout();
            });
        } else {
            doLogout();
        }
    }

    private void doLogout() {
        PxlsGame.i.logOut();
        stackOverlayContainer.removeActor(stackOverlayContainer.getActor());
        pixcountAndCooldownTable.setVisible(false);
        bottomContainer.setActor(login);
    }

    public Template getTemplate() {
        return template;
    }

    public int panX() {
        return (int) Math.floor(center.x);
    }

    public int panY(boolean fix) {
        return fix ? (int) Math.floor(boardInfo.height - center.y) : (int) Math.floor(center.y);
    }

    public int panZoom() {
        return (int) Math.floor(zoom);
    }

    private static class AuthedBar extends Table {
        private final Label usernameLabel;
        private final Image lockImage;

        public AuthedBar() {
            setBackground(Pxls.getSkin().getDrawable("background"));
            pad(4, 12, 4, 12);

            usernameLabel = new TTFLabel("Not Authenticated").wrap(true);
            Image menuTriggerImage = new Image(Pxls.getSkin().getDrawable("menu"));
            lockImage = new Image(Pxls.getSkin().getDrawable("lock"));
            lockImage.setVisible(false);

            add(usernameLabel).left().growX();
            add(lockImage).size(48, 48).padRight(4).right();
            add(menuTriggerImage).size(48, 32).right();

            row();

            menuTriggerImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    fire(new MenuOpenRequested());
                }
            });
        }

        public void setUsername(String username) {
            usernameLabel.setText(username == null ? "Not Authenticated" : username);
        }

        public void setLockImageVisible(boolean v) {
            lockImage.setVisible(v);
        }
    }

    private class TemplateMoveModeHelper {
        private final boolean[] pointerDown = new boolean[]{false, false, false, false, false, false, false, false, false, false};
        private final PxlsSlider sliderOpacity = new PxlsSlider().setPrepend("Opacity ");
        public Table moveModeControls;
        Cell cellBtnUp, cellBtnDown, cellBtnLeft, cellBtnRight;
        PxlsButton btnCancel, btnConfirm;
        private float _lastOpacity = 0f;
        private OrientationHelper.Orientation orientation = null;

        public TemplateMoveModeHelper() {
            btnCancel = new PxlsButton(" Cancel ").red();
            btnCancel.getLabel().setFontScale(0.25f);
            btnConfirm = new PxlsButton(" Confirm ").blue();
            btnConfirm.getLabel().setFontScale(0.25f);

            Image btnUp = new Image(Pxls.getSkin().getDrawable("arrow.gray.up"));
            Image btnDown = new Image(Pxls.getSkin().getDrawable("arrow.gray.down"));
            Image btnLeft = new Image(Pxls.getSkin().getDrawable("arrow.gray.left"));
            Image btnRight = new Image(Pxls.getSkin().getDrawable("arrow.gray.right"));

            btnUp.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.getGameState().getSafeTemplateState().movingOffsetY -= 1;
                }
            });

            btnDown.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.getGameState().getSafeTemplateState().movingOffsetY += 1;
                }
            });

            btnLeft.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.getGameState().getSafeTemplateState().movingOffsetX -= 1;
                }
            });

            btnRight.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Pxls.getGameState().getSafeTemplateState().movingOffsetX += 1;
                }
            });

            btnCancel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    PxlsGame.i.confirm("Are you sure you want to cancel template movement?", confirmed -> {
                        if (confirmed) {
                            Pxls.getGameState().getSafeTemplateState().finalizeMove(true);
                            topCell.setActor(authedBar);
                            moveDone();
                        }
                    });
                }
            });

            btnConfirm.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    PxlsGame.i.confirm("Is this where you want to move the template to?", confirmed -> {
                        if (confirmed) {
                            Pxls.getGameState().getSafeTemplateState().finalizeMove(false);
                            topCell.setActor(authedBar);
                            Pxls.getPrefsHelper().SaveGameState(Pxls.getGameState(), true);
                            moveDone();
                        }
                    });
                }
            });

            moveModeControls = new Table(Pxls.getSkin());
            moveModeControls.add(btnCancel).padRight(8).padTop(4).padBottom(4).center();
            moveModeControls.add(new Container()).padRight(8).padTop(4).padBottom(4).center();
            moveModeControls.add(btnConfirm).padRight(16).padTop(4).padBottom(4).center().row();
            moveModeControls.add(new SolidContainer()).growX().height(2).pad(2, 0, 2, 0).colspan(3).row();

            moveModeControls.add(sliderOpacity).colspan(3).growX().row();

            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX();
            cellBtnUp = moveModeControls.add(btnUp).size(48, 48).pad(4, 4, 4, 4);
            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX().row();

            cellBtnLeft = moveModeControls.add(btnLeft).size(48, 48).right().pad(4, 4, 4, 4);
            moveModeControls.add(new Container()).pad(4, 4, 4, 4);
            cellBtnRight = moveModeControls.add(btnRight).size(48, 48).left().pad(4, 4, 4, 4);
            moveModeControls.row();

            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX();
            cellBtnDown = moveModeControls.add(btnDown).size(48, 48).pad(4, 4, 4, 4);
            moveModeControls.add(new Container()).pad(4, 4, 4, 4).fillX().row();

            sliderOpacity.setValue(Pxls.getGameState().getSafeTemplateState().opacity);
            sliderOpacity.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Pxls.getGameState().getSafeTemplateState().opacity = sliderOpacity.getValue();
                }
            });

            moveModeControls.setBackground(Pxls.getSkin().getDrawable("background"));
        }

        void moveStart() {
            if (PxlsGame.i.orientationHelper != null) {
                orientation = PxlsGame.i.orientationHelper.getOrientation();
                PxlsGame.i.orientationHelper.setOrientation(OrientationHelper.Orientation.PORTRAIT);
            }
            _lastOpacity = Pxls.getGameState().getSafeTemplateState().opacity;
            sliderOpacity.setValue(_lastOpacity);
        }

        void moveDone() {
            if (orientation != null && PxlsGame.i.orientationHelper != null) {
                PxlsGame.i.orientationHelper.setOrientation(OrientationHelper.Orientation.FULL_SENSOR);
            }
            Pxls.getGameState().getSafeTemplateState().opacity = _lastOpacity;
        }
    }
}
