package space.pxls.packets.socket;

public class ServerUsers {
    public final String type = "users";

    private final Integer count;

    public ServerUsers(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }
}
