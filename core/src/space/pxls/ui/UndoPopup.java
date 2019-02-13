package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import space.pxls.OrientationHelper;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class UndoPopup extends Container<Container<Label>> {
    private long popDownTime;
    private boolean up;
    private final Container<Label> container;
    private Label label;
    private float time = 0f;

    public UndoPopup() {
        label = new Label("Undo", Pxls.skin);
        label.setFontScale(0.4f);
        label.setAlignment(Align.center);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fire(new UndoEvent());
                popDown();
            }
        });

        container = new Container<Label>(label);
        container.setBackground(Pxls.skin.getDrawable("background"));
        container.pad(8);
        container.fillX();

        fillX();
        setActor(container);
        setClip(true);

        redraw();
    }

    private float getYPos () {
        return getPadLeft() / 2.0f;
    }

    public void popUp(float time) {
        if (up) return;
        up = true;
        popDownTime = (System.currentTimeMillis() + (long) (time * 1000));

        _popMoveTo(getYPos(), 0);
    }

    private void popDown() {
        if (!up) return;
        up = false;

        _popMoveTo(getYPos(), -container.getHeight());
    }

    private void _popMoveTo(float x, float y) {
        MoveToAction mta = new MoveToAction();
        mta.setPosition(x, y);
        mta.setDuration(0.2f);
        mta.setInterpolation(Interpolation.exp5In);

        container.addAction(mta);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (System.currentTimeMillis() > popDownTime && up) {
            popDown();
        }

        if (!up && !container.hasActions()) {
            container.setY(-container.getHeight());
        }
    }

    public static class UndoEvent extends Event {}

    public void redraw() {
        if (up) {
            _popMoveTo(getYPos(), 0);
        } else {
            _popMoveTo(getYPos(), -container.getHeight());
        }

        label.setFontScale(PxlsGame.i.orientationHelper.getSimpleOrientation() == OrientationHelper.SimpleOrientation.LANDSCAPE ? 0.2f : 0.4f);
    }
}
