package com.sun.media.jfxmediaimpl.platform;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.MetadataParser;
import com.sun.media.jfxmedia.locator.Locator;
public abstract class Platform {
public static Platform getPlatformInstance() {
throw new UnsupportedOperationException("Invalid platform class.");
}
public boolean loadPlatform() {
return false;
}
public boolean canPlayContentType(String contentType) {
String[] contentTypes = getSupportedContentTypes();
if (contentTypes != null) {
for (String type : contentTypes) {
if (type.equalsIgnoreCase(contentType)) {
return true;
}
}
}
return false;
}
public boolean canPlayProtocol(String protocol) {
String[] protocols = getSupportedProtocols();
if (protocols != null) {
for (String p : protocols) {
if (p.equalsIgnoreCase(protocol)) {
return true;
}
}
}
return false;
}
public String[] getSupportedContentTypes() {
return new String[0];
}
public String[] getSupportedProtocols() {
return new String[0];
}
public MetadataParser createMetadataParser(Locator source) {
return null;
}
public abstract Media createMedia(Locator source);
public abstract MediaPlayer createMediaPlayer(Locator source);
}
