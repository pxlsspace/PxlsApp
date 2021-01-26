package space.pxls;

import java.util.ArrayList;
import java.util.List;

public class Role {
    String id;
    String name;
    Boolean guest;
    Boolean defaultRole;
    List<Role> inherits = new ArrayList<Role>();
    List<Badge> badges;
    List<String> permissions;
}
