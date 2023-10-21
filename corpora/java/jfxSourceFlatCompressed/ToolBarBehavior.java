package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.control.ToolBar;
import static javafx.scene.input.KeyCode.*;
import static com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
public class ToolBarBehavior extends BehaviorBase<ToolBar> {
private final InputMap<ToolBar> toolBarInputMap;
public ToolBarBehavior(ToolBar toolbar) {
super(toolbar);
toolBarInputMap = createInputMap();
addDefaultMapping(toolBarInputMap,
new KeyMapping(new KeyBinding(F5).ctrl(), e -> {
if (!toolbar.getItems().isEmpty()) {
toolbar.getItems().get(0).requestFocus();
}
})
);
}
@Override public InputMap<ToolBar> getInputMap() {
return toolBarInputMap;
}
}
