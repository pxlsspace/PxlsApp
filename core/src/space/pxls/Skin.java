package space.pxls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Skin extends com.badlogic.gdx.scenes.scene2d.ui.Skin {
    public Skin() {
        add("pixel", new TextureRegionDrawable(new TextureRegion(new Texture("pixel.png"))), Drawable.class);
        add("palette", new TextureRegionDrawable(new TextureRegion(new Texture("palette.png"))), Drawable.class);
        add("cog", new TextureRegionDrawable(new TextureRegion(new Texture("cog_black.png"))), Drawable.class);
        add("menu", new TextureRegionDrawable(new TextureRegion(new Texture("menu.png"))), Drawable.class);
        add("logout", new TextureRegionDrawable(new TextureRegion(new Texture("logout_black.png"))), Drawable.class);
        add("times", new TextureRegionDrawable(new TextureRegion(new Texture("times.png"))), Drawable.class);
        add("checked", new TextureRegionDrawable(new TextureRegion(new Texture("checked.png"))), Drawable.class);
        add("unchecked", new TextureRegionDrawable(new TextureRegion(new Texture("unchecked.png"))), Drawable.class);

        add("light-patch", new NinePatch(new Texture("light.9.png"), 13, 14, 14, 14), NinePatch.class);
        add("white-patch", new NinePatch(new Texture("white.9.png"), 5, 5, 5, 5), NinePatch.class);

        add("default-horizontal", new Slider.SliderStyle(new TextureRegionDrawable(new TextureRegion(new Texture("slider.png"))), new TextureRegionDrawable(new TextureRegion(new Texture("slider-knob.png")))));

        BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.setUseIntegerPositions(false);
        for (TextureRegion textureRegion : font.getRegions()) {
            textureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        add("default", font);
        add("default", new Label.LabelStyle(font, Color.BLACK));

        TintedDrawable td = new TintedDrawable();
        td.color = new Color(1, 1, 1, 0.7f);
        add("background", newDrawable("pixel", 1, 1, 1, 0.85f), Drawable.class);
    }
}
