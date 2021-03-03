package space.pxls.data;

import java.util.List;

public abstract class User {
    private final String username;
    private final String login;
    private final List<Role> roles;
    private final Integer pixelCount;
    private final Integer pixelCountAllTime;
    private final Boolean banned;
    private final Long banExpiry;
    private final String banReason;
    private final String method;
    private final PlacementOverrides placementOverrides;
    private final Boolean chatBanned;
    private final String chatbanReason;
    private final Boolean chatbanIsPerma;
    private final Long chatbanExpiry;
    private final Boolean renameRequested;
    private final String discordName;
    private final Number chatNameColor;

    public User(String username, String login, List<Role> roles, Integer pixelCount, Integer pixelCountAllTime,
                Boolean banned, Long banExpiry, String banReason, String method, PlacementOverrides placementOverrides,
                Boolean chatBanned, String chatbanReason, Boolean chatbanIsPerma, Long chatbanExpiry,
                Boolean renameRequested, String discordName, Number chatNameColor) {
        this.username = username;
        this.login = login;
        this.roles = roles;
        this.pixelCount = pixelCount;
        this.pixelCountAllTime = pixelCountAllTime;
        this.banned = banned;
        this.banExpiry = banExpiry;
        this.banReason = banReason;
        this.method = method;
        this.placementOverrides = placementOverrides;
        this.chatBanned = chatBanned;
        this.chatbanReason = chatbanReason;
        this.chatbanIsPerma = chatbanIsPerma;
        this.chatbanExpiry = chatbanExpiry;
        this.renameRequested = renameRequested;
        this.discordName = discordName;
        this.chatNameColor = chatNameColor;
    }

    public String getUsername() {
        return username;
    }

    public String getSanitizedUsername() {
        return method.equals("ip") ? "-snip-" : username;
    }

    public String getLogin() {
        return login;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Integer getPixelCount() {
        return pixelCount;
    }

    public Integer getPixelCountAllTime() {
        return pixelCountAllTime;
    }

    public Boolean isBanned() {
        return banned;
    }

    public Long getBanExpiry() {
        return banExpiry;
    }

    public String getBanReason() {
        return banReason;
    }

    public String getMethod() {
        return method;
    }

    public PlacementOverrides getPlacementOverrides() {
        return placementOverrides;
    }

    public Boolean isChatBanned() {
        return chatBanned;
    }

    public String getChatbanReason() {
        return chatbanReason;
    }

    public Boolean isChatbanPerma() {
        return chatbanIsPerma;
    }

    public Long getChatbanExpiry() {
        return chatbanExpiry;
    }

    public Boolean isRenameRequested() {
        return renameRequested;
    }

    public String getDiscordName() {
        return discordName;
    }

    public Number getChatNameColor() {
        return chatNameColor;
    }

    @Override
    public String toString() {
        return String.format("Name: %s; Banned: %s; Ban Expiry: %s; Ban Reason: %s; Login Method: %s", username, banned, banExpiry, banReason, method);
    }
}
