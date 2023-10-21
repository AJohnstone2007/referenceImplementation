package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class AndroidAcceleratedScreen extends AcceleratedScreen {
AndroidAcceleratedScreen(int[] attributes) throws GLException {
super(attributes);
}
boolean initPlatformLibraries() {
return super.initPlatformLibraries();
}
@Override
protected long platformGetNativeDisplay() {
return 0;
}
@Override
protected long platformGetNativeWindow() {
long answer = NativePlatformFactory.getNativePlatform()
.getScreen().getNativeHandle();
return answer;
}
}
