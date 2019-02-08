package space.pxls.renderers;

import com.badlogic.gdx.math.Vector2;

import space.pxls.ui.CanvasScreen;

public interface Renderer {
    void pixel(final int x, final int y, final int color);
    void render(float zoom, Vector2 screenCenter, Vector2 canvasSize, Vector2 canvasCorner);

    CanvasScreen getCanvasScreen();
    void setCanvasScreen(CanvasScreen screen);
}
