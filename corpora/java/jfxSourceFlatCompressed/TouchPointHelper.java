package com.sun.javafx.scene.input;
import com.sun.javafx.util.Utils;
import javafx.scene.input.TouchPoint;
public class TouchPointHelper {
private static TouchPointAccessor touchPointAccessor;
static {
Utils.forceInit(TouchPoint.class);
}
private TouchPointHelper() {
}
public static void reset(TouchPoint touchPoint) {
touchPointAccessor.reset(touchPoint);
}
public static void setTouchPointAccessor(final TouchPointAccessor newAccessor) {
if (touchPointAccessor != null) {
throw new IllegalStateException();
}
touchPointAccessor = newAccessor;
}
public interface TouchPointAccessor {
void reset(TouchPoint touchPoint);
}
}
