package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.ButtonBase;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import static com.sun.javafx.scene.control.inputmap.InputMap.*;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.SPACE;
public class ButtonBehavior<C extends ButtonBase> extends BehaviorBase<C> {
private final InputMap<C> buttonInputMap;
private boolean keyDown;
private InvalidationListener focusListener = this::focusChanged;
public ButtonBehavior(C control) {
super(control);
buttonInputMap = createInputMap();
addDefaultMapping(buttonInputMap, FocusTraversalInputMap.getFocusTraversalMappings());
addDefaultMapping(buttonInputMap,
new KeyMapping(SPACE, KeyEvent.KEY_PRESSED, this::keyPressed),
new KeyMapping(SPACE, KeyEvent.KEY_RELEASED, this::keyReleased),
new MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
new MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
new MouseMapping(MouseEvent.MOUSE_ENTERED, this::mouseEntered),
new MouseMapping(MouseEvent.MOUSE_EXITED, this::mouseExited),
new KeyMapping(new KeyBinding(ENTER, KeyEvent.KEY_PRESSED), this::keyPressed, event -> PlatformUtil.isMac()),
new KeyMapping(new KeyBinding(ENTER, KeyEvent.KEY_RELEASED), this::keyReleased, event -> PlatformUtil.isMac())
);
control.focusedProperty().addListener(focusListener);
}
@Override public InputMap<C> getInputMap() {
return buttonInputMap;
}
@Override public void dispose() {
getNode().focusedProperty().removeListener(focusListener);
super.dispose();
}
private void focusChanged(Observable o) {
final ButtonBase button = getNode();
if (keyDown && !button.isFocused()) {
keyDown = false;
button.disarm();
}
}
protected void keyPressed(KeyEvent e) {
if (! getNode().isPressed() && ! getNode().isArmed()) {
keyDown = true;
getNode().arm();
}
}
protected void keyReleased(KeyEvent e) {
if (keyDown) {
keyDown = false;
if (getNode().isArmed()) {
getNode().disarm();
getNode().fire();
}
}
}
protected void mousePressed(MouseEvent e) {
if (! getNode().isFocused() && getNode().isFocusTraversable()) {
getNode().requestFocus();
}
boolean valid = (e.getButton() == MouseButton.PRIMARY &&
! (e.isMiddleButtonDown() || e.isSecondaryButtonDown() ||
e.isShiftDown() || e.isControlDown() || e.isAltDown() || e.isMetaDown()));
if (! getNode().isArmed() && valid) {
getNode().arm();
}
}
protected void mouseReleased(MouseEvent e) {
if (! keyDown && getNode().isArmed()) {
getNode().fire();
getNode().disarm();
}
}
protected void mouseEntered(MouseEvent e) {
if (! keyDown && getNode().isPressed()) {
getNode().arm();
}
}
protected void mouseExited(MouseEvent e) {
if (! keyDown && getNode().isArmed()) {
getNode().disarm();
}
}
}
