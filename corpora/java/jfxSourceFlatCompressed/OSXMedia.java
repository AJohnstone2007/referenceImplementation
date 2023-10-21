package com.sun.media.jfxmediaimpl.platform.osx;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.NativeMedia;
import com.sun.media.jfxmediaimpl.platform.Platform;
final class OSXMedia extends NativeMedia {
OSXMedia(Locator source) {
super(source);
}
@Override
public Platform getPlatform() {
return OSXPlatform.getPlatformInstance();
}
@Override
public void dispose() {
}
}
