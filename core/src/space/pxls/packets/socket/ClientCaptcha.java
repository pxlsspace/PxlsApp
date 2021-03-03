package space.pxls.packets.socket;

public class ClientCaptcha {
    public final String type = "captcha";

    private final String token;

    public ClientCaptcha(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
