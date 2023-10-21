package com.sun.glass.ui.ios;
import com.sun.glass.ui.GestureSupport;
import com.sun.glass.ui.TouchInputSupport;
import com.sun.glass.ui.View;
final class IosGestureSupport {
private native static void _initIDs();
static {
_initIDs();
}
private final static double multiplier = 1.0;
private final static boolean isDirect = true;
private final static GestureSupport gestures = new GestureSupport(false);
private final static TouchInputSupport touches =
new TouchInputSupport(gestures.createTouchCountListener(), false);
public static void notifyBeginTouchEvent(
View view, int modifiers, int touchEventCount) {
touches.notifyBeginTouchEvent(view, modifiers, isDirect, touchEventCount);
}
public static void notifyNextTouchEvent(
View view, int state, long id, float x, float y) {
touches.notifyNextTouchEvent(view, state, id, (int)x, (int)y, (int)x, (int)y);
}
public static void notifyEndTouchEvent(View view) {
touches.notifyEndTouchEvent(view);
}
public static void rotateGesturePerformed(View view, int modifiers, int x,
int y, int xAbs, int yAbs,
float rotation) {
gestures.handleDeltaRotation(view, modifiers, isDirect, false, x, y,
xAbs, yAbs, (180.0f / Math.PI) * rotation);
}
public static void scrollGesturePerformed(View view, int modifiers,
boolean inertia, float x,
float y, float xAbs, float yAbs,
float dx, float dy) {
gestures.handleDeltaScrolling(view, modifiers, isDirect, inertia,
touches.getTouchCount(), (int)x, (int)y,
(int)xAbs, (int)yAbs, dx, dy, multiplier, multiplier);
}
public static void swipeGesturePerformed(View view, int modifiers, int dir,
int x, int y, int xAbs, int yAbs) {
GestureSupport.handleSwipePerformed(view, modifiers, isDirect, false, touches.
getTouchCount(), dir, x, y, xAbs, yAbs);
}
public static void magnifyGesturePerformed(View view, int modifiers, int x,
int y, int xAbs, int yAbs,
float scale) {
gestures.handleDeltaZooming(view, modifiers, isDirect, false, x, y, xAbs,
yAbs, scale, View.GESTURE_NO_DOUBLE_VALUE);
}
public static void gestureFinished(View view, int modifiers, int x, int y,
int xAbs, int yAbs) {
if (gestures.isScrolling()) {
gestures.handleScrollingEnd(view, modifiers, touches.getTouchCount(),
isDirect, false, x, y, xAbs, yAbs);
}
if (gestures.isRotating()) {
gestures.handleRotationEnd(view, modifiers, isDirect, false, x, y,
xAbs, yAbs);
}
if (gestures.isZooming()) {
gestures.handleZoomingEnd(view, modifiers, isDirect, false, x, y,
xAbs, yAbs);
}
}
}
