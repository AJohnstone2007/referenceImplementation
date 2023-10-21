package com.sun.media.jfxmediaimpl.platform.gstreamer;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmediaimpl.MediaUtils;
import com.sun.media.jfxmediaimpl.platform.Platform;
import java.util.Arrays;
public final class GSTPlatform extends Platform {
private static final String[] CONTENT_TYPES = {
"audio/x-aiff",
"audio/mp3",
"audio/mpeg",
"audio/x-wav",
"video/mp4",
"audio/x-m4a",
"video/x-m4v",
"application/vnd.apple.mpegurl",
"audio/mpegurl"
};
private static final String[] PROTOCOLS = {
"file",
"http",
"https",
"jrt",
"resource"
};
private static GSTPlatform globalInstance = null;
@Override
public boolean loadPlatform() {
MediaError ret;
try {
ret = MediaError.getFromCode(gstInitPlatform());
} catch (UnsatisfiedLinkError ule) {
ret = MediaError.ERROR_MANAGER_ENGINEINIT_FAIL;
}
if (ret != MediaError.ERROR_NONE) {
MediaUtils.nativeError(GSTPlatform.class, ret);
}
return true;
}
public static synchronized Platform getPlatformInstance() {
if (null == globalInstance) {
globalInstance = new GSTPlatform();
}
return globalInstance;
}
private GSTPlatform() {}
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
return new GSTMedia(source);
}
@Override
public MediaPlayer createMediaPlayer(Locator source) {
GSTMediaPlayer player;
try {
player = new GSTMediaPlayer(source);
} catch (Exception e) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "GSTPlatform caught exception while creating media player: "+e);
}
return null;
}
if (HostUtils.isMacOSX()) {
String contentType = source.getContentType();
if ("video/mp4".equals(contentType) || "video/x-m4v".equals(contentType)
|| source.getStringLocation().endsWith(".m3u8"))
{
String scheme = source.getURI().getScheme();
final long timeout = (scheme.equals("http") || scheme.equals("https")) ?
60000L : 5000L;
final long iterationTime = 50L;
long timeWaited = 0L;
final Object lock = new Object();
PlayerState state = player.getState();
while (timeWaited < timeout &&
(state == PlayerState.UNKNOWN || state == PlayerState.STALLED)) {
try {
synchronized(lock) {
lock.wait(iterationTime);
timeWaited += iterationTime;
}
} catch (InterruptedException ex) {
}
if (player.isErrorEventCached()) {
break;
}
state = player.getState();
}
if (player.getState() != PlayerState.READY) {
player.dispose();
player = null;
}
}
}
return player;
}
private static native int gstInitPlatform();
}
