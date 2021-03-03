package space.pxls.packets.socket;

import space.pxls.data.Pixel;

public class ClientPixel extends Pixel {
    public final String type = "pixel";

    public ClientPixel(Pixel pixel) {
        super(pixel.getX(), pixel.getY(), pixel.getColor());
    }
}
