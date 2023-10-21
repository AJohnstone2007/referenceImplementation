package com.sun.prism.es2;
import java.lang.annotation.Native;
import com.sun.glass.ui.monocle.AcceleratedScreen;
class MonocleGLContext extends GLContext {
@Native private AcceleratedScreen accScreen;
MonocleGLContext(long nativeCtxInfo) {
this.nativeCtxInfo = nativeCtxInfo;
}
MonocleGLContext(GLDrawable drawable, GLPixelFormat pixelFormat,
boolean vSyncRequest, AcceleratedScreen accScreen,
long nativeCtxInfo) {
this.accScreen = accScreen;
this.nativeCtxInfo = nativeCtxInfo;
}
@Override
long getNativeHandle() {
return 0l;
}
@Override
void makeCurrent(GLDrawable drawable) {
if (drawable != null) {
accScreen.enableRendering(true);
} else {
accScreen.enableRendering(false);
}
}
}
