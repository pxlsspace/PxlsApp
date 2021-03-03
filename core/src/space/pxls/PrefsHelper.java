package space.pxls;

import com.badlogic.gdx.Preferences;

import java.util.Timer;
import java.util.TimerTask;

import space.pxls.structs.CanvasState;
import space.pxls.structs.HeatmapState;
import space.pxls.structs.PxlsGameState;
import space.pxls.structs.TemplateState;

public class PrefsHelper {
    private final Preferences preferences;
    // Mechanics
    private boolean cachedKeepColorSelected = false;
    private boolean cachedAllowGreaterZoom = false;
    private boolean cachedRememberCanvasState = true;
    private boolean cachedRememberTemplate = false;
    private boolean cachedHideUserCount = false;
    //private boolean cachedShouldVibrate = true;
    //private boolean cachedShouldPrevibe = true;
    //private boolean cachedShouldVibeOnStack = true;
    // Miscellaneous
    private boolean hasSeenMoveModeTutorial = false;
    // Overlays
    private boolean cachedGridEnabled = false;
    private boolean cachedHeatmapEnabled = false;
    private boolean cachedVirginmapEnabled = false;
    // GameState lazy properties
    private PxlsGameState toFlush;

    public PrefsHelper(Preferences preferences) {
        this.preferences = preferences;
        getKeepColorSelected(true);
        getAllowGreaterZoom(true);
        getRememberCanvasState(true);
        getRememberTemplate(true);
        getHideUserCount(true);
        getHeatmapEnabled(true);
        getHasSeenMoveModeTutorial(true);
        getVirginmapEnabled(true);
        //getShouldVibrate(true);
        //getShouldPrevibe(true);
        //getShouldVibeOnStack(true);
        Timer stateFlushTimer = new Timer("PxlsApp-GameStateFlushTimer", true);
        stateFlushTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doGameStateFlush();
            }
        }, 100, 1000);
    }

    /**
     * DESTRUCTIVE - Clears all preferences and flushes.
     *
     * @see Preferences#clear()
     */
    public void clear() {
        preferences.clear();
        preferences.flush();
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

    public void setKeepColorSelected(boolean keepColorSelected) {
        this.cachedKeepColorSelected = keepColorSelected;
        this.preferences.putBoolean("keepColorSelected", keepColorSelected);
        this.preferences.flush();
    }

    public boolean getKeepColorSelected(boolean reload) {
        if (!reload) return this.cachedKeepColorSelected;
        this.cachedKeepColorSelected = this.preferences.getBoolean("keepColorSelected", true);
        return this.cachedKeepColorSelected;
    }

    public boolean getAllowGreaterZoom() {
        return getAllowGreaterZoom(false);
    }

    public void setAllowGreaterZoom(boolean allowGreaterZoom) {
        this.cachedAllowGreaterZoom = allowGreaterZoom;
        this.preferences.putBoolean("allowGreaterZoom", allowGreaterZoom);
        this.preferences.flush();
    }

    public boolean getAllowGreaterZoom(boolean reload) {
        if (!reload) return this.cachedAllowGreaterZoom;
        this.cachedAllowGreaterZoom = this.preferences.getBoolean("allowGreaterZoom", true);
        return this.cachedAllowGreaterZoom;
    }

    public boolean getRememberCanvasState() {
        return getRememberCanvasState(false);
    }

    public void setRememberCanvasState(boolean rememberCanvasState) {
        this.cachedRememberCanvasState = rememberCanvasState;
        this.preferences.putBoolean("rememberCanvasState", rememberCanvasState);
        this.preferences.flush();
        if (!rememberCanvasState) {
            ClearGameState();
        }
    }

    public boolean getRememberCanvasState(boolean reload) {
        if (!reload) return this.cachedRememberCanvasState;
        this.cachedRememberCanvasState = this.preferences.getBoolean("rememberCanvasState", true);
        return this.cachedRememberCanvasState;
    }

    public boolean getHasSeenMoveModeTutorial() {
        return getHasSeenMoveModeTutorial(false);
    }

    public void setHasSeenMoveModeTutorial(boolean b) {
        hasSeenMoveModeTutorial = b;
        this.preferences.putBoolean("hasSeenMoveModeTutorial", b);
        this.preferences.flush();
    }

    public boolean getHasSeenMoveModeTutorial(boolean reload) {
        if (!reload) return hasSeenMoveModeTutorial;
        hasSeenMoveModeTutorial = this.preferences.getBoolean("hasSeenMoveModeTutorial", false);
        return hasSeenMoveModeTutorial;
    }

    public boolean getRememberTemplate() {
        return getRememberTemplate(false);
    }

    public void setRememberTemplate(boolean toSet) {
        this.cachedRememberTemplate = toSet;
        this.preferences.putBoolean("rememberTemplate", toSet);
        this.preferences.flush();
    }

    public boolean getRememberTemplate(boolean reload) {
        if (!reload) return this.cachedRememberTemplate;
        this.cachedRememberTemplate = this.preferences.getBoolean("rememberTemplate", false);
        return this.cachedRememberTemplate;
    }

    public boolean getGridEnabled() {
        return getGridEnabled(false);
    }

    public void setGridEnabled(boolean toSet) {
        this.cachedGridEnabled = toSet;
        this.preferences.putBoolean("gridEnabled", toSet);
        this.preferences.flush();
    }

    public boolean getGridEnabled(boolean reload) {
        if (!reload) return this.cachedGridEnabled;
        this.cachedGridEnabled = this.preferences.getBoolean("gridEnabled", false);
        return this.cachedGridEnabled;
    }

    public boolean getVirginmapEnabled() {
        return getVirginmapEnabled(false);
    }

    public void setVirginmapEnabled(boolean toSet) {
        this.cachedVirginmapEnabled = toSet;
        this.preferences.putBoolean("virginmapEnabled", toSet);
        this.preferences.flush();
    }

    public boolean getVirginmapEnabled(boolean reload) {
        if (!reload) return this.cachedVirginmapEnabled;
        this.cachedVirginmapEnabled = this.preferences.getBoolean("virginmapEnabled", false);
        return this.cachedVirginmapEnabled;
    }

    public boolean getHeatmapEnabled() {
        return getHeatmapEnabled(false);
    }

    public void setHeatmapEnabled(boolean toSet) {
        this.cachedHeatmapEnabled = toSet;
        this.preferences.putBoolean("heatmapEnabled", toSet);
        this.preferences.flush();
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

//    public void setShouldVibrate(boolean toSet) {
//        this.cachedShouldVibrate = toSet;
//        this.preferences.putBoolean("shouldVibrate", toSet);
//        this.preferences.flush();
//    }
//    public boolean getShouldVibrate(boolean reload) {
//        if (!reload) return this.cachedShouldVibrate;
//        this.cachedShouldVibrate = this.preferences.getBoolean("shouldVibrate", true);
//        return this.cachedShouldVibrate;
//    }
//    public boolean getShouldVibrate() {
//        return getShouldVibrate(false);
//    }
//
//    public void setShouldPrevibe(boolean toSet) {
//        this.cachedShouldPrevibe = toSet;
//        this.preferences.putBoolean("shouldPrevibe", toSet);
//        this.preferences.flush();
//    }
//    public boolean getShouldPrevibe(boolean reload) {
//        if (!reload) return this.cachedShouldPrevibe;
//        this.cachedShouldPrevibe = this.preferences.getBoolean("shouldPrevibe", true);
//        return this.cachedShouldPrevibe;
//    }
//    public boolean getShouldPrevibe() {
//        return getShouldPrevibe(false);
//    }
//
//    public void setShouldVibeOnStack(boolean toSet) {
//        this.cachedShouldVibeOnStack = toSet;
//        this.preferences.putBoolean("shouldVibeOnStack", toSet);
//        this.preferences.flush();
//    }
//    public boolean getShouldVibeOnStack(boolean reload) {
//        if (!reload) return this.cachedShouldVibeOnStack;
//        this.cachedShouldVibeOnStack = this.preferences.getBoolean("shouldVibeOnStack", true);
//        return this.cachedShouldVibeOnStack;
//    }
//    public boolean getShouldVibeOnStack() {
//        return getShouldVibeOnStack(false);
//    }

    public PxlsGameState GetSavedGameState() {
        PxlsGameState toRet = new PxlsGameState();
        toRet.serialize(preferences.get());
        return toRet;
    }

    public void SaveGameState(PxlsGameState gameState) {
        SaveGameState(gameState, false);
    }

    public void SaveGameState(PxlsGameState gameState, boolean immediate) {
        this.toFlush = gameState;
        if (immediate) doGameStateFlush();
    }

    private void doGameStateFlush() {
        if (this.toFlush != null) {
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
            if (this.toFlush.virginmapState != null) {
                this.preferences.put(this.toFlush.virginmapState.deserialize());
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
