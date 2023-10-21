package com.sun.glass.ui.monocle;
import com.sun.glass.utils.NativeLibLoader;
class AndroidPlatform extends NativePlatform {
AndroidPlatform() {
NativeLibLoader.loadLibrary("glass_monocle");
}
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
return AndroidInputDeviceRegistry.getInstance();
}
@Override
protected NativeCursor createCursor() {
return logSelectedCursor(new NullCursor());
}
@Override
protected NativeScreen createScreen() {
return new AndroidScreen();
}
@Override
public synchronized AcceleratedScreen getAcceleratedScreen(
int[] attributes) throws GLException {
if (accScreen == null) {
accScreen = new AndroidAcceleratedScreen(attributes);
}
return accScreen;
}
}
