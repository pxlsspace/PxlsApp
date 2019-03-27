package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.Locale;

import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.renderers.Canvas;
import space.pxls.ui.Components.TTFLabel;

public class CooldownOverlay {
    private static CooldownOverlay _instance;
    private TTFLabel lblCooldown;
    private long cooldownExpiry;
    private boolean[] vibeState = new boolean[] {true, true, true, true};
    private boolean alertedZero = false;

    public CooldownOverlay() {
        lblCooldown = new TTFLabel("00:00");
        lblCooldown.setAlignment(Align.center);
    }

    public static CooldownOverlay getInstance() {
        if (_instance == null) _instance = new CooldownOverlay();
        return _instance;
    }

    public void updateCooldown(float cooldown) {
        cooldownExpiry = System.currentTimeMillis() + (long) (cooldown * 1000);
        if (cooldown > 0) {
            vibeState[0] = vibeState[1] = vibeState[2] = false;
            updateCooldown();
            alertedZero = false;
        } else {
            vibeState[0] = vibeState[1] = vibeState[2] = true;
            alertedZero = true;
        }
    }

    private void updateCooldown() {
        long now = System.currentTimeMillis();
        float timeLeft = (cooldownExpiry - now) / 1000f;
        final boolean hasTimeLeft = timeLeft > 0;

        int stateI = ((int)Math.floor(timeLeft)) + 1;
        if (Pxls.prefsHelper.getShouldVibrate() && timeLeft >= -1 && timeLeft <= 2 && PxlsGame.i.vibrationHelper != null) {
            if (!vibeState[stateI]) {
                if (Pxls.prefsHelper.getShouldPrevibe()) {
                    PxlsGame.i.vibrationHelper.vibrate(stateI > 0 ? 50 : 500);
                } else {
                    if (stateI == 0) {
                        PxlsGame.i.vibrationHelper.vibrate(500);
                    }
                }
                vibeState[stateI] = true; //still flag vibestate as true even if we didn't previbe so that we don't call the code 100 times
            }
        }

        timeLeft++; // better human-readability

        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        this.lblCooldown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        this.lblCooldown.setVisible(hasTimeLeft);
        this.lblCooldown.getParent().setVisible(hasTimeLeft);
        if (alertedZero) return;
        if (!hasTimeLeft && PxlsGame.i.getScreen() instanceof CanvasScreen) {
            alertedZero = true;
            ((CanvasScreen) PxlsGame.i.getScreen()).updateCooldownActors();
        }
    }

    public Label getCooldownLabel() {
        return lblCooldown;
    }

    public long getCooldownExpiry() {
        return cooldownExpiry;
    }

    public void simulateAct() {
        updateCooldown();
    }
}
