package com.sun.media.jfxmediaimpl.platform.osx;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmediaimpl.platform.Platform;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
public final class OSXPlatform extends Platform {
private static final String[] CONTENT_TYPES = {
"audio/x-aiff",
"audio/mp3",
"audio/mpeg",
"audio/x-m4a",
"video/mp4",
"video/x-m4v",
"application/vnd.apple.mpegurl",
"audio/mpegurl"
};
private static final String[] PROTOCOLS = {
"file",
"http",
"https"
};
private static final class OSXPlatformInitializer {
private static final OSXPlatform globalInstance;
static {
boolean isLoaded = false;
try {
@SuppressWarnings("removal")
boolean tmp = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
boolean avf = false;
try {
NativeLibLoader.loadLibrary("jfxmedia_avf");
avf = true;
} catch (UnsatisfiedLinkError ule) {}
return avf;
});
isLoaded = tmp;
} catch (Exception e) {
}
if (isLoaded) {
globalInstance = new OSXPlatform();
} else {
globalInstance = null;
}
}
}
public static Platform getPlatformInstance() {
return OSXPlatformInitializer.globalInstance;
}
private OSXPlatform() {
}
@Override
public boolean loadPlatform() {
if (!HostUtils.isMacOSX()) {
return false;
}
try {
return osxPlatformInit();
} catch (UnsatisfiedLinkError ule) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Unable to load OSX platform.");
}
return false;
}
}
@Override
public String[] getSupportedContentTypes() {
return Arrays.copyOf(CONTENT_TYPES, CONTENT_TYPES.length);
}
@Override
public String[] getSupportedProtocols() {
return Arrays.copyOf(PROTOCOLS, PROTOCOLS.length);
}
@Override
public Media createMedia(Locator source) {
return new OSXMedia(source);
}
@Override
public MediaPlayer createMediaPlayer(Locator source) {
try {
return new OSXMediaPlayer(source);
} catch (Exception ex) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "OSXPlatform caught exception while creating media player: "+ex);
ex.printStackTrace();
}
}
return null;
}
private static native boolean osxPlatformInit();
}
