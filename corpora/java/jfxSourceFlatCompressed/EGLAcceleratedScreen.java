package com.sun.glass.ui.monocle;
public class EGLAcceleratedScreen extends AcceleratedScreen {
private long eglWindowHandle = -1;
EGLAcceleratedScreen(int[] attributes) throws GLException {
eglWindowHandle = platformGetNativeWindow();
eglDisplay = nGetEglDisplayHandle();
nEglInitialize(eglDisplay);
nEglBindApi(EGL.EGL_OPENGL_ES_API);
long eglConfig = nEglChooseConfig(eglDisplay, attributes);
if (eglConfig == -1) {
throw new IllegalArgumentException("Could not create an EGLChooseConfig");
}
eglSurface = nEglCreateWindowSurface(eglDisplay, eglConfig, eglWindowHandle);
eglContext = nEglCreateContext(eglDisplay, eglConfig);
}
@Override
protected long platformGetNativeWindow() {
String displayID = System.getProperty("egl.displayid", "/dev/dri/card1" );
return nPlatformGetNativeWindow(displayID);
}
@Override
public void enableRendering(boolean flag) {
if (flag) {
nEglMakeCurrent(eglDisplay, eglSurface, eglSurface,
eglContext);
} else {
nEglMakeCurrent(eglDisplay, 0, 0, eglContext);
}
}
@Override
public boolean swapBuffers() {
boolean result = false;
synchronized (NativeScreen.framebufferSwapLock) {
result = nEglSwapBuffers(eglDisplay, eglSurface);
}
return result;
}
private native long nPlatformGetNativeWindow(String displayID);
private native long nGetEglDisplayHandle();
private native boolean nEglInitialize(long handle);
private native boolean nEglBindApi(int v);
private native long nEglChooseConfig(long eglDisplay, int[] attribs);
private native boolean nEglMakeCurrent(long eglDisplay, long eglDrawSurface, long eglReadSurface, long eglContext);
private native long nEglCreateWindowSurface(long eglDisplay, long eglConfig, long nativeWindow);
private native long nEglCreateContext(long eglDisplay, long eglConfig);
private native boolean nEglSwapBuffers(long eglDisplay, long eglSurface);
}
