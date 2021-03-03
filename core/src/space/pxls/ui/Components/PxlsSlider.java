package space.pxls.ui.Components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import space.pxls.Pxls;

public class PxlsSlider extends Table {
    private Slider slider;
    private TTFLabel label;
    private String prepend = "";
    private String append = "";

    public PxlsSlider() {
        slider = new Slider(0f, 1f, 0.1f, false, Pxls.getSkin());
        label = new TTFLabel("0%");

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateLabel();
                fire(new ChangeEvent());
            }
        });

        add(label).center().row();
        add(slider).growX().fillY().pad(16f);
    }

    public PxlsSlider setPrepend(String prepend) {
        this.prepend = prepend;
        updateLabel();
        return this;
    }

    public PxlsSlider setAppend(String append) {
        this.append = append;
        updateLabel();
        return this;
    }

    public TTFLabel getLabel() {
        return label;
    }

    public Slider getSlider() {
        return slider;
    }

    public PxlsSlider setValue(float f) {
        slider.setValue(f);
        updateLabel();
        return this;
    }

    public float getValue() {
        return slider.getValue();
    }

    public void updateLabel() {
        label.setText(String.format("%s%s%%%s", prepend, (int)(slider.getValue() * 100), append)); //{prepend}0%{append}
    }

}
