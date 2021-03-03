package space.pxls.packets.socket;

import space.pxls.data.Notification;

public class ServerNotification {
    public final String type = "notification";

    private final Notification notification;

    public ServerNotification(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
