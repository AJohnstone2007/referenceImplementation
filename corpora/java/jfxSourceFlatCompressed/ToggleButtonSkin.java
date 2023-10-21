package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ToggleButtonBehavior;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
public class ToggleButtonSkin extends LabeledSkinBase<ToggleButton> {
private final BehaviorBase<ToggleButton> behavior;
public ToggleButtonSkin(ToggleButton control) {
super(control);
behavior = new ToggleButtonBehavior<>(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
}
