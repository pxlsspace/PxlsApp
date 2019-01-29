package space.pxls.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

import space.pxls.ui.CanvasScreen;
import space.pxls.ui.LoadScreen;

public class Canvas {
    private CanvasScreen parent;
    public final LoadScreen.BoardInfo info;
    public FrameBuffer canvasBuffer;
    public Texture canvasTexture;

    public Canvas(byte[] initialData, final LoadScreen.BoardInfo info) {
        this.info = info;

        final Pixmap p = new Pixmap(info.width, info.height, Pixmap.Format.RGBA8888);
        p.getPixels().put(initialData).position(0);

        System.out.println("constructing FrameBuffer with info" + info);

        canvasTexture = new Texture(info.width, info.height, Pixmap.Format.RGBA8888);
        canvasTexture.draw(p, 0, 0);
        canvasTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
    }

    public Canvas setParent(CanvasScreen parent) {
        this.parent = parent;
        return this;
    }

    public void pixel(final int x, final int y, final int color) {
        if (parent == null) return;

        Pixmap temp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        temp.setColor(Color.valueOf(info.palette.get(color)));
        temp.drawPixel(0,0);
        canvasTexture.draw(temp, x, y);
        temp.dispose();
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        if (parent == null) return;
        parent.batch.draw(canvasTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
    }
}
