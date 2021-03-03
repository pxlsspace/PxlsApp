package space.pxls.packets.socket;

import space.pxls.data.PlacementOverrides;

public class ServerAdminPlacementOverrides {
    public final String type = "admin_placement_overrides";

    private final PlacementOverrides placementOverrides;

    public ServerAdminPlacementOverrides(PlacementOverrides placementOverrides) {
        this.placementOverrides = placementOverrides;
    }

    public PlacementOverrides getPlacementOverrides() {
        return placementOverrides;
    }
}
