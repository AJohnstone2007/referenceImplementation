package com.sun.glass.ui;
import static javafx.scene.AccessibleAttribute.PARENT;
import static javafx.scene.AccessibleAttribute.ROLE;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.tk.quantum.QuantumToolkit;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
public abstract class Accessible {
private EventHandler eventHandler;
private View view;
public static abstract class EventHandler {
public Object getAttribute(AccessibleAttribute attribute, Object... parameters) {
return null;
}
public void executeAction(AccessibleAction action, Object... parameters) {
}
@SuppressWarnings("removal")
public abstract AccessControlContext getAccessControlContext();
}
public EventHandler getEventHandler() {
return this.eventHandler;
}
public void setEventHandler(EventHandler eventHandler) {
this.eventHandler = eventHandler;
}
public void setView(View view) {
this.view = view;
}
public View getView() {
return view;
}
public void dispose() {
eventHandler = null;
view = null;
}
public boolean isDisposed() {
return getNativeAccessible() == 0L;
}
@Override
public String toString() {
return getClass().getSimpleName() + " (" + eventHandler + ")";
}
protected boolean isIgnored() {
AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
if (role == null) return true;
return role == AccessibleRole.NODE || role == AccessibleRole.PARENT;
}
protected abstract long getNativeAccessible();
protected Accessible getAccessible(Scene scene) {
if (scene == null) return null;
return SceneHelper.getAccessible(scene);
}
protected Accessible getAccessible(Node node) {
if (node == null) return null;
return NodeHelper.getAccessible(node);
}
protected long getNativeAccessible(Node node) {
if (node == null) return 0L;
Accessible acc = getAccessible(node);
if (acc == null) return 0L;
return acc.getNativeAccessible();
}
protected Accessible getContainerAccessible(AccessibleRole targetRole) {
Node node = (Node)getAttribute(PARENT);
while (node != null) {
Accessible acc = getAccessible(node);
AccessibleRole role = (AccessibleRole)acc.getAttribute(ROLE);
if (role == targetRole) return acc;
node = (Node)acc.getAttribute(PARENT);
}
return null;
}
@SuppressWarnings("removal")
private final AccessControlContext getAccessControlContext() {
AccessControlContext acc = null;
try {
acc = eventHandler.getAccessControlContext();
} catch (Exception e) {
}
return acc;
}
private class GetAttribute implements PrivilegedAction<Object> {
AccessibleAttribute attribute;
Object[] parameters;
@Override public Object run() {
Object result = eventHandler.getAttribute(attribute, parameters);
if (result != null) {
Class<?> clazz = attribute.getReturnType();
if (clazz != null) {
try {
clazz.cast(result);
} catch (Exception e) {
String msg = "The expected return type for the " + attribute +
" attribute is " + clazz.getSimpleName() +
" but found " + result.getClass().getSimpleName();
System.err.println(msg);
return null;
}
}
}
return result;
}
}
private GetAttribute getAttribute = new GetAttribute();
@SuppressWarnings("removal")
public Object getAttribute(AccessibleAttribute attribute, Object... parameters) {
AccessControlContext acc = getAccessControlContext();
if (acc == null) return null;
return QuantumToolkit.runWithoutRenderLock(() -> {
getAttribute.attribute = attribute;
getAttribute.parameters = parameters;
return AccessController.doPrivileged(getAttribute, acc);
});
}
private class ExecuteAction implements PrivilegedAction<Void> {
AccessibleAction action;
Object[] parameters;
@Override public Void run() {
eventHandler.executeAction(action, parameters);
return null;
}
}
private ExecuteAction executeAction = new ExecuteAction();
@SuppressWarnings("removal")
public void executeAction(AccessibleAction action, Object... parameters) {
AccessControlContext acc = getAccessControlContext();
if (acc == null) return;
QuantumToolkit.runWithoutRenderLock(() -> {
executeAction.action = action;
executeAction.parameters = parameters;
return AccessController.doPrivileged(executeAction, acc);
});
}
public abstract void sendNotification(AccessibleAttribute notification);
}
