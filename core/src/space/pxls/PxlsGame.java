package space.pxls;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import space.pxls.ui.Screens.CanvasScreen;
import space.pxls.ui.Screens.LoadScreen;

public class PxlsGame extends Game {
    public static PxlsGame i;
    public CaptchaRunner captchaRunner;
    public LoginRunner loginRunner;
    public URI startupURI;
    public String VersionString = "0.0.0";
    public OrientationHelper orientationHelper;
    public VibrationHelper vibrationHelper;
    public ImageHelper imageHelper;
    public boolean isPIP = false;
    public boolean isMultiWindow = false;

    public PxlsGame() {}
    public PxlsGame(String versionString) {
        VersionString = versionString;
    }

    @Override
    public void create() {
        Pxls.init();
        Pxls.prefsHelper = new PrefsHelper(Gdx.app.getPreferences("pxls"));

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
        if (uri == null) return;
        
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
        String paramX = params.get("x");
        if (paramX != null && paramX.length() > 0) {
            String paramY = params.get("y");
            if (paramY != null && paramY.length() > 0) {
                String paramScale = params.get("scale");
                int posX = 0;
                int posY = 0;
                try {
                    float scale = 1f;
                    posX = Integer.parseInt(paramX);
                    posY = Integer.parseInt(paramY);
                    try {
                        scale = Float.parseFloat(paramScale);
                    } catch (Exception e) { /* ignored */ }
                    _screen.moveTo(posX, posY, scale);
                } catch (Exception e) {
                    /*ignored*/
                }
            }
        }
        if (url == null) {
//            _screen.template.load(0, 0, -1, 0.5f, "");
            //TODO: why was this here? we don't want to clear template here... no reason to. people may be clicking a link to jump to a specific spot to fix a grief on their already loaded template.
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
        req.setHeader("User-Agent", Pxls.getUA());
        try {
            URI uri = new URI(url);
            req.setUrl(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString() + "?"+uri.getQuery()+"&json=1");
        } catch (URISyntaxException e) {
            alert("Authentification failed");
            return;
        }
        req.setUrl(req.getUrl().replace("|", "%7C"));
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
                t.printStackTrace();
                System.err.println("Failed to log in");
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

    public void confirm(String message, final ConfirmCallback callback) {
        final GDXButtonDialog gdxButtonDialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("Yes");
        gdxButtonDialog.addButton("No");
        gdxButtonDialog.setClickListener(new ButtonClickListener() {
            @Override
            public void click(final int button) {
                gdxButtonDialog.dismiss();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.done(button == 0); //`button` is the Nth button clicked based on `addButton` calls, zero-based. So, if button == 0, then the "yes" button was clicked. return true in that case since this is a confirmation dialog.
                    }
                });
            }
        });
        gdxButtonDialog.build().show();
    }

    public void input(String prompt, final InputCallback callback) {
        input(prompt, "", callback);
    }
    public void input(String prompt, Object defaultResponse, final InputCallback callback) {
        GDXTextPrompt gdxTextPrompt = Pxls.dialogs.newDialog(GDXTextPrompt.class);
        gdxTextPrompt.setMaxLength(2147483646);
        gdxTextPrompt.setTitle("pxls.space");
        gdxTextPrompt.setMessage(prompt);
        gdxTextPrompt.setCancelButtonLabel("Cancel");
        gdxTextPrompt.setConfirmButtonLabel("OK");
        gdxTextPrompt.setTextPromptListener(new TextPromptListener() {
            @Override
            public void cancel() {
                callback.cancelled();
            }

            @Override
            public void confirm(String text) {
                callback.input(text);
            }
        });
        gdxTextPrompt.setValue(String.valueOf(defaultResponse));
        gdxTextPrompt.build().show();
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
                req.setHeader("User-Agent", Pxls.getUA());
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
        Pxls.prefsHelper.setToken(token);
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

    public interface ConfirmCallback {
        void done(boolean confirmed);
    }

    public interface InputCallback {
        void cancelled();
        void input(String response);
    }

    public Map<String, String> parseTemplateURL(String URL) {
        try {
            URI uri = URI.create(URL);
            //https://pxls.space/#template=https://i.trg0d.com/2mL4534Ka6Z&tw=100&oo=1&ox=10&oy=15&x=0&y=0&scale=30
            if (uri.getFragment().length() > 0) {
                String[] split = uri.getFragment().split("&");
                Map<String, String> uriArgs = new HashMap<String,String>();
                for (String arg : split) {
                    String[] argSplit = arg.split("=");
                    uriArgs.put(argSplit[0], argSplit[1]);
                }
                if (uriArgs.containsKey("template") && uriArgs.containsKey("tw") && uriArgs.containsKey("ox") && uriArgs.containsKey("oy")) {
                    Map<String, String> toRet = new HashMap<String, String>(uriArgs);
                    if (!toRet.containsKey("tw")) {
                        toRet.put("tw", "-1");
                    }
                    if (!toRet.containsKey("oo")) {
                        toRet.put("oo", "0.5");
                    }
                    return toRet;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {/*ignored*/}

        return null;
    }

    public static boolean widthGTHeight() {
        return Gdx.graphics.getWidth() > Gdx.graphics.getHeight();
    }
}
