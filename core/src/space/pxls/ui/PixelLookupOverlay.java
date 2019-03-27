package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.kevinsawicki.timeago.TimeAgo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import space.pxls.ui.Components.TTFLabel;

public class PixelLookupOverlay extends Table {
    private int x;
    private int y;
    private String username;
    private long time;
    private int pixels;
    private int pixelsAlltime;

    private TTFLabel close;
    private TTFLabel report;


    public PixelLookupOverlay(final int x, final int y, String username, long time, int pixels, int pixelsAlltime, final int id, boolean loggedIn) {
        super(Pxls.skin);
        this.x = x;
        this.y = y;
        this.username = username;
        this.time = time;
        this.pixels = pixels;
        this.pixelsAlltime = pixelsAlltime;

        close = new TTFLabel("Close");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getParent().removeActor(PixelLookupOverlay.this);
            }
        });

        pad(6);
        add(new TTFLabel("Coords:").fontScale(0.85f)).expandX().left();
        add(new TTFLabel("(" + x + ", " + y + ")").fontScale(0.75f).wrap(true)).fillX().left().row();

        add(new TTFLabel("Placed by:").fontScale(0.85f)).expandX().left();
        add(new TTFLabel(username).fontScale(0.75f).wrap(true)).fillX().left().row();

        add(new TTFLabel("Placed at:").fontScale(0.85f)).expandX().left();
        add(new TTFLabel(System.currentTimeMillis() - time <= 1000*60 ? "just now" : new TimeAgo().timeAgo(time)).fontScale(0.75f).wrap(true)).fillX().left().row();

        add(new TTFLabel("Pixels by user:").fontScale(0.85f)).expandX().left();
        add(new TTFLabel(Integer.toString(pixels)).fontScale(0.75f).wrap(true)).fillX().left().row();

        add(new TTFLabel("Alltime Pixels:").fontScale(0.85f)).expandX().left();
        add(new TTFLabel(Integer.toString(pixelsAlltime)).fontScale(0.75f).wrap(true)).fillX().left().row();

        add(new SolidContainer()).colspan(2).growX().height(2).row();

        if (loggedIn) {
            report = new TTFLabel("Report");
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
                req.setHeader("User-Agent", Pxls.getUA());
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

    public TTFLabel getClose() {
        return close;
    }

    public TTFLabel getReport() {
        return report;
    }

    public int getPixelsAlltime() {
        return pixelsAlltime;
    }

    public int getPixels() {
        return pixels;
    }

    public long getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public int getReportY() {
        return y;
    }

    public int getReportX() {
        return x;
    }
}
