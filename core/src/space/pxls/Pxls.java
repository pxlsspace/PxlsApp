package space.pxls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import space.pxls.structs.PxlsGameState;

public class Pxls {
    private static final String domain = "https://pxls.space";
    private static final String wsPath = "wss://pxls.space/ws";
    private static final Preferences prefs = Gdx.app.getPreferences("pxls");
    private static final GDXDialogs dialogs = GDXDialogsSystem.install();
    private static final Gson gson = new Gson();
    private static PrefsHelper prefsHelper;
    private static Skin skin;
    private static SpriteBatch batch;
    private static PxlsGameState gameState;

    public static String getDomain() {
        return domain;
    }

    public static String getWsPath() {
        return wsPath;
    }

    public static Preferences getPrefs() {
        return prefs;
    }

    public static PrefsHelper getPrefsHelper() {
        return prefsHelper;
    }

    public static void setPrefsHelper(PrefsHelper prefsHelper) {
        Pxls.prefsHelper = prefsHelper;
    }

    public static GDXDialogs getDialogs() {
        return dialogs;
    }

    public static Gson getGson() {
        return gson;
    }

    public static Skin getSkin() {
        return skin;
    }

    public static void setSkin(Skin skin) {
        Pxls.skin = skin;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static void setBatch(SpriteBatch batch) {
        Pxls.batch = batch;
    }

    public static PxlsGameState getGameState() {
        return gameState;
    }

    public static void setGameState(PxlsGameState gameState) {
        Pxls.gameState = gameState;
    }

    public static String getMoveModeTutorial() {
        return "Pan/zome/etc as you normally would.\n\n" +
                "If you double tap a pixel, the top left corner of the template will move to where you tapped.\n\n" +
                "The nudge buttons (arrows) will move the template 1 pixel in the specified direction.";
    }

    public static String getUserAgent() {
        String _v = "0.0.0";
        if (PxlsGame.i != null) {
            _v = PxlsGame.i.VersionString;
        }
        return String.format("PxlsAndroid/%s (Android %s) ", _v, Gdx.app.getVersion());
    }
}
