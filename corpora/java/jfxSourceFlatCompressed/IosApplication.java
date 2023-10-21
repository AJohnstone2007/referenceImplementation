package com.sun.glass.ui.ios;
import com.sun.glass.ui.*;
import com.sun.glass.ui.CommonDialogs.ExtensionFilter;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
public final class IosApplication extends Application {
private static native void _initIDs();
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
Application.loadNativeLibrary();
return null;
});
_initIDs();
}
@Override
protected void runLoop(final Runnable launchable) {
ClassLoader ccl = IosApplication.class.getClassLoader();
_runLoop(launchable, ccl);
}
private native void _runLoop(Runnable launchable, ClassLoader contextClassLoader);
@Override
protected void finishTerminating() {
setEventThread(null);
super.finishTerminating();
}
private void setEventThread() {
setEventThread(Thread.currentThread());
}
@Override
public Window createWindow(Window owner, Screen screen, int styleMask) {
return new IosWindow(owner, screen, styleMask);
}
@Override
public View createView() {
return new IosView();
}
@Override
public Cursor createCursor(int type) {
return new IosCursor(type);
}
@Override
public Cursor createCursor(int x, int y, Pixels pixels) {
return new IosCursor(x, y, pixels);
}
@Override
protected void staticCursor_setVisible(boolean visible) { }
@Override
protected Size staticCursor_getBestSize(int width, int height) {
return null;
}
@Override
public Pixels createPixels(int width, int height, ByteBuffer data) {
return new IosPixels(width, height, data);
}
@Override
public Pixels createPixels(int width, int height, IntBuffer data) {
return new IosPixels(width, height, data);
}
@Override
public Pixels createPixels(int width, int height, IntBuffer data, float scalex, float scaley) {
return new IosPixels(width, height, data, scalex, scaley);
}
@Override
protected int staticPixels_getNativeFormat() {
return 0;
}
@Override
public GlassRobot createRobot() {
return new IosRobot();
}
@Override
protected native double staticScreen_getVideoRefreshPeriod();
@Override
protected native Screen[] staticScreen_getScreens();
@Override
public Timer createTimer(Runnable runnable) {
return new IosTimer(runnable);
}
@Override
protected int staticTimer_getMinPeriod() {
return IosTimer.getMinPeriod_impl();
}
@Override
protected int staticTimer_getMaxPeriod() {
return IosTimer.getMaxPeriod_impl();
}
@Override
protected FileChooserResult staticCommonDialogs_showFileChooser(Window owner, String folder, String filename, String title, int type, boolean multipleMode, ExtensionFilter[] extensionFilters, int defaultFilterIndex) {
return new FileChooserResult();
}
@Override
protected File staticCommonDialogs_showFolderChooser(Window owner, String folder, String title) {
return null;
}
private native Object _enterNestedEventLoopImpl();
private native void _leaveNestedEventLoopImpl(Object retValue);
@Override
protected Object _enterNestedEventLoop() {
return _enterNestedEventLoopImpl();
}
@Override
protected void _leaveNestedEventLoop(Object retValue) {
_leaveNestedEventLoopImpl(retValue);
}
@Override
protected long staticView_getMultiClickTime() {
return IosView._getMultiClickTime();
}
@Override
protected int staticView_getMultiClickMaxX() {
return IosView._getMultiClickMaxX();
}
@Override
protected int staticView_getMultiClickMaxY() {
return IosView._getMultiClickMaxY();
}
@Override
native protected void _invokeAndWait(Runnable runnable);
@Override
native protected void _invokeLater(Runnable runnable);
@Override
protected boolean _supportsTransparentWindows() {
return true;
}
@Override protected boolean _supportsUnifiedWindows() {
return false;
}
public native static void _setStatusBarHidden(boolean hidden);
public native static void _setStatusBarHiddenWithAnimation(boolean hidden, int animation);
public native static void _setStatusBarOrientationAnimated(int interfaceOrientation, boolean animated);
public native static void _setStatusBarStyleAnimated(int statusBarStyle, boolean animated);
public native static boolean _getStatusBarHidden();
public native static int _getStatusBarStyle();
public native static int _getStatusBarOrientation();
@Override
public boolean hasTouch() {
return true;
}
@Override
public boolean hasMultiTouch() {
return true;
}
@Override
protected native int _getKeyCodeForChar(char c);
}
