package space.pxls;

public class Account {
    private String name;

    private boolean banned;
    private long banExpiry;
    private String banReason;

    public Account(String name, boolean banned, long banExpiry, String banReason) {
        this.name = name;
        this.banned = banned;
        this.banExpiry = banExpiry;
        this.banReason = banReason;
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

    @Override
    public String toString() {
        return String.format("Name: %s, Banned: %s, banExpiry: %s, banReason: %s", name, banned, banExpiry, banReason);
    }
}
