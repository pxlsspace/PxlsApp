package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;


import space.pxls.Pxls;

public class UserCountOverlay extends Stack {
    private Label lblOnlineCount;
    private Boolean receivedCount = false;

    public UserCountOverlay(int count) {
        this();
        setCount(count);
    }
    public UserCountOverlay() {
        setVisible(false);

        lblOnlineCount = new Label("", Pxls.skin);
        lblOnlineCount.setFontScale(0.3f);
        Table onlineCount = new Table();
        onlineCount.pad(8);
        onlineCount.add(new Image(Pxls.skin.getDrawable("user"))).growX().right().size(32, 32);
        onlineCount.add(lblOnlineCount).right().row();

        Container temp = new Container();
        temp.setBackground(new NinePatchDrawable(Pxls.skin.getPatch("rounded.topLeft")));
        add(temp);
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
        if (!Pxls.prefsHelper.getHideUserCount() && !isVisible()) setVisible(true);
        receivedCount = true;

        this.lblOnlineCount.setText(String.valueOf(count));
    }
}
