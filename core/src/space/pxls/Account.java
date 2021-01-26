package space.pxls;

public class Account {
    private String name;

    private boolean banned;
    private long banExpiry;
    private String banReason;
    private String method;

    public Account(String name, boolean banned, long banExpiry, String banReason, String method) {
        this.name = name;
        this.banned = banned;
        this.banExpiry = banExpiry;
        this.banReason = banReason;
        this.method = method;
    }

    public String getSanitizedName() {
        return this.method.equals("ip") ? "-snip-" : this.name;
    }

    public String getName() {
        return name;
    }

    public boolean isBanned() {
        return banned;
    }

    public long getBanExpiry() {
        return banExpiry;
    }

    public String getBanReason() {
        return banReason;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Banned: %s, banExpiry: %s, banReason: %s, method: %s", name, banned, banExpiry, banReason, method);
    }
}
