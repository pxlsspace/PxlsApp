package space.pxls.ui.Screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Map;

import space.pxls.PrefsHelper;
import space.pxls.structs.CanvasState;
import space.pxls.structs.TemplateState;
import space.pxls.data.User;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.ui.Components.PxlsButton;
import space.pxls.ui.Components.PxlsCheckBox;
import space.pxls.ui.Components.PxlsSlider;
import space.pxls.ui.Components.SolidContainer;
import space.pxls.ui.Components.TTFLabel;
import space.pxls.ui.Components.TitledTableHelper;

public class MenuScreen extends ScreenAdapter {
    private final Stage stage;
    private final User user;
    private final CanvasScreen canvasScreen;
    public int w=0,h=0;

    public MenuScreen(CanvasScreen canvasScreen, User loggedInUser) {
        this.stage = new Stage();
        this.canvasScreen = canvasScreen;
        this.user = loggedInUser;
        build();
    }

    private void build() {
        Gdx.input.setInputProcessor(stage);
        Color shadeColor = new Color(0,0,0,0.05f);
        final MenuScreen self = this;
        final Image logoutIcon = new Image(Pxls.getSkin().getDrawable("logout"));

        PrefsHelper prefsHelper = Pxls.getPrefsHelper();
        TemplateState templateState = Pxls.getGameState().getSafeTemplateState();
        CanvasState canvasState = Pxls.getGameState().getSafeCanvasState();

        //settings checkboxes
        final PxlsCheckBox cbKeepSelected = new PxlsCheckBox("Keep color selected", prefsHelper.getKeepColorSelected());
        final PxlsCheckBox cbGreaterZoom = new PxlsCheckBox("Allow greater zoom", prefsHelper.getAllowGreaterZoom());
        final PxlsCheckBox cbRememberState = new PxlsCheckBox("Save last scroll/zoom", prefsHelper.getRememberCanvasState());
        final PxlsCheckBox cbRememberTemplate = new PxlsCheckBox("Save last template", prefsHelper.getRememberTemplate());
        final PxlsCheckBox cbHeatmap = new PxlsCheckBox("Enable Heatmap", prefsHelper.getHeatmapEnabled());
        final PxlsCheckBox cbTemplate = new PxlsCheckBox("Enable Template", templateState.enabled);
        final PxlsCheckBox cbMoveMode = new PxlsCheckBox("Move Mode", templateState.moveMode);
        final PxlsCheckBox cbGrid = new PxlsCheckBox("Enable Grid", prefsHelper.getGridEnabled());
        final PxlsCheckBox cbHideUserCount = new PxlsCheckBox("Hide UserCount", prefsHelper.getHideUserCount());
        final PxlsCheckBox cbVirginmapEnabled = new PxlsCheckBox("Enable VirginMap", prefsHelper.getVirginmapEnabled());
        final PxlsCheckBox cbCanvasLocked = new PxlsCheckBox("Lock Canvas", canvasState.locked);
//        final PxlsCheckBox cbShouldVibe = new PxlsCheckBox("Enable vibration", prefsHelper.getShouldVibrate());
//        final PxlsCheckBox cbShouldVibeOnStack = new PxlsCheckBox("Vibrate on stack gain", prefsHelper.getShouldVibeOnStack());
//        final PxlsCheckBox cbShouldPrevibe = new PxlsCheckBox("Enable \"previbe\" ticks", prefsHelper.getShouldPrevibe());

        final PxlsButton btnGetTemplateURL = new PxlsButton("Get current template link");
        final PxlsButton btnShowMoveModeTutorial = new PxlsButton("Show MoveMode tutorial");

        btnShowMoveModeTutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                PxlsGame.i.alert(Pxls.getMoveModeTutorial(), () -> prefsHelper.setHasSeenMoveModeTutorial(true));
            }
        });

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

        final PxlsSlider sliderHeatmapOpacity = new PxlsSlider();
        sliderHeatmapOpacity.setValue(Pxls.getGameState().getSafeHeatmapState().opacity);

        final PxlsSlider sliderVirginmapOpacity = new PxlsSlider();
        sliderVirginmapOpacity.setValue(Pxls.getGameState().getSafeVirginmapState().opacity);

        final PxlsSlider sliderTemplateOpacity = new PxlsSlider();
        sliderTemplateOpacity.setValue(Pxls.getGameState().getSafeTemplateState().opacity);

        final PxlsButton btnLoadTemplateFromClipboard = new PxlsButton("Load From Clipboard");
        btnLoadTemplateFromClipboard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                final Map<String, String> templateValues = PxlsGame.i.parseTemplateURL(Gdx.app.getClipboard().getContents());
                if (templateValues != null) {
                    PxlsGame.i.confirm("Are you sure you want to load a template from your clipboard?", confirmed -> {
                        if (confirmed) {
                            canvasScreen.template.load(Integer.parseInt(templateValues.get("ox")), Integer.parseInt(templateValues.get("oy")), Float.parseFloat(templateValues.get("tw")), Float.valueOf(templateValues.get("oo")), templateValues.get("template"));
                            sliderTemplateOpacity.setValue(Float.parseFloat(templateValues.get("oo")));
                            cbTemplate.setChecked(true);
                        }
                    });
                } else {
                    PxlsGame.i.alert("Clipboard was not a valid pxls.space template URL.");
                }
            }
        });

        Table tableMisc = new TitledTableHelper("Misc");
        tableMisc.add(cbKeepSelected).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableMisc.add(cbGreaterZoom).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableMisc.add(cbRememberState).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableMisc.add(cbHideUserCount).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableMisc.add(cbCanvasLocked).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tableHeatmap = new TitledTableHelper("Heatmap");
        tableHeatmap.add(cbHeatmap).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableHeatmap.add(sliderHeatmapOpacity).colspan(2).growX().fillY().row();

        Table tableVirginmap = new TitledTableHelper("Virginmap");
        tableVirginmap.add(cbVirginmapEnabled).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableVirginmap.add(sliderVirginmapOpacity).colspan(2).growX().fillY().row();

        Table tableGrid = new TitledTableHelper("Grid");
        tableGrid.add(cbGrid).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tableTemplate = new TitledTableHelper("Template");
        tableTemplate.add(cbTemplate).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableTemplate.add(cbRememberTemplate).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableTemplate.add(cbMoveMode).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tableTemplate.add(sliderTemplateOpacity).colspan(2).growX().fillY().row();
        tableTemplate.add(btnLoadTemplateFromClipboard).pad(8).colspan(2).growX().center().row();
        tableTemplate.add(btnGetTemplateURL).pad(8).colspan(2).growX().center().row();
        tableTemplate.add(btnShowMoveModeTutorial).pad(8).colspan(2).growX().center().row();

