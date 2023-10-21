package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.NodeHelper;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
public class TwoLevelFocusPopupBehavior extends TwoLevelFocusBehavior {
public TwoLevelFocusPopupBehavior(PopupControl popup) {
tlPopup = popup;
setExternalFocus(false);
tlPopup.addEventHandler(KeyEvent.ANY, keyEventListener);
tlPopup.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventListener);
tlPopup.focusedProperty().addListener(focusListener);
origEventDispatcher = tlPopup.getEventDispatcher();
tlPopup.setEventDispatcher(tlfEventDispatcher);
}
public TwoLevelFocusPopupBehavior(Node node) {
tlNode = node;
setExternalFocus(false);
tlNode.addEventHandler(KeyEvent.ANY, keyEventListener);
tlNode.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventListener);
tlNode.focusedProperty().addListener(focusListener);
origEventDispatcher = tlNode.getEventDispatcher();
tlNode.setEventDispatcher(tlfEventDispatcher);
}
public void dispose() {
tlNode.removeEventHandler(KeyEvent.ANY, keyEventListener);
tlNode.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventListener);
tlNode.focusedProperty().removeListener(focusListener);
tlNode.setEventDispatcher(origEventDispatcher);
}
final EventDispatcher preemptiveEventDispatcher = (event, tail) -> {
if (event instanceof KeyEvent && event.getEventType() == KeyEvent.KEY_PRESSED) {
if (!((KeyEvent)event).isMetaDown() && !((KeyEvent)event).isControlDown() && !((KeyEvent)event).isAltDown()) {
if (isExternalFocus()) {
Object obj = event.getTarget();
switch (((KeyEvent)event).getCode()) {
case TAB :
if (((KeyEvent)event).isShiftDown()) {
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.PREVIOUS);
}
else {
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.NEXT);
}
event.consume();
break;
case UP :
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.UP);
event.consume();
break;
case DOWN :
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.DOWN);
event.consume();
break;
case LEFT :
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.LEFT);
event.consume();
break;
case RIGHT :
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.RIGHT);
event.consume();
break;
case ENTER :
setExternalFocus(false);
event.consume();
break;
default :
Scene s = tlNode.getScene();
Event.fireEvent(s, event);
event.consume();
break;
}
}
}
}
return event;
};
final EventDispatcher preemptivePopupEventDispatcher = (event, tail) -> {
if (event instanceof KeyEvent && event.getEventType() == KeyEvent.KEY_PRESSED) {
if (!((KeyEvent)event).isMetaDown() && !((KeyEvent)event).isControlDown() && !((KeyEvent)event).isAltDown()) {
if (!isExternalFocus()) {
Object obj = event.getTarget();
switch (((KeyEvent)event).getCode()) {
case TAB :
case ENTER :
event.consume();
break;
case UP :
case DOWN :
break;
case LEFT :
if (obj instanceof Node) {
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.LEFT);
event.consume();
}
else if (obj instanceof Scene) {
Node node = ((Scene)obj).getFocusOwner();
if (node != null) {
NodeHelper.traverse(node, com.sun.javafx.scene.traversal.Direction.LEFT);
event.consume();
}
}
break;
case RIGHT :
if (obj instanceof Node) {
NodeHelper.traverse((Node) obj, com.sun.javafx.scene.traversal.Direction.RIGHT);
event.consume();
}
else if (obj instanceof Scene) {
Node node = ((Scene)obj).getFocusOwner();
if (node != null) {
NodeHelper.traverse(node, com.sun.javafx.scene.traversal.Direction.RIGHT);
event.consume();
}
}
break;
default :
Scene s = null;
if (tlNode != null) {
s = tlNode.getScene();
Event.fireEvent(s, event);
}
event.consume();
break;
}
}
}
}
return event;
};
final EventDispatcher tlfEventDispatcher = (event, tail) -> {
if ((event instanceof KeyEvent)) {
if (isExternalFocus()) {
tail = tail.prepend(preemptiveEventDispatcher);
return tail.dispatchEvent(event);
}
else {
tail = tail.prepend(preemptivePopupEventDispatcher);
tail = tail.prepend(origEventDispatcher);
return tail.dispatchEvent(event);
}
}
return origEventDispatcher.dispatchEvent(event, tail);
};
private Event postDispatchTidyup(Event event) {
if (event instanceof KeyEvent && event.getEventType() == KeyEvent.KEY_PRESSED) {
if (!isExternalFocus()) {
if (!((KeyEvent)event).isMetaDown() && !((KeyEvent)event).isControlDown() && !((KeyEvent)event).isAltDown()) {
switch (((KeyEvent)event).getCode()) {
case TAB :
case UP :
case DOWN :
case LEFT :
case RIGHT :
event.consume();
break;
case ENTER :
setExternalFocus(true);
event.consume();
break;
default :
break;
}
}
}
}
return event;
}
private final EventHandler<KeyEvent> keyEventListener = e -> {
postDispatchTidyup(e);
};
final ChangeListener<Boolean> focusListener = (observable, oldVal, newVal) -> {
};
private final EventHandler<MouseEvent> mouseEventListener = e -> {
setExternalFocus(false);
};
}
