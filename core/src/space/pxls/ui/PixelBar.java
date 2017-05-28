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

public class PixelBar extends Stack {
    private final Table table;
    private final Container<Label> cooldownContainer;
    private final Label cooldownLabel;
    private List<String> palette;
    private int currentColor = -1;
    private long cooldownExpiry;

    public PixelBar(final List<String> palette) {
        super();
        this.palette = palette;

        table = new Table();
        table.pad(8);
        table.setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                changeColor(-1);
                return true;
            }
        });

        boolean twoRows = Gdx.graphics.getWidth() < Gdx.graphics.getHeight();
        for (int i = 0; i < palette.size(); i++) {
            String s = palette.get(i);

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

            final Cell<Image> cell = table.add(img).expandX().fillX().height(40);

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

        add(table);

        cooldownLabel = new Label("00:00", Pxls.skin);
        cooldownLabel.setAlignment(Align.center);
        cooldownLabel.setFontScale(twoRows ? 0.5f : 0.25f);
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

    public void updateCooldown(float cooldown) {
        cooldownExpiry = System.currentTimeMillis() + (long) (cooldown * 1000);
        updateCooldown();
    }

    private void updateCooldown() {
        long now = System.currentTimeMillis();
        float timeLeft = (cooldownExpiry - now) / 1000f;

        this.cooldownContainer.setVisible(timeLeft > 0);

        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        this.cooldownLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void changeColor(int newColor) {
        if (currentColor >= 0 && newColor != currentColor) {
            MoveByAction mba = new MoveByAction();
            mba.setAmountY(-16);
            mba.setDuration(0.1f);
            mba.setInterpolation(Interpolation.exp5);
            table.getChildren().get(currentColor).addAction(mba);
        }

        int lastColor = currentColor;
        currentColor = newColor;

        if (currentColor >= 0 && lastColor != newColor) {
            MoveByAction mba = new MoveByAction();
            mba.setAmountY(16);
            mba.setDuration(0.1f);
            mba.setInterpolation(Interpolation.exp5);
            table.getChildren().get(currentColor).addAction(mba);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateCooldown();
    }
}
