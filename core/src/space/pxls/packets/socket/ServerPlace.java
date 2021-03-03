package space.pxls.packets.socket;

import java.util.Collection;

import space.pxls.data.Pixel;

public class ServerPlace {
    public final String type = "pixel";

    private final Collection<Pixel> pixels;

    public ServerPlace(Collection<Pixel> pixels) {
        this.pixels = pixels;
    }

    public Collection<Pixel> getPixels() {
        return pixels;
    }
}
