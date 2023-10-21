package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.Logging;
public abstract class NativePlatform {
private static InputDeviceRegistry inputDeviceRegistry;
private final RunnableProcessor runnableProcessor;
private final PlatformLogger logger = Logging.getJavaFXLogger();
private NativeCursor cursor;
protected List<NativeScreen> screens;
protected AcceleratedScreen accScreen;
@SuppressWarnings("removal")
protected static final boolean useCursor =
AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
final String str =
System.getProperty("monocle.cursor.enabled", "true");
return "true".equalsIgnoreCase(str);
});
protected NativePlatform() {
runnableProcessor = new RunnableProcessor();
}
void shutdown() {
runnableProcessor.shutdown();
if (cursor != null) {
cursor.shutdown();
}
if (screens != null) {
for (NativeScreen screen: screens) {
screen.shutdown();
}
}
}
RunnableProcessor getRunnableProcessor() {
return runnableProcessor;
}
synchronized InputDeviceRegistry getInputDeviceRegistry() {
if (inputDeviceRegistry == null) {
inputDeviceRegistry = createInputDeviceRegistry();
}
return inputDeviceRegistry;
}
protected abstract InputDeviceRegistry createInputDeviceRegistry();
protected abstract NativeCursor createCursor();
synchronized NativeCursor getCursor() {
if (cursor == null) {
cursor = createCursor();
}
return cursor;
}
protected abstract NativeScreen createScreen();
protected synchronized List<NativeScreen> createScreens() {
if (screens == null) {
screens = new ArrayList<>(1);
screens.add(createScreen());
}
return screens;
}
synchronized NativeScreen getScreen() {
if (screens == null) {
screens = createScreens();
}
return (screens.size() == 0 ? null : screens.get(0));
}
synchronized List<NativeScreen> getScreens() {
if (screens == null) {
screens = createScreens();
}
return screens;
}
public synchronized AcceleratedScreen getAcceleratedScreen(int[] attributes)
throws GLException, UnsatisfiedLinkError {
if (accScreen == null) {
accScreen = new AcceleratedScreen(attributes);
}
return accScreen;
}
protected NativeCursor logSelectedCursor(final NativeCursor cursor) {
if (logger.isLoggable(Level.FINE)) {
final String name = cursor == null ? null : cursor.getClass().getSimpleName();
logger.fine("Using native cursor: {0}", name);
}
return cursor;
}
}
