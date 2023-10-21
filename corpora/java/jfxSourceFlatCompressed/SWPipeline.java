package com.sun.prism.sw;
import com.sun.glass.ui.Screen;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.ResourceFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.HashMap;
public final class SWPipeline extends GraphicsPipeline {
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
NativeLibLoader.loadLibrary("prism_sw");
return null;
});
}
@Override public boolean init() {
return true;
}
private static SWPipeline theInstance;
private SWPipeline() {
}
public static SWPipeline getInstance() {
if (theInstance == null) {
theInstance = new SWPipeline();
}
return theInstance;
}
private final HashMap<Integer, SWResourceFactory> factories =
new HashMap<Integer, SWResourceFactory>(1);
@Override
public int getAdapterOrdinal(Screen screen) {
return Screen.getScreens().indexOf(screen);
}
@Override public ResourceFactory getResourceFactory(Screen screen) {
Integer index = Integer.valueOf(screen.getAdapterOrdinal());
SWResourceFactory factory = factories.get(index);
if (factory == null) {
factory = new SWResourceFactory(screen);
factories.put(index, factory);
}
return factory;
}
@Override public ResourceFactory getDefaultResourceFactory(List<Screen> screens) {
return getResourceFactory(Screen.getMainScreen());
}
@Override public boolean is3DSupported() {
return false;
}
@Override
public boolean isVsyncSupported() {
return false;
}
@Override
public boolean supportsShaderType(ShaderType type) {
return false;
}
@Override
public boolean supportsShaderModel(ShaderModel model) {
return false;
}
@Override public void dispose() {
super.dispose();
}
@Override
public boolean isUploading() {
return true;
}
}
