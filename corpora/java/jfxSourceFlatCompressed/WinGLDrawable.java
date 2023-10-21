package com.sun.prism.es2;
class WinGLDrawable extends GLDrawable {
private static native long nCreateDrawable(long nativeWindow, long nativeCtxInfo);
private static native long nGetDummyDrawable(long nativeCtxInfo);
private static native boolean nSwapBuffers(long nativeDInfo);
WinGLDrawable(GLPixelFormat pixelFormat) {
super(0L, pixelFormat);
long nDInfo = nGetDummyDrawable(pixelFormat.getNativePFInfo());
setNativeDrawableInfo(nDInfo);
}
WinGLDrawable(long nativeWindow, GLPixelFormat pixelFormat) {
super(nativeWindow, pixelFormat);
long nDInfo = nCreateDrawable(nativeWindow, pixelFormat.getNativePFInfo());
setNativeDrawableInfo(nDInfo);
}
@Override
boolean swapBuffers(GLContext glCtx) {
return nSwapBuffers(getNativeDrawableInfo());
}
}
