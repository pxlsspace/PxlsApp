package space.pxls.packets.socket;

public class ServerCaptchaStatus {
    public final String type = "captcha_status";

    private final Boolean success;

    public ServerCaptchaStatus(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }
}
