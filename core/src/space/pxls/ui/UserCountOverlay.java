package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;


import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class UserCountOverlay extends Stack {
    private Label lblOnlineCount;
    private Boolean receivedCount = false;
    private Cell userIconCell;
    private Container bgContainer;
    private Table onlineCount;

    public UserCountOverlay(int count) {
        this();
        setCount(count);
    }
    public UserCountOverlay() {
        setVisible(false);

        lblOnlineCount = new Label("", Pxls.skin);
        lblOnlineCount.setFontScale(0.3f);
        onlineCount = new Table();
        onlineCount.pad(8);
        userIconCell = onlineCount.add(new Image(Pxls.skin.getDrawable("user"))).growX().right().size(32, 32);
        onlineCount.add(lblOnlineCount).right().row();

        bgContainer = new Container();
        bgContainer.setBackground(new NinePatchDrawable(Pxls.skin.getPatch("rounded.topLeft")));
        add(bgContainer);
        add(onlineCount);

        redraw();
    }

    /**
     * Used to determine if we should render. Keeps track of whether or not we've actually received any usercount. We shouldn't setVisible(true) unless we have populated our label with text and that only happens on setCount().
     * @return Boolean whether or not {@link #setCount(int)} has been called or not.
     * @see #setCount(int)
     */
    public boolean hasReceivedCount() {
        return this.receivedCount;
    }

    public void setCount(int count) {
        if (!Pxls.prefsHelper.getHideUserCount() && !isVisible()) setVisible(true);
        receivedCount = true;

        this.lblOnlineCount.setText(String.valueOf(count));
    }

    public void redraw() {
        boolean isLandscape = PxlsGame.i.orientationHelper.getSimpleOrientation() == space.pxls.OrientationHelper.SimpleOrientation.LANDSCAPE;
        lblOnlineCount.setFontScale(isLandscape ? 0.2f : 0.3f);
        userIconCell.size(isLandscape ? 16 : 32, isLandscape ? 16 : 32);
        if (isLandscape) {
            bgContainer.setBackground(Pxls.skin.getDrawable("background"));
            onlineCount.pad(0, 8, 0, 8);
        } else {
            bgContainer.setBackground(new NinePatchDrawable(Pxls.skin.getPatch("rounded.topLeft")));
            onlineCount.pad(8);
        }
    }
}
