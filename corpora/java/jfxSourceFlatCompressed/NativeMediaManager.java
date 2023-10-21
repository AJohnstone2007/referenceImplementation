package com.sun.media.jfxmediaimpl;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.media.jfxmedia.*;
import com.sun.media.jfxmedia.events.MediaErrorListener;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.platform.PlatformManager;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.WeakHashMap;
public class NativeMediaManager {
private static boolean isNativeLayerInitialized = false;
private final List<WeakReference<MediaErrorListener>> errorListeners =
new ArrayList();
private final static NativeMediaPlayerDisposer playerDisposer =
new NativeMediaPlayerDisposer();
private final static Map<MediaPlayer,Boolean> allMediaPlayers =
new WeakHashMap();
private final List<String> supportedContentTypes =
new ArrayList();
private final List<String> supportedProtocols =
new ArrayList<>();
private static class NativeMediaManagerInitializer {
private static final NativeMediaManager globalInstance
= new NativeMediaManager();
}
public static NativeMediaManager getDefaultInstance() {
return NativeMediaManagerInitializer.globalInstance;
}
@SuppressWarnings("removal")
protected NativeMediaManager() {
try {
AccessController.doPrivileged((PrivilegedExceptionAction) () -> {
ArrayList<String> dependencies = new ArrayList<>();
if (HostUtils.isWindows() || HostUtils.isMacOSX()) {
NativeLibLoader.loadLibrary("glib-lite");
}
if (!HostUtils.isLinux() && !HostUtils.isIOS()) {
NativeLibLoader.loadLibrary("gstreamer-lite");
} else {
dependencies.add("gstreamer-lite");
}
if (HostUtils.isLinux()) {
dependencies.add("fxplugins");
dependencies.add("avplugin");
dependencies.add("avplugin-54");
dependencies.add("avplugin-56");
dependencies.add("avplugin-57");
dependencies.add("avplugin-ffmpeg-56");
dependencies.add("avplugin-ffmpeg-57");
dependencies.add("avplugin-ffmpeg-58");
}
if (HostUtils.isMacOSX()) {
dependencies.add("fxplugins");
dependencies.add("glib-lite");
dependencies.add("jfxmedia_avf");
}
if (HostUtils.isWindows()) {
dependencies.add("fxplugins");
dependencies.add("glib-lite");
}
NativeLibLoader.loadLibrary("jfxmedia", dependencies);
return null;
});
} catch (PrivilegedActionException pae) {
MediaUtils.error(null, MediaError.ERROR_MANAGER_ENGINEINIT_FAIL.code(),
"Unable to load one or more dependent libraries.", pae);
}
if (!Logger.initNative()) {
MediaUtils.error(null, MediaError.ERROR_MANAGER_LOGGER_INIT.code(),
"Unable to init logger", null);
}
}
synchronized static void initNativeLayer() {
if (!isNativeLayerInitialized) {
PlatformManager.getManager().loadPlatforms();
isNativeLayerInitialized = true;
}
}
private synchronized void loadContentTypes() {
if (!supportedContentTypes.isEmpty()) {
return;
}
List<String> npt = PlatformManager.getManager().getSupportedContentTypes();
if (null != npt && !npt.isEmpty()) {
supportedContentTypes.addAll(npt);
}
if (Logger.canLog(Logger.DEBUG)) {
StringBuilder sb = new StringBuilder("JFXMedia supported content types:\n");
for (String type : supportedContentTypes) {
sb.append("    ");
sb.append(type);
sb.append("\n");
}
Logger.logMsg(Logger.DEBUG, sb.toString());
}
}
private synchronized void loadProtocols() {
if (!supportedProtocols.isEmpty()) {
return;
}
List<String> npt = PlatformManager.getManager().getSupportedProtocols();
if (null != npt && !npt.isEmpty()) {
supportedProtocols.addAll(npt);
}
if (Logger.canLog(Logger.DEBUG)) {
StringBuilder sb = new StringBuilder("JFXMedia supported protocols:\n");
for (String type : supportedProtocols) {
sb.append("    ");
sb.append(type);
sb.append("\n");
}
Logger.logMsg(Logger.DEBUG, sb.toString());
}
}
public boolean canPlayContentType(String contentType) {
if (contentType == null) {
throw new IllegalArgumentException("contentType == null!");
}
if (supportedContentTypes.isEmpty()) {
loadContentTypes();
}
for (String type : supportedContentTypes) {
if (contentType.equalsIgnoreCase(type)) {
return true;
}
}
return false;
}
public String[] getSupportedContentTypes() {
if (supportedContentTypes.isEmpty()) {
loadContentTypes();
}
return supportedContentTypes.toArray(new String[1]);
}
public boolean canPlayProtocol(String protocol) {
if (protocol == null) {
throw new IllegalArgumentException("protocol == null!");
}
if (supportedProtocols.isEmpty()) {
loadProtocols();
}
for (String type : supportedProtocols) {
if (protocol.equalsIgnoreCase(type)) {
return true;
}
}
return false;
}
public static MetadataParser getMetadataParser(Locator locator) {
return PlatformManager.getManager().createMetadataParser(locator);
}
public MediaPlayer getPlayer(Locator locator) {
initNativeLayer();
MediaPlayer player = PlatformManager.getManager().createMediaPlayer(locator);
if (null == player) {
throw new MediaException("Could not create player!");
}
allMediaPlayers.put(player, Boolean.TRUE);
return player;
}
public Media getMedia(Locator locator) {
initNativeLayer();
return PlatformManager.getManager().createMedia(locator);
}
public void addMediaErrorListener(MediaErrorListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l == null) {
it.remove();
}
}
this.errorListeners.add(new WeakReference<MediaErrorListener>(listener));
}
}
public void removeMediaErrorListener(MediaErrorListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
public static void registerMediaPlayerForDispose(Object obj, MediaPlayer player) {
MediaDisposer.addResourceDisposer(obj, player, playerDisposer);
}
public List<MediaPlayer> getAllMediaPlayers() {
List<MediaPlayer> allPlayers = null;
if (!allMediaPlayers.isEmpty()) {
allPlayers = new ArrayList<MediaPlayer>(allMediaPlayers.keySet());
}
return allPlayers;
}
List<WeakReference<MediaErrorListener>> getMediaErrorListeners() {
return this.errorListeners;
}
private static class NativeMediaPlayerDisposer implements MediaDisposer.ResourceDisposer {
public void disposeResource(Object resource) {
MediaPlayer player = (MediaPlayer) resource;
if (player != null) {
player.dispose();
}
}
}
}
