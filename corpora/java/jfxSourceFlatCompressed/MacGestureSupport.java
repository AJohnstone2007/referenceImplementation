package com.sun.glass.ui.mac;
import com.sun.glass.ui.TouchInputSupport;
import com.sun.glass.ui.GestureSupport;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.View;
import java.security.AccessController;
import java.security.PrivilegedAction;
final class MacGestureSupport {
private native static void _initIDs();
static {
_initIDs();
}
private final static int GESTURE_ROTATE = 100;
private final static int GESTURE_MAGNIFY = 101;
private final static int GESTURE_SWIPE = 102;
private final static int SCROLL_SRC_WHEEL = 50;
private final static int SCROLL_SRC_GESTURE = 51;
private final static int SCROLL_SRC_INERTIA = 52;
private final static double multiplier = 10.0;
private final static boolean isDirect = false;
private final static GestureSupport gestures = new GestureSupport(false);
private final static TouchInputSupport touches =
new MacTouchInputSupport(gestures.createTouchCountListener(), false);
public static void notifyBeginTouchEvent(View view, int modifiers,
int touchEventCount) {
touches.notifyBeginTouchEvent(view, modifiers, isDirect, touchEventCount);
}
public static void notifyNextTouchEvent(View view, int state, long id,
float x, float y) {
final int intX = (int) (10000 * x);
final int intY = 10000 - (int) (10000 * y);
touches.notifyNextTouchEvent(view, state, id, intX, intY, intX, intY);
}
public static void notifyEndTouchEvent(View view) {
touches.notifyEndTouchEvent(view);
}
public static void rotateGesturePerformed(View view, int modifiers, int x,
int y, int xAbs, int yAbs,
float rotation) {
gestures.handleDeltaRotation(view, modifiers, isDirect, false, x, y,
xAbs, yAbs, -rotation);
}
public static void scrollGesturePerformed(View view, int modifiers,
int sender, int x, int y,
int xAbs, int yAbs, float dx,
float dy) {
final int touchCount = touches.getTouchCount();
final boolean isInertia = (sender == SCROLL_SRC_INERTIA);
switch (sender) {
case SCROLL_SRC_WHEEL:
case SCROLL_SRC_INERTIA:
GestureSupport.handleScrollingPerformed(view, modifiers, isDirect,
isInertia, touchCount, x, y,
xAbs, yAbs, dx, dy, multiplier,
multiplier);
break;
case SCROLL_SRC_GESTURE:
gestures.handleDeltaScrolling(view, modifiers, isDirect,
isInertia, touchCount, x, y, xAbs,
yAbs, dx, dy, multiplier, multiplier);
break;
default:
System.err.println("Unknown scroll gesture sender: " + sender);
break;
}
}
public static void swipeGesturePerformed(View view, int modifiers, int dir,
int x, int y, int xAbs, int yAbs) {
gestures.handleSwipePerformed(view, modifiers, isDirect, false, touches.
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
