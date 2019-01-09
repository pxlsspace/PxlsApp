package space.pxls;

import com.badlogic.gdx.Preferences;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import space.pxls.structs.CanvasState;
import space.pxls.structs.HeatmapState;
import space.pxls.structs.PxlsGameState;
import space.pxls.structs.TemplateState;

public class PrefsHelper {
    private Preferences preferences;
    private boolean hasFlushedImmediately = false;

    //Mechanics
    private boolean cachedKeepColorSelected = false;
    private boolean cachedAllowGreaterZoom = false;
    private boolean cachedRememberCanvasState = true;
    private boolean cachedRememberTemplate = false;
    private boolean cachedHideUserCount = false;

    //Overlays
    private boolean cachedGridEnabled = false;
    private boolean cachedHeatmapEnabled = false;
    private boolean cachedVirginmapEnabled = false;

    //GameState Lazy Properties
    private PxlsGameState toFlush;
    private Timer stateFlushTimer = new Timer("PxlsApp-GameStateFlushTimer", true);

    public PrefsHelper(Preferences preferences) {
        this.preferences = preferences;
        getKeepColorSelected(true);
        getAllowGreaterZoom(true);
        getRememberCanvasState(true);
        getRememberTemplate(true);
        getHideUserCount(true);
        getHeatmapEnabled(true);
        stateFlushTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doGameStateFlush();
            }
        }, 100, 1000);
    }

    public String getToken() {
        return this.preferences.getString("token", null);
    }
    public void setToken(String token) {
        this.preferences.putString("token", token);
        this.preferences.flush();
    }

    public boolean getKeepColorSelected() {
        return getKeepColorSelected(false);
    }
    public boolean getKeepColorSelected(boolean reload) {
        if (!reload) return this.cachedKeepColorSelected;
        this.cachedKeepColorSelected = this.preferences.getBoolean("keepColorSelected", true);
        return this.cachedKeepColorSelected;
    }
    public void setKeepColorSelected(boolean keepColorSelected) {
        this.cachedKeepColorSelected = keepColorSelected;
        this.preferences.putBoolean("keepColorSelected", keepColorSelected);
        this.preferences.flush();
    }

    public boolean getAllowGreaterZoom() {
        return getAllowGreaterZoom(false);
    }
    public boolean getAllowGreaterZoom(boolean reload) {
        if (!reload) return this.cachedAllowGreaterZoom;
        this.cachedAllowGreaterZoom = this.preferences.getBoolean("allowGreaterZoom", true);
        return this.cachedAllowGreaterZoom;
    }
    public void setAllowGreaterZoom(boolean allowGreaterZoom) {
        this.cachedAllowGreaterZoom = allowGreaterZoom;
        this.preferences.putBoolean("allowGreaterZoom", allowGreaterZoom);
        this.preferences.flush();
    }

    public boolean getRememberCanvasState() {
        return getRememberCanvasState(false);
    }
    public boolean getRememberCanvasState(boolean reload) {
        if (!reload) return this.cachedRememberCanvasState;
        this.cachedRememberCanvasState = this.preferences.getBoolean("rememberCanvasState", true);
        return this.cachedRememberCanvasState;
    }
    public void setRememberCanvasState(boolean rememberCanvasState) {
        this.cachedRememberCanvasState = rememberCanvasState;
        this.preferences.putBoolean("rememberCanvasState", rememberCanvasState);
        this.preferences.flush();
        if (!rememberCanvasState) {
            ClearGameState();
        }
    }

    public void setRememberTemplate(boolean toSet) {
        this.cachedRememberTemplate = toSet;
    }
    public boolean getRememberTemplate() {
        return getRememberTemplate(true);
    }
    public boolean getRememberTemplate(boolean reload) {
        if (!reload) return this.cachedRememberTemplate;
        this.cachedRememberTemplate = this.preferences.getBoolean("rememberTemplate", false);
        return this.cachedRememberTemplate;
    }

    public void setGridEnabled(boolean toSet) {
        this.cachedGridEnabled = toSet;
        this.preferences.putBoolean("gridEnabled", toSet);
        this.preferences.flush();
    }
    public boolean getGridEnabled() {
        return getGridEnabled(false);
    }
    public boolean getGridEnabled(boolean reload) {
        if (!reload) return this.cachedGridEnabled;
        this.cachedGridEnabled = this.preferences.getBoolean("gridEnabled", false);
        return this.cachedGridEnabled;
    }

    public void setHeatmapEnabled(boolean toSet) {
        this.cachedHeatmapEnabled = toSet;
        this.preferences.putBoolean("heatmapEnabled", toSet);
        this.preferences.flush();
    }
    public boolean getHeatmapEnabled() {
        return getHeatmapEnabled(false);
    }
    public boolean getHeatmapEnabled(boolean reload) {
        if (!reload) return this.cachedHeatmapEnabled;
        this.cachedHeatmapEnabled = this.preferences.getBoolean("heatmapEnabled", false);
        return this.cachedHeatmapEnabled;
    }

    public void setHideUerCount(boolean toSet) {
        this.cachedHideUserCount = toSet;
        this.preferences.putBoolean("hideUserCount", toSet);
        this.preferences.flush();
    }
    public boolean getHideUserCount() {
        return getHideUserCount(false);
    }
    public boolean getHideUserCount(boolean reload) {
        if (!reload) return this.cachedHideUserCount;
        this.cachedHideUserCount = this.preferences.getBoolean("hideUserCount", false);
        return this.cachedHideUserCount;
    }

    public PxlsGameState GetSavedGameState() {
        PxlsGameState toRet = new PxlsGameState();
        toRet.serialize(preferences.get());
        return toRet;
    }

    public Map<String, ?> getAll() {
        return this.preferences.get();
    }

    public void SaveGameState(PxlsGameState gameState) {
        this.toFlush = gameState;
    }
    private void doGameStateFlush() {
        if (this.toFlush != null) {
            System.out.println("deserializing " + this.toFlush);
            if (this.cachedRememberCanvasState && this.toFlush.canvasState != null) { //if we're supposed to be remembering the canvas state, and the canvas state has been recorded, add to preferences
                this.preferences.put(this.toFlush.canvasState.deserialize());
            }
            if (this.toFlush.templateState != null) {
                this.preferences.put(this.toFlush.templateState.deserialize());
            }
            if (this.toFlush.heatmapState != null) {
                this.preferences.put(this.toFlush.heatmapState.deserialize());
            }
            if (this.toFlush.gridState != null) {
                this.preferences.put(this.toFlush.gridState.deserialize());
            }
            this.preferences.flush();
            this.toFlush = null;
        }
    }

    public void ClearGameState() {
        for (String key : CanvasState.ConfigKeys.KEYS) {
            this.preferences.remove(key);
        }
        for (String key : TemplateState.ConfigKeys.KEYS) {
            this.preferences.remove(key);
        }
        for (String key : HeatmapState.ConfigKeys.KEYS) {
            this.preferences.remove(key);
        }
        this.preferences.flush();
    }
}
