package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PxlsLabel extends Label {
    public PxlsLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    @Override
    public float getMinWidth() {
        return 0f;
    }
}