package com.sun.glass.ui.monocle;
class OMAPPlatform extends LinuxPlatform {
@Override
protected NativeCursor createCursor() {
final NativeCursor c = useCursor ? new OMAPCursor() : new NullCursor();
return logSelectedCursor(c);
}
@Override
protected NativeScreen createScreen() {
return new OMAPScreen();
}
}
