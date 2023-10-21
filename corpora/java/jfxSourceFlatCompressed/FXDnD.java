package com.sun.javafx.embed.swing;
import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.embed.swing.SwingNode;
import com.sun.javafx.embed.swing.newimpl.FXDnDInteropN;
final public class FXDnD {
public static boolean fxAppThreadIsDispatchThread;
private FXDnDInteropN fxdndiop;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PrivilegedAction<Object>() {
public Object run() {
fxAppThreadIsDispatchThread =
"true".equals(System.getProperty("javafx.embed.singleThread"));
return null;
}
});
}
public FXDnD(SwingNode node) {
fxdndiop = new FXDnDInteropN();
fxdndiop.setNode(node);
}
public Object createDragSourceContext(DragGestureEvent dge)
throws InvalidDnDOperationException {
return fxdndiop.createDragSourceContext(dge);
}
public <T extends DragGestureRecognizer> T createDragGestureRecognizer(
Class<T> abstractRecognizerClass,
DragSource ds, Component c, int srcActions,
DragGestureListener dgl)
{
return fxdndiop.createDragGestureRecognizer(ds, c, srcActions, dgl);
}
public void addDropTarget(DropTarget dt) {
SwingNode node = fxdndiop.getNode();
if (node != null) {
fxdndiop.addDropTarget(dt, node);
}
}
public void removeDropTarget(DropTarget dt) {
SwingNode node = fxdndiop.getNode();
if (node != null) {
fxdndiop.removeDropTarget(dt, node);
}
}
}
