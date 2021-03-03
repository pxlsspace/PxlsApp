package space.pxls;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import space.pxls.client.PacketHandler;
import space.pxls.data.User;

public class PxlsClient {
    private final PacketHandler packetHandler;
    public boolean loggedIn;
    private User user;

    public PxlsClient(final UpdateCallback updateCallback) {
        packetHandler = new PacketHandler(this, updateCallback);
        try {
            loggedIn = false;
            packetHandler.initialize();
            packetHandler.getSocket().connect();
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public interface UpdateCallback {
        void pixel(int x, int y, int color);

        void users(int users);

        void updateUser(User user);

        void cooldown(float seconds);

        void canUndo(float seconds);

        void stack(int count, String cause);

        void runCaptcha();

        void showFailedCaptcha();

        void showAlert(String message);
    }
}
