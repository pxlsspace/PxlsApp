package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Map;

import space.pxls.Account;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class MenuScreen extends ScreenAdapter {
    private Stage stage;
    private Account account;
    private CanvasScreen canvasScreen;
    public int w=0,h=0;

    public MenuScreen(CanvasScreen canvasScreen, Account loggedInAccount) {
        this.stage = new Stage();
        this.canvasScreen = canvasScreen;
        this.account = loggedInAccount;
        build();
    }

    private void build() {
        Gdx.input.setInputProcessor(stage);
        Color shadeColor = new Color(0,0,0,0.05f);
        final MenuScreen self = this;
        final Image logoutIcon = new Image(Pxls.skin.getDrawable("logout"));

        //settings checkboxes
        final PxlsCheckBox cbKeepSelected = new PxlsCheckBox("Keep color selected", Pxls.prefsHelper.getKeepColorSelected());
        final PxlsCheckBox cbGreaterZoom = new PxlsCheckBox("Allow greater zoom", Pxls.prefsHelper.getAllowGreaterZoom());
        final PxlsCheckBox cbRememberState = new PxlsCheckBox("Save last scroll/zoom", Pxls.prefsHelper.getRememberCanvasState());
        final PxlsCheckBox cbRememberTemplate = new PxlsCheckBox("Save last template", Pxls.prefsHelper.getRememberTemplate());
        final PxlsCheckBox cbHeatmap = new PxlsCheckBox("Enable Heatmap", Pxls.prefsHelper.getHeatmapEnabled());
        final PxlsCheckBox cbTemplate = new PxlsCheckBox("Enable Template", Pxls.gameState.getSafeTemplateState().enabled);
        final PxlsCheckBox cbMoveMode = new PxlsCheckBox("Move Mode", Pxls.gameState.getSafeTemplateState().moveMode);
        final PxlsCheckBox cbGrid = new PxlsCheckBox("Enable Grid", Pxls.prefsHelper.getGridEnabled());
        final PxlsCheckBox cbHideUserCount = new PxlsCheckBox("Hide UserCount", Pxls.prefsHelper.getHideUserCount());
        final PxlsCheckBox cbVirginmapEnabled = new PxlsCheckBox("Enable VirginMap", Pxls.prefsHelper.getVirginmapEnabled());
        final PxlsCheckBox cbCanvasLocked = new PxlsCheckBox("Lock Canvas", Pxls.gameState.getSafeCanvasState().locked);
        final PxlsCheckBox cbShouldVibe = new PxlsCheckBox("Enable vibration", Pxls.prefsHelper.getShouldVibrate());
        final PxlsCheckBox cbShouldVibeOnStack = new PxlsCheckBox("Vibrate on stack gain", Pxls.prefsHelper.getShouldVibeOnStack());
        final PxlsCheckBox cbShouldPrevibe = new PxlsCheckBox("Enable \"previbe\" ticks", Pxls.prefsHelper.getShouldPrevibe());

        final PxlsButton btnGetTemplateURL = new PxlsButton("Get current template link");

        btnGetTemplateURL.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                PxlsGame.i.input("Pxls.space template URL", canvasScreen.getTemplate().makePxlsURL(), new PxlsGame.InputCallback() {
                    @Override
                    public void cancelled() { /* ignored */ }

                    @Override
                    public void input(String response) { /* ignored */ }
                });
            }
        });

        final PxlsSlider sliderHeatmapOpacity = new PxlsSlider().setPrepend("Opacity: ");
        sliderHeatmapOpacity.setValue(Pxls.gameState.getSafeHeatmapState().opacity);

        final PxlsSlider sliderTemplateOpacity = new PxlsSlider().setPrepend("Opacity: ");
        sliderTemplateOpacity.setValue(Pxls.gameState.getSafeTemplateState().opacity);

        final PxlsButton btnLoadTemplateFromClipboard = new PxlsButton("Load From Clipboard");
        btnLoadTemplateFromClipboard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                final Map<String, String> templateValues = PxlsGame.i.parseTemplateURL(Gdx.app.getClipboard().getContents());
                if (templateValues != null) {
                    PxlsGame.i.confirm("Are you sure you want to load a template from your clipboard?", new PxlsGame.ConfirmCallback() {
                        @Override
                        public void done(boolean confirmed) {
                            if (confirmed) {
                                canvasScreen.template.load(Integer.parseInt(templateValues.get("ox")), Integer.parseInt(templateValues.get("oy")), Float.valueOf(templateValues.get("tw")), Float.valueOf(templateValues.get("oo")), templateValues.get("template"));
                                sliderTemplateOpacity.setValue(Float.valueOf(templateValues.get("oo")));
                                cbTemplate.setChecked(true);
                            }
                        }
                    });
                } else {
                    PxlsGame.i.alert("Clipboard was not a valid pxls.space template URL.");
                }
            }
        });

        final PxlsSlider sliderVirginmapOpacity = new PxlsSlider().setPrepend("Opacity: ");
        sliderVirginmapOpacity.setValue(Pxls.gameState.getSafeVirginmapState().opacity);

        Table tcMisc = new TitledTableHelper("Misc");
        tcMisc.add(cbKeepSelected).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbGreaterZoom).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbRememberState).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbHideUserCount).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbCanvasLocked).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tcHeatmap = new TitledTableHelper("Heatmap");
        tcHeatmap.add(cbHeatmap).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcHeatmap.add(sliderHeatmapOpacity).colspan(2).growX().fillY().row();

        Table tcGrid = new TitledTableHelper("Grid");
        tcGrid.add(cbGrid).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tcTemplate = new TitledTableHelper("Template");
        tcTemplate.add(cbTemplate).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcTemplate.add(cbRememberTemplate).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcTemplate.add(cbMoveMode).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcTemplate.add(sliderTemplateOpacity).colspan(2).growX().fillY().row();
        tcTemplate.add(btnLoadTemplateFromClipboard).pad(8).colspan(2).growX().center().row();
        tcTemplate.add(btnGetTemplateURL).pad(8).colspan(2).growX().center().row();

        Table tcVirginmap = new TitledTableHelper("Virginmap");
        tcVirginmap.add(cbVirginmapEnabled).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcVirginmap.add(sliderVirginmapOpacity).colspan(2).growX().fillY().row();

        Table tcVibration = new TitledTableHelper("Vibration");
        tcVibration.add(cbShouldVibe).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcVibration.add(cbShouldPrevibe).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcVibration.add(cbShouldVibeOnStack).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Button closeButton = new Button(Pxls.skin.getDrawable("times"));
        Table table = new Table();
        table.setFillParent(true);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //Toggle states
                Pxls.prefsHelper.setKeepColorSelected(cbKeepSelected.isChecked());
                Pxls.prefsHelper.setAllowGreaterZoom(cbGreaterZoom.isChecked());
                Pxls.prefsHelper.setRememberCanvasState(cbRememberState.isChecked());
                Pxls.prefsHelper.setRememberTemplate(cbRememberTemplate.isChecked());
                Pxls.prefsHelper.setGridEnabled(cbGrid.isChecked());
                Pxls.prefsHelper.setHeatmapEnabled(cbGrid.isChecked());
                Pxls.prefsHelper.setHideUerCount(cbHideUserCount.isChecked());
                Pxls.prefsHelper.setHeatmapEnabled(cbHeatmap.isChecked());
                Pxls.prefsHelper.setVirginmapEnabled(cbVirginmapEnabled.isChecked());
                Pxls.prefsHelper.setShouldVibrate(cbShouldVibe.isChecked());
                Pxls.prefsHelper.setShouldPrevibe(cbShouldPrevibe.isChecked());
                Pxls.prefsHelper.setShouldVibeOnStack(cbShouldVibeOnStack.isChecked());
                Pxls.gameState.getSafeCanvasState().locked = (cbCanvasLocked.isChecked());
                Pxls.gameState.getSafeTemplateState().enabled = cbTemplate.isChecked();
                Pxls.gameState.getSafeTemplateState().moveMode = cbMoveMode.isChecked();

                Pxls.gameState.getSafeHeatmapState().opacity = sliderHeatmapOpacity.getValue();
                Pxls.gameState.getSafeTemplateState().opacity = sliderTemplateOpacity.getValue();
                Pxls.gameState.getSafeVirginmapState().opacity = sliderVirginmapOpacity.getValue();

                //Flush modified GameState immediately
                Pxls.prefsHelper.SaveGameState(Pxls.gameState, true);

                //Return to canvas
                PxlsGame.i.setScreen(self.canvasScreen);

                //Tell the canvasScreen settings have closed. Will handle showing/hiding the grid/heatmap/etc
                self.canvasScreen.menuClosed();
            }
        });

        Table topBarTable = new Table().pad(12);
        topBarTable.add(new PxlsLabel(this.account == null ? "Not Logged In" : "Logged in as " + this.account.getSanitizedName()).setFontScaleChain(0.5f)).left();
        topBarTable.add(closeButton).size(80,80).expandX().right();

        table.add(new Stack(new SolidContainer(shadeColor), topBarTable)).growX().row();

        Table contentTable = new Table();
        contentTable.add(tcMisc).padTop(24).growX().row();
        contentTable.add(tcVibration).padTop(48).growX().row();
        contentTable.add(tcHeatmap).padTop(48).growX().row();
        contentTable.add(tcVirginmap).padTop(48).growX().row();
        contentTable.add(tcTemplate).padTop(48).growX().row();

        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setCancelTouchFocus(false);
        table.add(contentPane).grow().padLeft(24).padRight(24).row();
        table.add(new Container()).padBottom(24).colspan(2).growY().row(); //Adds a cell that fills the remaining height between the last cell and the next one. Used as a spacer to stick our logout to the bottom and to ensure everything displays properly
        if (this.account != null) {
            Table tblLogout = new Table();
            tblLogout.add(logoutIcon).growX().right().size(96, 96);
            tblLogout.add(new PxlsLabel("Logout").setFontScaleChain(.6f)).right().row();
            tblLogout.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    PxlsGame.i.setScreen(self.canvasScreen);
                    self.canvasScreen.logout(false);
                }
            });

            table.add(new Stack(new SolidContainer(shadeColor), tblLogout)).colspan(2).growX().right().row();
        }

        for (Actor a : stage.getActors()) {
            a.remove();
        }
        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private Label makeLabel(String text) {
        Label toReturn = new Label(text, Pxls.skin);
        toReturn.setFontScale(0.3f);
        return toReturn;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (w != width || h != height) {
            MenuScreen ms = new MenuScreen(canvasScreen, account);
            ms.w = width;
            ms.h = height;
            PxlsGame.i.setScreen(ms);
        }
    }
}
