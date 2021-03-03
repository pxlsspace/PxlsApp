package space.pxls.data;

public class Badge {
    private final String displayName;
    private final String tooltip;
    private final String type;
    private final String cssIcon;

    public Badge(String displayName, String tooltip, String type, String cssIcon) {
        this.displayName = displayName;
        this.tooltip = tooltip;
        this.type = type;
        this.cssIcon = cssIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getType() {
        return type;
    }

    public String getCssIcon() {
        return cssIcon;
    }
}
