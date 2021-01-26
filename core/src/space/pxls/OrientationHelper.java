package space.pxls;

public interface OrientationHelper {
    enum Orientation {
        BEHIND,
        FULL_SENSOR,
        LANDSCAPE,
        NOSENSOR,
        PORTRAIT,
        REVERSE_LANDSCAPE,
        REVERSE_PORTRAIT,
        SENSOR,
        SENSOR_LANDSCAPE,
        SENSOR_PORTRAIT,
        UNSPECIFIED,
        USER
    }
    enum SimpleOrientation {
        LANDSCAPE,
        PORTRAIT,
        NA
    }
    void setOrientation(Orientation orientation);
    void setOrientation(SimpleOrientation orientation);
    Orientation getOrientation();
    SimpleOrientation getSimpleOrientation();
}
