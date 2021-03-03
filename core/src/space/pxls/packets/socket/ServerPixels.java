package space.pxls.packets.socket;

public class ServerPixels {
    public final String type = "pixels";

    private final Integer count;
    private final String cause;

    public ServerPixels(Integer count, String cause) {
        this.count = count;
        this.cause = cause;
    }

    public Integer getCount() {
        return count;
    }

    public String getCause() {
        return cause;
    }
}
