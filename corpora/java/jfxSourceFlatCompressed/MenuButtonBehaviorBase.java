package com.sun.javafx.scene.control.behavior;
import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import static com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import static javafx.scene.input.KeyCode.*;
public abstract class MenuButtonBehaviorBase<C extends MenuButton> extends ButtonBehavior<C> {
private final InputMap<C> buttonInputMap;
public MenuButtonBehaviorBase(final C menuButton) {
super(menuButton);
buttonInputMap = super.getInputMap();
removeMapping(MouseEvent.MOUSE_RELEASED);
addDefaultMapping(new KeyMapping(ESCAPE, e -> getNode().hide()));
addDefaultMapping(new KeyMapping(CANCEL, e -> getNode().hide()));
InputMap<C> customFocusInputMap = new InputMap<>(menuButton);
addDefaultMapping(customFocusInputMap, new KeyMapping(UP, this::overrideTraversalInput));
addDefaultMapping(customFocusInputMap, new KeyMapping(DOWN, this::overrideTraversalInput));
addDefaultMapping(customFocusInputMap, new KeyMapping(LEFT, this::overrideTraversalInput));
addDefaultMapping(customFocusInputMap, new KeyMapping(RIGHT, this::overrideTraversalInput));
addDefaultChildMap(buttonInputMap, customFocusInputMap);
}
private void overrideTraversalInput(KeyEvent event) {
final MenuButton button = getNode();
final Side popupSide = button.getPopupSide();
if (!button.isShowing() &&
(event.getCode() == UP && popupSide == Side.TOP) ||
(event.getCode() == DOWN && (popupSide == Side.BOTTOM || popupSide == Side.TOP)) ||
(event.getCode() == LEFT && (popupSide == Side.RIGHT || popupSide == Side.LEFT)) ||
(event.getCode() == RIGHT && (popupSide == Side.RIGHT || popupSide == Side.LEFT))) {
button.show();
}
}
protected void openAction() {
if (getNode().isShowing()) {
getNode().hide();
} else {
getNode().show();
}
}
public void mousePressed(MouseEvent e, boolean behaveLikeButton) {
final C control = getNode();
if (behaveLikeButton) {
if (control.isShowing()) {
control.hide();
}
super.mousePressed(e);
} else {
if (!control.isFocused() && control.isFocusTraversable()) {
control.requestFocus();
}
if (control.isShowing()) {
control.hide();
} else {
if (e.getButton() == MouseButton.PRIMARY) {
control.show();
}
}
}
}
public void mouseReleased(MouseEvent e, boolean behaveLikeButton) {
if (behaveLikeButton) {
super.mouseReleased(e);
} else {
if (getNode().isShowing() && !getNode().contains(e.getX(), e.getY())) {
getNode().hide();
}
getNode().disarm();
}
}
}
