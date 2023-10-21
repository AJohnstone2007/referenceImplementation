package com.sun.glass.ui;
import com.sun.glass.events.MouseEvent;
import com.sun.glass.events.ViewEvent;
import java.lang.annotation.Native;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
public abstract class View {
@Native public final static int GESTURE_NO_VALUE = Integer.MAX_VALUE;
@Native public final static double GESTURE_NO_DOUBLE_VALUE = Double.NaN;
@Native public final static byte IME_ATTR_INPUT = 0x00;
@Native public final static byte IME_ATTR_TARGET_CONVERTED = 0x01;
@Native public final static byte IME_ATTR_CONVERTED = 0x02;
@Native public final static byte IME_ATTR_TARGET_NOTCONVERTED = 0x03;
@Native public final static byte IME_ATTR_INPUT_ERROR = 0x04;
@SuppressWarnings("removal")
final static boolean accessible = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
String force = System.getProperty("glass.accessible.force");
if (force != null) return Boolean.parseBoolean(force);
try {
String platform = Platform.determinePlatform();
String major = System.getProperty("os.version").replaceFirst("(\\d+)\\.\\d+.*", "$1");
String minor = System.getProperty("os.version").replaceFirst("\\d+\\.(\\d+).*", "$1");
int v = Integer.parseInt(major) * 100 + Integer.parseInt(minor);
return (platform.equals(Platform.MAC) && v >= 1009) ||
(platform.equals(Platform.WINDOWS) && v >= 601);
} catch (Exception e) {
return false;
}
});
public static class EventHandler {
public void handleViewEvent(View view, long time, int type) {
}
public void handleKeyEvent(View view, long time, int action,
int keyCode, char[] keyChars, int modifiers) {
}
public void handleMenuEvent(View view, int x, int y, int xAbs,
int yAbs, boolean isKeyboardTrigger) {
}
public void handleMouseEvent(View view, long time, int type, int button,
int x, int y, int xAbs, int yAbs,
int modifiers, boolean isPopupTrigger, boolean isSynthesized)
{
}
public void handleScrollEvent(View view, long time,
int x, int y, int xAbs, int yAbs,
double deltaX, double deltaY, int modifiers, int lines, int chars,
int defaultLines, int defaultChars,
double xMultiplier, double yMultiplier)
{
}
public void handleInputMethodEvent(long time, String text,
int[] clauseBoundary,
int[] attrBoundary, byte[] attrValue,
int commitCount, int cursorPos) {
}
public double[] getInputMethodCandidatePos(int offset) {
return null;
}
public void handleDragStart(View view, int button, int x, int y, int xAbs, int yAbs,
ClipboardAssistance dropSourceAssistant) {
}
public void handleDragEnd(View view, int performedAction) {
}
public int handleDragEnter(View view, int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
return recommendedDropAction;
}
public int handleDragOver(View view, int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
return recommendedDropAction;
}
public void handleDragLeave(View view, ClipboardAssistance dropTargetAssistant) {
}
public int handleDragDrop(View view, int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
return Clipboard.ACTION_NONE;
}
public void handleBeginTouchEvent(View view, long time, int modifiers,
boolean isDirect, int touchEventCount) {
}
public void handleNextTouchEvent(View view, long time, int type,
long touchId, int x, int y, int xAbs,
int yAbs) {
}
public void handleEndTouchEvent(View view, long time) {
}
public void handleScrollGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int touchCount,
int x, int y, int xAbs, int yAbs,
double dx, double dy,
double totaldx, double totaldy,
double multiplierX, double multiplierY) {
}
public void handleZoomGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int x, int y,
int xAbs, int yAbs, double scale,
double expansion, double totalscale,
double totalexpansion) {
}
public void handleRotateGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int x, int y,
int xAbs, int yAbs, double dangle,
double totalangle) {
}
public void handleSwipeGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int touchCount,
int dir, int x, int y, int xAbs,
int yAbs) {
}
public Accessible getSceneAccessible() {
return null;
}
}
public static long getMultiClickTime() {
Application.checkEventThread();
return Application.GetApplication().staticView_getMultiClickTime();
}
public static int getMultiClickMaxX() {
Application.checkEventThread();
return Application.GetApplication().staticView_getMultiClickMaxX();
}
public static int getMultiClickMaxY() {
Application.checkEventThread();
return Application.GetApplication().staticView_getMultiClickMaxY();
}
protected abstract void _enableInputMethodEvents(long ptr, boolean enable);
protected void _finishInputMethodComposition(long ptr) {
}
private volatile long ptr;
private Window window;
private EventHandler eventHandler;
private int width = -1;
private int height = -1;
private boolean isValid = false;
private boolean isVisible = false;
private boolean inFullscreen = false;
static final public class Capability {
@Native static final public int k3dKeyValue = 0;
@Native static final public int kSyncKeyValue = 1;
@Native static final public int k3dProjectionKeyValue = 2;
@Native static final public int k3dProjectionAngleKeyValue = 3;
@Native static final public int k3dDepthKeyValue = 4;
@Native static final public int kHiDPIAwareKeyValue = 5;
static final public Object k3dKey = Integer.valueOf(k3dKeyValue);
static final public Object kSyncKey = Integer.valueOf(kSyncKeyValue);
static final public Object k3dProjectionKey = Integer.valueOf(k3dProjectionKeyValue);
static final public Object k3dProjectionAngleKey = Integer.valueOf(k3dProjectionAngleKeyValue);
static final public Object k3dDepthKey = Integer.valueOf(k3dDepthKeyValue);
static final public Object kHiDPIAwareKey = Integer.valueOf(kHiDPIAwareKeyValue);
}
protected abstract long _create(Map capabilities);
protected View() {
Application.checkEventThread();
this.ptr = _create(Application.GetApplication().getDeviceDetails());
if (this.ptr == 0L) {
throw new RuntimeException("could not create platform view");
}
}
private void checkNotClosed() {
if (this.ptr == 0L) {
throw new IllegalStateException("The view has already been closed");
}
}
public boolean isClosed() {
Application.checkEventThread();
return this.ptr == 0L;
}
protected abstract long _getNativeView(long ptr);
public long getNativeView() {
Application.checkEventThread();
checkNotClosed();
return _getNativeView(this.ptr);
}
public Window getWindow() {
Application.checkEventThread();
return this.window;
}
protected abstract int _getX(long ptr);
public int getX() {
Application.checkEventThread();
checkNotClosed();
return _getX(this.ptr);
}
protected abstract int _getY(long ptr);
public int getY() {
Application.checkEventThread();
checkNotClosed();
return _getY(this.ptr);
}
public int getWidth() {
Application.checkEventThread();
return this.width;
}
public int getHeight() {
Application.checkEventThread();
return this.height;
}
protected abstract void _setParent(long ptr, long parentPtr);
void setWindow(Window window) {
Application.checkEventThread();
checkNotClosed();
this.window = window;
_setParent(this.ptr, window == null ? 0L : window.getNativeHandle());
this.isValid = this.ptr != 0 && window != null;
}
void setVisible(boolean visible) {
this.isVisible = visible;
}
protected abstract boolean _close(long ptr);
public void close() {
Application.checkEventThread();
if (this.ptr == 0) {
return;
}
if (isInFullscreen()) {
_exitFullscreen(this.ptr, false);
}
Window host = getWindow();
if (host != null) {
host.setView(null);
}
this.isValid = false;
_close(this.ptr);
this.ptr = 0;
}
public EventHandler getEventHandler() {
Application.checkEventThread();
return this.eventHandler;
}
public void setEventHandler(EventHandler eventHandler) {
Application.checkEventThread();
this.eventHandler = eventHandler;
}
private void handleViewEvent(long time, int type) {
if (this.eventHandler != null) {
this.eventHandler.handleViewEvent(this, time, type);
}
}
private void handleKeyEvent(long time, int action,
int keyCode, char[] keyChars, int modifiers) {
if (this.eventHandler != null) {
this.eventHandler.handleKeyEvent(this, time, action, keyCode, keyChars, modifiers);
}
}
private void handleMouseEvent(long time, int type, int button, int x, int y,
int xAbs, int yAbs,
int modifiers, boolean isPopupTrigger,
boolean isSynthesized) {
if (eventHandler != null) {
eventHandler.handleMouseEvent(this, time, type, button, x, y, xAbs,
yAbs, modifiers,
isPopupTrigger, isSynthesized);
}
}
private void handleMenuEvent(int x, int y, int xAbs, int yAbs, boolean isKeyboardTrigger) {
if (this.eventHandler != null) {
this.eventHandler.handleMenuEvent(this, x, y, xAbs, yAbs, isKeyboardTrigger);
}
}
public void handleBeginTouchEvent(View view, long time, int modifiers,
boolean isDirect, int touchEventCount) {
if (eventHandler != null) {
eventHandler.handleBeginTouchEvent(view, time, modifiers, isDirect,
touchEventCount);
}
}
public void handleNextTouchEvent(View view, long time, int type,
long touchId, int x, int y, int xAbs,
int yAbs) {
if (eventHandler != null) {
eventHandler.handleNextTouchEvent(view, time, type, touchId, x, y, xAbs, yAbs);
}
}
public void handleEndTouchEvent(View view, long time) {
if (eventHandler != null) {
eventHandler.handleEndTouchEvent(view, time);
}
}
public void handleScrollGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int touchCount,
int x, int y, int xAbs, int yAbs,
double dx, double dy, double totaldx,
double totaldy, double multiplierX,
double multiplierY) {
if (eventHandler != null) {
eventHandler.handleScrollGestureEvent(view, time, type, modifiers, isDirect,
isInertia, touchCount, x, y, xAbs, yAbs,
dx, dy, totaldx, totaldy, multiplierX, multiplierY);
}
}
public void handleZoomGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int originx,
int originy, int originxAbs,
int originyAbs, double scale,
double expansion, double totalscale,
double totalexpansion) {
if (eventHandler != null) {
eventHandler.handleZoomGestureEvent(view, time, type, modifiers, isDirect,
isInertia, originx, originy, originxAbs,
originyAbs, scale, expansion, totalscale,
totalexpansion);
}
}
public void handleRotateGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int originx,
int originy, int originxAbs,
int originyAbs, double dangle,
double totalangle) {
if (eventHandler != null) {
eventHandler.handleRotateGestureEvent(view, time, type, modifiers, isDirect,
isInertia, originx, originy, originxAbs,
originyAbs, dangle, totalangle);
}
}
public void handleSwipeGestureEvent(View view, long time, int type,
int modifiers, boolean isDirect,
boolean isInertia, int touchCount,
int dir, int originx, int originy,
int originxAbs, int originyAbs) {
if (eventHandler != null) {
eventHandler.handleSwipeGestureEvent(view, time, type, modifiers, isDirect,
isInertia, touchCount, dir, originx,
originy, originxAbs, originyAbs);
}
}
private void handleInputMethodEvent(long time, String text, int[] clauseBoundary,
int[] attrBoundary, byte[] attrValue,
int commitCount, int cursorPos) {
if (this.eventHandler != null) {
this.eventHandler.handleInputMethodEvent(time, text, clauseBoundary,
attrBoundary, attrValue,
commitCount, cursorPos);
}
}
public void enableInputMethodEvents(boolean enable) {
Application.checkEventThread();
checkNotClosed();
_enableInputMethodEvents(this.ptr, enable);
}
public void finishInputMethodComposition() {
Application.checkEventThread();
checkNotClosed();
_finishInputMethodComposition(this.ptr);
}
private double[] getInputMethodCandidatePos(int offset) {
if (this.eventHandler != null) {
return this.eventHandler.getInputMethodCandidatePos(offset);
}
return null;
}
private void handleDragStart(int button, int x, int y, int xAbs, int yAbs,
ClipboardAssistance dropSourceAssistant) {
if (this.eventHandler != null) {
this.eventHandler.handleDragStart(this, button, x, y, xAbs, yAbs, dropSourceAssistant);
}
}
private void handleDragEnd(int performedAction) {
if (this.eventHandler != null) {
this.eventHandler.handleDragEnd(this, performedAction);
}
}
private int handleDragEnter(int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
if (this.eventHandler != null) {
return this.eventHandler.handleDragEnter(this, x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
} else {
return recommendedDropAction;
}
}
private int handleDragOver(int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
if (this.eventHandler != null) {
return this.eventHandler.handleDragOver(this, x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
} else {
return recommendedDropAction;
}
}
private void handleDragLeave(ClipboardAssistance dropTargetAssistant) {
if (this.eventHandler != null) {
this.eventHandler.handleDragLeave(this, dropTargetAssistant);
}
}
private int handleDragDrop(int x, int y, int xAbs, int yAbs,
int recommendedDropAction, ClipboardAssistance dropTargetAssistant) {
if (this.eventHandler != null) {
return this.eventHandler.handleDragDrop(this, x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
} else {
return Clipboard.ACTION_NONE;
}
}
protected abstract void _scheduleRepaint(long ptr);
public void scheduleRepaint() {
Application.checkEventThread();
checkNotClosed();
_scheduleRepaint(this.ptr);
}
protected abstract void _begin(long ptr);
public void lock() {
checkNotClosed();
_begin(this.ptr);
}
protected abstract void _end(long ptr);
public void unlock() {
checkNotClosed();
_end(this.ptr);
}
protected abstract int _getNativeFrameBuffer(long ptr);
public int getNativeFrameBuffer() {
return _getNativeFrameBuffer(this.ptr);
}
protected abstract void _uploadPixels(long ptr, Pixels pixels);
public void uploadPixels(Pixels pixels) {
Application.checkEventThread();
checkNotClosed();
lock();
try {
_uploadPixels(this.ptr, pixels);
} finally {
unlock();
}
}
protected abstract boolean _enterFullscreen(long ptr, boolean animate, boolean keepRatio, boolean hideCursor);
public boolean enterFullscreen(boolean animate, boolean keepRatio, boolean hideCursor) {
Application.checkEventThread();
checkNotClosed();
return _enterFullscreen(this.ptr, animate, keepRatio, hideCursor);
}
protected abstract void _exitFullscreen(long ptr, boolean animate);
public void exitFullscreen(boolean animate) {
Application.checkEventThread();
checkNotClosed();
_exitFullscreen(this.ptr, animate);
}
public boolean isInFullscreen() {
Application.checkEventThread();
return this.inFullscreen;
}
public boolean toggleFullscreen(boolean animate, boolean keepRatio, boolean hideCursor) {
Application.checkEventThread();
checkNotClosed();
if (!this.inFullscreen) {
enterFullscreen(animate, keepRatio, hideCursor);
} else {
exitFullscreen(animate);
}
_scheduleRepaint(this.ptr);
return this.inFullscreen;
}
public void updateLocation() {
notifyView(ViewEvent.MOVE);
}
protected void notifyView(int type) {
if (type == ViewEvent.REPAINT) {
if (isValid) {
handleViewEvent(System.nanoTime(), type);
}
}
else
{
boolean synthesizeMOVE = false;
switch (type) {
case ViewEvent.REMOVE:
isValid = false;
synthesizeMOVE = true;
break;
case ViewEvent.ADD:
isValid = true;
synthesizeMOVE = true;
break;
case ViewEvent.FULLSCREEN_ENTER:
this.inFullscreen = true;
synthesizeMOVE = true;
if (getWindow() != null) {
getWindow().notifyFullscreen(true);
}
break;
case ViewEvent.FULLSCREEN_EXIT:
this.inFullscreen = false;
synthesizeMOVE = true;
if (getWindow() != null) {
getWindow().notifyFullscreen(false);
}
break;
case ViewEvent.MOVE:
case ViewEvent.RESIZE:
break;
default:
System.err.println("Unknown view event type: " + type);
return;
}
handleViewEvent(System.nanoTime(), type);
if (synthesizeMOVE) {
handleViewEvent(System.nanoTime(), ViewEvent.MOVE);
}
}
}
protected void notifyResize(int width, int height) {
if (this.width == width && this.height == height) {
return;
}
this.width = width;
this.height = height;
handleViewEvent(System.nanoTime(), ViewEvent.RESIZE);
}
protected void notifyRepaint(int x, int y, int width, int height) {
notifyView(ViewEvent.REPAINT);
}
protected void notifyMenu(int x, int y, int xAbs, int yAbs, boolean isKeyboardTrigger) {
handleMenuEvent(x, y, xAbs, yAbs, isKeyboardTrigger);
}
private static WeakReference<View> lastClickedView = null;
private static int lastClickedButton;
private static long lastClickedTime;
private static int lastClickedX, lastClickedY;
private static int clickCount;
private static boolean dragProcessed = false;
protected void notifyMouse(int type, int button, int x, int y, int xAbs,
int yAbs, int modifiers, boolean isPopupTrigger,
boolean isSynthesized) {
if (this.window != null) {
if (this.window.handleMouseEvent(type, button, x, y, xAbs, yAbs)) {
return;
}
}
long now = System.nanoTime();
if (type == MouseEvent.DOWN) {
View lastClickedView = View.lastClickedView == null ? null : View.lastClickedView.get();
if (lastClickedView == this &&
lastClickedButton == button &&
(now - lastClickedTime) <= 1000000L*getMultiClickTime() &&
Math.abs(x - lastClickedX) <= getMultiClickMaxX() &&
Math.abs(y - lastClickedY) <= getMultiClickMaxY())
{
clickCount++;
} else {
clickCount = 1;
View.lastClickedView = new WeakReference<View>(this);
lastClickedButton = button;
lastClickedX = x;
lastClickedY = y;
}
lastClickedTime = now;
}
handleMouseEvent(now, type, button, x, y, xAbs, yAbs,
modifiers, isPopupTrigger, isSynthesized);
if (type == MouseEvent.DRAG) {
if (!dragProcessed) {
notifyDragStart(button, x, y, xAbs, yAbs);
dragProcessed = true;
}
} else {
dragProcessed = false;
}
}
protected void notifyScroll(int x, int y, int xAbs, int yAbs,
double deltaX, double deltaY, int modifiers, int lines, int chars,
int defaultLines, int defaultChars,
double xMultiplier, double yMultiplier)
{
if (this.eventHandler != null) {
this.eventHandler.handleScrollEvent(this, System.nanoTime(),
x, y, xAbs, yAbs, deltaX, deltaY, modifiers, lines, chars,
defaultLines, defaultChars, xMultiplier, yMultiplier);
}
}
protected void notifyKey(int type, int keyCode, char[] keyChars, int modifiers) {
handleKeyEvent(System.nanoTime(), type, keyCode, keyChars, modifiers);
}
protected void notifyInputMethod(String text, int[] clauseBoundary,
int[] attrBoundary, byte[] attrValue,
int committedTextLength, int caretPos, int visiblePos) {
handleInputMethodEvent(System.nanoTime(), text, clauseBoundary,
attrBoundary, attrValue, committedTextLength, caretPos);
}
protected double[] notifyInputMethodCandidatePosRequest(int offset) {
double[] ret = getInputMethodCandidatePos(offset);
if (ret == null) {
ret = new double[2];
ret[0] = 0.0;
ret[1] = 0.0;
}
return ret;
}
private ClipboardAssistance dropSourceAssistant;
protected void notifyDragStart(int button, int x, int y, int xAbs, int yAbs) {
dropSourceAssistant = new ClipboardAssistance(Clipboard.DND) {
@Override public void actionPerformed(int performedAction) {
notifyDragEnd(performedAction);
}
};
handleDragStart(button, x, y, xAbs, yAbs, dropSourceAssistant);
if (dropSourceAssistant != null) {
dropSourceAssistant.close();
dropSourceAssistant = null;
}
}
protected void notifyDragEnd(int performedAction) {
handleDragEnd(performedAction);
if (dropSourceAssistant != null) {
dropSourceAssistant.close();
dropSourceAssistant = null;
}
}
ClipboardAssistance dropTargetAssistant;
protected int notifyDragEnter(int x, int y, int xAbs, int yAbs, int recommendedDropAction) {
dropTargetAssistant = new ClipboardAssistance(Clipboard.DND) {
@Override public void flush() {
throw new UnsupportedOperationException("Flush is forbidden from target!");
}
};
return handleDragEnter(x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
}
protected int notifyDragOver(int x, int y, int xAbs, int yAbs, int recommendedDropAction) {
return handleDragOver(x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
}
protected void notifyDragLeave() {
handleDragLeave(dropTargetAssistant);
dropTargetAssistant.close();
}
protected int notifyDragDrop(int x, int y, int xAbs, int yAbs, int recommendedDropAction) {
int performedAction = handleDragDrop(x, y, xAbs, yAbs, recommendedDropAction, dropTargetAssistant);
dropTargetAssistant.close();
return performedAction;
}
public void notifyBeginTouchEvent(int modifiers, boolean isDirect,
int touchEventCount) {
handleBeginTouchEvent(this, System.nanoTime(), modifiers, isDirect,
touchEventCount);
}
public void notifyNextTouchEvent(int type, long touchId, int x, int y,
int xAbs, int yAbs) {
handleNextTouchEvent(this, System.nanoTime(), type, touchId, x, y, xAbs,
yAbs);
}
public void notifyEndTouchEvent() {
handleEndTouchEvent(this, System.nanoTime());
}
public void notifyScrollGestureEvent(int type, int modifiers,
boolean isDirect, boolean isInertia,
int touchCount, int x, int y, int xAbs,
int yAbs, double dx, double dy,
double totaldx, double totaldy,
double multiplierX, double multiplierY) {
handleScrollGestureEvent(this, System.nanoTime(), type, modifiers,
isDirect, isInertia, touchCount, x, y, xAbs,
yAbs, dx, dy, totaldx, totaldy, multiplierX, multiplierY);
}
public void notifyZoomGestureEvent(int type, int modifiers, boolean isDirect,
boolean isInertia, int originx,
int originy, int originxAbs,
int originyAbs, double scale,
double expansion, double totalscale,
double totalexpansion) {
handleZoomGestureEvent(this, System.nanoTime(), type, modifiers,
isDirect, isInertia, originx, originy, originxAbs,
originyAbs, scale, expansion, totalscale,
totalexpansion);
}
public void notifyRotateGestureEvent(int type, int modifiers,
boolean isDirect, boolean isInertia,
int originx, int originy,
int originxAbs, int originyAbs,
double dangle, double totalangle) {
handleRotateGestureEvent(this, System.nanoTime(), type, modifiers,
isDirect, isInertia, originx, originy,
originxAbs, originyAbs, dangle, totalangle);
}
public void notifySwipeGestureEvent(int type, int modifiers,
boolean isDirect, boolean isInertia,
int touchCount, int dir, int originx,
int originy, int originxAbs,
int originyAbs) {
handleSwipeGestureEvent(this, System.nanoTime(), type, modifiers,
isDirect, isInertia, touchCount, dir, originx,
originy, originxAbs, originyAbs);
}
long getAccessible() {
Application.checkEventThread();
checkNotClosed();
if (accessible) {
Accessible acc = eventHandler.getSceneAccessible();
if (acc != null) {
acc.setView(this);
return acc.getNativeAccessible();
}
}
return 0L;
}
}
