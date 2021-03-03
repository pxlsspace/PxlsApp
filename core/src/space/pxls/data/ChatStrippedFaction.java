package space.pxls.data;

public class ChatStrippedFaction {
    private final Integer id;
    private final String name;
    private final String tag;
    private final Integer color;

    public ChatStrippedFaction(Integer id, String name, String tag, Integer color) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Integer getColor() {
        return color;
    }
}
