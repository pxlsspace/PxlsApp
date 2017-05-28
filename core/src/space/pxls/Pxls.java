package space.pxls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;

public class Pxls {
    public static void init() {
        boolean debug = false;
        if (debug) {
            domain = "http://192.168.0.23.nip.io:4567";
            wsPath = "ws://192.168.0.23.nip.io:4567/ws";
        } else {
            domain = "https://pxls.space";
            wsPath = "wss://pxls.space/ws";
        }
    }
    public static String domain;
    public static String wsPath;
    public static final Preferences prefs = Gdx.app.getPreferences("pxls");
    public static final GDXDialogs dialogs = GDXDialogsSystem.install();
    public static final Gson gson = new Gson();
    public static Skin skin;
    public static SpriteBatch batch;

    public static String getAuthToken() {
        return prefs.getString("token", null);
    }

    public static void setAuthToken(String authToken) {
        prefs.putString("token", authToken);
        prefs.flush();
    }
}
