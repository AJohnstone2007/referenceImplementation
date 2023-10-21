package javafx.scene.control;
import javafx.event.ActionEvent;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.SplitMenuButtonSkin;
public class SplitMenuButton extends MenuButton {
public SplitMenuButton() {
this((MenuItem[])null);
}
public SplitMenuButton(MenuItem... items) {
if (items != null) {
getItems().addAll(items);
}
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.SPLIT_MENU_BUTTON);
setMnemonicParsing(true);
}
@Override public void fire() {
if (!isDisabled()) {
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new SplitMenuButtonSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "split-menu-button";
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case EXPANDED: return isShowing();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE:
fire();
break;
case EXPAND:
show();
break;
case COLLAPSE:
hide();
break;
default: super.executeAccessibleAction(action);
}
}
}
