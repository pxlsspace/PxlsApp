package space.pxls;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.JsonObject;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import space.pxls.ui.CanvasScreen;
import space.pxls.ui.LoadScreen;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.UnsupportedEncodingException;

public class PxlsGame extends Game {
    public static PxlsGame i;
    public CaptchaRunner captchaRunner;
    public LoginRunner loginRunner;

    @Override
    public void create() {
        Pxls.init();

        i = this;
        setScreen(new LoadScreen());

        Pxls.batch = new SpriteBatch();
        Pxls.skin = new Skin();
    }

    public void handleAuthenticationCallback(String url) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(url);
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                if (res.startsWith("You are doing that too much")) {
                    alert(res);
                    return;
                }

                JsonObject jo = Pxls.gson.fromJson(res, JsonObject.class);
                if (jo.has("error")) {
                    alert(jo.get("message").getAsString());
                    return;
                }

                if (!jo.get("signup").getAsBoolean()) {
                    applyToken(jo.get("token").getAsString());
                } else {
                    doSignupPrompt(jo.get("token").getAsString());
                }
            }

            @Override
            public void failed(Throwable t) {
                alert("Failed logging in");
            }

            @Override
            public void cancelled() {

            }
        });
    }

    public void alert(String message) {
        final GDXButtonDialog gdxButtonDialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("OK");
        gdxButtonDialog.setClickListener(new ButtonClickListener() {
            @Override
            public void click(int button) {
                gdxButtonDialog.dismiss();
            }
        });
        gdxButtonDialog.build().show();
    }

    public void alert(String message, final ButtonCallback callback) {
        final GDXButtonDialog gdxButtonDialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("OK");
        gdxButtonDialog.setClickListener(new ButtonClickListener() {
            @Override
            public void click(int button) {
                gdxButtonDialog.dismiss();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.clicked();
                    }
                });
            }
        });
        gdxButtonDialog.build().show();
    }

    private void doSignupPrompt(final String signupToken) {
        GDXTextPrompt gdxTextPrompt = Pxls.dialogs.newDialog(GDXTextPrompt.class);
        gdxTextPrompt.setTitle("pxls.space");
        gdxTextPrompt.setMessage("Please select your username.");
        gdxTextPrompt.setCancelButtonLabel("Cancel");
        gdxTextPrompt.setConfirmButtonLabel("Create account");
        gdxTextPrompt.setTextPromptListener(new TextPromptListener() {
            @Override
            public void cancel() {
            }

            @Override
            public void confirm(String text) {
                if (text.isEmpty() || !text.matches("[a-zA-Z0-9_\\-]+")) {
                    showRetryMessage(signupToken, "Invalid username.");
                    return;
                }

                Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
                req.setUrl(Pxls.domain + "/signup");
                try {
                    req.setContent("username=" + URLEncoder.encode(text, "utf-8") + "&token=" + URLEncoder.encode(signupToken, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("uho");
                }
                Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        String res = httpResponse.getResultAsString();
                        JsonObject jo = Pxls.gson.fromJson(res, JsonObject.class);

                        if (jo.has("error")) {
                            showRetryMessage(signupToken, jo.get("message").getAsString());
                            return;
                        }

                        applyToken(jo.get("token").getAsString());
                    }

                    @Override
                    public void failed(Throwable t) {

                    }

                    @Override
                    public void cancelled() {

                    }
                });
            }
        });
        gdxTextPrompt.build().show();
    }

    private void showRetryMessage(final String signupToken, String error) {
        final GDXButtonDialog dialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
        dialog.setTitle("pxls.space");
        dialog.setMessage(error);
        dialog.addButton("Cancel");
        dialog.addButton("Retry");
        dialog.setClickListener(new ButtonClickListener() {
            @Override
            public void click(int button) {
                if (button == 1) {
                    doSignupPrompt(signupToken);
                }
                dialog.dismiss();
            }
        });
        dialog.build().show();
    }

    private void applyToken(String token) {
        Pxls.setAuthToken(token);
        if (screen instanceof CanvasScreen) {
            ((CanvasScreen) screen).reconnect();
        }
    }

    public void logOut() {
        applyToken("");
    }

    private String getCookieHeader(Net.HttpResponse httpResponse) {
        String cookie = null;
        for (Map.Entry<String, List<String>> stringListEntry : httpResponse.getHeaders().entrySet()) {
            if (stringListEntry.getKey() != null && stringListEntry.getKey().equalsIgnoreCase("Set-Cookie")) {
                cookie = stringListEntry.getValue().get(0);
            }
        }
        return cookie;
    }

    public interface CaptchaRunner {
        void doCaptcha(String token, CaptchaCallback captchaCallback);
    }

    public interface LoginRunner {
        void doLogin(String method, String url);
    }

    public interface CaptchaCallback {
        void done(String token);
    }

    public interface ButtonCallback {
        void clicked();
    }
}
