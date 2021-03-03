package space.pxls.client;

import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import space.pxls.Pxls;
import space.pxls.PxlsClient;
import space.pxls.PxlsGame;
import space.pxls.data.ChatMessage;
import space.pxls.data.Pixel;
import space.pxls.packets.chat.ServerChatMessage;
import space.pxls.packets.socket.ClientCaptcha;
import space.pxls.packets.socket.ClientPixel;
import space.pxls.packets.socket.ClientUndo;
import space.pxls.packets.socket.ServerACK;
import space.pxls.packets.socket.ServerAdminPlacementOverrides;
import space.pxls.packets.socket.ServerAlert;
import space.pxls.packets.socket.ServerCanUndo;
import space.pxls.packets.socket.ServerCaptchaRequired;
import space.pxls.packets.socket.ServerCaptchaStatus;
import space.pxls.packets.socket.ServerCooldown;
import space.pxls.packets.socket.ServerError;
import space.pxls.packets.socket.ServerNotification;
import space.pxls.packets.socket.ServerPixelCountUpdate;
import space.pxls.packets.socket.ServerPixels;
import space.pxls.packets.socket.ServerPlace;
import space.pxls.packets.socket.ServerReceivedReport;
import space.pxls.packets.socket.ServerRename;
import space.pxls.packets.socket.ServerRenameSuccess;
import space.pxls.packets.socket.ServerUserInfo;
import space.pxls.packets.socket.ServerUsers;
import space.pxls.ui.Screens.LoadScreen;

public class PacketHandler {
    private PxlsClient client;
    private PxlsClient.UpdateCallback updateCallback;
    private WebSocketClient socket;

    private Pixel pendingPixel;

    public PacketHandler(PxlsClient client, PxlsClient.UpdateCallback updateCallback) {
        this.client = client;
        this.updateCallback = updateCallback;
    }

    public void initialize() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        String wsPath = Pxls.getWsPath();

        Map<String, String> headers = new HashMap<>();
        if (Pxls.getPrefsHelper().getToken() != null) {
            headers.put("Cookie", "pxls-token=" + Pxls.getPrefsHelper().getToken());
        }
        headers.put("User-Agent", Pxls.getUserAgent());
        socket = new WebSocketClient(new URI(wsPath), new Draft_6455(), headers, 30 * 1000) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {}

