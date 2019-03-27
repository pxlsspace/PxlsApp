package space.pxls.ui.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SolidContainer extends Container {
    public SolidContainer() {
        this(Color.BLACK);
    }
    public SolidContainer(Color color) {
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(SolidContainer.getFilled(color)))));
    }

    public static Pixmap getFilled(Color color) {
        Pixmap toRet = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        toRet.setColor(color);
        toRet.fill();
        return toRet;
    }
}
