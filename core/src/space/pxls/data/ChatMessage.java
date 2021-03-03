package space.pxls.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatMessage {
    private final Integer id;
    private final String author;
    private final Long date;
    @SerializedName(value = "message_raw")
    private final String messageRaw;
    private final ChatPurge purge;
    private final List<Badge> badges;
    private final List<String> authorNameClass;
    private final Number authorNameColor;
    private final ChatStrippedFaction strippedFaction;

    public ChatMessage(Integer id, String author, Long date, String messageRaw, ChatPurge purge, List<Badge> badges, List<String> authorNameClass, Number authorNameColor, ChatStrippedFaction strippedFaction) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.messageRaw = messageRaw;
        this.purge = purge;
        this.badges = badges;
        this.authorNameClass = authorNameClass;
        this.authorNameColor = authorNameColor;
        this.strippedFaction = strippedFaction;
    }

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public Long getDate() {
        return date;
    }

    public String getMessageRaw() {
        return messageRaw;
    }

    public ChatPurge getPurge() {
        return purge;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public List<String> getAuthorNameClass() {
        return authorNameClass;
    }

    public Number getAuthorNameColor() {
        return authorNameColor;
    }

    public ChatStrippedFaction getStrippedFaction() {
        return strippedFaction;
    }

    public ChatMessage asSnipRedacted() {
        return new ChatMessage(id, "-snip-", date, messageRaw, purge, badges, authorNameClass, authorNameColor, null);
    }
}
