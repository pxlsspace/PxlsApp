package space.pxls.packets.socket;

public class ServerError {
    public final String type = "error";

    private final String message;

    public ServerError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
