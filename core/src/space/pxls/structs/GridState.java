package space.pxls.structs;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class GridState {
    public static class ConfigKeys {
        public static String color = "gridColor";
        public static String[] KEYS = new String[] {color};
    }

    public static Map<String, Object> DefaultValuesMap;
    static {
        DefaultValuesMap = new HashMap<String, Object>();
        DefaultValuesMap.put(ConfigKeys.color, Color.GRAY);
    }

    public Color color = Color.GRAY;
    public GridState() {
        this((Color) DefaultValuesMap.get(ConfigKeys.color));
    }
    public GridState(Color color) {
        this.color = color;
    }

    public Map<String, Object> deserialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConfigKeys.color, this.color);

        return map;
    }

    @Override
    public String toString() {
        return String.format("color: (r: %s, g: %s, b: %s, a: %s)", this.color.r, this.color.g, this.color.b, this.color.a);
    }
}
