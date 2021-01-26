package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
