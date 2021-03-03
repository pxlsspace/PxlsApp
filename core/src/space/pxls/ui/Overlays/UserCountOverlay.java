package space.pxls.ui.Overlays;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import space.pxls.Pxls;
import space.pxls.ui.Components.TTFLabel;

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

        lblOnlineCount = new TTFLabel("");
        onlineCount = new Table();
        onlineCount.pad(8);
        userIconCell = onlineCount.add(new Image(Pxls.getSkin().getDrawable("user"))).growX().right().size(32, 32).padRight(8);
        onlineCount.add(lblOnlineCount).right().row();

        bgContainer = new Container();
        bgContainer.setBackground(new NinePatchDrawable(Pxls.getSkin().getPatch("rounded.topLeft")));
        add(bgContainer);
        add(onlineCount);
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
        if (!Pxls.getPrefsHelper().getHideUserCount() && !isVisible()) setVisible(true);
        receivedCount = true;

        this.lblOnlineCount.setText(String.valueOf(count));
    }
}
