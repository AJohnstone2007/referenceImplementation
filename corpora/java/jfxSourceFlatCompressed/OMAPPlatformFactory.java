package com.sun.glass.ui.monocle;
import java.io.File;
class OMAPPlatformFactory extends NativePlatformFactory {
@Override
protected boolean matches() {
return new File("/sys/devices/omapdss").exists();
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
return new OMAPPlatform();
}
}
