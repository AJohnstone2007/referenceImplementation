package com.sun.media.jfxmedia;
import com.sun.media.jfxmedia.events.MediaErrorListener;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.NativeMediaManager;
import java.util.List;
public class MediaManager {
private MediaManager() {
}
public static String[] getSupportedContentTypes() {
return NativeMediaManager.getDefaultInstance().getSupportedContentTypes();
}
public static boolean canPlayContentType(String contentType) {
if (contentType == null) {
throw new IllegalArgumentException("contentType == null!");
}
return NativeMediaManager.getDefaultInstance().canPlayContentType(contentType);
}
public static boolean canPlayProtocol(String protocol) {
if (protocol == null) {
throw new IllegalArgumentException("protocol == null!");
}
return NativeMediaManager.getDefaultInstance().canPlayProtocol(protocol);
}
public static MetadataParser getMetadataParser(Locator locator) {
if (locator == null) {
throw new IllegalArgumentException("locator == null!");
}
return NativeMediaManager.getDefaultInstance().getMetadataParser(locator);
}
public static Media getMedia(Locator locator) {
if (locator == null) {
throw new IllegalArgumentException("locator == null!");
}
return NativeMediaManager.getDefaultInstance().getMedia(locator);
}
public static MediaPlayer getPlayer(Locator locator) {
if (locator == null) {
throw new IllegalArgumentException("locator == null!");
}
return NativeMediaManager.getDefaultInstance().getPlayer(locator);
}
public static void addMediaErrorListener(MediaErrorListener listener) {
if (listener == null) {
throw new IllegalArgumentException("listener == null!");
}
NativeMediaManager.getDefaultInstance().addMediaErrorListener(listener);
}
public static void removeMediaErrorListener(MediaErrorListener listener) {
if (listener == null) {
throw new IllegalArgumentException("listener == null!");
}
NativeMediaManager.getDefaultInstance().removeMediaErrorListener(listener);
}
public static void registerMediaPlayerForDispose(Object obj, MediaPlayer player) {
NativeMediaManager.registerMediaPlayerForDispose(obj, player);
}
public static List<MediaPlayer> getAllMediaPlayers() {
return NativeMediaManager.getDefaultInstance().getAllMediaPlayers();
}
}
