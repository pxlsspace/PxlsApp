package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.kevinsawicki.timeago.TimeAgo;
import space.pxls.Pxls;

import java.util.Date;

public class PixelLookupOverlay extends Table {
    private int x;
    private int y;
    private String username;
    private long time;
    private int pixels;

    public PixelLookupOverlay(int x, int y, String username, long time, int pixels) {
        super(Pxls.skin);
        this.x = x;
        this.y = y;
        this.username = username;
        this.time = time;
        this.pixels = pixels;

        Label coordsLabel = new Label("Coords:", Pxls.skin);
        coordsLabel.setFontScale(0.3f);
        Label coords = new Label("(" + x + ", " + y + ")", Pxls.skin);
        coords.setFontScale(0.3f);

        Label userLabel = new Label("Placed by:", Pxls.skin);
        userLabel.setFontScale(0.3f);
        Label user = new Label(username, Pxls.skin);
        user.setFontScale(0.3f);

        Label timeLabel = new Label("Placed at:", Pxls.skin);
        timeLabel.setFontScale(0.3f);
        Label tme = new Label(new TimeAgo().timeAgo(time), Pxls.skin);
        tme.setFontScale(0.3f);

        Label pixelsLabel = new Label("Pixels by user:", Pxls.skin);
        pixelsLabel.setFontScale(0.3f);
        Label pxls = new Label(Integer.toString(pixels), Pxls.skin);
        pxls.setFontScale(0.3f);

        Label close = new Label("Close", Pxls.skin);
        close.setFontScale(0.2f);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getParent().removeActor(PixelLookupOverlay.this);
            }
        });

        pad(12);
        add(coordsLabel).expandX().left();
        add(coords).expandX().right().row();

        add(userLabel).expandX().left();
        add(user).expandX().right().row();

        add(timeLabel).expandX().left();
        add(tme).expandX().right().row();

        add(pixelsLabel).expandX().left();
        add(pxls).expandX().right().row();

        add(close).colspan(2).expandX().right().row();
    }
}
