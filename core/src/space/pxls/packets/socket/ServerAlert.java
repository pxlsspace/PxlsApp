package space.pxls.packets.socket;

public class ServerAlert {
    public final String type = "alert";

    private final String sender;
    private final String message;

    public ServerAlert(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
