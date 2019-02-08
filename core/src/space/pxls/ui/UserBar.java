package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class UserBar extends Table {
    private Label label;
    private Cell menuButtonCell;
    public UserBar(String username) {
        pad(8);

        label = new Label("Logged in as " + username, Pxls.skin);
        label.setFontScale(0.25f);
        add(label).expandX().left();

        Button button = new Button(Pxls.skin.getDrawable("menu"));
        menuButtonCell = add(button).width(48).height(32).expandX().right();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fire(new MenuOpenRequestedEvent(event, x, y));
            }
        });

        redraw();
    }

    public static class LogoutEvent extends Event {}

    public static class MenuOpenRequestedEvent extends Event {
        public MenuOpenRequestedEvent(InputEvent event, float x, float y) {}
    }

    public void redraw() {
        boolean widthGTHeight = PxlsGame.widthGTHeight();
        label.setFontScale(widthGTHeight ? 0.1f : 0.25f);
        menuButtonCell.width(widthGTHeight ? 32 : 48).height(widthGTHeight ? 16 : 32);
    }
}
