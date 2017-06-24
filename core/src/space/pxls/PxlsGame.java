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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.UnsupportedEncodingException;

public class PxlsGame extends Game {
    public static PxlsGame i;
    public CaptchaRunner captchaRunner;
    public LoginRunner loginRunner;
    public URI startupURI;
    @Override
    public void create() {
        Pxls.init();

        i = this;
        setScreen(new LoadScreen());

        Pxls.batch = new SpriteBatch();
        Pxls.skin = new Skin();
    }

    private Map<String, String> parseQuery(String s) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        try {
            String query = s;
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx == -1) {
                    continue;
                }
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                if (!key.isEmpty() && query_pairs.get(key) == null) {
                    query_pairs.put(key, URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException e) {
            
        }
        return query_pairs;
    }

    public void handleView(URI uri) {
        if (!(screen instanceof CanvasScreen)) {
            return;
        }
        CanvasScreen _screen = (CanvasScreen)screen;
        
        String query = uri.getQuery();
        if (query == null) {
            query = "";
        }
        String hash = uri.getFragment();
        if (hash == null) {
            hash = "";
        }
        query = hash + "&" + query; // prioritize # over ?
        Map<String, String> params = parseQuery(query);
        String url = params.get("template");
        if (url == null) {
            _screen.template.load(0, 0, -1, 0.5f, "");
            return; // nothing to do
        }
        String s_x = params.get("ox");
        if (s_x == null) {
            s_x = "0";
        }
        String s_y = params.get("oy");
        if (s_y == null) {
            s_y = "0";
        }
        String s_tw = params.get("tw");
        if (s_tw == null) {
            s_tw = "-1";
        }
        String s_oo = params.get("oo");
        if (s_oo == null) {
            s_oo = "0.5";
        }
        int x = Integer.parseInt(s_x);
        int y = Integer.parseInt(s_y);
        int tw = Integer.parseInt(s_tw);
        float oo = Float.valueOf(s_oo);
        _screen.template.load(x, y, tw, oo, url);
    }

    public void handleAuthenticationCallback(String url) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        try {
            URI uri = new URI(url);
            req.setUrl(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString() + "?"+uri.getQuery()+"&json=1");
        } catch (URISyntaxException e) {
            alert("Authentification failed");
            return;
        }
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
