package com.sun.glass.ui.monocle;
class VNCPlatform extends HeadlessPlatform {
@Override
protected InputDeviceRegistry createInputDeviceRegistry() {
InputDeviceRegistry registry = new InputDeviceRegistry() {
{
devices.add(new InputDevice() {
@Override public boolean isTouch() { return true; }
@Override public boolean isMultiTouch() { return false; }
@Override public boolean isRelative() { return false; }
@Override public boolean is5Way() { return false; }
@Override public boolean isFullKeyboard() { return false; }
});
}
};
return registry;
}
@Override
protected NativeScreen createScreen() {
return new VNCScreen();
}
}
