package com.sun.glass.ui.monocle;
class DispmanPlatform extends LinuxPlatform {
@Override
protected NativeCursor createCursor() {
final NativeCursor c = useCursor ? new DispmanCursor() : new NullCursor();
return logSelectedCursor(c);
}
@Override
protected NativeScreen createScreen() {
return new DispmanScreen();
}
@Override public synchronized AcceleratedScreen getAcceleratedScreen(int[] attributes)
throws GLException{
if (accScreen == null) {
accScreen = new DispmanAcceleratedScreen(attributes);
}
return accScreen;
}
}
