package space.pxls;

import java.util.List;

public class Account {
    String username;
    String login;
    List<Role> roles;
    int pixelCount;
    int pixelCountAllTime;
    Boolean banned;
    Long banExpiry;
    String banReason;
    String method;
    Object placementOverrides;
    Boolean chatBanned;
    String chatbanReason;
    Boolean chatbanIsPerma;
    Long chatbanExpiry;
    Boolean renameRequested;
    String discordName;
    Number chatNameColor;

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

    public int getPixelCount() {
        return pixelCount;
    }

    public int getPixelCountAllTime() {
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

    public Object getPlacementOverrides() {
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
