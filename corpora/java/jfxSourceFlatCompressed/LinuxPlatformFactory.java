package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class LinuxPlatformFactory extends NativePlatformFactory {
@Override
protected boolean matches() {
@SuppressWarnings("removal")
String os = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty("os.name"));
return os != null && os.equals("Linux");
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
return new LinuxPlatform();
}
}
