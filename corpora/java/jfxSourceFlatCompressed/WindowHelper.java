package com.sun.javafx.stage;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.util.Utils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.stage.Screen;
import javafx.stage.Window;
import java.security.AccessControlContext;
public class WindowHelper {
private static final WindowHelper theInstance;
private static WindowAccessor windowAccessor;
static {
theInstance = new WindowHelper();
Utils.forceInit(Window.class);
}
protected WindowHelper() {
}
private static WindowHelper getInstance() {
return theInstance;
}
public static void initHelper(Window window) {
setHelper(window, getInstance());
}
private static WindowHelper getHelper(Window window) {
return windowAccessor.getHelper(window);
}
protected static void setHelper(Window window, WindowHelper windowHelper) {
windowAccessor.setHelper(window, windowHelper);
}
public static void visibleChanging(Window window, boolean visible) {
getHelper(window).visibleChangingImpl(window, visible);
}
public static void visibleChanged(Window window, boolean visible) {
getHelper(window).visibleChangedImpl(window, visible);
}
protected void visibleChangingImpl(Window window, boolean visible) {
windowAccessor.doVisibleChanging(window, visible);
}
protected void visibleChangedImpl(Window window, boolean visible) {
windowAccessor.doVisibleChanged(window, visible);
}
public static TKStage getPeer(Window window) {
return windowAccessor.getPeer(window);
}
public static void setPeer(Window window, TKStage peer) {
windowAccessor.setPeer(window, peer);
}
public static WindowPeerListener getPeerListener(Window window) {
return windowAccessor.getPeerListener(window);
}
public static void setPeerListener(Window window, WindowPeerListener peerListener) {
windowAccessor.setPeerListener(window, peerListener);
}
public static void setFocused(Window window, boolean value) {
windowAccessor.setFocused(window, value);
}
public static void notifyLocationChanged(final Window window,
final double x,
final double y) {
windowAccessor.notifyLocationChanged(window, x, y);
}
public static void notifySizeChanged(final Window window,
final double width,
final double height) {
windowAccessor.notifySizeChanged(window, width, height);
}
public static void notifyScaleChanged(final Window window,
final double newOutputScaleX,
final double newOutputScaleY) {
windowAccessor.notifyScaleChanged(window, newOutputScaleX, newOutputScaleY);
}
@SuppressWarnings("removal")
static AccessControlContext getAccessControlContext(Window window) {
return windowAccessor.getAccessControlContext(window);
}
public static void setWindowAccessor(final WindowAccessor newAccessor) {
if (windowAccessor != null) {
throw new IllegalStateException();
}
windowAccessor = newAccessor;
}
public static WindowAccessor getWindowAccessor() {
return windowAccessor;
}
public interface WindowAccessor {
WindowHelper getHelper(Window window);
void setHelper(Window window, WindowHelper windowHelper);
void doVisibleChanging(Window window, boolean visible);
void doVisibleChanged(Window window, boolean visible);
TKStage getPeer(Window window);
void setPeer(Window window, TKStage peer);
WindowPeerListener getPeerListener(Window window);
void setPeerListener(Window window, WindowPeerListener peerListener);
void setFocused(Window window, boolean value);
void notifyLocationChanged(Window window, double x, double y);
void notifySizeChanged(Window window, double width, double height);
void notifyScreenChanged(Window window, Object from, Object to);
float getPlatformScaleX(Window window);
float getPlatformScaleY(Window window);
void notifyScaleChanged(Window window, double newOutputScaleX, double newOutputScaleY);
ReadOnlyObjectProperty<Screen> screenProperty(Window window);
@SuppressWarnings("removal")
AccessControlContext getAccessControlContext(Window window);
}
}
