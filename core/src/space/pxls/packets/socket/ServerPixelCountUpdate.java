package space.pxls.packets.socket;

import space.pxls.data.User;

public class ServerPixelCountUpdate {
    public final String type = "pixelCounts";

    private final Integer pixelCount;
    private final Integer pixelCountAllTime;

    public ServerPixelCountUpdate(Integer pixelCount, Integer pixelCountAllTime) {
        this.pixelCount = pixelCount;
        this.pixelCountAllTime = pixelCountAllTime;
    }

    public ServerPixelCountUpdate(User user) {
        this.pixelCount = user.getPixelCount();
        this.pixelCountAllTime = user.getPixelCountAllTime();
    }

    public Integer getPixelCount() {
        return pixelCount;
    }

    public Integer getPixelCountAllTime() {
        return pixelCountAllTime;
    }
}
