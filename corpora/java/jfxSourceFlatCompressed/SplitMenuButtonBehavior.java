package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.SplitMenuButton;
import com.sun.javafx.scene.control.inputmap.InputMap;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
public class SplitMenuButtonBehavior extends MenuButtonBehaviorBase<SplitMenuButton> {
public SplitMenuButtonBehavior(final SplitMenuButton splitMenuButton) {
super(splitMenuButton);
addDefaultMapping(new InputMap.KeyMapping(SPACE, KEY_PRESSED, this::keyPressed));
addDefaultMapping(new InputMap.KeyMapping(SPACE, KEY_RELEASED, this::keyReleased));
addDefaultMapping(new InputMap.KeyMapping(ENTER, KEY_PRESSED, this::keyPressed));
addDefaultMapping(new InputMap.KeyMapping(ENTER, KEY_RELEASED, this::keyReleased));
}
}
