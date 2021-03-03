package space.pxls.packets.chat;

import space.pxls.data.ChatMessage;

public class ServerChatMessage {
    private final String type = "chat_message";

    private final ChatMessage message;

    public ServerChatMessage(ChatMessage message) {
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }
}
