package space.pxls.packets.socket;

public class ServerCooldown {
    public final String type = "cooldown";

    private final Float wait;

    public ServerCooldown(Float wait) {
        this.wait = wait;
    }

    public Float getWait() {
        return wait;
    }
}
