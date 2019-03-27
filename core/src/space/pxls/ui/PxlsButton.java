package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import space.pxls.Pxls;
import space.pxls.ui.Components.TTFLabel;

public class PxlsButton extends TextButton {
    public PxlsButton(String text) {
        super(text, Pxls.skin);
        setLabel(new TTFLabel(text));
        getLabel().getStyle().fontColor = new Color(1f, 1f, 1f, 1f);
        getLabel().setAlignment(Align.center);
    }

    public PxlsButton blue() {
        setStyle(Pxls.skin.get("blue", TextButton.TextButtonStyle.class));
        return this;
    }

    public PxlsButton red() {
        setStyle(Pxls.skin.get("red", TextButton.TextButtonStyle.class));
        return this;
    }
}
