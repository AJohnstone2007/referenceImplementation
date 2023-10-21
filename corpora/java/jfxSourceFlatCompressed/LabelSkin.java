package javafx.scene.control.skin;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
public class LabelSkin extends LabeledSkinBase<Label> {
public LabelSkin(final Label control) {
super(control);
consumeMouseEvents(false);
registerChangeListener(control.labelForProperty(), e -> mnemonicTargetChanged());
}
}
