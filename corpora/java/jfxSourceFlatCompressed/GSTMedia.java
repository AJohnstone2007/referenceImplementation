package com.sun.media.jfxmediaimpl.platform.gstreamer;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.MediaUtils;
import com.sun.media.jfxmediaimpl.NativeMedia;
import com.sun.media.jfxmediaimpl.platform.Platform;
final class GSTMedia extends NativeMedia {
private final Object markerMutex = new Object();
protected long refNativeMedia;
GSTMedia(Locator locator) {
super(locator);
init();
}
@Override
public Platform getPlatform() {
return GSTPlatform.getPlatformInstance();
}
private void init() {
long[] nativeMediaHandle = new long[1];
MediaError ret;
Locator loc = getLocator();
ret = MediaError.getFromCode(gstInitNativeMedia(loc,
loc.getContentType(), loc.getContentLength(),
nativeMediaHandle));
if (ret != MediaError.ERROR_NONE && ret != MediaError.ERROR_PLATFORM_UNSUPPORTED) {
MediaUtils.nativeError(this, ret);
}
this.refNativeMedia = nativeMediaHandle[0];
}
long getNativeMediaRef() {
return refNativeMedia;
}
@Override
public synchronized void dispose() {
if (0 != refNativeMedia) {
gstDispose(refNativeMedia);
refNativeMedia = 0L;
}
}
private native int gstInitNativeMedia(Locator locator,
String contentType,
long sizeHint,
long[] nativeMediaHandle);
private native void gstDispose(long refNativeMedia);
}
