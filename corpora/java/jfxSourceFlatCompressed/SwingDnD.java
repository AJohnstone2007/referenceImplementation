package com.sun.javafx.embed.swing;
import com.sun.javafx.embed.EmbeddedSceneDSInterface;
import com.sun.javafx.embed.EmbeddedSceneDTInterface;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.HostDragStartListener;
import com.sun.javafx.tk.Toolkit;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javafx.scene.input.TransferMode;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
final public class SwingDnD {
private final Transferable dndTransferable = new DnDTransferable();
private final DragSource dragSource;
private final DragSourceListener dragSourceListener;
private SwingDragSource swingDragSource;
private EmbeddedSceneDTInterface fxDropTarget;
private EmbeddedSceneDSInterface fxDragSource;
private MouseEvent me;
public SwingDnD(final JComponent comp, final EmbeddedSceneInterface embeddedScene) {
comp.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent me) {
storeMouseEvent(me);
}
@Override
public void mouseDragged(MouseEvent me) {
storeMouseEvent(me);
}
@Override
public void mousePressed(MouseEvent me) {
storeMouseEvent(me);
}
@Override
public void mouseReleased(MouseEvent me) {
storeMouseEvent(me);
}
});
dragSource = new DragSource();
dragSourceListener = new DragSourceAdapter() {
@Override
public void dragDropEnd(final DragSourceDropEvent dsde) {
assert fxDragSource != null;
try {
fxDragSource.dragDropEnd(dropActionToTransferMode(dsde.getDropAction()));
} finally {
fxDragSource = null;
}
}
};
DropTargetListener dtl = new DropTargetAdapter() {
private TransferMode lastTransferMode;
@Override
public void dragEnter(final DropTargetDragEvent e) {
if ((swingDragSource != null) || (fxDropTarget != null)) {
return;
}
assert swingDragSource == null;
swingDragSource = new SwingDragSource();
swingDragSource.updateContents(e, false);
assert fxDropTarget == null;
fxDropTarget = embeddedScene.createDropTarget();
final Point orig = e.getLocation();
final Point screen = new Point(orig);
SwingUtilities.convertPointToScreen(screen, comp);
lastTransferMode = fxDropTarget.handleDragEnter(
orig.x, orig.y, screen.x, screen.y,
dropActionToTransferMode(e.getDropAction()), swingDragSource);
applyDragResult(lastTransferMode, e);
}
@Override
public void dragExit(final DropTargetEvent e) {
assert swingDragSource != null;
assert fxDropTarget != null;
try {
fxDropTarget.handleDragLeave();
} finally {
endDnD();
lastTransferMode = null;
}
}
@Override
public void dragOver(final DropTargetDragEvent e) {
assert swingDragSource != null;
swingDragSource.updateContents(e, false);
assert fxDropTarget != null;
final Point orig = e.getLocation();
final Point screen = new Point(orig);
SwingUtilities.convertPointToScreen(screen, comp);
lastTransferMode = fxDropTarget.handleDragOver(
orig.x, orig.y, screen.x, screen.y,
dropActionToTransferMode(e.getDropAction()));
applyDragResult(lastTransferMode, e);
}
@Override
public void drop(final DropTargetDropEvent e) {
assert swingDragSource != null;
applyDropResult(lastTransferMode, e);
swingDragSource.updateContents(e, true);
final Point orig = e.getLocation();
final Point screen = new Point(orig);
SwingUtilities.convertPointToScreen(screen, comp);
assert fxDropTarget != null;
try {
lastTransferMode = fxDropTarget.handleDragDrop(
orig.x, orig.y, screen.x, screen.y,
dropActionToTransferMode(e.getDropAction()));
try {
applyDropResult(lastTransferMode, e);
} catch (InvalidDnDOperationException ignore) {
}
} finally {
e.dropComplete(lastTransferMode != null);
endDnD();
lastTransferMode = null;
}
}
};
comp.setDropTarget(new DropTarget(comp,
DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE | DnDConstants.ACTION_LINK, dtl));
}
public void addNotify() {
dragSource.addDragSourceListener(dragSourceListener);
}
public void removeNotify() {
dragSource.removeDragSourceListener(dragSourceListener);
}
public HostDragStartListener getDragStartListener() {
return (dragSource, dragAction) -> {
assert Toolkit.getToolkit().isFxUserThread();
assert dragSource != null;
SwingUtilities.invokeLater(() -> {
assert fxDragSource == null;
assert swingDragSource == null;
assert fxDropTarget == null;
fxDragSource = dragSource;
startDrag(me, dndTransferable, dragSource.
getSupportedActions(), dragAction);
});
};
}
private void startDrag(final MouseEvent e, final Transferable t,
final Set<TransferMode> sa,
final TransferMode dragAction)
{
assert sa.contains(dragAction);
final class StubDragGestureRecognizer extends DragGestureRecognizer {
StubDragGestureRecognizer(DragSource ds) {
super(ds, e.getComponent());
setSourceActions(transferModesToDropActions(sa));
appendEvent(e);
}
@Override
protected void registerListeners() {
}
@Override
protected void unregisterListeners() {
}
}
final Point pt = new Point(e.getX(), e.getY());
final int action = transferModeToDropAction(dragAction);
final DragGestureRecognizer dgs = new StubDragGestureRecognizer(dragSource);
final List<InputEvent> events =
Arrays.asList(new InputEvent[] { dgs.getTriggerEvent() });
final DragGestureEvent dse = new DragGestureEvent(dgs, action, pt, events);
dse.startDrag(null, t);
}
private void endDnD() {
assert swingDragSource != null;
assert fxDropTarget != null;
fxDropTarget = null;
swingDragSource = null;
}
private void storeMouseEvent(final MouseEvent me) {
this.me = me;
}
private void applyDragResult(final TransferMode dragResult,
final DropTargetDragEvent e)
{
if (dragResult == null) {
e.rejectDrag();
} else {
e.acceptDrag(transferModeToDropAction(dragResult));
}
}
private void applyDropResult(final TransferMode dropResult,
final DropTargetDropEvent e)
{
if (dropResult == null) {
e.rejectDrop();
} else {
e.acceptDrop(transferModeToDropAction(dropResult));
}
}
public static TransferMode dropActionToTransferMode(final int dropAction) {
switch (dropAction) {
case DnDConstants.ACTION_COPY:
return TransferMode.COPY;
case DnDConstants.ACTION_MOVE:
return TransferMode.MOVE;
case DnDConstants.ACTION_LINK:
return TransferMode.LINK;
case DnDConstants.ACTION_NONE:
return null;
default:
throw new IllegalArgumentException();
}
}
public static int transferModeToDropAction(final TransferMode tm) {
switch (tm) {
case COPY:
return DnDConstants.ACTION_COPY;
case MOVE:
return DnDConstants.ACTION_MOVE;
case LINK:
return DnDConstants.ACTION_LINK;
default:
throw new IllegalArgumentException();
}
}
public static Set<TransferMode> dropActionsToTransferModes(
final int dropActions)
{
final Set<TransferMode> tms = EnumSet.noneOf(TransferMode.class);
if ((dropActions & DnDConstants.ACTION_COPY) != 0) {
tms.add(TransferMode.COPY);
}
if ((dropActions & DnDConstants.ACTION_MOVE) != 0) {
tms.add(TransferMode.MOVE);
}
if ((dropActions & DnDConstants.ACTION_LINK) != 0) {
tms.add(TransferMode.LINK);
}
return Collections.unmodifiableSet(tms);
}
public static int transferModesToDropActions(final Set<TransferMode> tms) {
int dropActions = DnDConstants.ACTION_NONE;
for (TransferMode tm : tms) {
dropActions |= transferModeToDropAction(tm);
}
return dropActions;
}
private final class DnDTransferable implements Transferable {
@Override
public Object getTransferData(final DataFlavor flavor)
throws UnsupportedEncodingException
{
assert fxDragSource != null;
assert SwingUtilities.isEventDispatchThread();
String mimeType = DataFlavorUtils.getFxMimeType(flavor);
return DataFlavorUtils.adjustFxData(
flavor, fxDragSource.getData(mimeType));
}
@Override
public DataFlavor[] getTransferDataFlavors() {
assert fxDragSource != null;
assert SwingUtilities.isEventDispatchThread();
final String mimeTypes[] = fxDragSource.getMimeTypes();
final ArrayList<DataFlavor> flavors =
new ArrayList<DataFlavor>(mimeTypes.length);
for (String mime : mimeTypes) {
DataFlavor flavor = null;
try {
flavor = new DataFlavor(mime);
} catch (ClassNotFoundException e) {
continue;
}
flavors.add(flavor);
}
return flavors.toArray(new DataFlavor[0]);
}
@Override
public boolean isDataFlavorSupported(final DataFlavor flavor) {
assert fxDragSource != null;
assert SwingUtilities.isEventDispatchThread();
return fxDragSource.isMimeTypeAvailable(
DataFlavorUtils.getFxMimeType(flavor));
}
}
}
