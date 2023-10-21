package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.MenuButton;
import static com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.SPACE;
public class MenuButtonBehavior extends MenuButtonBehaviorBase<MenuButton> {
public MenuButtonBehavior(final MenuButton menuButton) {
super(menuButton);
addDefaultMapping(new KeyMapping(SPACE, e -> openAction()));
addDefaultMapping(new KeyMapping(ENTER, e -> openAction()));
}
}
