package space.pxls;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    //public VibrationHelper vibrationHelper;
    public ImageHelper imageHelper;
    public boolean isPIP = false;
    public boolean isMultiWindow = false;

    public PxlsGame() {
    }

    public PxlsGame(String versionString) {
        VersionString = versionString;
    }

    public static boolean widthGTHeight() {
        return Gdx.graphics.getWidth() > Gdx.graphics.getHeight();
    }

    @Override
    public void create() {
        Pxls.setPrefsHelper(new PrefsHelper(Gdx.app.getPreferences("pxls")));

        i = this;
        setScreen(new LoadScreen());

        Pxls.setBatch(new SpriteBatch());
        Pxls.setSkin(new Skin());
    }

    private Map<String, String> parseQuery(String s) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        try {
            String[] pairs = s.split("&");
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
            e.printStackTrace();
        }
        return query_pairs;
    }

    public void handleView(URI uri) {
        if (!(screen instanceof CanvasScreen)) return;

        CanvasScreen _screen = (CanvasScreen) screen;
        if (uri == null) return;

        String query = uri.getQuery();
        if (query == null) query = "";

        String hash = uri.getFragment();
        if (hash == null) hash = "";

        query = hash + "&" + query; // prioritize # over ?
        Map<String, String> params = parseQuery(query);
        String url = params.get("template");
        String paramX = params.get("x");
        if (paramX != null && paramX.length() > 0) {
            String paramY = params.get("y");
            if (paramY != null && paramY.length() > 0) {
                String paramScale = params.get("scale");
                int posX;
                int posY;
                try {
                    float scale = 1f;
                    posX = Integer.parseInt(paramX);
                    posY = Integer.parseInt(paramY);
                    try {
                        scale = Float.parseFloat(paramScale);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    _screen.moveTo(posX, posY, scale);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (url == null) {
            //_screen.template.load(0, 0, -1, 0.5f, "");
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
        float oo = Float.parseFloat(s_oo);
        _screen.template.load(x, y, tw, oo, url);
    }

    public void handleAuthenticationCallback(final String url) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setHeader("User-Agent", Pxls.getUserAgent());
        try {
            URI uri = new URI(url);
            req.setUrl(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString() + "?" + uri.getQuery() + "&json=1");
        } catch (URISyntaxException e) {
            System.err.printf("[pa] Auth failed. Attempted to create an invalid URI from the URL %s", url);
//            alert("Authentification failed");
            input("PLEASE SEND THIS TO A DEVELOPER *PRIVATELY*. IT CONTAINS SENSITIVE INFORMATION", getStackTraceAsString(new Error("Auth Failed: Bad Login URI ( " + url + " )", e)), new InputCallback() {
                @Override
                public void cancelled() {
                }

                @Override
                public void input(String response) {
                }
            });
            return;
        }
        req.setUrl(req.getUrl().replace("|", "%7C"));
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                if (res.startsWith("You are doing that too much")) {
                    System.err.println("[pa] Auth failed. Caught 'you are doing that too much'");
                    alert(res);
                    return;
                }

                JsonObject jo = null;
                try {
                    jo = Pxls.getGson().fromJson(res, JsonObject.class);
                } catch (JsonSyntaxException jse) {
                    input("PLEASE SEND THIS TO A DEVELOPER *PRIVATELY*. IT CONTAINS SENSITIVE INFORMATION", getStackTraceAsString(new Error(String.format("Failed to parse the response from url '%s' into valid JSON. Response: '%s'", res, url), jse)), new InputCallback() {
                        @Override
                        public void cancelled() {
                        }

                        @Override
                        public void input(String response) {
                        }
                    });
                }

                if (jo != null) {
                    if (jo.has("error")) {
                        System.err.println("[pa] Auth failed. The `error` object exists on the parsed JSON. Displaying to user");
                        //                    alert(jo.get("message").getAsString());
                        input("PLEASE SEND THIS TO A DEVELOPER *PRIVATELY*. IT CONTAINS SENSITIVE INFORMATION", String.format("Got rejection `%s` for URL `%s`", jo.get("message").getAsString(), url), new InputCallback() {
                            @Override
                            public void cancelled() {
                            }

                            @Override
                            public void input(String response) {
                            }
                        });
                        return;
                    }

                    if (!jo.get("signup").getAsBoolean()) {
                        System.err.println("[pa] Auth success");
                        applyToken(jo.get("token").getAsString());
                    } else {
                        doSignupPrompt(jo.get("token").getAsString());
                    }
                } else {
                    alert("Failed to authenticate (`jo` was null), cannot continue.");
                }
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                System.err.println("[pa] Auth failed, the WebRequest threw an error. See above");
                input("PLEASE SEND THIS TO A DEVELOPER *PRIVATELY*. IT CONTAINS SENSITIVE INFORMATION", getStackTraceAsString(new Error("Auth Failed: WebReq Died", t)), new InputCallback() {
                    @Override
                    public void cancelled() {
                    }

                    @Override
                    public void input(String response) {
                    }
                });
            }

            @Override
            public void cancelled() {
                System.err.println("[pa] Auth WebRequest cancelled");
            }
        });
    }

    private String getStackTraceAsString(Error e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString().replaceAll("\r\n", "\\n");
    }

    public void alert(String message) {
        final GDXButtonDialog gdxButtonDialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("OK");
        gdxButtonDialog.setClickListener(button -> gdxButtonDialog.dismiss());
        gdxButtonDialog.build().show();
    }

    public void alert(String message, final ButtonCallback callback) {
        final GDXButtonDialog gdxButtonDialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("OK");
        gdxButtonDialog.setClickListener(button -> {
            gdxButtonDialog.dismiss();
            Gdx.app.postRunnable(callback::clicked);
        });
        gdxButtonDialog.build().show();
    }

    public void confirm(String message, final ConfirmCallback callback) {
        final GDXButtonDialog gdxButtonDialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        gdxButtonDialog.setTitle("pxls.space");
        gdxButtonDialog.setMessage(message);
        gdxButtonDialog.addButton("Yes");
        gdxButtonDialog.addButton("No");
        gdxButtonDialog.setClickListener(button -> {
            gdxButtonDialog.dismiss();
            Gdx.app.postRunnable(() -> {
                callback.done(button == 0); //`button` is the Nth button clicked based on `addButton` calls, zero-based. So, if button == 0, then the "yes" button was clicked. return true in that case since this is a confirmation dialog.
            });
        });
        gdxButtonDialog.build().show();
    }

    public void input(String prompt, Object defaultResponse, final InputCallback callback) {
        GDXTextPrompt gdxTextPrompt = Pxls.getDialogs().newDialog(GDXTextPrompt.class);
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
        GDXTextPrompt gdxTextPrompt = Pxls.getDialogs().newDialog(GDXTextPrompt.class);
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
                req.setUrl(Pxls.getDomain() + "/signup");
                req.setHeader("User-Agent", Pxls.getUserAgent());
                try {
                    req.setContent("username=" + URLEncoder.encode(text, "utf-8") + "&token=" + URLEncoder.encode(signupToken, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("uho");
                }
                Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        String res = httpResponse.getResultAsString();
                        JsonObject jo = Pxls.getGson().fromJson(res, JsonObject.class);

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
        final GDXButtonDialog dialog = Pxls.getDialogs().newDialog(GDXButtonDialog.class);
        dialog.setTitle("pxls.space");
        dialog.setMessage(error);
        dialog.addButton("Cancel");
        dialog.addButton("Retry");
        dialog.setClickListener(button -> {
            if (button == 1) {
                doSignupPrompt(signupToken);
            }
            dialog.dismiss();
        });
        dialog.build().show();
    }

    private void applyToken(String token) {
        Pxls.getPrefsHelper().setToken(token);
        if (screen instanceof CanvasScreen) {
            ((CanvasScreen) screen).reconnect();
        }
    }

    public void logOut() {
        applyToken("");
    }

    public Map<String, String> parseTemplateURL(String URL) {
        try {
            URI uri = URI.create(URL);
            if (uri.getFragment().length() > 0) {
                String[] split = uri.getFragment().split("&");
                Map<String, String> uriArgs = new HashMap<>();
                for (String arg : split) {
                    String[] argSplit = arg.split("=");
                    uriArgs.put(argSplit[0], argSplit[1]);
                }
                if (uriArgs.containsKey("template") && uriArgs.containsKey("tw") && uriArgs.containsKey("ox") && uriArgs.containsKey("oy")) {
                    Map<String, String> toRet = new HashMap<>(uriArgs);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
}
