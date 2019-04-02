package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import space.pxls.Pxls;
import space.pxls.Skin;
import space.pxls.ui.Components.SolidContainer;
import space.pxls.ui.Overlays.CooldownOverlay;

public class PixelBar extends Stack {
    enum PopState {
        UP,
        DOWN,
        TOGGLE
    }

    private final HorizontalGroup pixelRow;
    private final Container<Label> cooldownContainer;
    private final Label cooldownLabel;
    private List<String> palette;
    private int currentColor = -1;
    private boolean[] isUp;

    public PixelBar(final List<String> palette) {
        super();
        this.palette = palette;
        isUp = new boolean[palette.size()];

        pixelRow = new HorizontalGroup();
        pixelRow.pad(8).wrap(true).space(4).wrapSpace(4).setTouchable(Touchable.enabled);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Pxls.prefsHelper.getKeepColorSelected()) {
                    Actor a = pixelRow.hit(x, y, false);
                    if (a instanceof PixelImage) {
                        changeColor(((PixelImage) a).idx);
                    }
                } else {
                    changeColor(-1);
                }
                return true;
            }
        });

        pixelRow.clearChildren();

        boolean twoRows = Gdx.graphics.getWidth() < Gdx.graphics.getHeight();
        for (int i = 0; i < palette.size(); i++) {
            String s = palette.get(i);
            isUp[i] = false;

            Color c = Color.valueOf(s);
            final PixelImage img = new PixelImage(Pxls.skin, "palette");
            img.setColor(c);

            final int finalI = i;
            img.idx = i;
            img.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    changeColor(finalI);
                    event.stop();
                    return true;
                }
            });

            pixelRow.addActor(img);
        }

        if (currentColor >= 0) {
            updateSelected();
        }

        add(pixelRow);

        cooldownLabel = CooldownOverlay.getInstance().getCooldownLabel();
        cooldownContainer = new Container<Label>(cooldownLabel);
        cooldownContainer.fillX().align(Align.center).background(new TextureRegionDrawable(new TextureRegion(new Texture(SolidContainer.getFilled(new Color(1f, 1f, 1f, .9f))))));
        add(cooldownContainer);

        cooldownContainer.setTouchable(Touchable.enabled);
        cooldownContainer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    public Container<Label> getCooldownContainer() {
        return cooldownContainer;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void changeColor(int newColor) {
        if (newColor == currentColor) {
            newColor = -1;
        }
        if (currentColor >= 0) {
            pop(currentColor, PopState.DOWN);
        }

        int lastColor = currentColor;
        currentColor = newColor;

        if (currentColor >= 0 && lastColor != newColor) {
            pop(currentColor, PopState.UP);
        }
    }

    private void pop(int i) {
        pop(i, PopState.TOGGLE);
    }

    private void pop(int i, PopState forceState) {
        if (forceState == null) forceState = PopState.TOGGLE;
        if (i < 0 || i > isUp.length-1) return;
        switch(forceState) {
            case UP:
                if (isUp[i]) return;
                popUp(i);
                break;
            case DOWN:
                if (!isUp[i]) return;
                popDown(i);
                break;
            case TOGGLE:
                if (isUp[i]) {
                    popDown(i);
                } else {
                    popUp(i);
                }
                break;
        }
    }

    private void popUp(final int i) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (isUp[i]) return;
                MoveByAction mba = new MoveByAction();
                mba.setAmountY(8);
                mba.setDuration(0.1f);
                mba.setInterpolation(Interpolation.exp5);
                isUp[i] = true;

                pixelRow.getChildren().get(i).addAction(mba);
            }
        });
    }

    private void popDown(final int i) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (!isUp[i]) return;
                MoveByAction mba = new MoveByAction();
                mba.setAmountY(-8f);
                mba.setDuration(0.1f);
                mba.setInterpolation(Interpolation.exp5);
                isUp[i] = false;

                pixelRow.getChildren().get(i).addAction(mba);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        CooldownOverlay.getInstance().simulateAct();
        boolean stillCooled = ((CooldownOverlay.getInstance().getCooldownExpiry() - System.currentTimeMillis()) / 1000f) > 0;
        cooldownContainer.setVisible(!Pxls.prefsHelper.getKeepColorSelected() && stillCooled);
    }

    public void updateSelected() {
        pop(currentColor, PopState.UP);
    }

    class PixelImage extends Image {
        public int idx = -1;

        public PixelImage(Skin skin, String palette) {
            super(skin, palette);
        }

        @Override
        public float getPrefHeight() {
            return 48;
        }

        @Override
        public float getPrefWidth() {
            return 48;
        }
    }
}
