package space.pxls.data;

public class PlacementOverrides {
    private final Boolean ignoreCooldown;
    private final Boolean canPlaceAnyColor;
    private final Boolean ignorePlacemap;

    public PlacementOverrides(boolean ignoreCooldown, boolean canPlaceAnyColor, boolean ignorePlacemap) {
        this.ignoreCooldown = ignoreCooldown;
        this.canPlaceAnyColor = canPlaceAnyColor;
        this.ignorePlacemap = ignorePlacemap;
    }

    public boolean hasIgnoreCooldown() {
        return ignoreCooldown;
    }

    public boolean getCanPlaceAnyColor() {
        return canPlaceAnyColor;
    }

    public boolean hasIgnorePlacemap() {
        return ignorePlacemap;
    }
}
