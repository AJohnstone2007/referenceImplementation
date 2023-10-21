package javafx.scene.control.skin;
import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import com.sun.javafx.scene.control.behavior.MenuButtonBehavior;
public class MenuButtonSkin extends MenuButtonSkinBase<MenuButton> {
static final String AUTOHIDE = "autoHide";
private final MenuButtonBehavior behavior;
public MenuButtonSkin(final MenuButton control) {
super(control);
this.behavior = new MenuButtonBehavior(control);
popup.setOnAutoHide(e -> {
MenuButton menuButton = getSkinnable();
if (!menuButton.getProperties().containsKey(AUTOHIDE)) {
menuButton.getProperties().put(AUTOHIDE, Boolean.TRUE);
}
});
popup.setOnShown(event -> {
if (requestFocusOnFirstMenuItem) {
requestFocusOnFirstMenuItem();
requestFocusOnFirstMenuItem = false;
} else {
ContextMenuContent cmContent = (ContextMenuContent) popup.getSkin().getNode();
if (cmContent != null) {
cmContent.requestFocus();
}
}
});
if (control.getOnAction() == null) {
control.setOnAction(e -> control.show());
}
label.setLabelFor(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override MenuButtonBehavior getBehavior() {
return behavior;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case MNEMONIC: return label.queryAccessibleAttribute(AccessibleAttribute.MNEMONIC);
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
