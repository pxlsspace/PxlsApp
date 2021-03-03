package space.pxls.ui.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import space.pxls.Pxls;

public class TTFLabel extends Label {
    public boolean noMinWidth = false;

    public TTFLabel(String text) {
        super(text, new Label.LabelStyle(Pxls.getSkin().getFont(), new Color(0f, 0f, 0f, 1f)));
    }

    public TTFLabel(String text, int dp) {
        super(text, new Label.LabelStyle(Pxls.getSkin().getFontForDP(dp), new Color(0f, 0f, 0f, 1f)));
    }

    public TTFLabel setNoMinWidth(boolean b) {
        this.noMinWidth = b;
        return this;
    }

    public TTFLabel fontScale(float f) {
        setFontScale(f);
        return this;
    }

    public TTFLabel wrap(boolean b) {
        setWrap(b);
        return this;
    }

    @Override
    public float getMinWidth() {
        return noMinWidth ? 0 : super.getMinWidth();
    }
}