//        Table tcVibration = new TitledTableHelper("Vibration");
//        tcVibration.add(cbShouldVibe).padTop(6).padLeft(5).colspan(2).expandX().left().row();
//        tcVibration.add(cbShouldPrevibe).padTop(6).padLeft(5).colspan(2).expandX().left().row();
//        tcVibration.add(cbShouldVibeOnStack).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Button closeButton = new Button(Pxls.getSkin().getDrawable("times"));
        Table table = new Table();
        table.setFillParent(true);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //Toggle states
                prefsHelper.setKeepColorSelected(cbKeepSelected.isChecked());
                prefsHelper.setAllowGreaterZoom(cbGreaterZoom.isChecked());
                prefsHelper.setRememberCanvasState(cbRememberState.isChecked());
                prefsHelper.setRememberTemplate(cbRememberTemplate.isChecked());
                prefsHelper.setGridEnabled(cbGrid.isChecked());
                prefsHelper.setHeatmapEnabled(cbGrid.isChecked());
                prefsHelper.setHideUerCount(cbHideUserCount.isChecked());
                prefsHelper.setHeatmapEnabled(cbHeatmap.isChecked());
                prefsHelper.setVirginmapEnabled(cbVirginmapEnabled.isChecked());
//                prefsHelper.setShouldVibrate(cbShouldVibe.isChecked());
//                prefsHelper.setShouldPrevibe(cbShouldPrevibe.isChecked());
//                prefsHelper.setShouldVibeOnStack(cbShouldVibeOnStack.isChecked());
                Pxls.getGameState().getSafeCanvasState().locked = (cbCanvasLocked.isChecked());
                Pxls.getGameState().getSafeTemplateState().enabled = cbTemplate.isChecked();
                Pxls.getGameState().getSafeTemplateState().moveMode = cbMoveMode.isChecked();

                Pxls.getGameState().getSafeHeatmapState().opacity = sliderHeatmapOpacity.getValue();
                Pxls.getGameState().getSafeTemplateState().opacity = sliderTemplateOpacity.getValue();
                Pxls.getGameState().getSafeVirginmapState().opacity = sliderVirginmapOpacity.getValue();

                //Flush modified GameState immediately
                prefsHelper.SaveGameState(Pxls.getGameState(), true);

                //Return to canvas
                PxlsGame.i.setScreen(self.canvasScreen);

                //Tell the canvasScreen settings have closed. Will handle showing/hiding the grid/heatmap/etc
                self.canvasScreen.menuClosed();
            }
        });

        Table topBarTable = new Table().pad(12);
        topBarTable.add(new TTFLabel(this.user == null ? "Not Logged In" : this.user.getSanitizedUsername(), 16).wrap(true)).growX().left();
        topBarTable.add(closeButton).size(80,80).expandX().right();

        table.add(new Stack(new SolidContainer(shadeColor), topBarTable)).growX().row();

        Table contentTable = new Table();
        contentTable.add(tableMisc).padTop(24).growX().row();
//        contentTable.add(tcVibration).padTop(48).growX().row();
        contentTable.add(tableHeatmap).padTop(48).growX().row();
        contentTable.add(tableVirginmap).padTop(48).growX().row();
        contentTable.add(tableTemplate).padTop(48).growX().row();

        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setCancelTouchFocus(false);
        table.add(contentPane).grow().padLeft(24).padRight(24).row();
        table.add(new Container()).padBottom(24).colspan(2).growY().row(); //Adds a cell that fills the remaining height between the last cell and the next one. Used as a spacer to stick our logout to the bottom and to ensure everything displays properly
        if (this.user != null) {
            Table tblLogout = new Table();
            tblLogout.add(logoutIcon).growX().right().size(96, 96);
            tblLogout.add(new TTFLabel("Logout", 32)).right().row();
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

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height);
        if (w != width || h != height) {
            MenuScreen ms = new MenuScreen(canvasScreen, user);
            ms.w = width;
            ms.h = height;
            PxlsGame.i.setScreen(ms);
        }
    }
}
