package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class X11PlatformFactory extends NativePlatformFactory {
@Override
protected boolean matches() {
@SuppressWarnings("removal")
String display = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getenv("DISPLAY"));
return display != null;
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
return new X11Platform();
}
}
