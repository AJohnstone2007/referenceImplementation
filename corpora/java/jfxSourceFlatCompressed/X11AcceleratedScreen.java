package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class X11AcceleratedScreen extends AcceleratedScreen {
private static X xLib = X.getX();
private X.XDisplay nativeDisplay;
X11AcceleratedScreen(int[] attributes) throws GLException {
super(attributes);
}
@Override
protected long platformGetNativeDisplay() {
if (nativeDisplay == null) {
@SuppressWarnings("removal")
boolean doMaliWorkaround =
AccessController.doPrivileged(
(PrivilegedAction<Boolean>) () ->
Boolean.getBoolean(
"monocle.maliSignedStruct"));
X.XDisplay display = new X.XDisplay(xLib.XOpenDisplay(null));
if (doMaliWorkaround) {
long address = 0x7000000;
nativeDisplay = new X.XDisplay(
ls.mmap(address, display.sizeof(),
LinuxSystem.PROT_READ | LinuxSystem.PROT_WRITE,
LinuxSystem.MAP_PRIVATE
| LinuxSystem.MAP_ANONYMOUS,
-1, 0)
);
ls.memcpy(nativeDisplay.p, display.p, display.sizeof());
} else {
nativeDisplay = display;
}
}
return nativeDisplay.p;
}
@Override
protected long platformGetNativeWindow() {
return NativePlatformFactory.getNativePlatform()
.getScreen().getNativeHandle();
}
@Override
public boolean swapBuffers() {
xLib.XLockDisplay(nativeDisplay.p);
super.swapBuffers();
NativeCursor cursor = NativePlatformFactory.getNativePlatform().getCursor();
if (cursor instanceof X11WarpingCursor) {
((X11WarpingCursor) cursor).warp();
}
xLib.XUnlockDisplay(nativeDisplay.p);
return true;
}
}
