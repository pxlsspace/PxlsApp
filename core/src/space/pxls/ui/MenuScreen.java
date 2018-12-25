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
import space.pxls.structs.GridState;

public class MenuScreen extends ScreenAdapter {
    private Stage stage;
    private Account account;
    private CanvasScreen lastScreen;

    public MenuScreen(CanvasScreen lastScreen, Account loggedInAccount) {
        this.stage = new Stage();
        this.lastScreen = lastScreen;
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
        final PxlsCheckBox cbGrid = new PxlsCheckBox("Enable Grid", Pxls.prefsHelper.getGridEnabled());

        //placeholder
        final Slider sliderHeatmapOpacity = new Slider(0f, 1f, 0.1f, false, Pxls.skin);
        final PxlsLabel lblHeatmapOpacityPercent = new PxlsLabel(((int) Math.floor(Pxls.gameState.getSafeHeatmapState().opacity * 100)) + "%");
        sliderHeatmapOpacity.setValue(Pxls.gameState.getSafeHeatmapState().opacity);
        sliderHeatmapOpacity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lblHeatmapOpacityPercent.setText(((int) Math.floor(sliderHeatmapOpacity.getValue() * 100)) + "%");
            }
        });

        //Debug buttons
        final PxlsLabel lblPrintPrefs = new PxlsLabel("Print Prefs").setFontScaleChain(0.6f);
        final PxlsLabel lblPrintGameState = new PxlsLabel("Print GameState").setFontScaleChain(0.6f);
        final PxlsLabel lblClearGameState = new PxlsLabel("Clear GameState").setFontScaleChain(0.6f);

        lblPrintGameState.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                PxlsGame.i.alert(Pxls.gameState.toString());
            }
        });
        lblClearGameState.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                PxlsGame.i.confirm("Clear GameState?", new PxlsGame.ConfirmCallback() {
                    @Override
                    public void clicked(boolean confirmed) {
                        if (confirmed) {
                            Pxls.prefsHelper.ClearGameState();
                            PxlsGame.i.alert("Cleared GameState");
                        }
                    }
                });
            }
        });
        lblPrintPrefs.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                StringBuilder builder = new StringBuilder();
                Map<String, ?> prefs = Pxls.prefsHelper.getAll();
                for (Map.Entry<String, ?> stringEntry : prefs.entrySet()) {
                    builder.append(stringEntry.getKey()).append(": ").append(String.valueOf(stringEntry.getValue())).append("\n");
                }
                PxlsGame.i.alert(builder.toString());
            }
        });

        Table tcMisc = new TitledTableHelper("Misc");
        tcMisc.add(cbKeepSelected).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbGreaterZoom).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcMisc.add(cbRememberState).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tcHeatmap = new TitledTableHelper("Heatmap");
        tcHeatmap.add(cbHeatmap).padTop(6).padLeft(5).colspan(2).expandX().left().row();
        tcHeatmap.add(lblHeatmapOpacityPercent).left();
        tcHeatmap.add(sliderHeatmapOpacity).growX().fillY().center().row();

        Table tcGrid = new TitledTableHelper("Grid");
        tcGrid.add(cbGrid).padTop(6).padLeft(5).colspan(2).expandX().left().row();

        Table tcTemplate = new TitledTableHelper("Template");
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        tcTemplate.add(new PxlsLabel("Not yet").setFontScaleChain(0.4f)).growX().left().row();
        //normal controls (opacity slider, totalWidth, offset, URL) TODO NON-BLOCKING
        //btn: "add from clipboard" TODO NON-BLOCKING
        //checkbox: "move mode" TODO NON-BLOCKING

        Table tcDebug = new TitledTableHelper("Debug");
        tcDebug.add(lblPrintGameState).expandX().padTop(16).left().row();
        tcDebug.add(lblClearGameState).expandX().padTop(16).left().row();
        tcDebug.add(lblPrintPrefs).expandX().padTop(16).left().row();

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

                // Set colors/etc for some overlay stuff
//                Pxls.prefsHelper.setGridColor(gridColorChooser.getValue()); //TODO BLOCKING
//                Pxls.prefsHelper.setHeatmapOpacity(0f); //TODO BLOCKING
                Pxls.gameState.getSafeHeatmapState().opacity = sliderHeatmapOpacity.getValue();

                //Save state if necessary
                if (cbRememberState.isChecked() || cbGrid.isChecked() || cbHeatmap.isChecked()) {
                    Pxls.prefsHelper.SaveGameState(Pxls.gameState);
                }

                //Flag overlay states on main canvas screen
                PxlsGame.i.heatmapState(cbHeatmap.isChecked());
                PxlsGame.i.gridState(cbGrid.isChecked());

                //Return to canvas
                PxlsGame.i.setScreen(self.lastScreen);
            }
        });

        Table topBarTable = new Table().pad(12);
        topBarTable.add(new PxlsLabel(this.account == null ? "Not Logged In" : "Logged in as " + this.account.getName()).setFontScaleChain(0.5f)).left();
        topBarTable.add(closeButton).size(80,80).expandX().right();

        table.add(new Stack(new SolidContainer(shadeColor), topBarTable)).growX().row();

        Table contentTable = new Table();
        //!! cbRememberTemplate is not exposed by design. not fully implemented. TODO NON-BLOCKING
        contentTable.add(tcMisc).padTop(24).growX().row();
        contentTable.add(tcHeatmap).padTop(48).growX().row();
        contentTable.add(tcGrid).padTop(48).growX().row();
        contentTable.add(tcDebug).padTop(48).growX().row();
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
                    PxlsGame.i.setScreen(self.lastScreen);
                    self.lastScreen.logout(false);
                }
            });

            table.add(new Stack(new SolidContainer(shadeColor), tblLogout)).colspan(2).growX().right().row();
        }

        stage.addActor(table);
//        stage.setDebugAll(true);
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
}
