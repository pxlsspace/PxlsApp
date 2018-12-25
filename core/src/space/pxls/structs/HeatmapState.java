package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

public class HeatmapState {
    public static class ConfigKeys {
        public static String opacity = "heatmapOpacity";
        public static String[] KEYS = new String[] {opacity};
    }

    public static Map<String, Object> DefaultValuesMap;
    static {
        DefaultValuesMap = new HashMap<String, Object>();
        DefaultValuesMap.put(ConfigKeys.opacity, 0.5f);
    }

    public float opacity;
    public HeatmapState() {
        this((Float) DefaultValuesMap.get(ConfigKeys.opacity));
    }
    public HeatmapState(float opacity) {
        this.opacity = opacity;
    }

    public Map<String, Object> deserialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConfigKeys.opacity, this.opacity);
        return map;
    }

    @Override
    public String toString() {
        return String.format("opacity: %s", this.opacity);
    }
}
