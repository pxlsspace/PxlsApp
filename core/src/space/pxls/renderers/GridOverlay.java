package space.pxls.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import space.pxls.Pxls;
import space.pxls.ui.CanvasScreen;

public class GridOverlay implements Renderer {
    private CanvasScreen parent;
    public Texture gridTexture;

    public GridOverlay(CanvasScreen parent) {
        this.parent = parent;
        Color fillColor = new Color(0f, 0f, 0f, 0f);
        final Pixmap p = new Pixmap(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0f, 0f, 0f, 0f));
        p.fill();

        gridTexture = new Texture(parent.boardInfo.width, parent.boardInfo.height, Pixmap.Format.RGBA8888);
        gridTexture.draw(p, 0, 0);
        gridTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
    }

    public GridOverlay setParent(CanvasScreen parent) {
        this.parent = parent;
        return this;
    }

    public void pixel(int x, int y, int color) {

    }

    public void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner) {
        if (parent == null) return;
        if (!Pxls.prefsHelper.getGridEnabled()) return;
        if (zoom < 5) return;
        parent.batch.draw(gridTexture, canvasCorner.x, canvasCorner.y, canvasSize.x, canvasSize.y);
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
