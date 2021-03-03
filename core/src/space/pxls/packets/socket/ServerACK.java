package space.pxls.packets.socket;

public class ServerACK {
    public final String type = "ACK";

    private final String ackFor;
    private final Integer x;
    private final Integer y;

    public ServerACK(String ackFor, Integer x, Integer y) {
        this.ackFor = ackFor;
        this.x = x;
        this.y = y;
    }

    public String getAckFor() {
        return ackFor;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
}
