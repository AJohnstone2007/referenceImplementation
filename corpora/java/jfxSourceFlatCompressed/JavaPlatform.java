package com.sun.media.jfxmediaimpl.platform.java;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.MetadataParser;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.MediaUtils;
import com.sun.media.jfxmediaimpl.platform.Platform;
public final class JavaPlatform extends Platform {
private static JavaPlatform globalInstance = null;
public static synchronized Platform getPlatformInstance() {
if (null == globalInstance) {
globalInstance = new JavaPlatform();
}
return globalInstance;
}
JavaPlatform() {}
@Override
public boolean loadPlatform() {
return true;
}
@Override
public MetadataParser createMetadataParser(Locator source) {
String contentType = source.getContentType();
if (contentType.equals(MediaUtils.CONTENT_TYPE_MPA) ||
contentType.equals(MediaUtils.CONTENT_TYPE_MP3)) {
return new ID3MetadataParser(source);
}
return null;
}
@Override
public Media createMedia(Locator source) {
return null;
}
@Override
public MediaPlayer createMediaPlayer(Locator source) {
return null;
}
}
