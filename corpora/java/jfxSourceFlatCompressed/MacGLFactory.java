package com.sun.prism.es2;
import com.sun.prism.es2.GLPixelFormat.Attributes;
import java.util.HashMap;
class MacGLFactory extends GLFactory {
private static native long nInitialize(int[] attrArr);
private static native int nGetAdapterOrdinal(long nativeScreen);
private static native int nGetAdapterCount();
private static native boolean nGetIsGL2(long nativeCtxInfo);
private GLGPUInfo preQualificationFilter[] = null;
private GLGPUInfo rejectList[] = {
new GLGPUInfo("ati", "radeon x1600 opengl engine"),
new GLGPUInfo("ati", "radeon x1900 opengl engine"),
new GLGPUInfo("intel", "gma x3100 opengl engine")
};
@Override
GLGPUInfo[] getPreQualificationFilter() {
return preQualificationFilter;
}
@Override
GLGPUInfo[] getRejectList() {
return rejectList;
}
@Override
GLContext createGLContext(long nativeCtxInfo) {
return new MacGLContext(nativeCtxInfo);
}
@Override
GLContext createGLContext(GLDrawable drawable, GLPixelFormat pixelFormat,
GLContext shareCtx, boolean vSyncRequest) {
GLContext glassCtx = new MacGLContext(drawable, pixelFormat, shareCtx, vSyncRequest);
GLContext prismCtx = new MacGLContext(drawable, pixelFormat, shareCtx, vSyncRequest);
HashMap devDetails = (HashMap) ES2Pipeline.getInstance().getDeviceDetails();
devDetails.put("contextPtr", glassCtx.getNativeHandle());
return prismCtx;
}
@Override
GLDrawable createDummyGLDrawable(GLPixelFormat pixelFormat) {
return new MacGLDrawable(pixelFormat);
}
@Override
GLDrawable createGLDrawable(long nativeWindow, GLPixelFormat pixelFormat) {
return new MacGLDrawable(nativeWindow, pixelFormat);
}
@Override
GLPixelFormat createGLPixelFormat(long nativeScreen, Attributes attributes) {
return new MacGLPixelFormat(nativeScreen, attributes);
}
@Override
boolean initialize(Class psClass, Attributes attrs) {
int attrArr[] = new int[GLPixelFormat.Attributes.NUM_ITEMS];
attrArr[GLPixelFormat.Attributes.RED_SIZE] = attrs.getRedSize();
attrArr[GLPixelFormat.Attributes.GREEN_SIZE] = attrs.getGreenSize();
attrArr[GLPixelFormat.Attributes.BLUE_SIZE] = attrs.getBlueSize();
attrArr[GLPixelFormat.Attributes.ALPHA_SIZE] = attrs.getAlphaSize();
attrArr[GLPixelFormat.Attributes.DEPTH_SIZE] = attrs.getDepthSize();
attrArr[GLPixelFormat.Attributes.DOUBLEBUFFER] = attrs.isDoubleBuffer() ? 1 : 0;
attrArr[GLPixelFormat.Attributes.ONSCREEN] = attrs.isOnScreen() ? 1 : 0;
nativeCtxInfo = nInitialize(attrArr);
if (nativeCtxInfo == 0) {
return false;
} else {
gl2 = nGetIsGL2(nativeCtxInfo);
return true;
}
}
@Override
int getAdapterCount() {
return nGetAdapterCount();
}
@Override
int getAdapterOrdinal(long nativeScreen) {
return nGetAdapterOrdinal(nativeScreen);
}
@Override
void updateDeviceDetails(HashMap deviceDetails) {
deviceDetails.put("shareContextPtr", getShareContext().getNativeHandle());
}
}
