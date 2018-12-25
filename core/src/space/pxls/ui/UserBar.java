package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

        Button button = new Button(Pxls.skin.getDrawable("menu"));
        add(button).width(48).height(32).expandX().right();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fire(new MenuOpenRequestedEvent(event, x, y));
            }
        });
    }

    public static class LogoutEvent extends Event {}

    public static class MenuOpenRequestedEvent extends Event {
        public MenuOpenRequestedEvent(InputEvent event, float x, float y) {}
    }
}
