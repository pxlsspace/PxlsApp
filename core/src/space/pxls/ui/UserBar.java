package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class UserBar extends Table {
    private Label label;
    private Cell menuButtonCell, lockIconCell;
    private Image lockImage;
    public UserBar(String username) {
        pad(8);

        lockImage = new Image(Pxls.skin.getDrawable("lock"));

        label = new Label("Logged in as " + username, Pxls.skin);
        label.setFontScale(0.25f);
        add(label).expandX().left();

        Button button = new Button(Pxls.skin.getDrawable("menu"));
        lockIconCell = add(lockImage).width(48).height(48).padRight(3).right();
        menuButtonCell = add(button).width(48).height(32).right();
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
        boolean isLandscape = PxlsGame.i.orientationHelper.getSimpleOrientation() == space.pxls.OrientationHelper.SimpleOrientation.LANDSCAPE;
        label.setFontScale(isLandscape ? 0.1f : 0.25f);
        menuButtonCell.width(isLandscape ? 32 : 48).height(isLandscape ? 16 : 32);
        lockIconCell.width(isLandscape ? 24 : 48).height(isLandscape ? 24 : 48);
        if (isLandscape) {
            pad(1, 4, 1, 4);
        } else {
            pad(8);
        }
    }

    public void setLockVisible(boolean visible) {
        lockImage.setVisible(visible);
    }
}
