package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class LinuxArch {
@SuppressWarnings("removal")
private static final int bits = AccessController.doPrivileged((PrivilegedAction<Integer>) () -> {
LinuxSystem system = LinuxSystem.getLinuxSystem();
return (int) system.sysconf(LinuxSystem._SC_LONG_BIT);
});;
static boolean is64Bit() {
return bits == 64;
}
static int getBits() {
return bits;
}
}
