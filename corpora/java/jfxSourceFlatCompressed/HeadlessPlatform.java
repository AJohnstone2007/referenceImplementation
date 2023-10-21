package com.sun.glass.ui.monocle;
class HeadlessPlatform extends NativePlatform {
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
return new LinuxInputDeviceRegistry(true);
}
@Override
protected NativeCursor createCursor() {
return logSelectedCursor(new NullCursor());
}
@Override
protected NativeScreen createScreen() {
return new HeadlessScreen();
}
}
