package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.MenuButtonBehavior;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.MouseEvent;
import com.sun.javafx.scene.control.behavior.SplitMenuButtonBehavior;
public class SplitMenuButtonSkin extends MenuButtonSkinBase<SplitMenuButton> {
private final SplitMenuButtonBehavior behavior;
public SplitMenuButtonSkin(final SplitMenuButton control) {
super(control);
this.behavior = new SplitMenuButtonBehavior(control);
behaveLikeButton = true;
arrowButton.addEventHandler(MouseEvent.ANY, event -> event.consume());
arrowButton.setOnMousePressed(e -> {
getBehavior().mousePressed(e, false);
e.consume();
});
arrowButton.setOnMouseReleased(e -> {
getBehavior().mouseReleased(e, false);
e.consume();
});
label.setLabelFor(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override SplitMenuButtonBehavior getBehavior() {
return behavior;
}
}
