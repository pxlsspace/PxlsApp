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

import space.pxls.OrientationHelper;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.ui.Components.TTFLabel;

public class LoginBar extends Table {
//    private final Label.LabelStyle ls;
    private TTFLabel title;
    public LoginPopup popup;
    public LoginBar() {
        popup = new LoginPopup();

        title = new TTFLabel("Sign in with...");
        title.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popup.toggle();
            }
        });
        add(title).colspan(5).row();
    }
}
