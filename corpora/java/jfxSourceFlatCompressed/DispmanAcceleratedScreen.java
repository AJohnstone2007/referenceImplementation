package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class DispmanAcceleratedScreen extends AcceleratedScreen {
DispmanAcceleratedScreen(int[] attributes) throws GLException {
super(attributes);
}
private native long _platformGetNativeWindow(int displayID, int layerID);
@Override
protected long platformGetNativeWindow() {
@SuppressWarnings("removal")
int displayID = AccessController.doPrivileged(
(PrivilegedAction<Integer>)
() -> Integer.getInteger("dispman.display", 0 ));
@SuppressWarnings("removal")
int layerID = AccessController.doPrivileged(
(PrivilegedAction<Integer>)
() -> Integer.getInteger("dispman.layer", 1));
return _platformGetNativeWindow(displayID, layerID);
}
}
