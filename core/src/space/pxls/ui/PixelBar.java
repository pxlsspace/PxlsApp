package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import space.pxls.Pxls;

import java.util.List;
import java.util.Locale;

public class PixelBar extends Stack {
    enum PopState {
        UP,
        DOWN,
        TOGGLE
    }

    private final Table pixelListTable;
    private final Container<Label> cooldownContainer;
    private final Label cooldownLabel;
    private List<String> palette;
    private int currentColor = -1;
    private long cooldownExpiry;
    public boolean havePopped = false;
    private boolean[] isUp;

    public PixelBar(final List<String> palette) {
        super();
        this.palette = palette;
        isUp = new boolean[palette.size()];

        pixelListTable = new Table();
        pixelListTable.pad(8);
        pixelListTable.setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                changeColor(-1);
                return true;
            }
        });

        redraw();

        add(pixelListTable);

        cooldownLabel = new Label("00:00", Pxls.skin);
        cooldownLabel.setAlignment(Align.center);
        cooldownLabel.setFontScale(Gdx.graphics.getWidth() < Gdx.graphics.getHeight() ? 0.5f : 0.25f);
        cooldownContainer = new Container<Label>(cooldownLabel);
        cooldownContainer.fillX().align(Align.center).background(Pxls.skin.getDrawable("background"));
        add(cooldownContainer);

        cooldownContainer.setTouchable(Touchable.enabled);
        cooldownContainer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    public void redraw() {
        if (pixelListTable == null) throw new IllegalStateException("pixelListTable has not been initialized!");

        pixelListTable.clearChildren();

        boolean twoRows = Gdx.graphics.getWidth() < Gdx.graphics.getHeight();
        for (int i = 0; i < palette.size(); i++) {
            String s = palette.get(i);
            isUp[i] = false;

            Color c = Color.valueOf(s);
            final Image img = new Image(Pxls.skin, "palette");
            img.setColor(c);

            final int finalI = i;
            img.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    changeColor(finalI);
                    event.stop();
                    return true;
                }
            });

            final Cell<Image> cell = pixelListTable.add(img).expandX().fillX().height(40);

            int snapPoint = palette.size() / 2 - 1;
            int spacing = 4;

            int count = palette.size();
            if (twoRows) {
                if (i == snapPoint) {
                    cell.row();
                }
                count /= 2;
                spacing = 8;
            }

            int size = (640 - 16 - (count + 1) * spacing) / count;
            cell.width(size).height(size).space(spacing);
        }

        if (cooldownLabel != null) {
            cooldownLabel.setFontScale(Gdx.graphics.getWidth() < Gdx.graphics.getHeight() ? 0.5f : 0.25f);
        }

        if (currentColor >= 0) {
            updateSelected();
        }
    }

    public void updateCooldown(float cooldown) {
        cooldownExpiry = System.currentTimeMillis() + (long) (cooldown * 1000);
        updateCooldown();
    }

    private void updateCooldown() {
        long now = System.currentTimeMillis();
        float timeLeft = (cooldownExpiry - now) / 1000f;

        this.cooldownContainer.setVisible(timeLeft > 0);

        timeLeft++; // better human-readability

        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        this.cooldownLabel.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
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
                MoveByAction mba = new MoveByAction();
                mba.setAmountY(8);
                mba.setDuration(0.1f);
                mba.setInterpolation(Interpolation.exp5);
                isUp[i] = true;

                pixelListTable.getChildren().get(i).addAction(mba);
            }
        });
    }

    private void popDown(final int i) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                MoveByAction mba = new MoveByAction();
                mba.setAmountY(-8f);
                mba.setDuration(0.1f);
                mba.setInterpolation(Interpolation.exp5);
                isUp[i] = false;

                pixelListTable.getChildren().get(i).addAction(mba);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateCooldown();
    }

    public void updateSelected() {
        pop(currentColor, PopState.UP);
    }
}
