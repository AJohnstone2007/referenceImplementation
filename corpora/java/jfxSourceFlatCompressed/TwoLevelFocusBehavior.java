package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.PopupControl;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
public class TwoLevelFocusBehavior {
Node tlNode = null;
PopupControl tlPopup = null;
EventDispatcher origEventDispatcher = null;
public TwoLevelFocusBehavior() {
}
public TwoLevelFocusBehavior(Node node) {
tlNode = node;
tlPopup = null;
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
final EventDispatcher tlfEventDispatcher = (event, tail) -> {
if ((event instanceof KeyEvent)) {
if (isExternalFocus()) {
tail = tail.prepend(preemptiveEventDispatcher);
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
if (newVal && tlPopup != null) {
setExternalFocus(false);
}
else {
setExternalFocus(true);
}
};
private final EventHandler<MouseEvent> mouseEventListener = e -> {
setExternalFocus(false);
};
private boolean externalFocus = true;
public boolean isExternalFocus() {
return externalFocus;
}
private static final PseudoClass INTERNAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("internal-focus");
private static final PseudoClass EXTERNAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("external-focus");
public void setExternalFocus(boolean value) {
externalFocus = value;
if (tlNode != null && tlNode instanceof Control) {
tlNode.pseudoClassStateChanged(INTERNAL_PSEUDOCLASS_STATE, !value);
tlNode.pseudoClassStateChanged(EXTERNAL_PSEUDOCLASS_STATE, value);
}
else if (tlPopup != null) {
tlPopup.pseudoClassStateChanged(INTERNAL_PSEUDOCLASS_STATE, !value);
tlPopup.pseudoClassStateChanged(EXTERNAL_PSEUDOCLASS_STATE, value);
}
}
}
