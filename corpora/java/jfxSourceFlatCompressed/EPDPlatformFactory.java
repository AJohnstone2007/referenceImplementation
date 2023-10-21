package com.sun.glass.ui.monocle;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.Logging;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
class EPDPlatformFactory extends NativePlatformFactory {
private static final int MAJOR_VERSION = 1;
private static final int MINOR_VERSION = 0;
private static final String FB_FILE = "/proc/fb";
private static final String FB_NAME = "mxc_epdc_fb";
private final PlatformLogger logger = Logging.getJavaFXLogger();
EPDPlatformFactory() {
}
@Override
protected boolean matches() {
@SuppressWarnings("removal")
String fbinfo = AccessController.doPrivileged((PrivilegedAction<String>) () -> {
String line = null;
try (var reader = new BufferedReader(new FileReader(FB_FILE))) {
line = reader.readLine();
} catch (IOException e) {
logger.severe("Failed reading " + FB_FILE, e);
}
return line;
});
return fbinfo != null && fbinfo.contains(FB_NAME);
}
@Override
protected NativePlatform createNativePlatform() {
return new EPDPlatform();
}
@Override
protected int getMajorVersion() {
return MAJOR_VERSION;
}
@Override
protected int getMinorVersion() {
return MINOR_VERSION;
}
@Override
public String toString() {
return MessageFormat.format("{0}[majorVersion={1} minorVersion={2} matches=\"{3} in {4}\"]",
getClass().getName(), getMajorVersion(), getMinorVersion(), FB_NAME, FB_FILE);
}
}
