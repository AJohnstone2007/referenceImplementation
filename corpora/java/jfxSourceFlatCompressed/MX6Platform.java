package com.sun.glass.ui.monocle;
class MX6Platform extends LinuxPlatform {
@Override
protected NativeCursor createCursor() {
final NativeCursor c = useCursor ? new MX6Cursor() : new NullCursor();
return logSelectedCursor(c);
}
@Override
public synchronized AcceleratedScreen getAcceleratedScreen(int[] attributes)
throws GLException {
if (accScreen == null) {
accScreen = new MX6AcceleratedScreen(attributes);
}
return accScreen;
}
}
