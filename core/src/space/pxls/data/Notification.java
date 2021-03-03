package space.pxls.data;

public class Notification {
    private final int id;
    private final long time;
    private final long expiry;
    private final String who;
    private final String title;
    private final String content;

    public Notification(int id, long time, long expiry, String who, String title, String content) {
        this.id = id;
        this.time = time;
        this.expiry = expiry;
        this.who = who;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public long getExpiry() {
        return expiry;
    }

    public String getWho() {
        return who;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
