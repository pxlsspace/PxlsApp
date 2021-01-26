package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

public final class CanvasState {
    public static class ConfigKeys {
        public static String panX = "canvasPanX";
        public static String panY = "canvasPanY";
        public static String zoom = "canvasZoom";
        public static String[] KEYS = new String[] {panX, panY, zoom};
    }

    public static Map<String, Object> DefaultValuesMap;
    static {
        DefaultValuesMap = new HashMap<String, Object>();
        DefaultValuesMap.put(ConfigKeys.panX, -1);
        DefaultValuesMap.put(ConfigKeys.panY, -1);
        DefaultValuesMap.put(ConfigKeys.zoom, 1f);
    }

    public int panX = (Integer) DefaultValuesMap.get(ConfigKeys.panX);
    public int panY = (Integer) DefaultValuesMap.get(ConfigKeys.panY);
    public float zoom = (Float) DefaultValuesMap.get(ConfigKeys.zoom);
    public boolean locked = false; //not stateful by design

    public CanvasState() {}
    public CanvasState(int panX, int panY, float zoom) {
        this.panX = panX;
        this.panY = panY;
        this.zoom = zoom;
    }

    public static CanvasState from(int x, int y, int zoom) {
        return new CanvasState(x, y, zoom);
    }

    public Map<String, Object> deserialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConfigKeys.panX, this.panX);
        map.put(ConfigKeys.panY, this.panY);
        map.put(ConfigKeys.zoom, this.zoom);
        return map;
    }

    @Override
    public String toString() {
        return String.format("panX: %s, panY: %s, zoom: %s, locked: %s", panX, panY, zoom, locked);
    }
}
