package com.sun.glass.ui.monocle;
import java.util.ArrayList;
import java.util.List;
public class EGLPlatform extends LinuxPlatform {
private List<NativeScreen> screens;
public EGLPlatform() {
String lib = System.getProperty("monocle.egl.lib");
if (lib != null) {
long handle = LinuxSystem.getLinuxSystem().dlopen(lib, LinuxSystem.RTLD_LAZY | LinuxSystem.RTLD_GLOBAL);
if (handle == 0) {
throw new UnsatisfiedLinkError("EGLPlatform failed to load the requested library " + lib);
}
}
}
@Override
protected NativeCursor createCursor() {
boolean swcursor = Boolean.getBoolean("monocle.egl.swcursor");
final NativeCursor c = useCursor ? (swcursor ? new SoftwareCursor() : new EGLCursor()) : new NullCursor();
return logSelectedCursor(c);
}
@Override
protected NativeScreen createScreen() {
return new EGLScreen(0);
}
@Override
protected synchronized List<NativeScreen> createScreens() {
if (screens == null) {
int numScreens = nGetNumberOfScreens();
screens = new ArrayList<>(numScreens);
for (int i = 0; i < numScreens; i++) {
screens.add(new EGLScreen(i));
}
}
return screens;
}
@Override
public synchronized AcceleratedScreen getAcceleratedScreen(int[] attributes) throws GLException {
if (accScreen == null) {
accScreen = new EGLAcceleratedScreen(attributes);
}
return accScreen;
}
private native int nGetNumberOfScreens();
}
