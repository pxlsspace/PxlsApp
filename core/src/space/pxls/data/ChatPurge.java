package space.pxls.data;

public class ChatPurge {
    private final String initiator;
    private final String reason;

    public ChatPurge(String initiator, String reason) {
        this.initiator = initiator;
        this.reason = reason;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getReason() {
        return reason;
    }
}
