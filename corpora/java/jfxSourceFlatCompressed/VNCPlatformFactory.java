package com.sun.glass.ui.monocle;
class VNCPlatformFactory extends NativePlatformFactory {
@Override
protected boolean matches() {
return true;
}
@Override
protected int getMajorVersion() {
return 1;
}
@Override
protected int getMinorVersion() {
return 0;
}
@Override
protected NativePlatform createNativePlatform() {
return new VNCPlatform();
}
}
