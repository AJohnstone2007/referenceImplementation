package com.sun.prism;
import com.sun.glass.ui.Screen;
import com.sun.javafx.font.FontFactory;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.prism.impl.PrismSettings;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
public abstract class GraphicsPipeline {
public static enum ShaderType {
HLSL,
GLSL
}
public static enum ShaderModel {
SM3
}
private FontFactory fontFactory;
private final Set<Runnable> disposeHooks = new HashSet<Runnable>();
public abstract boolean init();
public void dispose() {
notifyDisposeHooks();
installedPipeline = null;
}
public void addDisposeHook(Runnable runnable) {
if (runnable == null) {
return;
}
synchronized (disposeHooks) {
disposeHooks.add(runnable);
}
}
private void notifyDisposeHooks() {
List<Runnable> hooks;
synchronized (disposeHooks) {
hooks = new ArrayList<Runnable>(disposeHooks);
disposeHooks.clear();
}
for (Runnable hook : hooks) {
hook.run();
}
}
public abstract int getAdapterOrdinal(Screen screen);
public abstract ResourceFactory getResourceFactory(Screen screen);
public abstract ResourceFactory getDefaultResourceFactory(List<Screen> screens);
public abstract boolean is3DSupported();
public boolean isMSAASupported() { return false; }
public abstract boolean isVsyncSupported();
public abstract boolean supportsShaderType(ShaderType type);
public abstract boolean supportsShaderModel(ShaderModel model);
public boolean supportsShader(ShaderType type, ShaderModel model) {
return (supportsShaderType(type) && supportsShaderModel(model));
}
public static ResourceFactory getDefaultResourceFactory() {
List<Screen> screens = Screen.getScreens();
return getPipeline().getDefaultResourceFactory(screens);
}
public FontFactory getFontFactory() {
if (fontFactory == null) {
fontFactory = PrismFontFactory.getFontFactory();
}
return fontFactory;
}
protected Map deviceDetails = null;
public Map getDeviceDetails() {
return deviceDetails;
}
protected void setDeviceDetails(Map details) {
deviceDetails = details;
}
private static GraphicsPipeline installedPipeline;
public static GraphicsPipeline createPipeline() {
if (PrismSettings.tryOrder.isEmpty()) {
if (PrismSettings.verbose) {
System.out.println("No Prism pipelines specified");
}
return null;
}
if (installedPipeline != null) {
throw new IllegalStateException("pipeline already created:"+
installedPipeline);
}
for (String prefix : PrismSettings.tryOrder) {
if ("j2d".equals(prefix)) {
System.err.println(
"WARNING: The prism-j2d pipeline should not be used as the software");
System.err.println(
"fallback pipeline. It is no longer tested nor intended to be used for");
System.err.println(
"on-screen rendering. Please use the prism-sw pipeline instead by setting");
System.err.println(
"the \"prism.order\" system property to \"sw\" rather than \"j2d\".");
}
if (PrismSettings.verbose) {
if ("j2d".equals(prefix) || "sw".equals(prefix)) {
System.err.println("*** Fallback to Prism SW pipeline");
}
}
String className =
"com.sun.prism."+prefix+"."+prefix.toUpperCase()+"Pipeline";
try {
if (PrismSettings.verbose) {
System.out.println("Prism pipeline name = " + className);
}
Class klass = Class.forName(className);
if (PrismSettings.verbose) {
System.out.println("(X) Got class = " + klass);
}
Method m = klass.getMethod("getInstance", (Class[])null);
GraphicsPipeline newPipeline = (GraphicsPipeline)
m.invoke(null, (Object[])null);
if (newPipeline != null && newPipeline.init()) {
if (PrismSettings.verbose) {
System.out.println("Initialized prism pipeline: " +
klass.getName());
}
installedPipeline = newPipeline;
return installedPipeline;
}
if (newPipeline != null) {
newPipeline.dispose();
newPipeline = null;
}
if (PrismSettings.verbose) {
System.err.println("GraphicsPipeline.createPipeline: error" +
" initializing pipeline " + className);
if (newPipeline == null) {
System.err.println("Reason: could not create an instance");
} else {
System.err.println("Reason: could not initialize the instance");
}
}
} catch (Throwable t) {
if (PrismSettings.verbose) {
System.err.println("GraphicsPipeline.createPipeline " +
"failed for " + className);
t.printStackTrace();
}
}
}
StringBuffer sBuf = new StringBuffer("Graphics Device initialization failed for :  ");
final Iterator<String> orderIterator =
PrismSettings.tryOrder.iterator();
if (orderIterator.hasNext()) {
sBuf.append(orderIterator.next());
while (orderIterator.hasNext()) {
sBuf.append(", ");
sBuf.append(orderIterator.next());
}
}
System.err.println(sBuf);
return null;
}
public static GraphicsPipeline getPipeline() {
return installedPipeline;
}
public boolean isEffectSupported() {
return true;
}
public boolean isUploading() {
return PrismSettings.forceUploadingPainter;
}
}