            @Override
            public void onMessage(String message) {
                boolean handled = handle(message);
                if (!handled) {
                    System.err.println("Unhandled packet: " + message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.err.println("WebSocket closed with code " + code + " for reason: " + reason + (remote ? " by remote" : ""));
                if (remote) {
                    PxlsGame.i.setScreen(new LoadScreen());
                } else {
                    try {
                        initialize();
                    } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };

        if (wsPath.startsWith("wss://")) {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, new SecureRandom());
            socket.setSocketFactory(sslContext.getSocketFactory());
        }
    }

    /**
     * Parses the given packet string and routes to the relevant method.
     * @param packetString The packet string.
     * @return Whether the packet was handled.
     */
    public boolean handle(String packetString) {
        JsonObject jsonObj = Pxls.getGson().fromJson(packetString, JsonObject.class);
        String type = jsonObj.get("type").getAsString();
        switch (type) {
            case "ack":
                return handle(Pxls.getGson().fromJson(packetString, ServerACK.class));
            case "admin_placement_overrides":
                return handle(Pxls.getGson().fromJson(packetString, ServerAdminPlacementOverrides.class));
            case "alert":
                return handle(Pxls.getGson().fromJson(packetString, ServerAlert.class));
            case "can_undo":
                return handle(Pxls.getGson().fromJson(packetString, ServerCanUndo.class));
            case "captcha_required":
                return handle(Pxls.getGson().fromJson(packetString, ServerCaptchaRequired.class));
            case "captcha_status":
                return handle(Pxls.getGson().fromJson(packetString, ServerCaptchaStatus.class));
            case "chat_message":
                return handle(Pxls.getGson().fromJson(packetString, ServerChatMessage.class));
            case "cooldown":
                return handle(Pxls.getGson().fromJson(packetString, ServerCooldown.class));
            case "error":
                return handle(Pxls.getGson().fromJson(packetString, ServerError.class));
            case "notification":
                return handle(Pxls.getGson().fromJson(packetString, ServerNotification.class));
            case "pixelCounts":
                return handle(Pxls.getGson().fromJson(packetString, ServerPixelCountUpdate.class));
            case "pixels":
                return handle(Pxls.getGson().fromJson(packetString, ServerPixels.class));
            case "pixel":
                return handle(Pxls.getGson().fromJson(packetString, ServerPlace.class));
            case "received_report":
                return handle(Pxls.getGson().fromJson(packetString, ServerReceivedReport.class));
            case "rename":
                return handle(Pxls.getGson().fromJson(packetString, ServerRename.class));
            case "rename_success":
                return handle(Pxls.getGson().fromJson(packetString, ServerRenameSuccess.class));
            case "userinfo":
                return handle(Pxls.getGson().fromJson(packetString, ServerUserInfo.class));
            case "users":
                return handle(Pxls.getGson().fromJson(packetString, ServerUsers.class));
        }
        return false;
    }

    private boolean handle(ServerACK packet) {
        return true;
    }

    private boolean handle(ServerAdminPlacementOverrides packet) {
        return true;
    }

    private boolean handle(ServerAlert packet) {
        updateCallback.showAlert(packet.getMessage());
        return true;
    }

    private boolean handle(ServerCanUndo packet) {
        updateCallback.canUndo(packet.getTime());
        return true;
    }

    private boolean handle(ServerCaptchaRequired packet) {
        updateCallback.runCaptcha();
        return true;
    }

    private boolean handle(ServerCaptchaStatus packet) {
        if (packet.getSuccess()) {
            if (pendingPixel != null) placePixel(pendingPixel);
        } else {
            updateCallback.showFailedCaptcha();
        }
        return true;
    }

    private boolean handle(ServerCooldown packet) {
        updateCallback.cooldown(packet.getWait());
        return true;
    }

    private boolean handle(ServerChatMessage packet) {
        ChatMessage message = packet.getMessage();
        System.out.println("Received chat message: " + message.getMessageRaw());
        return true;
    }

    private boolean handle(ServerError packet) {
        return true;
    }

    private boolean handle(ServerNotification packet) {
        return true;
    }

    private boolean handle(ServerPixelCountUpdate packet) {
        return true;
    }

    private boolean handle(ServerPixels packet) {
        updateCallback.stack(packet.getCount(), packet.getCause());
        return true;
    }

    private boolean handle(ServerPlace packet) {
        for (Pixel pixel : packet.getPixels()) {
            updateCallback.pixel(pixel.getX(), pixel.getY(), pixel.getColor());
        }
        return true;
    }

    private boolean handle(ServerReceivedReport packet) {
        return true;
    }

    private boolean handle(ServerRename packet) {
        return true;
    }

    private boolean handle(ServerRenameSuccess packet) {
        return true;
    }

    private boolean handle(ServerUserInfo packet) {
        client.setLoggedIn(true);
        client.setUser(packet);
        updateCallback.updateUser(packet);
        return true;
    }

    private boolean handle(ServerUsers packet) {
        return true;
    }

    public void placePixel(Pixel pixel) {
        if (client.isLoggedIn()) {
            socket.send(Pxls.getGson().toJson(new ClientPixel(pixel)));
            pendingPixel = pixel;
        }
    }

    public void undoPixel() {
        socket.send(Pxls.getGson().toJson(new ClientUndo()));
    }

    public void finishCaptcha(String token) {
        socket.send(Pxls.getGson().toJson(new ClientCaptcha(token)));
    }

    public PxlsClient getClient() {
        return client;
    }

    public void setClient(PxlsClient client) {
        this.client = client;
    }

    public PxlsClient.UpdateCallback getUpdateCallback() {
        return updateCallback;
    }

    public void setUpdateCallback(PxlsClient.UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public WebSocketClient getSocket() {
        return socket;
    }

    public void setSocket(WebSocketClient socket) {
        this.socket = socket;
    }

    public Pixel getPendingPixel() {
        return pendingPixel;
    }

    public void setPendingPixel(Pixel pendingPixel) {
        this.pendingPixel = pendingPixel;
    }
}
