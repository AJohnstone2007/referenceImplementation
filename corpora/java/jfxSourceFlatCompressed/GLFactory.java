package com.sun.prism.es2;
import com.sun.prism.impl.PrismSettings;
import com.sun.javafx.PlatformUtil;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
abstract class GLFactory {
private static native boolean
nIsGLExtensionSupported(long nativeContextObject, String glExtStr);
private static native String nGetGLVendor(long nativeCtxInfo);
private static native String nGetGLRenderer(long nativeCtxInfo);
private static native String nGetGLVersion(long nativeCtxInfo);
private static final GLFactory platformFactory;
long nativeCtxInfo;
boolean gl2 = false;
private GLContext shareCtx = null;
GLFactory() {
}
static {
final String factoryClassName;
if (PlatformUtil.isUnix()) {
if ("monocle".equals(PlatformUtil.getEmbeddedType()))
factoryClassName = "com.sun.prism.es2.MonocleGLFactory";
else
factoryClassName = "com.sun.prism.es2.X11GLFactory";
} else if (PlatformUtil.isWindows()) {
factoryClassName = "com.sun.prism.es2.WinGLFactory";
} else if (PlatformUtil.isMac()) {
factoryClassName = "com.sun.prism.es2.MacGLFactory";
} else if (PlatformUtil.isIOS()) {
factoryClassName = "com.sun.prism.es2.IOSGLFactory";
} else if (PlatformUtil.isAndroid()) {
factoryClassName = "com.sun.prism.es2.MonocleGLFactory";
} else {
factoryClassName = null;
System.err.println("GLFactory.static - No Platform Factory for: " + System.getProperty("os.name"));
}
if (PrismSettings.verbose) {
System.out.println("GLFactory using " + factoryClassName);
}
@SuppressWarnings("removal")
GLFactory tmp = factoryClassName == null ? null :
AccessController.doPrivileged(new FactoryLoader(factoryClassName));
platformFactory = tmp;
}
private static class FactoryLoader implements PrivilegedAction<GLFactory> {
private final String factoryClassName;
FactoryLoader(String factoryClassName) {
this.factoryClassName = factoryClassName;
}
@Override
public GLFactory run() {
GLFactory factory = null;
try {
factory = (GLFactory) Class.forName(factoryClassName).getDeclaredConstructor().newInstance();
} catch (Throwable t) {
System.err.println("GLFactory.static - Platform: "
+ System.getProperty("os.name")
+ " - not available: "
+ factoryClassName);
t.printStackTrace();
}
return factory;
}
}
static GLFactory getFactory() throws RuntimeException {
if (null != platformFactory) {
return platformFactory;
}
throw new RuntimeException("No native platform GLFactory available.");
}
abstract GLGPUInfo[] getPreQualificationFilter();
abstract GLGPUInfo[] getRejectList();
private static GLGPUInfo readGPUInfo(long nativeCtxInfo) {
String glVendor = nGetGLVendor(nativeCtxInfo);
String glRenderer = nGetGLRenderer(nativeCtxInfo);
return new GLGPUInfo(glVendor.toLowerCase(),
glRenderer.toLowerCase());
}
private static boolean matches(GLGPUInfo gpuInfo, GLGPUInfo[] gpuInfoArr) {
if (gpuInfoArr != null) {
for (int i = 0; i < gpuInfoArr.length; i++) {
if (gpuInfo.matches(gpuInfoArr[i])) {
return true;
}
}
}
return false;
}
private boolean inPreQualificationFilter(GLGPUInfo gpuInfo) {
GLGPUInfo[] preQualificationFilter = getPreQualificationFilter();
if (preQualificationFilter == null) {
return true;
}
return matches(gpuInfo, preQualificationFilter);
}
private boolean inRejectList(GLGPUInfo gpuInfo) {
return matches(gpuInfo, getRejectList());
}
boolean isQualified(long nativeCtxInfo) {
GLGPUInfo gpuInfo = readGPUInfo(nativeCtxInfo);
if (gpuInfo.vendor == null || gpuInfo.model == null
|| gpuInfo.vendor.contains("unknown")
|| gpuInfo.model.contains("unknown")) {
return false;
}
return inPreQualificationFilter(gpuInfo) && !inRejectList(gpuInfo);
}
abstract GLContext createGLContext(long nativeCtxInfo);
abstract GLContext createGLContext(GLDrawable drawable,
GLPixelFormat pixelFormat, GLContext shareCtx, boolean vSyncRequest);
abstract GLDrawable createGLDrawable(long nativeWindow, GLPixelFormat pixelFormat);
abstract GLDrawable createDummyGLDrawable(GLPixelFormat pixelFormat);
abstract GLPixelFormat createGLPixelFormat(long nativeScreen, GLPixelFormat.Attributes attrs);
boolean isGLGPUQualify() {
return isQualified(nativeCtxInfo);
}
abstract boolean initialize(Class psClass, GLPixelFormat.Attributes attrs);
GLContext getShareContext() {
if (shareCtx == null) {
shareCtx = createGLContext(nativeCtxInfo);
}
return shareCtx;
}
boolean isGL2() {
return gl2;
}
boolean isGLExtensionSupported(String sglExtStr) {
return nIsGLExtensionSupported(nativeCtxInfo, sglExtStr);
}
boolean isNPOTSupported() {
return (isGLExtensionSupported("GL_ARB_texture_non_power_of_two")
|| isGLExtensionSupported("GL_OES_texture_npot"));
}
abstract int getAdapterCount();
abstract int getAdapterOrdinal(long nativeScreen);
abstract void updateDeviceDetails(HashMap deviceDetails);
void printDriverInformation(int adapter) {
System.out.println("Graphics Vendor: " + nGetGLVendor(nativeCtxInfo));
System.out.println("       Renderer: " + nGetGLRenderer(nativeCtxInfo));
System.out.println("        Version: " + nGetGLVersion(nativeCtxInfo));
}
}
