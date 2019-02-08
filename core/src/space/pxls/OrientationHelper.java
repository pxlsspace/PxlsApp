package space.pxls;

public interface OrientationHelper {
    enum Orientation {
        BEHIND,
        FULL_SENSOR,
        FULL_USER,
        LANDSCAPE,
        LOCKED,
        NOSENSOR,
        PORTRAIT,
        REVERSE_LANDSCAPE,
        REVERSE_PORTRAIT,
        SENSOR,
        SENSOR_LANDSCAPE,
        SENSOR_PORTRAIT,
        UNSPECIFIED,
        USER,
        USER_LANDSCAPE,
        USER_PORTRAIT
    }
    void setOrientation(Orientation orientation);
    Orientation getOrientation();
}
