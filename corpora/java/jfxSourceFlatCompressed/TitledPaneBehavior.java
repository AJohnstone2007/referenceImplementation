package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.TitledPane;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.KeyCode.SPACE;
public class TitledPaneBehavior extends BehaviorBase<TitledPane> {
private final TitledPane titledPane;
private final InputMap<TitledPane> inputMap;
public TitledPaneBehavior(TitledPane pane) {
super(pane);
this.titledPane = pane;
inputMap = createInputMap();
addDefaultMapping(inputMap, FocusTraversalInputMap.getFocusTraversalMappings());
addDefaultMapping(
new InputMap.KeyMapping(SPACE, e -> {
if (titledPane.isCollapsible() && titledPane.isFocused()) {
titledPane.setExpanded(!titledPane.isExpanded());
titledPane.requestFocus();
}
}),
new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
);
}
@Override public InputMap<TitledPane> getInputMap() {
return inputMap;
}
public void mousePressed(MouseEvent e) {
getNode().requestFocus();
}
public void expand() {
titledPane.setExpanded(true);
}
public void collapse() {
titledPane.setExpanded(false);
}
public void toggle() {
titledPane.setExpanded(!titledPane.isExpanded());
}
}
