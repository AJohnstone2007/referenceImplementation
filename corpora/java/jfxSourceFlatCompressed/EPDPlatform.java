package com.sun.glass.ui.monocle;
class EPDPlatform extends LinuxPlatform {
EPDPlatform() {
EPDSystem.getEPDSystem().loadLibrary();
}
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
return new EPDInputDeviceRegistry(false);
}
@Override
protected NativeScreen createScreen() {
try {
return new EPDScreen();
} catch (RuntimeException e) {
return new HeadlessScreen();
}
}
}
