package space.pxls.ui.Components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import space.pxls.Pxls;

/**
 * A button with 2 states to represent checked/unchecked, and a label. Pretty simple.
 */
public class PxlsCheckBox extends Table {
    private boolean checked;
    private final Drawable checkedButton;
    private final Drawable uncheckedButton;
    private final Image drawnImage;
    private final Cell buttonCell;
    private final TTFLabel label;

    public PxlsCheckBox(String text) {
        this(text, false);
    }
    public PxlsCheckBox(String text, boolean checked) {
        this.checked = checked;
        this.checkedButton = (Pxls.getSkin().getDrawable("checked"));
        this.uncheckedButton = (Pxls.getSkin().getDrawable("unchecked"));
        this.drawnImage = new Image(checked ? this.checkedButton : this.uncheckedButton);
        this.buttonCell = add(this.drawnImage).left().size(48f, 48f).pad(8f).padRight(16f);
        this.label = new TTFLabel(text);
        add(label).expandX().row();

        final PxlsCheckBox self = this;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                self.checked = !self.checked;
                self.fire(new ChangeListener.ChangeEvent());
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.drawnImage.setDrawable(this.checked ? this.checkedButton : this.uncheckedButton);
        super.draw(batch, parentAlpha);
    }

    public Cell getButtonCell() {
        return buttonCell;
    }

    public TTFLabel getLabel() {
        return label;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean _checked) {
        this.checked = _checked;
    }
}
