package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

public class VirginmapState {
    public static class ConfigKeys {
        public static String opacity = "virginmapOpacity";
        public static String[] KEYS = new String[] {opacity};
    }

    public static Map<String, Object> DefaultValuesMap;
    static {
        DefaultValuesMap = new HashMap<String, Object>();
        DefaultValuesMap.put(ConfigKeys.opacity, 1f);
    }

    public float opacity;
    public VirginmapState() {
        this((Float) DefaultValuesMap.get(ConfigKeys.opacity));
    }
    public VirginmapState(float opacity) {
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
