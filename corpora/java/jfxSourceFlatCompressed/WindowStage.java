package com.sun.javafx.tk.quantum;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.AccessControlContext;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.sun.glass.events.WindowEvent;
import com.sun.glass.ui.*;
import com.sun.glass.ui.Window.Level;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.iio.common.PushbroomScaler;
import com.sun.javafx.iio.common.ScalerFactory;
import com.sun.javafx.tk.FocusCause;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKStage;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import static com.sun.javafx.FXPermissions.*;
class WindowStage extends GlassStage {
protected Window platformWindow;
protected javafx.stage.Stage fxStage;
private StageStyle style;
private GlassStage owner = null;
private Modality modality = Modality.NONE;
private final boolean securityDialog;
private OverlayWarning warning = null;
private boolean rtl = false;
private boolean transparent = false;
private boolean isPrimaryStage = false;
private boolean isPopupStage = false;
private boolean isInFullScreen = false;
private boolean isAlwaysOnTop = false;
private boolean inAllowedEventHandler = false;
private static List<WindowStage> activeWindows = new LinkedList<>();
private static Map<Window, WindowStage> platformWindows = new HashMap<>();
private static final Locale LOCALE = Locale.getDefault();
private static final ResourceBundle RESOURCES =
ResourceBundle.getBundle(WindowStage.class.getPackage().getName() +
".QuantumMessagesBundle", LOCALE);
public WindowStage(javafx.stage.Window peerWindow, boolean securityDialog, final StageStyle stageStyle, Modality modality, TKStage owner) {
this.style = stageStyle;
this.owner = (GlassStage)owner;
this.modality = modality;
this.securityDialog = securityDialog;
if (peerWindow instanceof javafx.stage.Stage) {
fxStage = (Stage)peerWindow;
} else {
fxStage = null;
}
transparent = stageStyle == StageStyle.TRANSPARENT;
if (owner == null) {
if (this.modality == Modality.WINDOW_MODAL) {
this.modality = Modality.NONE;
}
}
}
final void setIsPrimary() {
isPrimaryStage = true;
}
final void setIsPopup() {
isPopupStage = true;
}
final boolean isSecurityDialog() {
return securityDialog;
}
public final WindowStage init(GlassSystemMenu sysmenu) {
initPlatformWindow();
platformWindow.setEventHandler(new GlassWindowEventHandler(this));
if (sysmenu.isSupported()) {
sysmenu.createMenuBar();
platformWindow.setMenuBar(sysmenu.getMenuBar());
}
return this;
}
private void initPlatformWindow() {
if (platformWindow == null) {
Application app = Application.GetApplication();
Window ownerWindow = null;
if (owner instanceof WindowStage) {
ownerWindow = ((WindowStage)owner).platformWindow;
}
boolean resizable = false;
boolean focusable = true;
int windowMask = rtl ? Window.RIGHT_TO_LEFT : 0;
if (isPopupStage) {
windowMask |= Window.POPUP;
if (style == StageStyle.TRANSPARENT) {
windowMask |= Window.TRANSPARENT;
}
focusable = false;
} else {
switch (style) {
case UNIFIED:
if (app.supportsUnifiedWindows()) {
windowMask |= Window.UNIFIED;
}
case DECORATED:
windowMask |=
Window.TITLED | Window.CLOSABLE |
Window.MINIMIZABLE | Window.MAXIMIZABLE;
if (ownerWindow != null || modality != Modality.NONE) {
windowMask &=
~(Window.MINIMIZABLE | Window.MAXIMIZABLE);
}
resizable = true;
break;
case UTILITY:
windowMask |= Window.TITLED | Window.UTILITY | Window.CLOSABLE;
break;
default:
windowMask |=
(transparent ? Window.TRANSPARENT : Window.UNTITLED) | Window.CLOSABLE;
break;
}
}
if (modality != Modality.NONE) {
windowMask |= Window.MODAL;
}
platformWindow =
app.createWindow(ownerWindow, Screen.getMainScreen(), windowMask);
platformWindow.setResizable(resizable);
platformWindow.setFocusable(focusable);
if (securityDialog) {
platformWindow.setLevel(Window.Level.FLOATING);
}
if (fxStage != null && fxStage.getScene() != null) {
javafx.scene.paint.Paint paint = fxStage.getScene().getFill();
if (paint instanceof javafx.scene.paint.Color) {
javafx.scene.paint.Color color = (javafx.scene.paint.Color) paint;
platformWindow.setBackground((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
} else if (paint instanceof javafx.scene.paint.LinearGradient) {
javafx.scene.paint.LinearGradient lgradient = (javafx.scene.paint.LinearGradient) paint;
computeAndSetBackground(lgradient.getStops());
} else if (paint instanceof javafx.scene.paint.RadialGradient) {
javafx.scene.paint.RadialGradient rgradient = (javafx.scene.paint.RadialGradient) paint;
computeAndSetBackground(rgradient.getStops());
}
}
}
platformWindows.put(platformWindow, this);
}
private void computeAndSetBackground(List<javafx.scene.paint.Stop> stops) {
if (stops.size() == 1) {
javafx.scene.paint.Color color = stops.get(0).getColor();
platformWindow.setBackground((float) color.getRed(),
(float) color.getGreen(), (float) color.getBlue());
} else if (stops.size() > 1) {
javafx.scene.paint.Color color = stops.get(0).getColor();
javafx.scene.paint.Color color2 = stops.get(stops.size() - 1).getColor();
platformWindow.setBackground((float) ((color.getRed() + color2.getRed()) / 2.0),
(float) ((color.getGreen() + color2.getGreen()) / 2.0),
(float) ((color.getBlue() + color2.getBlue()) / 2.0));
}
}
final Window getPlatformWindow() {
return platformWindow;
}
static WindowStage findWindowStage(Window platformWindow) {
return platformWindows.get(platformWindow);
}
protected GlassStage getOwner() {
return owner;
}
protected ViewScene getViewScene() {
return (ViewScene)getScene();
}
StageStyle getStyle() {
return style;
}
@Override public TKScene createTKScene(boolean depthBuffer, boolean msaa, @SuppressWarnings("removal") AccessControlContext acc) {
ViewScene scene = new ViewScene(depthBuffer, msaa);
scene.setSecurityContext(acc);
return scene;
}
@Override public void setScene(TKScene scene) {
GlassScene oldScene = getScene();
if (oldScene == scene) {
return;
}
exitFullScreen();
super.setScene(scene);
if (scene != null) {
GlassScene newScene = getViewScene();
View view = newScene.getPlatformView();
QuantumToolkit.runWithRenderLock(() -> {
platformWindow.setView(view);
if (oldScene != null) oldScene.updateSceneState();
newScene.updateSceneState();
return null;
});
requestFocus();
} else {
QuantumToolkit.runWithRenderLock(() -> {
if (platformWindow != null) {
platformWindow.setView(null);
}
if (oldScene != null) {
oldScene.updateSceneState();
}
return null;
});
}
if (oldScene != null) {
ViewPainter painter = ((ViewScene)oldScene).getPainter();
QuantumRenderer.getInstance().disposePresentable(painter.presentable);
}
}
@Override public void setBounds(float x, float y, boolean xSet, boolean ySet,
float w, float h, float cw, float ch,
float xGravity, float yGravity,
float renderScaleX, float renderScaleY)
{
if (renderScaleX > 0.0 || renderScaleY > 0.0) {
if (renderScaleX > 0.0) {
platformWindow.setRenderScaleX(renderScaleX);
}
if (renderScaleY > 0.0) {
platformWindow.setRenderScaleY(renderScaleY);
}
ViewScene vscene = getViewScene();
if (vscene != null) {
vscene.updateSceneState();
vscene.entireSceneNeedsRepaint();
}
}
if (xSet || ySet || w > 0 || h > 0 || cw > 0 || ch > 0) {
platformWindow.setBounds(x, y, xSet, ySet, w, h, cw, ch, xGravity, yGravity);
}
}
@Override
public float getPlatformScaleX() {
return platformWindow.getPlatformScaleX();
}
@Override
public float getPlatformScaleY() {
return platformWindow.getPlatformScaleY();
}
@Override
public float getOutputScaleX() {
return platformWindow.getOutputScaleX();
}
@Override
public float getOutputScaleY() {
return platformWindow.getOutputScaleY();
}
@Override public void setMinimumSize(int minWidth, int minHeight) {
minWidth = (int) Math.ceil(minWidth * getPlatformScaleX());
minHeight = (int) Math.ceil(minHeight * getPlatformScaleY());
platformWindow.setMinimumSize(minWidth, minHeight);
}
@Override public void setMaximumSize(int maxWidth, int maxHeight) {
maxWidth = (int) Math.ceil(maxWidth * getPlatformScaleX());
maxHeight = (int) Math.ceil(maxHeight * getPlatformScaleY());
platformWindow.setMaximumSize(maxWidth, maxHeight);
}
static Image findBestImage(java.util.List icons, int width, int height) {
Image image = null;
double bestSimilarity = 3;
for (Object icon : icons) {
Image im = (Image)icon;
if (im == null || !(im.getPixelFormat() == PixelFormat.BYTE_RGB ||
im.getPixelFormat() == PixelFormat.BYTE_BGRA_PRE ||
im.getPixelFormat() == PixelFormat.BYTE_GRAY))
{
continue;
}
int iw = im.getWidth();
int ih = im.getHeight();
if (iw > 0 && ih > 0) {
double scaleFactor = Math.min((double)width / (double)iw,
(double)height / (double)ih);
int adjw;
int adjh;
double scaleMeasure = 1;
if (scaleFactor >= 2) {
scaleFactor = Math.floor(scaleFactor);
adjw = iw * (int)scaleFactor;
adjh = ih * (int)scaleFactor;
scaleMeasure = 1.0 - 0.5 / scaleFactor;
} else if (scaleFactor >= 1) {
scaleFactor = 1.0;
adjw = iw;
adjh = ih;
scaleMeasure = 0;
} else if (scaleFactor >= 0.75) {
scaleFactor = 0.75;
adjw = iw * 3 / 4;
adjh = ih * 3 / 4;
scaleMeasure = 0.3;
} else if (scaleFactor >= 0.6666) {
scaleFactor = 0.6666;
adjw = iw * 2 / 3;
adjh = ih * 2 / 3;
scaleMeasure = 0.33;
} else {
double scaleDivider = Math.ceil(1.0 / scaleFactor);
scaleFactor = 1.0 / scaleDivider;
adjw = (int)Math.round((double)iw / scaleDivider);
adjh = (int)Math.round((double)ih / scaleDivider);
scaleMeasure = 1.0 - 1.0 / scaleDivider;
}
double similarity = ((double)width - (double)adjw) / (double)width +
((double)height - (double)adjh) / (double)height +
scaleMeasure;
if (similarity < bestSimilarity) {
bestSimilarity = similarity;
image = im;
}
if (similarity == 0) break;
}
}
return image;
}
@Override public void setIcons(java.util.List icons) {
int SMALL_ICON_HEIGHT = 32;
int SMALL_ICON_WIDTH = 32;
if (PlatformUtil.isMac()) {
SMALL_ICON_HEIGHT = 128;
SMALL_ICON_WIDTH = 128;
} else if (PlatformUtil.isWindows()) {
SMALL_ICON_HEIGHT = 32;
SMALL_ICON_WIDTH = 32;
} else if (PlatformUtil.isLinux()) {
SMALL_ICON_HEIGHT = 128;
SMALL_ICON_WIDTH = 128;
}
if (icons == null || icons.size() < 1) {
platformWindow.setIcon(null);
return;
}
Image image = findBestImage(icons, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT);
if (image == null) {
return;
}
PushbroomScaler scaler = ScalerFactory.createScaler(image.getWidth(), image.getHeight(),
image.getBytesPerPixelUnit(),
SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, true);
ByteBuffer buf = (ByteBuffer) image.getPixelBuffer();
byte bytes[] = new byte[buf.limit()];
int iheight = image.getHeight();
for (int z = 0; z < iheight; z++) {
buf.position(z*image.getScanlineStride());
buf.get(bytes, 0, image.getScanlineStride());
scaler.putSourceScanline(bytes, 0);
}
buf.rewind();
final Image img = image.iconify(scaler.getDestination(), SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT);
platformWindow.setIcon(PixelUtils.imageToPixels(img));
}
@Override public void setTitle(String title) {
platformWindow.setTitle(title);
}
@Override public void setVisible(final boolean visible) {
if (!visible) {
removeActiveWindow(this);
if (modality == Modality.WINDOW_MODAL) {
if (owner != null && owner instanceof WindowStage) {
((WindowStage) owner).setEnabled(true);
}
} else if (modality == Modality.APPLICATION_MODAL) {
windowsSetEnabled(true);
}
if (!isPopupStage && owner != null && owner instanceof WindowStage) {
WindowStage ownerStage = (WindowStage)owner;
ownerStage.requestToFront();
}
}
QuantumToolkit.runWithRenderLock(() -> {
if (platformWindow != null) {
platformWindow.setVisible(visible);
}
super.setVisible(visible);
return null;
});
if (visible) {
if (modality == Modality.WINDOW_MODAL) {
if (owner != null && owner instanceof WindowStage) {
((WindowStage) owner).setEnabled(false);
}
} else if (modality == Modality.APPLICATION_MODAL) {
windowsSetEnabled(false);
}
}
applyFullScreen();
}
@Override boolean isVisible() {
return platformWindow.isVisible();
}
@Override public void setOpacity(float opacity) {
platformWindow.setAlpha(opacity);
GlassScene gs = getScene();
if (gs != null) {
gs.entireSceneNeedsRepaint();
}
}
public boolean needsUpdateWindow() {
return transparent && (Application.GetApplication().shouldUpdateWindow());
}
@Override public void setIconified(boolean iconified) {
if (platformWindow.isMinimized() == iconified) {
return;
}
platformWindow.minimize(iconified);
}
@Override public void setMaximized(boolean maximized) {
if (platformWindow.isMaximized() == maximized) {
return;
}
platformWindow.maximize(maximized);
}
@Override
public void setAlwaysOnTop(boolean alwaysOnTop) {
if (securityDialog) return;
if (isAlwaysOnTop == alwaysOnTop) {
return;
}
if (alwaysOnTop) {
if (hasPermission(SET_WINDOW_ALWAYS_ON_TOP_PERMISSION)) {
platformWindow.setLevel(Level.FLOATING);
} else {
alwaysOnTop = false;
if (stageListener != null) {
stageListener.changedAlwaysOnTop(alwaysOnTop);
}
}
} else {
platformWindow.setLevel(Level.NORMAL);
}
isAlwaysOnTop = alwaysOnTop;
}
@Override public void setResizable(boolean resizable) {
platformWindow.setResizable(resizable);
}
boolean isTrustedFullScreen() {
return hasPermission(UNRESTRICTED_FULL_SCREEN_PERMISSION);
}
void exitFullScreen() {
setFullScreen(false);
}
private boolean hasPermission(Permission perm) {
try {
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(perm, getAccessControlContext());
}
return true;
} catch (SecurityException se) {
return false;
}
}
private boolean fullScreenFromUserEvent = false;
private KeyCombination savedFullScreenExitKey = null;
public final KeyCombination getSavedFullScreenExitKey() {
return savedFullScreenExitKey;
}
private void applyFullScreen() {
if (platformWindow == null) {
return;
}
View v = platformWindow.getView();
if (isVisible() && v != null && v.isInFullscreen() != isInFullScreen) {
if (isInFullScreen) {
final boolean isTrusted = isTrustedFullScreen();
if (!isTrusted && !fullScreenFromUserEvent) {
exitFullScreen();
fullscreenChanged(false);
} else {
v.enterFullscreen(false, false, false);
if (warning != null && warning.inWarningTransition()) {
warning.setView(getViewScene());
} else {
boolean showWarning = true;
KeyCombination key = null;
String exitMessage = null;
if (isTrusted && (fxStage != null)) {
key = fxStage.getFullScreenExitKeyCombination();
exitMessage = fxStage.getFullScreenExitHint();
}
savedFullScreenExitKey =
key == null
? defaultFullScreenExitKeycombo
: key;
if (
"".equals(exitMessage) ||
(savedFullScreenExitKey.equals(KeyCombination.NO_MATCH))
) {
showWarning = false;
}
if (showWarning && exitMessage == null) {
if (key == null) {
exitMessage = RESOURCES.getString("OverlayWarningESC");
} else {
String f = RESOURCES.getString("OverlayWarningKey");
exitMessage = f.format(f, savedFullScreenExitKey.toString());
}
}
if (showWarning && warning == null) {
setWarning(new OverlayWarning(getViewScene()));
}
if (showWarning && warning != null) {
warning.warn(exitMessage);
}
}
}
} else {
if (warning != null) {
warning.cancel();
setWarning(null);
}
v.exitFullscreen(false);
}
fullScreenFromUserEvent = false;
} else if (!isVisible() && warning != null) {
warning.cancel();
setWarning(null);
}
}
void setWarning(OverlayWarning newWarning) {
this.warning = newWarning;
getViewScene().synchroniseOverlayWarning();
}
OverlayWarning getWarning() {
return warning;
}
@Override public void setFullScreen(boolean fullScreen) {
if (isInFullScreen == fullScreen) {
return;
}
if (isInAllowedEventHandler()) {
fullScreenFromUserEvent = true;
}
GlassStage fsWindow = activeFSWindow.get();
if (fullScreen && (fsWindow != null)) {
fsWindow.setFullScreen(false);
}
isInFullScreen = fullScreen;
applyFullScreen();
if (fullScreen) {
activeFSWindow.set(this);
}
}
@SuppressWarnings("removal")
void fullscreenChanged(final boolean fs) {
if (!fs) {
if (activeFSWindow.compareAndSet(this, null)) {
isInFullScreen = false;
}
} else {
isInFullScreen = true;
activeFSWindow.set(this);
}
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (stageListener != null) {
stageListener.changedFullscreen(fs);
}
return null;
}, getAccessControlContext());
}
@Override public void toBack() {
platformWindow.toBack();
}
@Override public void toFront() {
platformWindow.requestFocus();
platformWindow.toFront();
}
private boolean isClosePostponed = false;
private Window deadWindow = null;
@Override
public void postponeClose() {
isClosePostponed = true;
}
@Override
public void closePostponed() {
if (deadWindow != null) {
deadWindow.close();
deadWindow = null;
}
}
@Override public void close() {
super.close();
QuantumToolkit.runWithRenderLock(() -> {
if (platformWindow != null) {
platformWindows.remove(platformWindow);
if (isClosePostponed) {
deadWindow = platformWindow;
} else {
platformWindow.close();
}
platformWindow = null;
}
GlassScene oldScene = getViewScene();
if (oldScene != null) {
oldScene.updateSceneState();
}
return null;
});
}
void setPlatformWindowClosed() {
if (platformWindow != null) {
platformWindows.remove(platformWindow);
platformWindow = null;
}
}
static void addActiveWindow(WindowStage window) {
activeWindows.remove(window);
activeWindows.add(window);
}
static void removeActiveWindow(WindowStage window) {
activeWindows.remove(window);
}
final void handleFocusDisabled() {
if (activeWindows.isEmpty()) {
return;
}
WindowStage window = activeWindows.get(activeWindows.size() - 1);
window.setIconified(false);
window.requestToFront();
window.requestFocus();
}
@Override public boolean grabFocus() {
return platformWindow.grabFocus();
}
@Override public void ungrabFocus() {
platformWindow.ungrabFocus();
}
@Override public void requestFocus() {
platformWindow.requestFocus();
}
@Override public void requestFocus(FocusCause cause) {
switch (cause) {
case TRAVERSED_FORWARD:
platformWindow.requestFocus(WindowEvent.FOCUS_GAINED_FORWARD);
break;
case TRAVERSED_BACKWARD:
platformWindow.requestFocus(WindowEvent.FOCUS_GAINED_BACKWARD);
break;
case ACTIVATED:
platformWindow.requestFocus(WindowEvent.FOCUS_GAINED);
break;
case DEACTIVATED:
platformWindow.requestFocus(WindowEvent.FOCUS_LOST);
break;
}
}
@Override
protected void setPlatformEnabled(boolean enabled) {
super.setPlatformEnabled(enabled);
if (platformWindow != null) {
platformWindow.setEnabled(enabled);
}
if (enabled) {
if (platformWindow != null && platformWindow.isEnabled()
&& modality == Modality.APPLICATION_MODAL) {
requestToFront();
}
} else {
removeActiveWindow(this);
}
}
@Override
public void setEnabled(boolean enabled) {
if ((owner != null) && (owner instanceof WindowStage)) {
((WindowStage) owner).setEnabled(enabled);
}
if (enabled && (platformWindow == null || platformWindow.isClosed())) {
return;
}
setPlatformEnabled(enabled);
}
@Override
public long getRawHandle() {
return platformWindow.getRawHandle();
}
protected void requestToFront() {
if (platformWindow != null) {
platformWindow.toFront();
platformWindow.requestFocus();
}
}
public void setInAllowedEventHandler(boolean inAllowedEventHandler) {
this.inAllowedEventHandler = inAllowedEventHandler;
}
private boolean isInAllowedEventHandler() {
return inAllowedEventHandler;
}
@Override
public void requestInput(String text, int type, double width, double height,
double Mxx, double Mxy, double Mxz, double Mxt,
double Myx, double Myy, double Myz, double Myt,
double Mzx, double Mzy, double Mzz, double Mzt) {
platformWindow.requestInput(text, type, width, height,
Mxx, Mxy, Mxz, Mxt,
Myx, Myy, Myz, Myt,
Mzx, Mzy, Mzz, Mzt);
}
@Override
public void releaseInput() {
platformWindow.releaseInput();
}
@Override public void setRTL(boolean b) {
rtl = b;
}
}
