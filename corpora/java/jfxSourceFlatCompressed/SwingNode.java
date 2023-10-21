package javafx.embed.swing;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Window;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.embed.swing.Disposer;
import com.sun.javafx.embed.swing.DisposerRecord;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.prism.NGExternalNode;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.stage.FocusUngrabEvent;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.NodeHelper;
import static javafx.stage.WindowEvent.WINDOW_HIDDEN;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.newimpl.SwingNodeInteropN;
public class SwingNode extends Node {
private static boolean isThreadMerged;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PrivilegedAction<Object>() {
public Object run() {
isThreadMerged = Boolean.valueOf(
System.getProperty("javafx.embed.singleThread"));
return null;
}
});
SwingNodeHelper.setSwingNodeAccessor(new SwingNodeHelper.SwingNodeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((SwingNode) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((SwingNode) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((SwingNode) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((SwingNode) node).doComputeContains(localX, localY);
}
@Override
public Object getLightweightFrame(SwingNode node) {
return node.getLightweightFrame();
}
@Override
public ReentrantLock getPaintLock(SwingNode node) {
return node.getPaintLock();
}
@Override
public void setImageBuffer(SwingNode node, final int[] data,
final int x, final int y,
final int w, final int h, final int linestride,
final double scaleX, final double scaleY) {
node.setImageBuffer(data, x, y, w, h, linestride, scaleX, scaleY);
}
@Override
public void setImageBounds(SwingNode node, final int x, final int y,
final int w, final int h) {
node.setImageBounds(x, y, w, h);
}
@Override
public void repaintDirtyRegion(SwingNode node, final int dirtyX, final int dirtyY,
final int dirtyWidth, final int dirtyHeight) {
node.repaintDirtyRegion(dirtyX, dirtyY, dirtyWidth, dirtyHeight);
}
@Override
public void ungrabFocus(SwingNode node, boolean postUngrabEvent) {
node.ungrabFocus(postUngrabEvent);
}
@Override
public void setSwingPrefWidth(SwingNode node, int swingPrefWidth) {
node.swingPrefWidth = swingPrefWidth;
}
@Override
public void setSwingPrefHeight(SwingNode node, int swingPrefHeight) {
node.swingPrefHeight = swingPrefHeight;
}
@Override
public void setSwingMaxWidth(SwingNode node, int swingMaxWidth) {
node.swingMaxWidth = swingMaxWidth;
}
@Override
public void setSwingMaxHeight(SwingNode node, int swingMaxHeight) {
node.swingMaxHeight = swingMaxHeight;
}
@Override
public void setSwingMinWidth(SwingNode node, int swingMinWidth) {
node.swingMinWidth = swingMinWidth;
}
@Override
public void setSwingMinHeight(SwingNode node, int swingMinHeight) {
node.swingMinHeight = swingMinHeight;
}
@Override
public void setGrabbed(SwingNode node, boolean grab) {
node.grabbed = grab;
}
});
}
private double fxWidth;
private double fxHeight;
private int swingPrefWidth;
private int swingPrefHeight;
private int swingMaxWidth;
private int swingMaxHeight;
private int swingMinWidth;
private int swingMinHeight;
private volatile JComponent content;
private volatile Object lwFrame;
private final Object getLightweightFrame() { return lwFrame; }
private NGExternalNode peer;
private final ReentrantLock paintLock = new ReentrantLock();
private ReentrantLock getPaintLock() {
return paintLock;
}
private boolean skipBackwardUnrgabNotification;
private boolean grabbed;
private Timer deactivate;
private SwingNodeInteropN swNodeIOP;
{
SwingNodeHelper.initHelper(this);
}
public SwingNode() {
swNodeIOP = new SwingNodeInteropN();
setFocusTraversable(true);
setEventHandler(MouseEvent.ANY, new SwingMouseEventHandler());
setEventHandler(KeyEvent.ANY, new SwingKeyEventHandler());
setEventHandler(ScrollEvent.SCROLL, new SwingScrollEventHandler());
focusedProperty().addListener((observable, oldValue, newValue) -> {
activateLwFrame(newValue);
});
javafx.scene.text.Font.getFamilies();
}
private EventHandler windowHiddenHandler = (Event event) -> {
if (lwFrame != null && event.getTarget() instanceof Window) {
final Window w = (Window) event.getTarget();
TKStage tk = WindowHelper.getPeer(w);
if (tk != null) {
if (isThreadMerged) {
swNodeIOP.overrideNativeWindowHandle(lwFrame, 0L, null);
} else {
tk.postponeClose();
SwingNodeHelper.runOnEDT(() -> {
swNodeIOP.overrideNativeWindowHandle(lwFrame, 0L,
(Runnable) () -> SwingNodeHelper.runOnFxThread(
() -> tk.closePostponed()));
});
}
}
}
};
private Window hWindow = null;
private void notifyNativeHandle(Window window) {
if (hWindow != window) {
if (hWindow != null) {
hWindow.removeEventHandler(WINDOW_HIDDEN, windowHiddenHandler);
}
if (window != null) {
window.addEventHandler(WINDOW_HIDDEN, windowHiddenHandler);
}
hWindow = window;
}
if (lwFrame != null) {
long rawHandle = 0L;
if (window != null) {
TKStage tkStage = WindowHelper.getPeer(window);
if (tkStage != null) {
rawHandle = tkStage.getRawHandle();
}
}
swNodeIOP.overrideNativeWindowHandle(lwFrame, rawHandle, null);
}
}
public void setContent(final JComponent content) {
this.content = content;
SwingNodeHelper.runOnEDT(() -> setContentImpl(content));
}
public JComponent getContent() {
return content;
}
private void setContentImpl(JComponent content) {
if (lwFrame != null) {
swNodeIOP.disposeFrame(lwFrame);
lwFrame = null;
}
if (content != null) {
lwFrame = swNodeIOP.createLightweightFrame();
SwingNodeWindowFocusListener snfListener =
new SwingNodeWindowFocusListener(this);
swNodeIOP.addWindowFocusListener(lwFrame, snfListener);
if (getScene() != null) {
Window window = getScene().getWindow();
if (window != null) {
swNodeIOP.notifyDisplayChanged(lwFrame, window.getRenderScaleX(),
window.getRenderScaleY());
}
}
swNodeIOP.setContent(lwFrame, swNodeIOP.createSwingNodeContent(content, this));
swNodeIOP.setVisible(lwFrame, true);
Disposer.addRecord(this, swNodeIOP.createSwingNodeDisposer(lwFrame));
if (getScene() != null) {
notifyNativeHandle(getScene().getWindow());
}
SwingNodeHelper.runOnFxThread(() -> {
locateLwFrame();
if (focusedProperty().get()) {
activateLwFrame(true);
}
});
}
}
private List<Runnable> peerRequests = new ArrayList<>();
void setImageBuffer(final int[] data,
final int x, final int y,
final int w, final int h,
final int linestride,
final double scaleX,
final double scaleY)
{
Runnable r = () -> peer.setImageBuffer(IntBuffer.wrap(data), x, y, w, h,
w, h, linestride, scaleX, scaleY);
SwingNodeHelper.runOnFxThread(() -> {
if (peer != null) {
r.run();
} else {
peerRequests.clear();
peerRequests.add(r);
}
});
}
void setImageBounds(final int x, final int y, final int w, final int h) {
Runnable r = () -> peer.setImageBounds(x, y, w, h, w, h);
SwingNodeHelper.runOnFxThread(() -> {
if (peer != null) {
r.run();
} else {
peerRequests.add(r);
}
});
}
void repaintDirtyRegion(final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight) {
Runnable r = () -> {
peer.repaintDirtyRegion(dirtyX, dirtyY, dirtyWidth, dirtyHeight);
NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
};
SwingNodeHelper.runOnFxThread(() -> {
if (peer != null) {
r.run();
} else {
peerRequests.add(r);
}
});
}
@Override public boolean isResizable() {
return true;
}
@Override public void resize(final double width, final double height) {
super.resize(width, height);
if (width != this.fxWidth || height != this.fxHeight) {
this.fxWidth = width;
this.fxHeight = height;
NodeHelper.geomChanged(this);
NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
locateLwFrame();
}
});
}
}
@Override
public double prefWidth(double height) {
return swingPrefWidth;
}
@Override
public double prefHeight(double width) {
return swingPrefHeight;
}
@Override public double maxWidth(double height) {
return swingMaxWidth;
}
@Override public double maxHeight(double width) {
return swingMaxHeight;
}
@Override public double minWidth(double height) {
return swingMinWidth;
}
@Override public double minHeight(double width) {
return swingMinHeight;
}
private boolean doComputeContains(double localX, double localY) {
return true;
}
private final InvalidationListener locationListener = observable -> {
locateLwFrame();
};
@SuppressWarnings("removal")
private final EventHandler<FocusUngrabEvent> ungrabHandler = event -> {
if (!skipBackwardUnrgabNotification) {
if (lwFrame != null) {
AccessController.doPrivileged(new PostEventAction(
swNodeIOP.createUngrabEvent(lwFrame)));
}
}
};
private final ChangeListener<Boolean> windowVisibleListener = (observable, oldValue, newValue) -> {
if (!newValue) {
disposeLwFrame();
} else {
setContent(content);
}
};
private final ChangeListener<Window> sceneWindowListener = (observable, oldValue, newValue) -> {
if (oldValue != null) {
removeWindowListeners(oldValue);
}
notifyNativeHandle(newValue);
if (newValue != null) {
addWindowListeners(newValue);
}
};
private void removeSceneListeners(Scene scene) {
Window window = scene.getWindow();
if (window != null) {
removeWindowListeners(window);
}
scene.windowProperty().removeListener(sceneWindowListener);
}
private void addSceneListeners(final Scene scene) {
Window window = scene.getWindow();
if (window != null) {
addWindowListeners(window);
notifyNativeHandle(window);
}
scene.windowProperty().addListener(sceneWindowListener);
}
private void addWindowListeners(final Window window) {
window.xProperty().addListener(locationListener);
window.yProperty().addListener(locationListener);
window.widthProperty().addListener(locationListener);
window.heightProperty().addListener(locationListener);
window.renderScaleXProperty().addListener(locationListener);
window.addEventHandler(FocusUngrabEvent.FOCUS_UNGRAB, ungrabHandler);
window.showingProperty().addListener(windowVisibleListener);
setLwFrameScale(window.getRenderScaleX(), window.getRenderScaleY());
}
private void removeWindowListeners(final Window window) {
window.xProperty().removeListener(locationListener);
window.yProperty().removeListener(locationListener);
window.widthProperty().removeListener(locationListener);
window.heightProperty().removeListener(locationListener);
window.renderScaleXProperty().removeListener(locationListener);
window.removeEventHandler(FocusUngrabEvent.FOCUS_UNGRAB, ungrabHandler);
window.showingProperty().removeListener(windowVisibleListener);
}
private NGNode doCreatePeer() {
peer = new NGExternalNode();
peer.setLock(paintLock);
for (Runnable request : peerRequests) {
request.run();
}
peerRequests = null;
if (getScene() != null) {
addSceneListeners(getScene());
}
sceneProperty().addListener((observable, oldValue, newValue) -> {
if (oldValue != null) {
removeSceneListeners(oldValue);
disposeLwFrame();
}
if (newValue != null) {
if (content != null && lwFrame == null) {
setContent(content);
}
addSceneListeners(newValue);
}
});
NodeHelper.treeVisibleProperty(this).addListener((observable, oldValue, newValue) -> {
setLwFrameVisible(newValue);
});
return peer;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_VISIBLE)
|| NodeHelper.isDirty(this, DirtyBits.NODE_BOUNDS)) {
locateLwFrame();
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
peer.markContentDirty();
}
}
private void locateLwFrame() {
if (getScene() == null
|| lwFrame == null
|| getScene().getWindow() == null
|| !getScene().getWindow().isShowing()) {
return;
}
Window w = getScene().getWindow();
double renderScaleX = w.getRenderScaleX();
double renderScaleY = w.getRenderScaleY();
final Point2D loc = localToScene(0, 0);
final int windowX = (int) (w.getX());
final int windowY = (int) (w.getY());
final int windowW = (int) (w.getWidth());
final int windowH = (int) (w.getHeight());
final int frameX = (int) Math.round(w.getX() + getScene().getX() + loc.getX());
final int frameY = (int) Math.round(w.getY() + getScene().getY() + loc.getY());
final int frameW = (int) (fxWidth);
final int frameH = (int) (fxHeight);
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
swNodeIOP.notifyDisplayChanged(lwFrame, renderScaleX, renderScaleY);
swNodeIOP.setBounds(lwFrame, frameX, frameY, frameW, frameH);
swNodeIOP.setHostBounds(lwFrame, windowX, windowY, windowW, windowH);
}
});
}
private void activateLwFrame(final boolean activate) {
if (lwFrame == null) {
return;
}
if (PlatformUtil.isLinux()) {
if (deactivate == null || !deactivate.isRunning()) {
if (!activate) {
deactivate = new Timer(50, (e) -> {
{
if (lwFrame != null) {
swNodeIOP.emulateActivation(lwFrame, false);
}
}
});
deactivate.start();
return;
}
} else {
deactivate.stop();
}
}
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
swNodeIOP.emulateActivation(lwFrame, activate);
}
});
}
private void disposeLwFrame() {
if (lwFrame == null) {
return;
}
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
swNodeIOP.disposeFrame(lwFrame);
lwFrame = null;
}
});
}
private void setLwFrameVisible(final boolean visible) {
if (lwFrame == null) {
return;
}
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
swNodeIOP.setVisible(lwFrame, visible);
}
});
}
private void setLwFrameScale(final double scaleX, final double scaleY) {
if (lwFrame == null) {
return;
}
SwingNodeHelper.runOnEDT(() -> {
if (lwFrame != null) {
swNodeIOP.notifyDisplayChanged(lwFrame, scaleX, scaleY);
}
});
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
bounds.deriveWithNewBounds(0, 0, 0, (float)fxWidth, (float)fxHeight, 0);
tx.transform(bounds, bounds);
return bounds;
}
private static class SwingNodeWindowFocusListener implements WindowFocusListener {
private WeakReference<SwingNode> swingNodeRef;
SwingNodeWindowFocusListener(SwingNode swingNode) {
this.swingNodeRef = new WeakReference<SwingNode>(swingNode);
}
@Override
public void windowGainedFocus(WindowEvent e) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
swingNode.requestFocus();
}
});
}
@Override
public void windowLostFocus(WindowEvent e) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
swingNode.ungrabFocus(true);
}
});
}
}
private void ungrabFocus(boolean postUngrabEvent) {
if (PlatformUtil.isLinux()) return;
if (grabbed &&
getScene() != null &&
getScene().getWindow() != null &&
WindowHelper.getPeer(getScene().getWindow()) != null)
{
skipBackwardUnrgabNotification = !postUngrabEvent;
WindowHelper.getPeer(getScene().getWindow()).ungrabFocus();
skipBackwardUnrgabNotification = false;
grabbed = false;
}
}
private class PostEventAction implements PrivilegedAction<Void> {
private AWTEvent event;
PostEventAction(AWTEvent event) {
this.event = event;
}
@Override
public Void run() {
EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
eq.postEvent(event);
return null;
}
}
private class SwingMouseEventHandler implements EventHandler<MouseEvent> {
private final Set<MouseButton> mouseClickedAllowed = new HashSet<>();
@Override
public void handle(MouseEvent event) {
Object frame = swNodeIOP.getLightweightFrame();
if (frame == null) {
return;
}
int swingID = SwingEvents.fxMouseEventTypeToMouseID(event);
if (swingID < 0) {
return;
}
event.consume();
final EventType<?> type = event.getEventType();
if (type == MouseEvent.MOUSE_PRESSED) {
mouseClickedAllowed.add(event.getButton());
} else if (type == MouseEvent.MOUSE_RELEASED) {
} else if (type == MouseEvent.MOUSE_DRAGGED) {
mouseClickedAllowed.clear();
} else if (type == MouseEvent.MOUSE_CLICKED) {
if (event.getClickCount() == 1 && !mouseClickedAllowed.contains(event.getButton())) {
return;
}
mouseClickedAllowed.remove(event.getButton());
}
int swingModifiers = SwingEvents.fxMouseModsToMouseMods(event);
boolean swingPopupTrigger = event.isPopupTrigger();
int swingButton = SwingEvents.fxMouseButtonToMouseButton(event);
long swingWhen = System.currentTimeMillis();
int relX = (int) Math.round(event.getX());
int relY = (int) Math.round(event.getY());
int absX = (int) Math.round(event.getScreenX());
int absY = (int) Math.round(event.getScreenY());
java.awt.event.MouseEvent mouseEvent =
swNodeIOP.createMouseEvent(
frame, swingID, swingWhen, swingModifiers,
relX, relY, absX, absY,
event.getClickCount(), swingPopupTrigger, swingButton);
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PostEventAction(mouseEvent));
}
}
private class SwingScrollEventHandler implements EventHandler<ScrollEvent> {
@Override
public void handle(ScrollEvent event) {
Object frame = swNodeIOP.getLightweightFrame();
if (frame == null) {
return;
}
int swingModifiers = SwingEvents.fxScrollModsToMouseWheelMods(event);
final boolean isShift = (swingModifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
if (!isShift && event.getDeltaY() != 0.0) {
sendMouseWheelEvent(frame, event.getX(), event.getY(),
swingModifiers, event.getDeltaY() / event.getMultiplierY());
}
final double delta = isShift && event.getDeltaY() != 0.0
? event.getDeltaY() / event.getMultiplierY()
: event.getDeltaX() / event.getMultiplierX();
if (delta != 0.0) {
swingModifiers |= InputEvent.SHIFT_DOWN_MASK;
sendMouseWheelEvent(frame, event.getX(), event.getY(),
swingModifiers, delta);
}
}
private void sendMouseWheelEvent(Object source, double fxX, double fxY, int swingModifiers, double delta) {
int wheelRotation = (int) delta;
int signum = (int) Math.signum(delta);
if (signum * delta < 1) {
wheelRotation = signum;
}
int x = (int) Math.round(fxX);
int y = (int) Math.round(fxY);
MouseWheelEvent mouseWheelEvent =
swNodeIOP.createMouseWheelEvent(source, swingModifiers, x, y, -wheelRotation);
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PostEventAction(mouseWheelEvent));
}
}
private class SwingKeyEventHandler implements EventHandler<KeyEvent> {
@Override
public void handle(KeyEvent event) {
Object frame = swNodeIOP.getLightweightFrame();
if (frame == null) {
return;
}
if (event.getCharacter().isEmpty()) {
return;
}
if (event.getCode() == KeyCode.LEFT ||
event.getCode() == KeyCode.RIGHT ||
event.getCode() == KeyCode.UP ||
event.getCode() == KeyCode.DOWN ||
event.getCode() == KeyCode.TAB)
{
event.consume();
}
int swingID = SwingEvents.fxKeyEventTypeToKeyID(event);
if (swingID < 0) {
return;
}
int swingModifiers = SwingEvents.fxKeyModsToKeyMods(event);
int swingKeyCode = event.getCode().getCode();
char swingChar = event.getCharacter().charAt(0);
if (event.getEventType() == javafx.scene.input.KeyEvent.KEY_PRESSED) {
String text = event.getText();
if (text.length() == 1) {
swingChar = text.charAt(0);
}
}
long swingWhen = System.currentTimeMillis();
java.awt.event.KeyEvent keyEvent = swNodeIOP.createKeyEvent(frame,
swingID, swingWhen, swingModifiers, swingKeyCode,
swingChar);
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PostEventAction(keyEvent));
}
}
}
