package space.pxls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import space.pxls.structs.PxlsGameState;

public class Pxls {
    private enum DevEnvironments {
        PRODUCTION("https://pxls.space", "wss://pxls.space/ws"),
        SKE("http://192.168.0.23.nip.io:4567", "ws://192.168.0.23.nip.io:4567/ws"),
        SORUNOME("http://192.168.1.13:4567", "ws://192.168.1.13:4567/ws"),
        SOCC("http://192.168.86.100:4567", "ws://192.168.86.100:4567/ws");

        private String DOMAIN, WS_PATH;

        DevEnvironments(String DOMAIN, String WS_PATH) {
            this.DOMAIN = DOMAIN;
            this.WS_PATH = WS_PATH;
        }
    }

    public static void init() {
        domain = DevEnvironments.SOCC.DOMAIN;
        wsPath = DevEnvironments.SOCC.WS_PATH;
    }
    public static String domain;
    public static String wsPath;
    private static final Preferences prefs = Gdx.app.getPreferences("pxls");
    public static PrefsHelper prefsHelper;
    public static final GDXDialogs dialogs = GDXDialogsSystem.install();
    public static final Gson gson = new Gson();
    public static Skin skin;
    public static SpriteBatch batch;
    public static PxlsGameState gameState;
    public static String moveModeTutorial = "Pan/zoom/etc as you normally would.\n\nIf you double tap a pixel, the top left corner of the template will move to where you tapped.\n\nThe nudge buttons (arrows) will move the template 1 pixel in the specified direction.";
    public static String getUA() {
        String _v = "0.0.0";
        if (PxlsGame.i != null) {
            _v = PxlsGame.i.VersionString;
        }
        return String.format("PxlsAndroid/%s (Android %s) ", _v, Gdx.app.getVersion());
    }
}
