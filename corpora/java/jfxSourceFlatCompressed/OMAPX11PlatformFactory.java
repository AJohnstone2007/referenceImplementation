package com.sun.glass.ui.monocle;
class OMAPX11PlatformFactory extends OMAPPlatformFactory {
@Override
protected NativePlatform createNativePlatform() {
return new OMAPX11Platform();
}
@Override
protected int getMajorVersion() {
return 1;
}
@Override
protected int getMinorVersion() {
return 0;
}
}
