package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.event.ActionEvent;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.ButtonSkin;
public class Button extends ButtonBase {
public Button() {
initialize();
}
public Button(String text) {
super(text);
initialize();
}
public Button(String text, Node graphic) {
super(text, graphic);
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.BUTTON);
setMnemonicParsing(true);
}
private BooleanProperty defaultButton;
public final void setDefaultButton(boolean value) {
defaultButtonProperty().set(value);
}
public final boolean isDefaultButton() {
return defaultButton == null ? false : defaultButton.get();
}
public final BooleanProperty defaultButtonProperty() {
if (defaultButton == null) {
defaultButton = new BooleanPropertyBase(false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_DEFAULT, get());
}
@Override
public Object getBean() {
return Button.this;
}
@Override
public String getName() {
return "defaultButton";
}
};
}
return defaultButton;
}
private BooleanProperty cancelButton;
public final void setCancelButton(boolean value) {
cancelButtonProperty().set(value);
}
public final boolean isCancelButton() {
return cancelButton == null ? false : cancelButton.get();
}
public final BooleanProperty cancelButtonProperty() {
if (cancelButton == null) {
cancelButton = new BooleanPropertyBase(false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_CANCEL, get());
}
@Override
public Object getBean() {
return Button.this;
}
@Override
public String getName() {
return "cancelButton";
}
};
}
return cancelButton;
}
@Override public void fire() {
if (!isDisabled()) {
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new ButtonSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "button";
private static final PseudoClass PSEUDO_CLASS_DEFAULT
= PseudoClass.getPseudoClass("default");
private static final PseudoClass PSEUDO_CLASS_CANCEL
= PseudoClass.getPseudoClass("cancel");
}
