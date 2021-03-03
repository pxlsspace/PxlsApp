package space.pxls.packets.socket;

public class ServerCanUndo {
    public final String type = "can_undo";

    private final Long time;

    public ServerCanUndo(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }
}
