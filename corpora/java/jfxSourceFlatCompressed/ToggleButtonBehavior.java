package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.KeyEvent;
import static com.sun.javafx.scene.control.inputmap.InputMap.*;
import static javafx.scene.input.KeyCode.*;
public class ToggleButtonBehavior<C extends ToggleButton> extends ButtonBehavior<C>{
public ToggleButtonBehavior(C button) {
super(button);
ObservableList<Mapping<?>> mappings = FXCollections.observableArrayList(
new KeyMapping(RIGHT, e -> traverse(e, "ToggleNext-Right")),
new KeyMapping(LEFT, e -> traverse(e, "TogglePrevious-Left")),
new KeyMapping(DOWN, e -> traverse(e, "ToggleNext-Down")),
new KeyMapping(UP, e -> traverse(e, "TogglePrevious-Up"))
);
for (Mapping<?> mapping : mappings) {
mapping.setAutoConsume(false);
}
InputMap<C> overriddenFocusInput = new InputMap<>(button);
overriddenFocusInput.getMappings().addAll(mappings);
addDefaultChildMap(getInputMap(), overriddenFocusInput);
}
private int nextToggleIndex(final ObservableList<Toggle> toggles, final int from) {
Toggle toggle;
if (from < 0 || from >= toggles.size()) return 0;
int i = (from + 1) % toggles.size();
while (i != from && (toggle = toggles.get(i)) instanceof Node &&
((Node)toggle).isDisabled()) {
i = (i + 1) % toggles.size();
}
return i;
}
private int previousToggleIndex(final ObservableList<Toggle> toggles, final int from) {
Toggle toggle;
if (from < 0 || from >= toggles.size()) return toggles.size();
int i = Math.floorMod(from - 1, toggles.size());
while (i != from && (toggle = toggles.get(i)) instanceof Node &&
((Node)toggle).isDisabled()) {
i = Math.floorMod(i - 1, toggles.size());
}
return i;
}
private void traverse(KeyEvent e, String name) {
ToggleButton toggleButton = getNode();
final ToggleGroup toggleGroup = toggleButton.getToggleGroup();
if (toggleGroup == null) {
e.consume();
return;
}
ObservableList<Toggle> toggles = toggleGroup.getToggles();
final int currentToggleIdx = toggles.indexOf(toggleButton);
boolean traversingToNext = traversingToNext(name, toggleButton.getEffectiveNodeOrientation());
if (Utils.isTwoLevelFocus()) {
} else if (traversingToNext) {
int nextToggleIndex = nextToggleIndex(toggles, currentToggleIdx);
if (nextToggleIndex == currentToggleIdx) {
} else {
Toggle toggle = toggles.get(nextToggleIndex);
toggleGroup.selectToggle(toggle);
((Control)toggle).requestFocus();
e.consume();
}
} else {
int prevToggleIndex = previousToggleIndex(toggles, currentToggleIdx);
if (prevToggleIndex == currentToggleIdx) {
} else {
Toggle toggle = toggles.get(prevToggleIndex);
toggleGroup.selectToggle(toggle);
((Control)toggle).requestFocus();
e.consume();
}
}
}
private boolean traversingToNext(String name, NodeOrientation effectiveNodeOrientation) {
boolean rtl = effectiveNodeOrientation == NodeOrientation.RIGHT_TO_LEFT;
switch (name) {
case "ToggleNext-Right":
return rtl ? false : true;
case "ToggleNext-Down":
return true;
case "TogglePrevious-Left":
return rtl ? true : false;
case "TogglePrevious-Up":
return false;
default:
throw new IllegalArgumentException("Not a toggle action");
}
}
}
