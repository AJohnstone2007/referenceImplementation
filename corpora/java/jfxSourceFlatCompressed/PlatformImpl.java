package com.sun.javafx.application;
import static com.sun.javafx.FXPermissions.CREATE_TRANSPARENT_WINDOW_PERMISSION;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.tk.TKListener;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.ModuleHelper;
import java.lang.module.ModuleDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.util.FXPermission;
public class PlatformImpl {
private static AtomicBoolean initialized = new AtomicBoolean(false);
private static AtomicBoolean platformExit = new AtomicBoolean(false);
private static AtomicBoolean toolkitExit = new AtomicBoolean(false);
private static CountDownLatch startupLatch = new CountDownLatch(1);
private static AtomicBoolean listenersRegistered = new AtomicBoolean(false);
private static TKListener toolkitListener = null;
private static volatile boolean implicitExit = true;
private static boolean taskbarApplication = true;
private static boolean contextual2DNavigation;
private static AtomicInteger pendingRunnables = new AtomicInteger(0);
private static AtomicInteger numWindows = new AtomicInteger(0);
private static volatile boolean firstWindowShown = false;
private static volatile boolean lastWindowClosed = false;
private static AtomicBoolean reallyIdle = new AtomicBoolean(false);
private static Set<FinishListener> finishListeners =
new CopyOnWriteArraySet<FinishListener>();
private final static Object runLaterLock = new Object();
private static Boolean isGraphicsSupported;
private static Boolean isControlsSupported;
private static Boolean isMediaSupported;
private static Boolean isWebSupported;
private static Boolean isSWTSupported;
private static Boolean isSwingSupported;
private static Boolean isFXMLSupported;
private static Boolean hasTwoLevelFocus;
private static Boolean hasVirtualKeyboard;
private static Boolean hasTouch;
private static Boolean hasMultiTouch;
private static Boolean hasPointer;
private static boolean isThreadMerged = false;
private static String applicationType = "";
private static BooleanProperty accessibilityActive = new SimpleBooleanProperty();
private static CountDownLatch allNestedLoopsExitedLatch = new CountDownLatch(1);
@SuppressWarnings("removal")
private static final boolean verbose
= AccessController.doPrivileged((PrivilegedAction<Boolean>) () ->
Boolean.getBoolean("javafx.verbose"));
@SuppressWarnings("removal")
private static final boolean DEBUG
= AccessController.doPrivileged((PrivilegedAction<Boolean>) ()
-> Boolean.getBoolean("com.sun.javafx.application.debug"));
private static final FXPermission FXCANVAS_PERMISSION =
new FXPermission("accessFXCanvasInternals");
public static void setTaskbarApplication(boolean taskbarApplication) {
PlatformImpl.taskbarApplication = taskbarApplication;
}
public static boolean isTaskbarApplication() {
return taskbarApplication;
}
public static void setApplicationName(final Class appClass) {
runLater(() -> com.sun.glass.ui.Application.GetApplication().setName(appClass.getName()));
}
public static boolean isContextual2DNavigation() {
return contextual2DNavigation;
}
public static void startup(final Runnable r) {
startup(r, false);
}
public static void startup(final Runnable r, boolean preventDuplicateCalls) {
if (platformExit.get()) {
throw new IllegalStateException("Platform.exit has been called");
}
if (initialized.getAndSet(true)) {
if (preventDuplicateCalls) {
throw new IllegalStateException("Toolkit already initialized");
}
runLater(r);
return;
}
final Module module = PlatformImpl.class.getModule();
final ModuleDescriptor moduleDesc = module.getDescriptor();
if (!module.isNamed()
|| !"javafx.graphics".equals(module.getName())
|| moduleDesc == null
|| moduleDesc.isAutomatic()
|| moduleDesc.isOpen()) {
String warningStr = "Unsupported JavaFX configuration: "
+ "classes were loaded from '" + module + "'";
if (moduleDesc != null) {
warningStr += ", isAutomatic: " + moduleDesc.isAutomatic();
warningStr += ", isOpen: " + moduleDesc.isOpen();
}
Logging.getJavaFXLogger().warning(warningStr);
}
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
applicationType = System.getProperty("com.sun.javafx.application.type");
if (applicationType == null) applicationType = "";
contextual2DNavigation = Boolean.getBoolean(
"com.sun.javafx.isContextual2DNavigation");
String s = System.getProperty("com.sun.javafx.twoLevelFocus");
if (s != null) {
hasTwoLevelFocus = Boolean.valueOf(s);
}
s = System.getProperty("com.sun.javafx.virtualKeyboard");
if (s != null) {
if (s.equalsIgnoreCase("none")) {
hasVirtualKeyboard = false;
} else if (s.equalsIgnoreCase("javafx")) {
hasVirtualKeyboard = true;
} else if (s.equalsIgnoreCase("native")) {
hasVirtualKeyboard = true;
}
}
s = System.getProperty("com.sun.javafx.touch");
if (s != null) {
hasTouch = Boolean.valueOf(s);
}
s = System.getProperty("com.sun.javafx.multiTouch");
if (s != null) {
hasMultiTouch = Boolean.valueOf(s);
}
s = System.getProperty("com.sun.javafx.pointer");
if (s != null) {
hasPointer = Boolean.valueOf(s);
}
s = System.getProperty("javafx.embed.singleThread");
if (s != null) {
isThreadMerged = Boolean.valueOf(s);
if (isThreadMerged && !isSupported(ConditionalFeature.SWING)) {
isThreadMerged = false;
if (verbose) {
System.err.println(
"WARNING: javafx.embed.singleThread ignored (javafx.swing module not found)");
}
}
}
return null;
});
if (DEBUG) {
System.err.println("PlatformImpl::startup : applicationType = "
+ applicationType);
}
if ("FXCanvas".equals(applicationType)) {
initFXCanvas();
}
if (!taskbarApplication) {
@SuppressWarnings("removal")
var dummy2 = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
System.setProperty("glass.taskbarApplication", "false");
return null;
});
}
toolkitListener = new TKListener() {
@Override public void changedTopLevelWindows(List<TKStage> windows) {
numWindows.set(windows.size());
checkIdle();
}
@Override
public void exitedLastNestedLoop() {
if (platformExit.get()) {
allNestedLoopsExitedLatch.countDown();
}
checkIdle();
}
};
Toolkit.getToolkit().addTkListener(toolkitListener);
Toolkit.getToolkit().startup(() -> {
startupLatch.countDown();
r.run();
});
if (isThreadMerged) {
installFwEventQueue();
}
}
private static void initDeviceDetailsFXCanvas() {
final String eventProcProperty = "javafx.embed.eventProc";
@SuppressWarnings("removal")
final long eventProc = AccessController.doPrivileged((PrivilegedAction<Long>) () ->
Long.getLong(eventProcProperty, 0));
if (eventProc != 0L) {
Map map = com.sun.glass.ui.Application.getDeviceDetails();
if (map == null) {
map = new HashMap();
com.sun.glass.ui.Application.setDeviceDetails(map);
}
if (map.get(eventProcProperty) == null) {
map.put(eventProcProperty, eventProc);
}
}
}
private static void addExportsToFXCanvas(Class<?> fxCanvasClass) {
final String[] swtNeededPackages = {
"com.sun.glass.ui",
"com.sun.javafx.cursor",
"com.sun.javafx.embed",
"com.sun.javafx.stage",
"com.sun.javafx.tk"
};
if (DEBUG) {
System.err.println("addExportsToFXCanvas: class = " + fxCanvasClass);
}
Object thisModule = ModuleHelper.getModule(PlatformImpl.class);
Object javafxSwtModule = ModuleHelper.getModule(fxCanvasClass);
for (String pkg : swtNeededPackages) {
if (DEBUG) {
System.err.println("add export of " + pkg + " from "
+ thisModule + " to " + javafxSwtModule);
}
ModuleHelper.addExports(thisModule, pkg, javafxSwtModule);
}
}
private static void initFXCanvas() {
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm != null) {
try {
sm.checkPermission(FXCANVAS_PERMISSION);
} catch (SecurityException ex) {
System.err.println("FXCanvas: no permission to access JavaFX internals");
ex.printStackTrace();
return;
}
}
Predicate<StackWalker.StackFrame> classFilter = f ->
!f.getClassName().startsWith("javafx.application.")
&& !f.getClassName().startsWith("com.sun.javafx.application.");
@SuppressWarnings("removal")
final StackWalker walker = AccessController.doPrivileged((PrivilegedAction<StackWalker>) () ->
StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE));
Optional<StackWalker.StackFrame> frame = walker.walk(
s -> s.filter(classFilter).findFirst());
if (frame.isPresent()) {
Class<?> caller = frame.get().getDeclaringClass();
if (DEBUG) {
System.err.println("callerClassName = " + caller);
}
if ("javafx.embed.swt.FXCanvas".equals(caller.getName())) {
initDeviceDetailsFXCanvas();
addExportsToFXCanvas(caller);
}
}
}
private static void installFwEventQueue() {
invokeSwingFXUtilsMethod("installFwEventQueue");
}
private static void removeFwEventQueue() {
invokeSwingFXUtilsMethod("removeFwEventQueue");
}
private static void invokeSwingFXUtilsMethod(final String methodName) {
try {
Class swingFXUtilsClass = Class.forName("com.sun.javafx.embed.swing.SwingFXUtilsImpl");
Method installFwEventQueue = swingFXUtilsClass.getDeclaredMethod(methodName);
waitForStart();
installFwEventQueue.invoke(null);
} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
throw new RuntimeException("Property javafx.embed.singleThread is not supported");
} catch (InvocationTargetException e) {
throw new RuntimeException(e);
}
}
private static void waitForStart() {
if (startupLatch.getCount() > 0) {
try {
startupLatch.await();
} catch (InterruptedException ex) {
ex.printStackTrace();
}
}
}
public static boolean isFxApplicationThread() {
return Toolkit.getToolkit().isFxUserThread();
}
public static void runLater(final Runnable r) {
runLater(r, false);
}
private static void runLater(final Runnable r, boolean exiting) {
if (!initialized.get()) {
throw new IllegalStateException("Toolkit not initialized");
}
pendingRunnables.incrementAndGet();
waitForStart();
synchronized (runLaterLock) {
if (!exiting && toolkitExit.get()) {
pendingRunnables.decrementAndGet();
return;
}
@SuppressWarnings("removal")
final AccessControlContext acc = AccessController.getContext();
Toolkit.getToolkit().defer(() -> {
try {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
r.run();
return null;
}, acc);
} finally {
pendingRunnables.decrementAndGet();
checkIdle();
}
});
}
}
public static void runAndWait(final Runnable r) {
runAndWait(r, false);
}
private static void runAndWait(final Runnable r, boolean exiting) {
if (isFxApplicationThread()) {
try {
r.run();
} catch (Throwable t) {
System.err.println("Exception in runnable");
t.printStackTrace();
}
} else {
final CountDownLatch doneLatch = new CountDownLatch(1);
runLater(() -> {
try {
r.run();
} finally {
doneLatch.countDown();
}
}, exiting);
if (!exiting && toolkitExit.get()) {
throw new IllegalStateException("Toolkit has exited");
}
try {
doneLatch.await();
} catch (InterruptedException ex) {
ex.printStackTrace();
}
}
}
public static void setImplicitExit(boolean implicitExit) {
PlatformImpl.implicitExit = implicitExit;
checkIdle();
}
public static boolean isImplicitExit() {
return implicitExit;
}
public static void addListener(FinishListener l) {
listenersRegistered.set(true);
finishListeners.add(l);
}
public static void removeListener(FinishListener l) {
finishListeners.remove(l);
listenersRegistered.set(!finishListeners.isEmpty());
if (!listenersRegistered.get()) {
checkIdle();
}
}
private static void notifyFinishListeners(boolean exitCalled) {
if (listenersRegistered.get()) {
for (FinishListener l : finishListeners) {
if (exitCalled) {
l.exitCalled();
} else {
l.idle(implicitExit);
}
}
} else if (implicitExit || platformExit.get()) {
tkExit();
}
}
private static void checkIdle() {
if (!initialized.get()) {
return;
}
if (!isFxApplicationThread()) {
runLater(() -> {
});
return;
}
boolean doNotify = false;
synchronized (PlatformImpl.class) {
int numWin = numWindows.get();
if (numWin > 0) {
firstWindowShown = true;
lastWindowClosed = false;
reallyIdle.set(false);
} else if (numWin == 0 && firstWindowShown) {
lastWindowClosed = true;
}
if (lastWindowClosed && pendingRunnables.get() == 0
&& (toolkitExit.get() || !Toolkit.getToolkit().isNestedLoopRunning())) {
if (reallyIdle.getAndSet(true)) {
doNotify = true;
lastWindowClosed = false;
} else {
runLater(() -> {
});
}
}
}
if (doNotify) {
notifyFinishListeners(false);
}
}
private static final CountDownLatch platformExitLatch = new CountDownLatch(1);
static CountDownLatch test_getPlatformExitLatch() {
return platformExitLatch;
}
public static void tkExit() {
if (toolkitExit.getAndSet(true)) {
return;
}
if (initialized.get()) {
if (platformExit.get()) {
PlatformImpl.runAndWait(() -> {
if (Toolkit.getToolkit().isNestedLoopRunning()) {
Toolkit.getToolkit().exitAllNestedEventLoops();
} else {
allNestedLoopsExitedLatch.countDown();
}
}, true);
try {
allNestedLoopsExitedLatch.await();
} catch (InterruptedException e) {
throw new RuntimeException("Could not exit all nested event loops");
}
}
PlatformImpl.runAndWait(() -> {
Toolkit.getToolkit().exit();
}, true);
if (isThreadMerged) {
removeFwEventQueue();
}
Toolkit.getToolkit().removeTkListener(toolkitListener);
toolkitListener = null;
platformExitLatch.countDown();
}
}
public static BooleanProperty accessibilityActiveProperty() {
return accessibilityActive;
}
public static void exit() {
platformExit.set(true);
notifyFinishListeners(true);
}
private static Boolean checkForClass(String classname) {
try {
Class.forName(classname, false, PlatformImpl.class.getClassLoader());
return Boolean.TRUE;
} catch (ClassNotFoundException cnfe) {
return Boolean.FALSE;
}
}
public static boolean isSupported(ConditionalFeature feature) {
final boolean supported = isSupportedImpl(feature);
if (supported && (feature == ConditionalFeature.TRANSPARENT_WINDOW)) {
@SuppressWarnings("removal")
final SecurityManager securityManager =
System.getSecurityManager();
if (securityManager != null) {
try {
securityManager.checkPermission(CREATE_TRANSPARENT_WINDOW_PERMISSION);
} catch (final SecurityException e) {
return false;
}
}
return true;
}
return supported;
}
public static interface FinishListener {
public void idle(boolean implicitExit);
public void exitCalled();
}
public static void setDefaultPlatformUserAgentStylesheet() {
setPlatformUserAgentStylesheet(Application.STYLESHEET_MODENA);
}
private static boolean isModena = false;
private static boolean isCaspian = false;
public static boolean isModena() {
return isModena;
}
public static boolean isCaspian() {
return isCaspian;
}
public static void setPlatformUserAgentStylesheet(final String stylesheetUrl) {
if (isFxApplicationThread()) {
_setPlatformUserAgentStylesheet(stylesheetUrl);
} else {
runLater(() -> _setPlatformUserAgentStylesheet(stylesheetUrl));
}
}
public enum HighContrastScheme {
HIGH_CONTRAST_BLACK("high.contrast.black.theme"),
HIGH_CONTRAST_WHITE("high.contrast.white.theme"),
HIGH_CONTRAST_1("high.contrast.1.theme"),
HIGH_CONTRAST_2("high.contrast.2.theme");
private final String themeKey;
HighContrastScheme(String themeKey) {
this.themeKey = themeKey;
}
public String getThemeKey() {
return themeKey;
}
public static String fromThemeName(Function<String, String> keyFunction, String themeName) {
if (keyFunction == null || themeName == null) {
return null;
}
for (HighContrastScheme item : values()) {
if (themeName.equalsIgnoreCase(keyFunction.apply(item.getThemeKey()))) {
return item.toString();
}
}
return null;
}
}
private static String accessibilityTheme;
public static boolean setAccessibilityTheme(String platformTheme) {
if (accessibilityTheme != null) {
StyleManager.getInstance().removeUserAgentStylesheet(accessibilityTheme);
accessibilityTheme = null;
}
_setAccessibilityTheme(platformTheme);
if (accessibilityTheme != null) {
StyleManager.getInstance().addUserAgentStylesheet(accessibilityTheme);
return true;
}
return false;
}
private static void _setAccessibilityTheme(String platformTheme) {
@SuppressWarnings("removal")
final String userTheme = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty("com.sun.javafx.highContrastTheme"));
if (isCaspian()) {
if (platformTheme != null || userTheme != null) {
accessibilityTheme = "com/sun/javafx/scene/control/skin/caspian/highcontrast.css";
}
} else if (isModena()) {
if (userTheme != null) {
switch (userTheme.toUpperCase()) {
case "BLACKONWHITE":
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/blackOnWhite.css";
break;
case "WHITEONBLACK":
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/whiteOnBlack.css";
break;
case "YELLOWONBLACK":
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/yellowOnBlack.css";
break;
default:
}
} else {
if (platformTheme != null) {
switch (HighContrastScheme.valueOf(platformTheme)) {
case HIGH_CONTRAST_WHITE:
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/blackOnWhite.css";
break;
case HIGH_CONTRAST_BLACK:
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/whiteOnBlack.css";
break;
case HIGH_CONTRAST_1:
case HIGH_CONTRAST_2:
accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/yellowOnBlack.css";
break;
default:
}
}
}
}
}
private static void _setPlatformUserAgentStylesheet(String stylesheetUrl) {
isModena = isCaspian = false;
@SuppressWarnings("removal")
final String overrideStylesheetUrl = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty("javafx.userAgentStylesheetUrl"));
if (overrideStylesheetUrl != null) {
stylesheetUrl = overrideStylesheetUrl;
}
final List<String> uaStylesheets = new ArrayList<>();
if (Application.STYLESHEET_CASPIAN.equalsIgnoreCase(stylesheetUrl)) {
isCaspian = true;
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/caspian.css");
if (isSupported(ConditionalFeature.INPUT_TOUCH)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/embedded.css");
if (com.sun.javafx.util.Utils.isQVGAScreen()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/embedded-qvga.css");
}
if (PlatformUtil.isAndroid()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/android.css");
}
if (PlatformUtil.isIOS()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/ios.css");
}
}
if (isSupported(ConditionalFeature.TWO_LEVEL_FOCUS)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/two-level-focus.css");
}
if (isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/fxvk.css");
}
if (!isSupported(ConditionalFeature.TRANSPARENT_WINDOW)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/caspian-no-transparency.css");
}
} else if (Application.STYLESHEET_MODENA.equalsIgnoreCase(stylesheetUrl)) {
isModena = true;
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/modena.css");
if (isSupported(ConditionalFeature.INPUT_TOUCH)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/touch.css");
}
if (PlatformUtil.isEmbedded()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/modena-embedded-performance.css");
}
if (PlatformUtil.isAndroid()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/android.css");
}
if (PlatformUtil.isIOS()) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/ios.css");
}
if (isSupported(ConditionalFeature.TWO_LEVEL_FOCUS)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/two-level-focus.css");
}
if (isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/caspian/fxvk.css");
}
if (!isSupported(ConditionalFeature.TRANSPARENT_WINDOW)) {
uaStylesheets.add("com/sun/javafx/scene/control/skin/modena/modena-no-transparency.css");
}
} else {
uaStylesheets.add(stylesheetUrl);
}
_setAccessibilityTheme(Toolkit.getToolkit().getThemeName());
if (accessibilityTheme != null) {
uaStylesheets.add(accessibilityTheme);
}
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction) () -> {
StyleManager.getInstance().setUserAgentStylesheets(uaStylesheets);
return null;
});
}
@SuppressWarnings("removal")
public static void addNoTransparencyStylesheetToScene(final Scene scene) {
if (PlatformImpl.isCaspian()) {
AccessController.doPrivileged((PrivilegedAction) () -> {
StyleManager.getInstance().addUserAgentStylesheet(scene,
"com/sun/javafx/scene/control/skin/caspian/caspian-no-transparency.css");
return null;
});
} else if (PlatformImpl.isModena()) {
AccessController.doPrivileged((PrivilegedAction) () -> {
StyleManager.getInstance().addUserAgentStylesheet(scene,
"com/sun/javafx/scene/control/skin/modena/modena-no-transparency.css");
return null;
});
}
}
private static boolean isSupportedImpl(ConditionalFeature feature) {
switch (feature) {
case GRAPHICS:
if (isGraphicsSupported == null) {
isGraphicsSupported = checkForClass("javafx.stage.Stage");
}
return isGraphicsSupported;
case CONTROLS:
if (isControlsSupported == null) {
isControlsSupported = checkForClass(
"javafx.scene.control.Control");
}
return isControlsSupported;
case MEDIA:
if (isMediaSupported == null) {
isMediaSupported = checkForClass(
"javafx.scene.media.MediaView");
if (isMediaSupported && PlatformUtil.isEmbedded()) {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
String s = System.getProperty(
"com.sun.javafx.experimental.embedded.media",
"false");
isMediaSupported = Boolean.valueOf(s);
return null;
});
}
}
return isMediaSupported;
case WEB:
if (isWebSupported == null) {
isWebSupported = checkForClass("javafx.scene.web.WebView");
if (isWebSupported && PlatformUtil.isEmbedded()) {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
String s = System.getProperty(
"com.sun.javafx.experimental.embedded.web",
"false");
isWebSupported = Boolean.valueOf(s);
return null;
});
}
}
return isWebSupported;
case SWT:
if (isSWTSupported == null) {
isSWTSupported = checkForClass("javafx.embed.swt.FXCanvas");
}
return isSWTSupported;
case SWING:
if (isSwingSupported == null) {
isSwingSupported =
checkForClass("javax.swing.JComponent") &&
checkForClass("javafx.embed.swing.JFXPanel");
}
return isSwingSupported;
case FXML:
if (isFXMLSupported == null) {
isFXMLSupported = checkForClass("javafx.fxml.FXMLLoader")
&& checkForClass("javax.xml.stream.XMLInputFactory");
}
return isFXMLSupported;
case TWO_LEVEL_FOCUS:
if (hasTwoLevelFocus == null) {
return Toolkit.getToolkit().isSupported(feature);
}
return hasTwoLevelFocus;
case VIRTUAL_KEYBOARD:
if (hasVirtualKeyboard == null) {
return Toolkit.getToolkit().isSupported(feature);
}
return hasVirtualKeyboard;
case INPUT_TOUCH:
if (hasTouch == null) {
return Toolkit.getToolkit().isSupported(feature);
}
return hasTouch;
case INPUT_MULTITOUCH:
if (hasMultiTouch == null) {
return Toolkit.getToolkit().isSupported(feature);
}
return hasMultiTouch;
case INPUT_POINTER:
if (hasPointer == null) {
return Toolkit.getToolkit().isSupported(feature);
}
return hasPointer;
default:
return Toolkit.getToolkit().isSupported(feature);
}
}
}
