package space.pxls.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import space.pxls.ui.Screens.CanvasScreen;
import space.pxls.ui.Screens.LoadScreen;

public class Canvas implements Renderer {
    public final LoadScreen.BoardInfo info;
    public Texture canvasTexture;
    private CanvasScreen parent;

    public Canvas(byte[] initialData, final LoadScreen.BoardInfo info) {
        this.info = info;

        final Pixmap p = new Pixmap(info.width, info.height, Pixmap.Format.RGBA8888);
        p.getPixels().put(initialData).position(0);

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
        // 255 is transparent
        temp.setColor(color == 255 ? Color.CLEAR : Color.valueOf(info.palette.get(color).value));
        temp.drawPixel(0, 0);
        canvasTexture.draw(temp, x, y);
        temp.dispose();
    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        if (parent == null) return;
        parent.batch.draw(canvasTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
    }

    @Override
    public CanvasScreen getCanvasScreen() {
        return parent;
    }

    @Override
    public void setCanvasScreen(CanvasScreen screen) {
        this.parent = screen;
    }
}
