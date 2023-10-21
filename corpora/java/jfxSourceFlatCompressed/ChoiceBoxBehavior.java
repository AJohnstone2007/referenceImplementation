package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionModel;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.KeyCode.*;
import static com.sun.javafx.scene.control.inputmap.InputMap.*;
public class ChoiceBoxBehavior<T> extends BehaviorBase<ChoiceBox<T>> {
private final InputMap<ChoiceBox<T>> choiceBoxInputMap;
private TwoLevelFocusComboBehavior tlFocus;
public ChoiceBoxBehavior(ChoiceBox<T> control) {
super(control);
choiceBoxInputMap = createInputMap();
addDefaultMapping(choiceBoxInputMap,
new KeyMapping(SPACE, KeyEvent.KEY_PRESSED, this::keyPressed),
new KeyMapping(SPACE, KeyEvent.KEY_RELEASED, this::keyReleased),
new KeyMapping(ESCAPE, KeyEvent.KEY_RELEASED, e -> cancel()),
new KeyMapping(DOWN, KeyEvent.KEY_RELEASED, e -> showPopup()),
new KeyMapping(CANCEL, KeyEvent.KEY_RELEASED, e -> cancel()),
new MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
new MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased)
);
InputMap<ChoiceBox<T>> twoLevelFocusInputMap = new InputMap<>(control);
twoLevelFocusInputMap.setInterceptor(e -> !Utils.isTwoLevelFocus());
twoLevelFocusInputMap.getMappings().addAll(
new KeyMapping(ENTER, KeyEvent.KEY_PRESSED, this::keyPressed),
new KeyMapping(ENTER, KeyEvent.KEY_RELEASED, this::keyReleased)
);
addDefaultChildMap(choiceBoxInputMap, twoLevelFocusInputMap);
if (Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusComboBehavior(control);
}
}
@Override public InputMap<ChoiceBox<T>> getInputMap() {
return choiceBoxInputMap;
}
@Override public void dispose() {
if (tlFocus != null) tlFocus.dispose();
super.dispose();
}
public void select(int index) {
SelectionModel<T> sm = getNode().getSelectionModel();
if (sm == null) return;
sm.select(index);
}
public void close() {
getNode().hide();
}
public void showPopup() {
getNode().show();
}
public void mousePressed(MouseEvent e) {
ChoiceBox<T> choiceButton = getNode();
if (choiceButton.isFocusTraversable()) choiceButton.requestFocus();
}
public void mouseReleased(MouseEvent e) {
ChoiceBox<T> choiceButton = getNode();
if (choiceButton.isShowing() || !choiceButton.contains(e.getX(), e.getY())) {
choiceButton.hide();
}
else if (e.getButton() == MouseButton.PRIMARY) {
choiceButton.show();
}
}
private void keyPressed(KeyEvent e) {
ChoiceBox<T> choiceButton = getNode();
if (!choiceButton.isShowing()) {
choiceButton.show();
}
}
private void keyReleased(KeyEvent e) {
}
public void cancel() {
ChoiceBox<T> choiceButton = getNode();
choiceButton.hide();
}
}
