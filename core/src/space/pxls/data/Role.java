package space.pxls.data;

import java.util.ArrayList;
import java.util.List;

public class Role {
    private final String id;
    private final String name;
    private final Boolean guest;
    private final Boolean defaultRole;
    private final List<Role> inherits = new ArrayList<Role>();
    private final List<Badge> badges;
    private final List<String> permissions;

    public Role(String id, String name, Boolean guest, Boolean defaultRole, List<Badge> badges, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.guest = guest;
        this.defaultRole = defaultRole;
        this.badges = badges;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getGuest() {
        return guest;
    }

    public Boolean getDefaultRole() {
        return defaultRole;
    }

    public List<Role> getInherits() {
        return inherits;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
