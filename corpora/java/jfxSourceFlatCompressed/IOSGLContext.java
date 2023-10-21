package com.sun.prism.es2;
import com.sun.prism.impl.PrismSettings;
class IOSGLContext extends GLContext {
private static native long nInitialize(long nativeDInfo, long nativePFInfo,
long nativeshareCtxHandle, boolean vSyncRequest);
private static native long nGetNativeHandle(long nativeCtxInfo);
private static native void nMakeCurrent(long nativeCtxInfo, long nativeDInfo);
IOSGLContext(long nativeCtxInfo) {
this.nativeCtxInfo = nativeCtxInfo;
}
IOSGLContext(GLDrawable drawable, GLPixelFormat pixelFormat, GLContext shareCtx, boolean vSyncRequest) {
int attrArr[] = new int[GLPixelFormat.Attributes.NUM_ITEMS];
GLPixelFormat.Attributes attrs = pixelFormat.getAttributes();
attrArr[GLPixelFormat.Attributes.RED_SIZE] = attrs.getRedSize();
attrArr[GLPixelFormat.Attributes.GREEN_SIZE] = attrs.getGreenSize();
attrArr[GLPixelFormat.Attributes.BLUE_SIZE] = attrs.getBlueSize();
attrArr[GLPixelFormat.Attributes.ALPHA_SIZE] = attrs.getAlphaSize();
attrArr[GLPixelFormat.Attributes.DEPTH_SIZE] = attrs.getDepthSize();
attrArr[GLPixelFormat.Attributes.DOUBLEBUFFER] = attrs.isDoubleBuffer() ? 1 : 0;
attrArr[GLPixelFormat.Attributes.ONSCREEN] = attrs.isOnScreen() ? 1 : 0;
nativeCtxInfo = nInitialize(drawable.getNativeDrawableInfo(),
pixelFormat.getNativePFInfo(), shareCtx.getNativeHandle(),
vSyncRequest);
if (PrismSettings.verbose) {
System.err.println("Attributes = " + attrs);
System.err.println("  initialize() returns " + nativeCtxInfo);
}
}
@Override
long getNativeHandle() {
return nGetNativeHandle(nativeCtxInfo);
}
@Override
void makeCurrent(GLDrawable drawable) {
nMakeCurrent(nativeCtxInfo, drawable.getNativeDrawableInfo());
}
}
