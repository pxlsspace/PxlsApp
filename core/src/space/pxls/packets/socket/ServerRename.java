package space.pxls.packets.socket;

public class ServerRename {
    public final String type = "rename";

    private final Boolean requested;

    public ServerRename(Boolean requested) {
        this.requested = requested;
    }

    public Boolean isRequested() {
        return requested;
    }
}
