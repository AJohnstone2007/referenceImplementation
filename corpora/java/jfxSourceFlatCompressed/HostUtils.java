package com.sun.media.jfxmediaimpl;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class HostUtils {
private static String osName;
private static String osArch;
private static boolean embedded;
private static boolean is64bit = false;
static {
@SuppressWarnings("removal")
boolean tmp = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
osName = System.getProperty("os.name").toLowerCase();
osArch = System.getProperty("os.arch").toLowerCase();
is64bit = osArch.equals("x64")
|| osArch.equals("x86_64")
|| osArch.equals("ia64");
return Boolean.getBoolean("com.sun.javafx.isEmbedded");
});
embedded = tmp;
}
public static boolean is64Bit() {
return is64bit;
}
public static boolean isWindows() {
return osName.startsWith("windows");
}
public static boolean isMacOSX() {
return osName.startsWith("mac os x");
}
public static boolean isLinux() {
return osName.startsWith("linux");
}
public static boolean isIOS() {
return osName.startsWith("ios");
}
public static boolean isEmbedded() {
return embedded;
}
}
