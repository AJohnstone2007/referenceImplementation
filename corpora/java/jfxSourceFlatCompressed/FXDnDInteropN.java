package com.sun.javafx.embed.swing.newimpl;
import com.sun.javafx.embed.swing.CachingTransferable;
import com.sun.javafx.embed.swing.FXDnD;
import com.sun.javafx.embed.swing.SwingDnD;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.tk.Toolkit;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.SecondaryLoop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.DropTargetContextWrapper;
import jdk.swing.interop.LightweightFrameWrapper;
public class FXDnDInteropN {
public Component findComponentAt(Object frame, int x, int y,
boolean ignoreEnabled) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper) frame;
return lwFrame.findComponentAt(lwFrame, x, y, false);
}
public boolean isCompEqual(Component c, Object frame) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper) frame;
return lwFrame.isCompEqual(c,lwFrame);
}
public int convertModifiersToDropAction(int modifiers,
int supportedActions) {
return DragSourceContextWrapper.convertModifiersToDropAction(modifiers,
supportedActions);
}
public Object createDragSourceContext(DragGestureEvent dge)
throws InvalidDnDOperationException {
return new FXDragSourceContextPeer(dge);
}
public <T extends DragGestureRecognizer> T createDragGestureRecognizer(
DragSource ds, Component c, int srcActions,
DragGestureListener dgl) {
return (T) new FXDragGestureRecognizer(ds, c, srcActions, dgl);
}
private void runOnFxThread(Runnable runnable) {
if (Platform.isFxApplicationThread()) {
runnable.run();
} else {
Platform.runLater(runnable);
}
}
public SwingNode getNode() {
return nodeRef.get();
}
public void setNode(SwingNode swnode) {
this.nodeRef = new WeakReference<SwingNode>(swnode);
}
private WeakReference<SwingNode> nodeRef = null;
private class ComponentMapper<T> {
public int x, y;
public T object = null;
private ComponentMapper(Map<Component, T> map, int xArg, int yArg) {
this.x = xArg;
this.y = yArg;
SwingNode node = getNode();
if (node != null) {
final LightweightFrameWrapper lwFrame = (LightweightFrameWrapper) SwingNodeHelper.getLightweightFrame(node);
Component c = lwFrame.findComponentAt(lwFrame, x, y, false);
if (c == null) return;
synchronized (c.getTreeLock()) {
do {
object = map.get(c);
} while (object == null && (c = c.getParent()) != null);
if (object != null) {
while ((lwFrame.isCompEqual(c, lwFrame)) && c != null) {
x -= c.getX();
y -= c.getY();
c = c.getParent();
}
}
}
}
}
}
public <T> ComponentMapper<T> mapComponent(Map<Component, T> map, int x, int y) {
return new ComponentMapper<T>(map, x, y);
}
private boolean isDragSourceListenerInstalled = false;
private MouseEvent pressEvent = null;
private long pressTime = 0;
private volatile SecondaryLoop loop;
private final Map<Component, FXDragGestureRecognizer> recognizers = new HashMap<>();
private class FXDragGestureRecognizer extends MouseDragGestureRecognizer {
FXDragGestureRecognizer(DragSource ds, Component c, int srcActions,
DragGestureListener dgl)
{
super(ds, c, srcActions, dgl);
if (c != null) recognizers.put(c, this);
}
@Override public void setComponent(Component c) {
final Component old = getComponent();
if (old != null) recognizers.remove(old);
super.setComponent(c);
if (c != null) recognizers.put(c, this);
}
protected void registerListeners() {
runOnFxThread(() -> {
if (!isDragSourceListenerInstalled) {
SwingNode node = getNode();
if (node != null) {
node.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressHandler);
node.addEventHandler(MouseEvent.DRAG_DETECTED, onDragStartHandler);
node.addEventHandler(DragEvent.DRAG_DONE, onDragDoneHandler);
}
isDragSourceListenerInstalled = true;
}
});
}
protected void unregisterListeners() {
runOnFxThread(() -> {
if (isDragSourceListenerInstalled) {
SwingNode node = getNode();
if (node != null) {
node.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressHandler);
node.removeEventHandler(MouseEvent.DRAG_DETECTED, onDragStartHandler);
node.removeEventHandler(DragEvent.DRAG_DONE, onDragDoneHandler);
}
isDragSourceListenerInstalled = false;
}
});
}
private void fireEvent(int x, int y, long evTime, int modifiers) {
appendEvent(new java.awt.event.MouseEvent(getComponent(), java.awt.event.MouseEvent.MOUSE_PRESSED,
evTime, modifiers, x, y, 0, false));
int initialAction = DragSourceContextWrapper.convertModifiersToDropAction(
modifiers, getSourceActions());
fireDragGestureRecognized(initialAction, new java.awt.Point(x, y));
}
}
private void fireEvent(int x, int y, long evTime, int modifiers) {
ComponentMapper<FXDragGestureRecognizer> mapper = mapComponent(recognizers, x, y);
final FXDragGestureRecognizer r = mapper.object;
if (r != null) {
r.fireEvent(mapper.x, mapper.y, evTime, modifiers);
} else {
SwingNodeHelper.leaveFXNestedLoop(this);
}
}
private MouseEvent getInitialGestureEvent() {
return pressEvent;
}
private final EventHandler<MouseEvent> onMousePressHandler = (event) -> {
pressEvent = event;
pressTime = System.currentTimeMillis();
};
private volatile FXDragSourceContextPeer activeDSContextPeer;
private final EventHandler<MouseEvent> onDragStartHandler = (event) -> {
activeDSContextPeer = null;
final MouseEvent firstEv = getInitialGestureEvent();
SwingNodeHelper.runOnEDTAndWait(FXDnDInteropN.this, () -> fireEvent(
(int)firstEv.getX(), (int)firstEv.getY(), pressTime,
SwingEvents.fxMouseModsToMouseMods(firstEv)));
if (activeDSContextPeer == null) return;
event.consume();
SwingNode node = getNode();
if (node != null) {
Dragboard db = node.startDragAndDrop(SwingDnD.dropActionsToTransferModes(
activeDSContextPeer.sourceActions).toArray(new TransferMode[1]));
Map<DataFormat, Object> fxData = new HashMap<>();
for (String mt : activeDSContextPeer.transferable.getMimeTypes()) {
DataFormat f = DataFormat.lookupMimeType(mt);
if (f != null) fxData.put(f, activeDSContextPeer.transferable.getData(mt));
}
final boolean hasContent = db.setContent(fxData);
if (!hasContent) {
if (!FXDnD.fxAppThreadIsDispatchThread) {
loop.exit();
}
}
}
};
private final EventHandler<DragEvent> onDragDoneHandler = (event) -> {
event.consume();
if (!FXDnD.fxAppThreadIsDispatchThread) {
loop.exit();
}
if (activeDSContextPeer != null) {
final TransferMode mode = event.getTransferMode();
activeDSContextPeer.dragDone(
mode == null ? 0 : SwingDnD.transferModeToDropAction(mode),
(int)event.getX(), (int)event.getY());
}
};
private final class FXDragSourceContextPeer extends DragSourceContextWrapper {
private volatile int sourceActions = 0;
private final CachingTransferable transferable = new CachingTransferable();
@Override public void startSecondaryEventLoop(){
Toolkit.getToolkit().enterNestedEventLoop(this);
}
@Override public void quitSecondaryEventLoop(){
assert !Platform.isFxApplicationThread();
Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(FXDragSourceContextPeer.this, null));
}
@Override protected void setNativeCursor(Cursor c, int cType) {
}
private void dragDone(int operation, int x, int y) {
dragDropFinished(operation != 0, operation, x, y);
}
FXDragSourceContextPeer(DragGestureEvent dge) {
super(dge);
}
@Override protected void startDrag(Transferable trans, long[] formats, Map formatMap)
{
activeDSContextPeer = this;
transferable.updateData(trans, true);
sourceActions = getDragSourceContext().getSourceActions();
if (!FXDnD.fxAppThreadIsDispatchThread) {
loop = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
SwingNodeHelper.leaveFXNestedLoop(FXDnDInteropN.this);
if (!loop.enter()) {
}
}
}
};
private boolean isDropTargetListenerInstalled = false;
private volatile FXDropTargetContextPeer activeDTContextPeer = null;
private final Map<Component, DropTarget> dropTargets = new HashMap<>();
private final EventHandler<DragEvent> onDragEnteredHandler = (event) -> {
if (activeDTContextPeer == null) activeDTContextPeer = new FXDropTargetContextPeer();
int action = activeDTContextPeer.postDropTargetEvent(event);
if (action != 0) event.consume();
};
private final EventHandler<DragEvent> onDragExitedHandler = (event) -> {
if (activeDTContextPeer == null) activeDTContextPeer = new FXDropTargetContextPeer();
activeDTContextPeer.postDropTargetEvent(event);
activeDTContextPeer = null;
};
private final EventHandler<DragEvent> onDragOverHandler = (event) -> {
if (activeDTContextPeer == null) activeDTContextPeer = new FXDropTargetContextPeer();
int action = activeDTContextPeer.postDropTargetEvent(event);
if (action != 0) {
event.acceptTransferModes(SwingDnD.dropActionsToTransferModes(action).toArray(new TransferMode[1]));
event.consume();
}
};
private final EventHandler<DragEvent> onDragDroppedHandler = (event) -> {
if (activeDTContextPeer == null) activeDTContextPeer = new FXDropTargetContextPeer();
int action = activeDTContextPeer.postDropTargetEvent(event);
if (action != 0) {
event.setDropCompleted(activeDTContextPeer.success);
event.consume();
}
activeDTContextPeer = null;
};
private final class FXDropTargetContextPeer extends DropTargetContextWrapper {
private int targetActions = DnDConstants.ACTION_NONE;
private int currentAction = DnDConstants.ACTION_NONE;
private DropTarget dt = null;
private DropTargetContext ctx = null;
private final CachingTransferable transferable = new CachingTransferable();
private boolean success = false;
private int dropAction = 0;
@Override public synchronized void setTargetActions(int actions) { targetActions = actions; }
@Override public synchronized int getTargetActions() { return targetActions; }
@Override public synchronized DropTarget getDropTarget() { return dt; }
@Override public synchronized boolean isTransferableJVMLocal() { return false; }
@Override public synchronized DataFlavor[] getTransferDataFlavors() { return transferable.getTransferDataFlavors(); }
@Override public synchronized Transferable getTransferable() { return transferable; }
@Override public synchronized void acceptDrag(int dragAction) { currentAction = dragAction; }
@Override public synchronized void rejectDrag() { currentAction = DnDConstants.ACTION_NONE; }
@Override public synchronized void acceptDrop(int dropAction) { this.dropAction = dropAction; }
@Override public synchronized void rejectDrop() { dropAction = DnDConstants.ACTION_NONE; }
@Override public synchronized void dropComplete(boolean success) { this.success = success; }
private int postDropTargetEvent(DragEvent event)
{
ComponentMapper<DropTarget> mapper = mapComponent(dropTargets, (int)event.getX(), (int)event.getY());
final EventType<?> fxEvType = event.getEventType();
Dragboard db = event.getDragboard();
transferable.updateData(db, DragEvent.DRAG_DROPPED.equals(fxEvType));
final int sourceActions = SwingDnD.transferModesToDropActions(db.getTransferModes());
final int userAction = event.getTransferMode() == null ? DnDConstants.ACTION_NONE
: SwingDnD.transferModeToDropAction(event.getTransferMode());
DropTarget target = mapper.object != null ? mapper.object : dt;
SwingNodeHelper.runOnEDTAndWait(FXDnDInteropN.this, () -> {
if (target != dt) {
if (ctx != null) {
this.reset(ctx);
}
ctx = null;
currentAction = dropAction = DnDConstants.ACTION_NONE;
}
if (target != null) {
if (ctx == null) {
ctx = target.getDropTargetContext();
this.setDropTargetContext(ctx,
FXDropTargetContextPeer.this);
}
DropTargetListener dtl = (DropTargetListener)target;
if (DragEvent.DRAG_DROPPED.equals(fxEvType)) {
DropTargetDropEvent awtEvent = new DropTargetDropEvent(
ctx, new Point(mapper.x, mapper.y), userAction, sourceActions);
dtl.drop(awtEvent);
} else {
DropTargetDragEvent awtEvent = new DropTargetDragEvent(
ctx, new Point(mapper.x, mapper.y), userAction, sourceActions);
if (DragEvent.DRAG_OVER.equals(fxEvType)) dtl.dragOver(awtEvent);
else if (DragEvent.DRAG_ENTERED.equals(fxEvType)) dtl.dragEnter(awtEvent);
else if (DragEvent.DRAG_EXITED.equals(fxEvType)) dtl.dragExit(awtEvent);
}
}
dt = mapper.object;
if (dt == null) {
ctx = null;
currentAction = dropAction = DnDConstants.ACTION_NONE;
}
if (DragEvent.DRAG_DROPPED.equals(fxEvType) || DragEvent.DRAG_EXITED.equals(fxEvType)) {
ctx = null;
}
SwingNodeHelper.leaveFXNestedLoop(FXDnDInteropN.this);
});
if (DragEvent.DRAG_DROPPED.equals(fxEvType)) return dropAction;
return currentAction;
}
}
public void addDropTarget(DropTarget dt, SwingNode node) {
dropTargets.put(dt.getComponent(), dt);
Platform.runLater(() -> {
if (!isDropTargetListenerInstalled) {
node.addEventHandler(DragEvent.DRAG_ENTERED, onDragEnteredHandler);
node.addEventHandler(DragEvent.DRAG_EXITED, onDragExitedHandler);
node.addEventHandler(DragEvent.DRAG_OVER, onDragOverHandler);
node.addEventHandler(DragEvent.DRAG_DROPPED, onDragDroppedHandler);
isDropTargetListenerInstalled = true;
}
});
}
public void removeDropTarget(DropTarget dt, SwingNode node) {
dropTargets.remove(dt.getComponent());
Platform.runLater(() -> {
if (isDropTargetListenerInstalled && dropTargets.isEmpty()) {
node.removeEventHandler(DragEvent.DRAG_ENTERED, onDragEnteredHandler);
node.removeEventHandler(DragEvent.DRAG_EXITED, onDragExitedHandler);
node.removeEventHandler(DragEvent.DRAG_OVER, onDragOverHandler);
node.removeEventHandler(DragEvent.DRAG_DROPPED, onDragDroppedHandler);
isDropTargetListenerInstalled = false;
}
});
}
}
