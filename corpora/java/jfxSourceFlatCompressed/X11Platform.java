package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class X11Platform extends NativePlatform {
private final boolean x11Input;
@SuppressWarnings("removal")
X11Platform() {
LinuxSystem.getLinuxSystem().loadLibrary();
x11Input = AccessController.doPrivileged((PrivilegedAction<Boolean>)
() -> Boolean.getBoolean("x11.input"));
}
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
if (x11Input) {
return new X11InputDeviceRegistry();
} else {
return new LinuxInputDeviceRegistry(false);
}
}
@Override
protected NativeCursor createCursor() {
if (useCursor) {
final NativeCursor c = x11Input ? new X11Cursor() : new X11WarpingCursor();
return logSelectedCursor(c);
} else {
return logSelectedCursor(new NullCursor());
}
}
@Override
protected NativeScreen createScreen() {
return new X11Screen(x11Input);
}
@Override
public synchronized AcceleratedScreen getAcceleratedScreen(
int[] attributes) throws GLException {
if (accScreen == null) {
accScreen = new X11AcceleratedScreen(attributes);
}
return accScreen;
}
}
