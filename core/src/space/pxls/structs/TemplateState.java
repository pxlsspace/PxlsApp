package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

public class TemplateState {
    public static class ConfigKeys {
        public static String URL = "templateURL";
        public static String offsetX = "templateOffsetX";
        public static String offsetY = "templateOffsetY";
        public static String totalWidth = "templateTotalWidth";
        public static String opacity = "templateOpacity";
        public static String[] KEYS = new String[] {URL, offsetX, offsetY, totalWidth, opacity};
    }

    public static Map<String, Object> DefaultValuesMap;
    static {
        DefaultValuesMap = new HashMap<String, Object>();
        DefaultValuesMap.put(ConfigKeys.URL, "");
        DefaultValuesMap.put(ConfigKeys.offsetX, 0);
        DefaultValuesMap.put(ConfigKeys.offsetY, 0);
        DefaultValuesMap.put(ConfigKeys.totalWidth, -1f);
        DefaultValuesMap.put(ConfigKeys.opacity, 0.5f);
    }
    public String URL;
    public int offsetX;
    public int offsetY;
    public float totalWidth;
    public float opacity;

    public TemplateState() {
        this((String) DefaultValuesMap.get(ConfigKeys.URL),(Integer) DefaultValuesMap.get(ConfigKeys.offsetX),(Integer) DefaultValuesMap.get(ConfigKeys.offsetY),(Float) DefaultValuesMap.get(ConfigKeys.totalWidth),(Float) DefaultValuesMap.get(ConfigKeys.opacity));
    }
    public TemplateState(String URL, int offsetX, int offsetY, float totalWidth, float opacity) {
        this.URL = URL;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.totalWidth = totalWidth;
        this.opacity = opacity;
    }

    public Map<String, Object> deserialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConfigKeys.URL, this.URL);
        map.put(ConfigKeys.offsetX, this.offsetX);
        map.put(ConfigKeys.offsetY, this.offsetY);
        map.put(ConfigKeys.totalWidth, this.totalWidth);
        map.put(ConfigKeys.opacity, this.opacity);

        return map;
    }

    @Override
    public String toString() {
        return String.format("offsetX: %s, offsetY: %s, totalWidth: %s, opacity: %s, URL: %s", offsetX, offsetY, totalWidth, opacity, URL);
    }
}
