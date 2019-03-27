package space.pxls.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import space.pxls.Pxls;
import space.pxls.ui.Components.TTFLabel;

/**
 * A button with 2 states to represent checked/unchecked, and a label. Pretty simple.
 */
public class PxlsCheckBox extends Table {
    private boolean _checked = false;
    private Drawable _checkedButton = null;
    private Drawable _uncheckedButton = null;
    private Image _drawnImage = null;
    private Cell buttonCell = null;
    private TTFLabel label;

    public PxlsCheckBox(String text) {
        this(text, false);
    }
    public PxlsCheckBox(String text, boolean checked) {
        this._checked = checked;
        this._checkedButton = (Pxls.skin.getDrawable("checked"));
        this._uncheckedButton = (Pxls.skin.getDrawable("unchecked"));
        this._drawnImage = new Image(checked ? this._checkedButton : this._uncheckedButton);
        this.buttonCell = add(this._drawnImage).left().size(32, 32).padRight(16);
        this.label = new TTFLabel(text);
        add(label).expandX().row();

        final PxlsCheckBox self = this;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                self._checked = !self._checked;
                self.fire(new ChangeListener.ChangeEvent());
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this._drawnImage.setDrawable(this._checked ? this._checkedButton : this._uncheckedButton);
        super.draw(batch, parentAlpha);
    }

    public Cell getButtonCell() {
        return buttonCell;
    }

    public TTFLabel getLabel() {
        return label;
    }

    public boolean isChecked() {
        return _checked;
    }

    public void setChecked(boolean _checked) {
        this._checked = _checked;
    }
}
