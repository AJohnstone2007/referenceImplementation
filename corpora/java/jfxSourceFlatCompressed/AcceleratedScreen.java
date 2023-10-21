package com.sun.glass.ui.monocle;
public class AcceleratedScreen {
private static long glesLibraryHandle;
private static long eglLibraryHandle;
private static boolean initialized = false;
long eglSurface;
long eglContext;
long eglDisplay;
long nativeWindow;
protected static final LinuxSystem ls = LinuxSystem.getLinuxSystem();
private EGL egl;
long eglConfigs[] = {0};
protected long platformGetNativeDisplay() {
return 0L;
}
protected long platformGetNativeWindow() {
return 0L;
}
AcceleratedScreen() {
}
AcceleratedScreen(int[] attributes) throws GLException, UnsatisfiedLinkError {
egl = EGL.getEGL();
initPlatformLibraries();
int major[] = {0}, minor[]={0};
long nativeDisplay = platformGetNativeDisplay();
long nativeWindow = platformGetNativeWindow();
if (nativeDisplay == -1l) {
throw new GLException(0, "Could not get native display");
}
if (nativeWindow == -1l) {
throw new GLException(0, "Could not get native window");
}
eglDisplay =
egl.eglGetDisplay(nativeDisplay);
if (eglDisplay == EGL.EGL_NO_DISPLAY) {
throw new GLException(egl.eglGetError(),
"Could not get EGL display");
}
if (!egl.eglInitialize(eglDisplay, major, minor)) {
throw new GLException(egl.eglGetError(),
"Error initializing EGL");
}
if (!egl.eglBindAPI(EGL.EGL_OPENGL_ES_API)) {
throw new GLException(egl.eglGetError(),
"Error binding OPENGL API");
}
int configCount[] = {0};
if (!egl.eglChooseConfig(eglDisplay, attributes, eglConfigs,
1, configCount)) {
throw new GLException(egl.eglGetError(),
"Error choosing EGL config");
}
eglSurface =
egl.eglCreateWindowSurface(eglDisplay, eglConfigs[0],
nativeWindow, null);
if (eglSurface == EGL.EGL_NO_SURFACE) {
throw new GLException(egl.eglGetError(),
"Could not get EGL surface");
}
int emptyAttrArray [] = {};
eglContext = egl.eglCreateContext(eglDisplay, eglConfigs[0],
0, emptyAttrArray);
if (eglContext == EGL.EGL_NO_CONTEXT) {
throw new GLException(egl.eglGetError(),
"Could not get EGL context");
}
}
private void createSurface() {
nativeWindow = platformGetNativeWindow();
eglSurface = egl._eglCreateWindowSurface(eglDisplay, eglConfigs[0],
nativeWindow, null);
}
public void enableRendering(boolean flag) {
if (flag) {
egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface,
eglContext);
} else {
egl.eglMakeCurrent(eglDisplay, 0, 0, eglContext);
}
}
boolean initPlatformLibraries() throws UnsatisfiedLinkError{
if (!initialized) {
glesLibraryHandle = ls.dlopen("libGLESv2.so",
LinuxSystem.RTLD_LAZY | LinuxSystem.RTLD_GLOBAL);
if (glesLibraryHandle == 0l) {
throw new UnsatisfiedLinkError("Error loading libGLESv2.so");
}
eglLibraryHandle = ls.dlopen("libEGL.so",
LinuxSystem.RTLD_LAZY | LinuxSystem.RTLD_GLOBAL);
if (eglLibraryHandle == 0l) {
throw new UnsatisfiedLinkError("Error loading libEGL.so");
}
initialized = true;
}
return true;
}
public long getGLHandle() {
return glesLibraryHandle;
}
protected long getEGLHandle() { return eglLibraryHandle; }
public boolean swapBuffers() {
boolean result = false;
synchronized(NativeScreen.framebufferSwapLock) {
result = egl.eglSwapBuffers(eglDisplay, eglSurface);
if (!result) {
createSurface();
result = egl.eglSwapBuffers(eglDisplay, eglSurface);
}
}
return result;
}
}
