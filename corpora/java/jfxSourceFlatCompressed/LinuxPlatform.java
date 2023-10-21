package com.sun.glass.ui.monocle;
class LinuxPlatform extends NativePlatform {
LinuxPlatform() {
LinuxSystem.getLinuxSystem().loadLibrary();
}
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
return new LinuxInputDeviceRegistry(false);
}
@Override
protected NativeCursor createCursor() {
final NativeCursor c = useCursor ? new SoftwareCursor() : new NullCursor();
return logSelectedCursor(c);
}
@Override
protected NativeScreen createScreen() {
try {
return new FBDevScreen();
} catch (RuntimeException e) {
return new HeadlessScreen();
}
}
}
