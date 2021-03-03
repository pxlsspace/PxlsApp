package space.pxls.packets.socket;

import java.util.List;

import space.pxls.data.PlacementOverrides;
import space.pxls.data.Role;
import space.pxls.data.User;

public class ServerUserInfo extends User {
    public final String type = "userinfo";

    public ServerUserInfo(String username, String login, List<Role> roles, Integer pixelCount, Integer pixelCountAllTime,
                          Boolean banned, Long banExpiry, String banReason, String method, PlacementOverrides placementOverrides,
                          Boolean chatBanned, String chatbanReason, Boolean chatbanIsPerma, Long chatbanExpiry,
                          Boolean renameRequested, String discordName, Number chatNameColor) {
        super(username, login, roles, pixelCount, pixelCountAllTime, banned, banExpiry, banReason, method, placementOverrides, chatBanned, chatbanReason, chatbanIsPerma, chatbanExpiry, renameRequested, discordName, chatNameColor);
    }
}
