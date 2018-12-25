package space.pxls.ui;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import com.badlogic.gdx.Gdx;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;

import com.badlogic.gdx.utils.Align;
import com.github.kevinsawicki.timeago.TimeAgo;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import java.util.Date;

public class PixelLookupOverlay extends Table {
    private int x;
    private int y;
    private String username;
    private long time;
    private int pixels;
    private int pixelsAlltime;

    public PixelLookupOverlay(final int x, final int y, String username, long time, int pixels, int pixelsAlltime, final int id, boolean loggedIn) {
        super(Pxls.skin);
        this.x = x;
        this.y = y;
        this.username = username;
        this.time = time;
        this.pixels = pixels;
        this.pixelsAlltime = pixelsAlltime;

        Label coordsLabel = new Label("Coords:", Pxls.skin);
        coordsLabel.setFontScale(0.3f);
        Label coords = new PxlsLabel("(" + x + ", " + y + ")");

        Label userLabel = new Label("Placed by:", Pxls.skin);
        userLabel.setFontScale(0.3f);
        Label user = new PxlsLabel(username);

        Label timeLabel = new Label("Placed at:", Pxls.skin);
        timeLabel.setFontScale(0.3f);
        long current = System.currentTimeMillis();
        Label tme = new PxlsLabel(current - time <= 1000*60 ? "just now" : new TimeAgo().timeAgo(time));

        Label pixelsLabel = new Label("Pixels by user:", Pxls.skin);
        pixelsLabel.setFontScale(0.3f);
        Label pxls = new PxlsLabel(Integer.toString(pixels));

        Label pixelsAlltimeLabel = new Label("Alltime Pixels:", Pxls.skin);
        pixelsAlltimeLabel.setFontScale(0.3f);
        Label pxlsAlltime = new PxlsLabel(Integer.toString(pixelsAlltime));

        Label close = new PxlsLabel("Close").setFontScaleChain(0.2f);
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

        add(pixelsAlltimeLabel).expandX().left();
        add(pxlsAlltime).expandX().right().row();

        if (loggedIn) {
            Label report = new Label("Report", Pxls.skin);
            report.setFontScale(0.2f);
            report.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float _x, float _y) {
                    super.clicked(event, _x, _y);
                    report(x, y, id);
                }
            });
            add(report).expandX().left();
            add(close).expandX().right().row();
        } else {
            add(close).colspan(2).expandX().right().row();
        }
    }
    private void report(final int x, final int y, final int id) {
        GDXTextPrompt gdxTextPrompt = Pxls.dialogs.newDialog(GDXTextPrompt.class);
        gdxTextPrompt.setTitle("pxls.space");
        gdxTextPrompt.setMessage("Report message:");
        gdxTextPrompt.setMaxLength(500);
        gdxTextPrompt.setCancelButtonLabel("Cancel");
        gdxTextPrompt.setConfirmButtonLabel("Report");
        gdxTextPrompt.setTextPromptListener(new TextPromptListener() {
            @Override
            public void cancel() {
            }

            @Override
            public void confirm(String text) {
                text = text.trim();
                if (text.isEmpty()) {
                    PxlsGame.i.alert("A report can't be empty");
                    return;
                }
                Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
                req.setUrl(Pxls.domain + "/report");
                try {
                    req.setContent("id=" + Integer.toString(id) + "&x=" + Integer.toString(x) + "&y=" + Integer.toString(y) + "&message=" + URLEncoder.encode(text, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("uho");
                }
                req.setHeader("Cookie", "pxls-token=" + Pxls.prefsHelper.getToken());
                Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                            PxlsGame.i.alert("Report sent!");
                        } else {
                            PxlsGame.i.alert("Failed to send report!");
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        PxlsGame.i.alert("Failed to send report!");
                    }

                    @Override
                    public void cancelled() {
                    }
                });
            }
        });
        gdxTextPrompt.build().show();
    }
}
