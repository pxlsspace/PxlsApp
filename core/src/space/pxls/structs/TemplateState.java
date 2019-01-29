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
        public static String enabled = "templateUse";
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
        DefaultValuesMap.put(ConfigKeys.enabled, false);
    }
    public String URL;
    public int offsetX;
    public int offsetY;
    public float totalWidth;
    public float opacity;
    public boolean enabled;
    public boolean moveMode = false;

    public int movingOffsetX = 0;
    public int movingOffsetY = 0;

    public TemplateState() {
        this((String) DefaultValuesMap.get(ConfigKeys.URL),(Integer) DefaultValuesMap.get(ConfigKeys.offsetX),(Integer) DefaultValuesMap.get(ConfigKeys.offsetY),(Float) DefaultValuesMap.get(ConfigKeys.totalWidth),(Float) DefaultValuesMap.get(ConfigKeys.opacity), (Boolean) DefaultValuesMap.get(ConfigKeys.enabled));
    }
    public TemplateState(String URL, int offsetX, int offsetY, float totalWidth, float opacity, boolean enabled) {
        this.URL = URL;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.totalWidth = totalWidth;
        this.opacity = opacity;
        this.enabled = enabled;
        this.movingOffsetX = this.offsetX;
        this.movingOffsetY = this.offsetY;
    }

    public Map<String, Object> deserialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConfigKeys.URL, this.URL);
        map.put(ConfigKeys.offsetX, this.offsetX);
        map.put(ConfigKeys.offsetY, this.offsetY);
        map.put(ConfigKeys.totalWidth, this.totalWidth);
        map.put(ConfigKeys.opacity, this.opacity);
        map.put(ConfigKeys.enabled, this.enabled);

        return map;
    }

    public void setOffsetY(int offsetY) {
        setOffsetY(offsetY, false);
    }
    public void setOffsetY(int offsetY, boolean ignoreMoveMode) {
        if (ignoreMoveMode) {
            this.offsetY = offsetY;
        } else {
            if (moveMode) {
                this.movingOffsetY = offsetY;
            } else {
                this.offsetY = offsetY;
            }
        }
    }

    public void setOffsetX(int offsetX) {
        setOffsetX(offsetX, false);
    }
    public void setOffsetX(int offsetX, boolean ignoreMoveMode) {
        if (ignoreMoveMode) {
            this.offsetX = offsetX;
        } else {
            if (moveMode) {
                this.movingOffsetX = offsetX;
            } else {
                this.offsetX = offsetX;
            }
        }
    }

    public void stageForMoving() {
        movingOffsetX = offsetX;
        movingOffsetY = offsetY;
    }

    public void finalizeMove(boolean cancelled) {
        if (cancelled) {
            this.movingOffsetX = offsetX;
            this.movingOffsetY = offsetY;
        } else {
            this.offsetX = this.movingOffsetX;
            this.offsetY = this.movingOffsetY;
        }
        moveMode = false;
    }

    @Override
    public String toString() {
        return String.format("offsetX: %s, offsetY: %s, totalWidth: %s, opacity: %s, URL: %s, enabled: %s", offsetX, offsetY, totalWidth, opacity, URL, enabled);
    }
}
