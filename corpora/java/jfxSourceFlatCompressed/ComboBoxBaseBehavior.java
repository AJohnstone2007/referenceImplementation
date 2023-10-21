package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.input.*;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.*;
import static com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import static com.sun.javafx.scene.control.inputmap.InputMap.MouseMapping;
public class ComboBoxBaseBehavior<T> extends BehaviorBase<ComboBoxBase<T>> {
private final InputMap<ComboBoxBase<T>> inputMap;
private InvalidationListener focusListener = this::focusChanged;
private TwoLevelFocusComboBehavior tlFocus;
public ComboBoxBaseBehavior(final ComboBoxBase<T> comboBox) {
super(comboBox);
inputMap = createInputMap();
final EventHandler<KeyEvent> togglePopup = e -> {
showPopupOnMouseRelease = true;
if (getNode().isShowing()) hide();
else show();
};
KeyMapping enterPressed, enterReleased;
addDefaultMapping(inputMap,
new KeyMapping(F4, KEY_RELEASED, togglePopup),
new KeyMapping(new KeyBinding(UP).alt(), togglePopup),
new KeyMapping(new KeyBinding(DOWN).alt(), togglePopup),
new KeyMapping(SPACE, KEY_PRESSED, this::keyPressed),
new KeyMapping(SPACE, KEY_RELEASED, this::keyReleased),
enterPressed = new KeyMapping(ENTER, KEY_PRESSED, this::keyPressed),
enterReleased = new KeyMapping(ENTER, KEY_RELEASED, this::keyReleased),
new KeyMapping(ESCAPE, KEY_PRESSED, this::cancelEdit),
new KeyMapping(F10, KEY_PRESSED, this::forwardToParent),
new MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
new MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
new MouseMapping(MouseEvent.MOUSE_ENTERED, this::mouseEntered),
new MouseMapping(MouseEvent.MOUSE_EXITED, this::mouseExited)
);
enterPressed.setAutoConsume(false);
enterReleased.setAutoConsume(false);
comboBox.focusedProperty().addListener(focusListener);
if (Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusComboBehavior(comboBox);
}
}
@Override public void dispose() {
if (tlFocus != null) tlFocus.dispose();
getNode().focusedProperty().removeListener(focusListener);
super.dispose();
}
@Override public InputMap<ComboBoxBase<T>> getInputMap() {
return inputMap;
}
protected void focusChanged(Observable o) {
final ComboBoxBase<T> box = getNode();
if (keyDown && !box.isFocused()) {
keyDown = false;
box.disarm();
}
}
private boolean keyDown;
private void keyPressed(KeyEvent e) {
showPopupOnMouseRelease = true;
if (Utils.isTwoLevelFocus()) {
show();
if (tlFocus != null) {
tlFocus.setExternalFocus(false);
}
}
else {
if (! getNode().isPressed() && ! getNode().isArmed()) {
keyDown = true;
getNode().arm();
}
}
}
private void keyReleased(KeyEvent e) {
showPopupOnMouseRelease = true;
if (!Utils.isTwoLevelFocus()) {
if (keyDown) {
keyDown = false;
if (getNode().isArmed()) {
getNode().disarm();
}
}
}
}
private void forwardToParent(KeyEvent event) {
if (getNode().getParent() != null) {
getNode().getParent().fireEvent(event);
}
}
private void cancelEdit(KeyEvent event) {
ComboBoxBase comboBoxBase = getNode();
TextField textField = null;
if (comboBoxBase instanceof DatePicker) {
textField = ((DatePicker)comboBoxBase).getEditor();
} else if (comboBoxBase instanceof ComboBox) {
textField = comboBoxBase.isEditable() ? ((ComboBox)comboBoxBase).getEditor() : null;
}
if (textField != null && textField.getTextFormatter() != null) {
textField.cancelEdit();
} else {
forwardToParent(event);
}
}
public void mousePressed(MouseEvent e) {
arm(e);
}
public void mouseReleased(MouseEvent e) {
disarm();
if (showPopupOnMouseRelease) {
show();
} else {
showPopupOnMouseRelease = true;
hide();
}
}
public void mouseEntered(MouseEvent e) {
if (!getNode().isEditable()) {
mouseInsideButton = true;
} else {
final EventTarget target = e.getTarget();
mouseInsideButton = (target instanceof Node && "arrow-button".equals(((Node) target).getId()));
}
arm();
}
public void mouseExited(MouseEvent e) {
mouseInsideButton = false;
disarm();
}
private void arm(MouseEvent e) {
boolean valid = (e.getButton() == MouseButton.PRIMARY &&
! (e.isMiddleButtonDown() || e.isSecondaryButtonDown() ||
e.isShiftDown() || e.isControlDown() || e.isAltDown() || e.isMetaDown()));
if (! getNode().isArmed() && valid) {
getNode().arm();
}
}
public void show() {
if (! getNode().isShowing()) {
if (getNode().isFocusTraversable()) {
getNode().requestFocus();
}
getNode().show();
}
}
public void hide() {
if (getNode().isShowing()) {
getNode().hide();
}
}
private boolean showPopupOnMouseRelease = true;
private boolean mouseInsideButton = false;
public void onAutoHide(PopupControl popup) {
hide();
showPopupOnMouseRelease = mouseInsideButton ? !showPopupOnMouseRelease : true;
}
public void arm() {
if (getNode().isPressed()) {
getNode().arm();
}
}
public void disarm() {
if (! keyDown && getNode().isArmed()) {
getNode().disarm();
}
}
}
