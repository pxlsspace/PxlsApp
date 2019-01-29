package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import space.pxls.Pxls;

public class PxlsButton extends TextButton {
    public PxlsButton(String text) {
        super(text, Pxls.skin);
        getLabel().setFontScale(0.5f);
    }

    public PxlsButton blue() {
        setStyle(Pxls.skin.get("blue", TextButton.TextButtonStyle.class));
        return this;
    }

    public PxlsButton red() {
        setStyle(Pxls.skin.get("red", TextButton.TextButtonStyle.class));
        return this;
    }

    public PxlsButton setFontScale(float s) {
        getLabel().setFontScale(s);
        return this;
    }
}
