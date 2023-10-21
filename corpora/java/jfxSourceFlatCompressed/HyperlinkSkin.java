package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
public class HyperlinkSkin extends LabeledSkinBase<Hyperlink> {
private final BehaviorBase<Hyperlink> behavior;
public HyperlinkSkin(Hyperlink control) {
super(control);
behavior = new ButtonBehavior<>(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
}
