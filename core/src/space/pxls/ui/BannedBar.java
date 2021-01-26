package space.pxls.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.kevinsawicki.timeago.TimeAgo;
import space.pxls.ui.Components.TTFLabel;

public class BannedBar extends Table {
    public BannedBar(long expiry, String reason) {
        pad(8);
        String timeStr = "in " + new TimeAgo().time(System.currentTimeMillis() - expiry, true).replaceAll(" from now", "");
        String text = "You are banned from placing pixels. Reason: " + reason + ". Your ban will " + (expiry == 0 ? "never expire" : ("expire " + timeStr));
        Label lbl = new TTFLabel(text);
        lbl.setWrap(true);
        add(lbl).expandX().fillX().fillY();
    }
}
