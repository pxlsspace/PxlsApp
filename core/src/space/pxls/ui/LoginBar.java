package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.JsonObject;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class LoginBar extends Table {
    private final Label.LabelStyle ls;
    private Label title;
    public LoginPopup popup;
    public LoginBar() {
        popup = new LoginPopup();
        BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
        ls = new Label.LabelStyle(font, Color.BLACK);

        title = new Label("Sign in with...", ls);
        title.setFontScale(0.5f);
        title.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popup.toggle();
            }
        });
        add(title).colspan(5).row();

        redraw();
    }

    public void redraw() {
        title.setFontScale(PxlsGame.widthGTHeight() ? 0.25f : 0.5f);
        popup.redraw();
    }
}
