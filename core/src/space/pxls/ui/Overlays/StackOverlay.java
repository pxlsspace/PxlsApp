package space.pxls.ui.Overlays;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import space.pxls.ui.Components.TTFLabel;

public class StackOverlay extends Container<Container<Label>> {
    private final Container<Label> container;
    private final Label countLabel;
    private int count;
    private final int maxCount;
    private boolean normal;
    private long cooldownExpiry;

    public StackOverlay(final int count, final int maxCount) {
        super();
        this.count = count; // 5, but adds up to 6 as updateStack is called
        this.maxCount = maxCount + 1; // 6

        countLabel = new TTFLabel(this.count + "/" + this.maxCount); // 6/6
        container = new Container<>(countLabel);
        setActor(container);

        setClip(true);
        normal = true;
    }

    // called on each stack update (gain, consume, etc.)
    public void updateStack(int count, String cause) {
        this.count = count;
        updateStack();
//        if (Pxls.getPrefsHelper().getShouldVibrate() && Pxls.getPrefsHelper().getShouldVibeOnStack() && cause.equals("stackGain")) {
//            if (PxlsGame.i.vibrationHelper != null) {
//                PxlsGame.i.vibrationHelper.vibrate(500);
//            }
//        }
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

    public boolean onCooldown() {
        return cooldownExpiry - System.currentTimeMillis() > 0;
    }
}
