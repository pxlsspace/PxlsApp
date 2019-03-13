package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import space.pxls.Account;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.ui.events.MenuOpenRequested;

public class MainUI extends Table {
    private AuthedBar authedBar;

    public MainUI(CanvasScreen canvasScreen) {
        setFillParent(true);
        authedBar = new AuthedBar();
        addTopBar();
        add(spacer()).colspan(3).grow().row();
        add(new TTLabel("hi mom")).colspan(3).center().row();
//        addBottomBar();
//        addPalette();
    }

    public AuthedBar getAuthedBar() {
        return authedBar;
    }

    private void addTopBar() {
        add(authedBar).fillX().expandY().colspan(3).row();
    }

    public Container spacer() {
        return new Container();
    }

    public class AuthedBar extends Table {
        private TTLabel lblUsername;
        private Image imgMenuTrigger, lockImage;

        private Cell menuButtonCell, lockIconCell;

        public AuthedBar() {
            setBackground(Pxls.skin.getDrawable("background"));
            pad(3, 8, 3, 8);

            lblUsername = new TTLabel("Logged in as ...");
            imgMenuTrigger = new Image(Pxls.skin.getDrawable("menu"));
            lockImage = new Image(Pxls.skin.getDrawable("lock"));
            lockImage.setVisible(false);

            add(lblUsername).left().growX();
            lockIconCell = add(lockImage).size(48, 48).padRight(3).right();
            menuButtonCell = add(imgMenuTrigger).size(48, 32).right();

            row();

            imgMenuTrigger.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    fire(new MenuOpenRequested());
                }
            });
        }

        public void setUsername(Account account) {
            setUsername(account != null ? account.getSanitizedName() : null);
        }
        public void setUsername(String username) {
            if (username == null) {
                lblUsername.setText("Not logged in");
            } else {
                lblUsername.setText(String.format("Logged in as %s", username));
            }
        }

        public void setLockImageVisible(boolean v) {
            lockImage.setVisible(v);
        }

        public TTLabel getLblUsername() {
            return lblUsername;
        }

        public Image getImgMenuTrigger() {
            return imgMenuTrigger;
        }

        public Image getLockImage() {
            return lockImage;
        }

        public Cell getMenuButtonCell() {
            return menuButtonCell;
        }

        public Cell getLockIconCell() {
            return lockIconCell;
        }
    }

    private class TTLabel extends Label {
        public TTLabel(String text) {
            super(text, new LabelStyle(Pxls.skin.getFont(), new Color(0f, 0f, 0f, 1f)));
        }
    }
}
