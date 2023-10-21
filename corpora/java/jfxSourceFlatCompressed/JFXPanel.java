package javafx.embed.swing;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.Insets;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.GraphicsEnvironment;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.InvocationEvent;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.scene.Scene;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.stage.EmbeddedWindow;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.EmbeddedStageInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.embed.swing.SwingDnD;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.SwingCursors;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.embed.swing.newimpl.JFXPanelInteropN;
public class JFXPanel extends JComponent {
private final static PlatformLogger log = PlatformLogger.getLogger(JFXPanel.class.getName());
private static AtomicInteger instanceCount = new AtomicInteger(0);
private static PlatformImpl.FinishListener finishListener;
private transient HostContainer hostContainer;
private transient volatile EmbeddedWindow stage;
private transient volatile Scene scene;
private transient SwingDnD dnd;
private transient EmbeddedStageInterface stagePeer;
private transient EmbeddedSceneInterface scenePeer;
private int pWidth;
private int pHeight;
private double scaleFactorX = 1.0;
private double scaleFactorY = 1.0;
private volatile int pPreferredWidth = -1;
private volatile int pPreferredHeight = -1;
private volatile int screenX = 0;
private volatile int screenY = 0;
private BufferedImage pixelsIm;
private volatile float opacity = 1.0f;
private AtomicInteger disableCount = new AtomicInteger(0);
private boolean isCapturingMouse = false;
private static boolean fxInitialized;
private JFXPanelInteropN jfxPanelIOP;
private synchronized void registerFinishListener() {
if (instanceCount.getAndIncrement() > 0) {
return;
}
finishListener = new PlatformImpl.FinishListener() {
@Override public void idle(boolean implicitExit) {
}
@Override public void exitCalled() {
}
};
PlatformImpl.addListener(finishListener);
}
private synchronized void deregisterFinishListener() {
if (instanceCount.decrementAndGet() > 0) {
return;
}
PlatformImpl.removeListener(finishListener);
finishListener = null;
}
private synchronized static void initFx() {
if (fxInitialized) {
return;
}
@SuppressWarnings("removal")
EventQueue eventQueue = AccessController.doPrivileged(
(PrivilegedAction<EventQueue>) java.awt.Toolkit
.getDefaultToolkit()::getSystemEventQueue);
if (eventQueue.isDispatchThread()) {
SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
final Throwable[] th = {null};
new Thread(() -> {
try {
PlatformImpl.startup(() -> {});
} catch (Throwable t) {
th[0] = t;
} finally {
secondaryLoop.exit();
}
}).start();
secondaryLoop.enter();
if (th[0] != null) {
if (th[0] instanceof RuntimeException) {
throw (RuntimeException) th[0];
} else if (th[0] instanceof Error) {
throw (Error) th[0];
}
throw new RuntimeException("FX initialization failed", th[0]);
}
} else {
PlatformImpl.startup(() -> {});
}
fxInitialized = true;
}
public JFXPanel() {
super();
jfxPanelIOP = new JFXPanelInteropN();
initFx();
hostContainer = new HostContainer();
enableEvents(InputEvent.COMPONENT_EVENT_MASK |
InputEvent.FOCUS_EVENT_MASK |
InputEvent.HIERARCHY_BOUNDS_EVENT_MASK |
InputEvent.HIERARCHY_EVENT_MASK |
InputEvent.MOUSE_EVENT_MASK |
InputEvent.MOUSE_MOTION_EVENT_MASK |
InputEvent.MOUSE_WHEEL_EVENT_MASK |
InputEvent.KEY_EVENT_MASK |
InputEvent.INPUT_METHOD_EVENT_MASK);
setFocusable(true);
setFocusTraversalKeysEnabled(false);
}
public Scene getScene() {
return scene;
}
public void setScene(final Scene newScene) {
if (Toolkit.getToolkit().isFxUserThread()) {
setSceneImpl(newScene);
} else {
@SuppressWarnings("removal")
EventQueue eventQueue = AccessController.doPrivileged(
(PrivilegedAction<EventQueue>) java.awt.Toolkit
.getDefaultToolkit()::getSystemEventQueue);
SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
Platform.runLater(() -> {
try {
setSceneImpl(newScene);
} finally {
secondaryLoop.exit();
}
});
secondaryLoop.enter();
}
}
private void setSceneImpl(Scene newScene) {
if ((stage != null) && (newScene == null)) {
stage.hide();
stage = null;
}
scene = newScene;
if ((stage == null) && (newScene != null)) {
stage = new EmbeddedWindow(hostContainer);
}
if (stage != null) {
stage.setScene(newScene);
if (!stage.isShowing()) {
stage.show();
}
}
}
@Override
public final void setOpaque(boolean opaque) {
if (!opaque) {
super.setOpaque(opaque);
}
}
@Override
public final boolean isOpaque() {
return false;
}
private void sendMouseEventToFX(MouseEvent e) {
if (scenePeer == null || !isFxEnabled()) {
return;
}
switch (e.getID()) {
case MouseEvent.MOUSE_DRAGGED:
case MouseEvent.MOUSE_PRESSED:
case MouseEvent.MOUSE_RELEASED:
if (e.getButton() > 5) return;
break;
}
int extModifiers = e.getModifiersEx();
boolean primaryBtnDown = (extModifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0;
boolean middleBtnDown = (extModifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0;
boolean secondaryBtnDown = (extModifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0;
boolean backBtnDown = (extModifiers & MouseEvent.getMaskForButton(4)) != 0;
boolean forwardBtnDown = (extModifiers & MouseEvent.getMaskForButton(5)) != 0;
if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
if (!isCapturingMouse) {
return;
}
} else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
isCapturingMouse = true;
} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
if (!isCapturingMouse) {
return;
}
isCapturingMouse = primaryBtnDown || middleBtnDown || secondaryBtnDown || backBtnDown || forwardBtnDown;
} else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
return;
}
boolean popupTrigger = false;
if (e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED) {
popupTrigger = e.isPopupTrigger();
}
if(e.getID() == MouseEvent.MOUSE_WHEEL) {
scenePeer.scrollEvent(AbstractEvents.MOUSEEVENT_VERTICAL_WHEEL,
0, -SwingEvents.getWheelRotation(e),
0, 0,
40, 40,
e.getX(), e.getY(),
e.getXOnScreen(), e.getYOnScreen(),
(extModifiers & MouseEvent.SHIFT_DOWN_MASK) != 0,
(extModifiers & MouseEvent.CTRL_DOWN_MASK) != 0,
(extModifiers & MouseEvent.ALT_DOWN_MASK) != 0,
(extModifiers & MouseEvent.META_DOWN_MASK) != 0, false);
} else {
scenePeer.mouseEvent(
SwingEvents.mouseIDToEmbedMouseType(e.getID()),
SwingEvents.mouseButtonToEmbedMouseButton(e.getButton(), extModifiers),
primaryBtnDown, middleBtnDown, secondaryBtnDown,
backBtnDown, forwardBtnDown,
e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(),
(extModifiers & MouseEvent.SHIFT_DOWN_MASK) != 0,
(extModifiers & MouseEvent.CTRL_DOWN_MASK) != 0,
(extModifiers & MouseEvent.ALT_DOWN_MASK) != 0,
(extModifiers & MouseEvent.META_DOWN_MASK) != 0,
popupTrigger);
}
if (e.isPopupTrigger()) {
scenePeer.menuEvent(e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), false);
}
}
@Override
protected void processMouseEvent(MouseEvent e) {
if ((e.getID() == MouseEvent.MOUSE_PRESSED) &&
(e.getButton() == MouseEvent.BUTTON1)) {
if (isFocusable() && !hasFocus()) {
requestFocus();
if (stagePeer != null) {
int focusCause = AbstractEvents.FOCUSEVENT_ACTIVATED;
stagePeer.setFocused(true, focusCause);
}
}
}
sendMouseEventToFX(e);
super.processMouseEvent(e);
}
@Override
protected void processMouseMotionEvent(MouseEvent e) {
sendMouseEventToFX(e);
super.processMouseMotionEvent(e);
}
@Override
protected void processMouseWheelEvent(MouseWheelEvent e) {
sendMouseEventToFX(e);
super.processMouseWheelEvent(e);
}
private void sendKeyEventToFX(final KeyEvent e) {
if (scenePeer == null || !isFxEnabled()) {
return;
}
char[] chars = (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
? new char[] {}
: new char[] { SwingEvents.keyCharToEmbedKeyChar(e.getKeyChar()) };
scenePeer.keyEvent(
SwingEvents.keyIDToEmbedKeyType(e.getID()),
e.getKeyCode(), chars,
SwingEvents.keyModifiersToEmbedKeyModifiers(e.getModifiersEx()));
}
@Override
protected void processKeyEvent(KeyEvent e) {
sendKeyEventToFX(e);
super.processKeyEvent(e);
}
private void sendResizeEventToFX() {
if (stagePeer != null) {
stagePeer.setSize(pWidth, pHeight);
}
if (scenePeer != null) {
scenePeer.setSize(pWidth, pHeight);
}
}
@Override
protected void processComponentEvent(ComponentEvent e) {
switch (e.getID()) {
case ComponentEvent.COMPONENT_RESIZED: {
updateComponentSize();
break;
}
case ComponentEvent.COMPONENT_MOVED: {
if (updateScreenLocation()) {
sendMoveEventToFX();
}
break;
}
default: {
break;
}
}
super.processComponentEvent(e);
}
private void updateComponentSize() {
int oldWidth = pWidth;
int oldHeight = pHeight;
pWidth = Math.max(0, getWidth());
pHeight = Math.max(0, getHeight());
if (getBorder() != null) {
Insets i = getBorder().getBorderInsets(this);
pWidth -= (i.left + i.right);
pHeight -= (i.top + i.bottom);
}
double newScaleFactorX = scaleFactorX;
double newScaleFactorY = scaleFactorY;
Graphics g = getGraphics();
newScaleFactorX = GraphicsEnvironment.getLocalGraphicsEnvironment().
getDefaultScreenDevice().getDefaultConfiguration().
getDefaultTransform().getScaleX();
newScaleFactorY = GraphicsEnvironment.getLocalGraphicsEnvironment().
getDefaultScreenDevice().getDefaultConfiguration().
getDefaultTransform().getScaleY();
if (oldWidth != pWidth || oldHeight != pHeight ||
newScaleFactorX != scaleFactorX || newScaleFactorY != scaleFactorY)
{
createResizePixelBuffer(newScaleFactorX, newScaleFactorY);
if (scenePeer != null) {
scenePeer.setPixelScaleFactors((float) newScaleFactorX,
(float) newScaleFactorY);
}
scaleFactorX = newScaleFactorX;
scaleFactorY = newScaleFactorY;
sendResizeEventToFX();
}
}
private boolean updateScreenLocation() {
synchronized (getTreeLock()) {
if (isShowing()) {
Point p = getLocationOnScreen();
screenX = p.x;
screenY = p.y;
return true;
}
}
return false;
}
private void sendMoveEventToFX() {
if (stagePeer == null) {
return;
}
stagePeer.setLocation(screenX, screenY);
}
@Override
protected void processHierarchyBoundsEvent(HierarchyEvent e) {
if (e.getID() == HierarchyEvent.ANCESTOR_MOVED) {
if (updateScreenLocation()) {
sendMoveEventToFX();
}
}
super.processHierarchyBoundsEvent(e);
}
@Override
protected void processHierarchyEvent(HierarchyEvent e) {
if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
if (updateScreenLocation()) {
sendMoveEventToFX();
}
}
super.processHierarchyEvent(e);
}
private void sendFocusEventToFX(final FocusEvent e) {
if ((stage == null) || (stagePeer == null) || !isFxEnabled()) {
return;
}
boolean focused = (e.getID() == FocusEvent.FOCUS_GAINED);
int focusCause = (focused ? AbstractEvents.FOCUSEVENT_ACTIVATED :
AbstractEvents.FOCUSEVENT_DEACTIVATED);
if (focused) {
if (e.getCause() == FocusEvent.Cause.TRAVERSAL_FORWARD) {
focusCause = AbstractEvents.FOCUSEVENT_TRAVERSED_FORWARD;
} else if (e.getCause() == FocusEvent.Cause.TRAVERSAL_BACKWARD) {
focusCause = AbstractEvents.FOCUSEVENT_TRAVERSED_BACKWARD;
}
}
stagePeer.setFocused(focused, focusCause);
}
@Override
protected void processFocusEvent(FocusEvent e) {
sendFocusEventToFX(e);
super.processFocusEvent(e);
}
private void createResizePixelBuffer(double newScaleFactorX, double newScaleFactorY) {
if (scenePeer == null || pWidth <= 0 || pHeight <= 0) {
pixelsIm = null;
} else {
BufferedImage oldIm = pixelsIm;
int newPixelW = (int) Math.ceil(pWidth * newScaleFactorX);
int newPixelH = (int) Math.ceil(pHeight * newScaleFactorY);
pixelsIm = new BufferedImage(newPixelW, newPixelH,
SwingFXUtils.getBestBufferedImageType(
scenePeer.getPixelFormat(), null, false));
if (oldIm != null) {
double ratioX = newScaleFactorX / scaleFactorX;
double ratioY = newScaleFactorY / scaleFactorY;
int oldW = (int)Math.ceil(oldIm.getWidth() * ratioX);
int oldH = (int)Math.ceil(oldIm.getHeight() * ratioY);
Graphics g = pixelsIm.getGraphics();
try {
g.drawImage(oldIm, 0, 0, oldW, oldH, null);
} finally {
g.dispose();
}
}
}
}
@Override
protected void processInputMethodEvent(InputMethodEvent e) {
if (e.getID() == InputMethodEvent.INPUT_METHOD_TEXT_CHANGED) {
sendInputMethodEventToFX(e);
}
super.processInputMethodEvent(e);
}
private void sendInputMethodEventToFX(InputMethodEvent e) {
String t = InputMethodSupport.getTextForEvent(e);
int insertionIndex = 0;
if (e.getCaret() != null) {
insertionIndex = e.getCaret().getInsertionIndex();
}
scenePeer.inputMethodEvent(
javafx.scene.input.InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
InputMethodSupport.inputMethodEventComposed(t, e.getCommittedCharacterCount()),
t.substring(0, e.getCommittedCharacterCount()),
insertionIndex);
}
@Override
protected void paintComponent(Graphics g) {
if (scenePeer == null) {
return;
}
if (pixelsIm == null) {
createResizePixelBuffer(scaleFactorX, scaleFactorY);
if (pixelsIm == null) {
return;
}
}
DataBufferInt dataBuf = (DataBufferInt)pixelsIm.getRaster().getDataBuffer();
int[] pixelsData = dataBuf.getData();
IntBuffer buf = IntBuffer.wrap(pixelsData);
if (!scenePeer.getPixels(buf, pWidth, pHeight)) {
}
Graphics gg = null;
try {
gg = g.create();
if ((opacity < 1.0f) && (gg instanceof Graphics2D)) {
Graphics2D g2d = (Graphics2D)gg;
AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
g2d.setComposite(c);
}
if (getBorder() != null) {
Insets i = getBorder().getBorderInsets(this);
gg.translate(i.left, i.top);
}
gg.drawImage(pixelsIm, 0, 0, pWidth, pHeight, null);
double newScaleFactorX = scaleFactorX;
double newScaleFactorY = scaleFactorY;
newScaleFactorX = GraphicsEnvironment.getLocalGraphicsEnvironment().
getDefaultScreenDevice().getDefaultConfiguration().
getDefaultTransform().getScaleX();
newScaleFactorY = GraphicsEnvironment.getLocalGraphicsEnvironment().
getDefaultScreenDevice().getDefaultConfiguration().
getDefaultTransform().getScaleY();
if (scaleFactorX != newScaleFactorX || scaleFactorY != newScaleFactorY) {
createResizePixelBuffer(newScaleFactorX, newScaleFactorY);
scenePeer.setPixelScaleFactors((float) newScaleFactorX,
(float) newScaleFactorY);
scaleFactorX = newScaleFactorX;
scaleFactorY = newScaleFactorY;
}
} catch (Throwable th) {
th.printStackTrace();
} finally {
if (gg != null) {
gg.dispose();
}
}
}
@Override
public Dimension getPreferredSize() {
if (isPreferredSizeSet() || scenePeer == null) {
return super.getPreferredSize();
}
return new Dimension(pPreferredWidth, pPreferredHeight);
}
private boolean isFxEnabled() {
return this.disableCount.get() == 0;
}
private void setFxEnabled(boolean enabled) {
if (!enabled) {
if (disableCount.incrementAndGet() == 1) {
if (dnd != null) {
dnd.removeNotify();
}
}
} else {
if (disableCount.get() == 0) {
return;
}
if (disableCount.decrementAndGet() == 0) {
if (dnd != null) {
dnd.addNotify();
}
}
}
}
private transient AWTEventListener ungrabListener = event -> {
if (jfxPanelIOP.isUngrabEvent(event)) {
SwingNodeHelper.runOnFxThread(() -> {
if (JFXPanel.this.stagePeer != null &&
getScene() != null &&
getScene().getFocusOwner() != null &&
getScene().getFocusOwner().isFocused()) {
JFXPanel.this.stagePeer.focusUngrab();
}
});
}
if (event instanceof MouseEvent) {
if (event.getID() == MouseEvent.MOUSE_PRESSED && event.getSource() instanceof Component) {
final Window jfxPanelWindow = SwingUtilities.getWindowAncestor(JFXPanel.this);
final Component source = (Component)event.getSource();
final Window eventWindow = source instanceof Window ? (Window)source : SwingUtilities.getWindowAncestor(source);
if (jfxPanelWindow == eventWindow) {
SwingNodeHelper.runOnFxThread(() -> {
if (JFXPanel.this.stagePeer != null) {
JFXPanel.this.stagePeer.focusUngrab();
}
});
}
}
}
};
@SuppressWarnings("removal")
@Override
public void addNotify() {
super.addNotify();
registerFinishListener();
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
JFXPanel.this.getToolkit().addAWTEventListener(ungrabListener,
jfxPanelIOP.getMask());
return null;
});
updateComponentSize();
SwingNodeHelper.runOnFxThread(() -> {
if ((stage != null) && !stage.isShowing()) {
stage.show();
sendMoveEventToFX();
}
});
}
@Override
public InputMethodRequests getInputMethodRequests() {
EmbeddedSceneInterface scene = scenePeer;
if (scene == null) {
return null;
}
return new InputMethodSupport.InputMethodRequestsAdapter(scene.getInputMethodRequests());
}
@SuppressWarnings("removal")
@Override public void removeNotify() {
SwingNodeHelper.runOnFxThread(() -> {
if ((stage != null) && stage.isShowing()) {
stage.hide();
}
});
pixelsIm = null;
pWidth = 0;
pHeight = 0;
super.removeNotify();
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
JFXPanel.this.getToolkit().removeAWTEventListener(ungrabListener);
return null;
});
getInputContext().removeNotify(this);
deregisterFinishListener();
}
private void invokeOnClientEDT(Runnable r) {
jfxPanelIOP.postEvent(this, new InvocationEvent(this, r));
}
final BufferedImage test_getPixelsIm() {
return pixelsIm;
}
private class HostContainer implements HostInterface {
@Override
public void setEmbeddedStage(EmbeddedStageInterface embeddedStage) {
stagePeer = embeddedStage;
if (stagePeer == null) {
return;
}
if (pWidth > 0 && pHeight > 0) {
stagePeer.setSize(pWidth, pHeight);
}
invokeOnClientEDT(() -> {
if (stagePeer != null && JFXPanel.this.isFocusOwner()) {
stagePeer.setFocused(true, AbstractEvents.FOCUSEVENT_ACTIVATED);
}
});
sendMoveEventToFX();
}
@Override
public void setEmbeddedScene(EmbeddedSceneInterface embeddedScene) {
if (scenePeer == embeddedScene) {
return;
}
scenePeer = embeddedScene;
if (scenePeer == null) {
invokeOnClientEDT(() -> {
if (dnd != null) {
dnd.removeNotify();
dnd = null;
}
});
return;
}
if (pWidth > 0 && pHeight > 0) {
scenePeer.setSize(pWidth, pHeight);
}
scenePeer.setPixelScaleFactors((float) scaleFactorX, (float) scaleFactorY);
invokeOnClientEDT(() -> {
dnd = new SwingDnD(JFXPanel.this, scenePeer);
dnd.addNotify();
if (scenePeer != null) {
scenePeer.setDragStartListener(dnd.getDragStartListener());
}
});
}
@Override
public boolean requestFocus() {
return requestFocusInWindow();
}
@Override
public boolean traverseFocusOut(boolean forward) {
KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
if (forward) {
kfm.focusNextComponent(JFXPanel.this);
} else {
kfm.focusPreviousComponent(JFXPanel.this);
}
return true;
}
@Override
public void setPreferredSize(final int width, final int height) {
invokeOnClientEDT(() -> {
JFXPanel.this.pPreferredWidth = width;
JFXPanel.this.pPreferredHeight = height;
JFXPanel.this.revalidate();
});
}
@Override
public void repaint() {
invokeOnClientEDT(() -> {
JFXPanel.this.repaint();
});
}
@Override
public void setEnabled(final boolean enabled) {
JFXPanel.this.setFxEnabled(enabled);
}
@Override
public void setCursor(CursorFrame cursorFrame) {
final Cursor cursor = getPlatformCursor(cursorFrame);
invokeOnClientEDT(() -> {
JFXPanel.this.setCursor(cursor);
});
}
private Cursor getPlatformCursor(final CursorFrame cursorFrame) {
final Cursor cachedPlatformCursor =
cursorFrame.getPlatformCursor(Cursor.class);
if (cachedPlatformCursor != null) {
return cachedPlatformCursor;
}
final Cursor platformCursor =
SwingCursors.embedCursorToCursor(cursorFrame);
cursorFrame.setPlatforCursor(Cursor.class, platformCursor);
return platformCursor;
}
@Override
public boolean grabFocus() {
if (PlatformUtil.isLinux()) return true;
invokeOnClientEDT(() -> {
Window window = SwingUtilities.getWindowAncestor(JFXPanel.this);
if (window != null) {
jfxPanelIOP.grab(JFXPanel.this.getToolkit(), window);
}
});
return true;
}
@Override
public void ungrabFocus() {
if (PlatformUtil.isLinux()) return;
invokeOnClientEDT(() -> {
Window window = SwingUtilities.getWindowAncestor(JFXPanel.this);
if (window != null) {
jfxPanelIOP.ungrab(JFXPanel.this.getToolkit(), window);
}
});
}
}
}
