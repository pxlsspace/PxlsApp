package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import space.pxls.Pxls;

public class StackOverlay extends Container<Container<Label>> {
    private int count;
    private int maxCount;
    private final Container<Label> container;
    private final Label countLabel;
    private boolean normal;
    private long cooldownExpiry;
    private boolean isEmpty;

    public StackOverlay(final int count, final int maxCount) {
        super();
        this.count = count; // 5, but adds up to 6 as updateStack is called
        this.maxCount = maxCount + 1; // 6

        countLabel = new Label(this.count + "/" + this.maxCount, Pxls.skin); // 6/6
        countLabel.setFontScale(0.3f);
        container = new Container<Label>(countLabel);
        container.setBackground(new NinePatchDrawable(Pxls.skin.getPatch("rounded.topRight")));
        container.pad(8);
        setActor(container);

        setClip(true);
        normal = true;
    }

    // called on each stack update (gain, consume, etc.)
    public void updateStack(int count, String cause) {
        this.count = count;
        updateStack();
    }

    public void updateStack() {
        this.countLabel.setText(count + "/" + maxCount);
    }

    // called on new cooldown
    public void updateCooldown(float cooldown) {
        cooldownExpiry = System.currentTimeMillis() + (long) (cooldown * 1000);
        if (normal) {
            normal = false;
        }
        updateCooldown();
    }

    // called on each tick
    private void updateCooldown() {
        if (normal) {
            return;
        }
        long now = System.currentTimeMillis();
        float timeLeft = ((cooldownExpiry - now) / 1000f) + 1;

        int total = ((int) (timeLeft / 60) * 60) + (int) (timeLeft % 60);

        if (total > 0) {
            normal = false;
        } else {
            if (!normal && count == 0) {
                count++;
            }
            normal = true;
            updateStack();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateCooldown();
    }

    public void empty() {
        this.countLabel.setText("");
    }
}
