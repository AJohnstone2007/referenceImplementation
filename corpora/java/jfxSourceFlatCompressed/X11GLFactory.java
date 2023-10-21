package com.sun.prism.es2;
import com.sun.prism.es2.GLPixelFormat.Attributes;
import java.util.HashMap;
class X11GLFactory extends GLFactory {
private static native long nInitialize(int[] attrArr);
private static native int nGetAdapterOrdinal(long nativeScreen);
private static native int nGetAdapterCount();
private static native int nGetDefaultScreen(long nativeCtxInfo);
private static native long nGetDisplay(long nativeCtxInfo);
private static native long nGetVisualID(long nativeCtxInfo);
private GLGPUInfo preQualificationFilter[] = {
new GLGPUInfo("advanced micro devices", null),
new GLGPUInfo("amd", null),
new GLGPUInfo("ati", null),
new GLGPUInfo("intel", null),
new GLGPUInfo("nvidia", null),
new GLGPUInfo("nouveau", null),
new GLGPUInfo("x.org", null)
};
private GLGPUInfo rejectList[] = {
new GLGPUInfo("ati", "radeon x1300"),
new GLGPUInfo("ati", "radeon x1350"),
new GLGPUInfo("ati", "radeon x1400"),
new GLGPUInfo("ati", "radeon x1450"),
new GLGPUInfo("ati", "radeon x1500"),
new GLGPUInfo("ati", "radeon x1550"),
new GLGPUInfo("ati", "radeon x1600"),
new GLGPUInfo("ati", "radeon x1650"),
new GLGPUInfo("ati", "radeon x1700"),
new GLGPUInfo("ati", "radeon x1800"),
new GLGPUInfo("ati", "radeon x1900"),
new GLGPUInfo("ati", "radeon x1950"),
new GLGPUInfo("x.org", "amd rv505"),
new GLGPUInfo("x.org", "amd rv515"),
new GLGPUInfo("x.org", "amd rv516"),
new GLGPUInfo("x.org", "amd r520"),
new GLGPUInfo("x.org", "amd rv530"),
new GLGPUInfo("x.org", "amd rv535"),
new GLGPUInfo("x.org", "amd rv560"),
new GLGPUInfo("x.org", "amd rv570"),
new GLGPUInfo("x.org", "amd r580"),
new GLGPUInfo("nvidia", "geforce 6100"),
new GLGPUInfo("nvidia", "geforce 6150"),
new GLGPUInfo("nvidia", "geforce 6200"),
new GLGPUInfo("nvidia", "geforce 6500"),
new GLGPUInfo("nvidia", "geforce 6600"),
new GLGPUInfo("nvidia", "geforce 6700"),
new GLGPUInfo("nvidia", "geforce 6800"),
new GLGPUInfo("nvidia", "geforce 7025"),
new GLGPUInfo("nvidia", "geforce 7100"),
new GLGPUInfo("nvidia", "geforce 7150"),
new GLGPUInfo("nvidia", "geforce 7200"),
new GLGPUInfo("nvidia", "geforce 7300"),
new GLGPUInfo("nvidia", "geforce 7350"),
new GLGPUInfo("nvidia", "geforce 7500"),
new GLGPUInfo("nvidia", "geforce 7600"),
new GLGPUInfo("nvidia", "geforce 7650"),
new GLGPUInfo("nvidia", "geforce 7800"),
new GLGPUInfo("nvidia", "geforce 7900"),
new GLGPUInfo("nvidia", "geforce 7950")
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
return new X11GLContext(nativeCtxInfo);
}
@Override
GLContext createGLContext(GLDrawable drawable, GLPixelFormat pixelFormat,
GLContext shareCtx, boolean vSyncRequest) {
return new X11GLContext(drawable, pixelFormat, vSyncRequest);
}
@Override
GLDrawable createDummyGLDrawable(GLPixelFormat pixelFormat) {
return new X11GLDrawable(pixelFormat);
}
@Override
GLDrawable createGLDrawable(long nativeWindow, GLPixelFormat pixelFormat) {
return new X11GLDrawable(nativeWindow, pixelFormat);
}
@Override
GLPixelFormat createGLPixelFormat(long nativeScreen, Attributes attributes) {
return new X11GLPixelFormat(nativeScreen, attributes);
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
gl2 = true;
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
deviceDetails.put("XVisualID", Long.valueOf(nGetVisualID(nativeCtxInfo)));
deviceDetails.put("XDisplay", Long.valueOf(nGetDisplay(nativeCtxInfo)));
deviceDetails.put("XScreenID", Integer.valueOf(nGetDefaultScreen(nativeCtxInfo)));
}
}
