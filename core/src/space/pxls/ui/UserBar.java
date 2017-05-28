package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import space.pxls.Pxls;

public class UserBar extends Table {
    public UserBar(String username) {
        pad(8);

        Label lbl = new Label("Logged in as " + username, Pxls.skin);
        lbl.setFontScale(0.25f);
        add(lbl).expandX().left();
        Label lbl2 = new Label("Log out", Pxls.skin);
        lbl2.setFontScale(0.25f);
        add(lbl2);

        lbl2.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fire(new LogoutEvent());
                return true;
            }
        });
    }

    public static class LogoutEvent extends Event {}
}
