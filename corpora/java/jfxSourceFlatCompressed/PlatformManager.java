package com.sun.media.jfxmediaimpl.platform;
import com.sun.javafx.PlatformUtil;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.MetadataParser;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.platform.java.JavaPlatform;
import com.sun.media.jfxmediaimpl.HostUtils;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
public final class PlatformManager {
private static String enabledPlatforms;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction) () -> {
getPlatformSettings();
return null;
});
}
private static void getPlatformSettings() {
enabledPlatforms = System.getProperty("jfxmedia.platforms", "").toLowerCase();
}
private static boolean isPlatformEnabled(String name) {
if (null == enabledPlatforms || enabledPlatforms.length() == 0) {
return true;
}
return (enabledPlatforms.indexOf(name.toLowerCase()) != -1);
}
private static final class PlatformManagerInitializer {
private static final PlatformManager globalInstance = new PlatformManager();
}
public static PlatformManager getManager() {
return PlatformManagerInitializer.globalInstance;
}
private final List<Platform> platforms;
private PlatformManager() {
platforms = new ArrayList<>();
Platform platty;
if (isPlatformEnabled("JavaPlatform")) {
platty = JavaPlatform.getPlatformInstance();
if (null != platty) {
platforms.add(platty);
}
}
if (!HostUtils.isIOS() && isPlatformEnabled("GSTPlatform")) {
platty = getPlatformInstance(
"com.sun.media.jfxmediaimpl.platform.gstreamer.GSTPlatform");
if (null != platty) {
platforms.add(platty);
}
}
if (HostUtils.isMacOSX() && isPlatformEnabled("OSXPlatform")) {
platty = getPlatformInstance(
"com.sun.media.jfxmediaimpl.platform.osx.OSXPlatform");
if (null != platty) {
platforms.add(platty);
}
}
if (HostUtils.isIOS() && isPlatformEnabled("IOSPlatform")) {
platty = getPlatformInstance(
"com.sun.media.jfxmediaimpl.platform.ios.IOSPlatform");
if (null != platty) {
platforms.add(platty);
}
}
if (Logger.canLog(Logger.DEBUG)) {
StringBuilder sb = new StringBuilder("Enabled JFXMedia platforms: ");
for (Platform p : platforms) {
sb.append("\n   - ");
sb.append(p.getClass().getName());
}
Logger.logMsg(Logger.DEBUG, sb.toString());
}
}
private Platform getPlatformInstance(String platformClass) {
try {
Class c = Class.forName(platformClass);
Method m = c.getDeclaredMethod("getPlatformInstance", (Class[])null);
Object platform = m.invoke(null, (Object[])null);
return (Platform)platform;
} catch (Exception e) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Failed to get platform instance" +
" for " + platformClass + ". Exception: " +
e.getMessage());
}
}
return null;
}
public synchronized void loadPlatforms() {
Iterator<Platform> iter = platforms.iterator();
while (iter.hasNext()) {
Platform platty = iter.next();
if (!platty.loadPlatform()) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Failed to load platform: "+platty);
}
iter.remove();
}
}
}
public List<String> getSupportedContentTypes() {
ArrayList<String> outTypes = new ArrayList<String>();
if (!platforms.isEmpty()) {
for (Platform platty : platforms) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Getting content types from platform: "+platty);
}
String[] npt = platty.getSupportedContentTypes();
if (npt != null) {
for (String type : npt) {
if (!outTypes.contains(type)) {
outTypes.add(type);
}
}
}
}
}
return outTypes;
}
public List<String> getSupportedProtocols() {
ArrayList<String> outProtocols = new ArrayList<String>();
if (!platforms.isEmpty()) {
for (Platform platty : platforms) {
String[] npt = platty.getSupportedProtocols();
if (npt != null) {
for (String p : npt) {
if (!outProtocols.contains(p)) {
outProtocols.add(p);
}
}
}
}
}
if (PlatformUtil.isStaticBuild()) {
outProtocols.add("resource");
}
return outProtocols;
}
public MetadataParser createMetadataParser(Locator source) {
for (Platform platty : platforms) {
MetadataParser parser = platty.createMetadataParser(source);
if (parser != null) {
return parser;
}
}
return null;
}
public Media createMedia(Locator source) {
String mimeType = source.getContentType();
String protocol = source.getProtocol();
for (Platform platty : platforms) {
if (platty.canPlayContentType(mimeType) && platty.canPlayProtocol(protocol)) {
Media outMedia = platty.createMedia(source);
if (null != outMedia) {
return outMedia;
}
}
}
return null;
}
public MediaPlayer createMediaPlayer(Locator source) {
String mimeType = source.getContentType();
String protocol = source.getProtocol();
for (Platform platty : platforms) {
if (platty.canPlayContentType(mimeType) && platty.canPlayProtocol(protocol)) {
MediaPlayer outPlayer = platty.createMediaPlayer(source);
if (null != outPlayer) {
return outPlayer;
}
}
}
return null;
}
}
