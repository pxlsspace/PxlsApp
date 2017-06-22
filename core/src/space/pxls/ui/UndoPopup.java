package space.pxls.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import space.pxls.Pxls;

public class UndoPopup extends Container<Container<Label>> {
    private long popDownTime;
    private boolean up;
    private final Container<Label> container;

    public UndoPopup() {

        Label label = new Label("Undo", Pxls.skin);
        label.setFontScale(0.4f);
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
        setActor(container);

        setClip(true);
        //fire(new UndoEvent());
    }

    private float getYPos () {
        return getPadLeft() / 2.0f;
    }

    public void popUp(float time) {
        if (up) return;
        up = true;

        popDownTime = (System.currentTimeMillis() + (long) (time * 1000));

        MoveToAction mta = new MoveToAction();
        mta.setPosition(getYPos(), 0);
        mta.setDuration(0.2f);
        mta.setInterpolation(Interpolation.exp5Out);

        container.addAction(mta);
    }

    private void popDown() {
        if (!up) return;
        up = false;

        MoveToAction mta = new MoveToAction();
        mta.setPosition(getYPos(), -container.getHeight());
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
}
