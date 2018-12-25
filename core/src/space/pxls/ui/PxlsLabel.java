package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import space.pxls.Pxls;

public class PxlsLabel extends Label {
    public PxlsLabel(CharSequence text) {
        this(text, Pxls.skin);
    }
    public PxlsLabel(CharSequence text, Skin skin) {
        super(text, skin);
        setFontScale(0.3f);
    }

    public PxlsLabel setFontScaleChain(float scale) {
        setFontScale(scale);
        return this;
    }

    @Override
    public float getMinWidth() {
        return 0f;
    }
}