package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.css.PseudoClass;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.CheckBoxSkin;
public class CheckBox extends ButtonBase {
public CheckBox() {
initialize();
}
public CheckBox(String text) {
setText(text);
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.CHECK_BOX);
setAlignment(Pos.CENTER_LEFT);
setMnemonicParsing(true);
pseudoClassStateChanged(PSEUDO_CLASS_DETERMINATE, true);
}
private BooleanProperty indeterminate;
public final void setIndeterminate(boolean value) {
indeterminateProperty().set(value);
}
public final boolean isIndeterminate() {
return indeterminate == null ? false : indeterminate.get();
}
public final BooleanProperty indeterminateProperty() {
if (indeterminate == null) {
indeterminate = new BooleanPropertyBase(false) {
@Override protected void invalidated() {
final boolean active = get();
pseudoClassStateChanged(PSEUDO_CLASS_DETERMINATE, !active);
pseudoClassStateChanged(PSEUDO_CLASS_INDETERMINATE, active);
notifyAccessibleAttributeChanged(AccessibleAttribute.INDETERMINATE);
}
@Override
public Object getBean() {
return CheckBox.this;
}
@Override
public String getName() {
return "indeterminate";
}
};
}
return indeterminate;
}
private BooleanProperty selected;
public final void setSelected(boolean value) {
selectedProperty().set(value);
}
public final boolean isSelected() {
return selected == null ? false : selected.get();
}
public final BooleanProperty selectedProperty() {
if (selected == null) {
selected = new BooleanPropertyBase() {
@Override protected void invalidated() {
final Boolean v = get();
pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, v);
notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);
}
@Override
public Object getBean() {
return CheckBox.this;
}
@Override
public String getName() {
return "selected";
}
};
}
return selected;
}
private BooleanProperty allowIndeterminate;
public final void setAllowIndeterminate(boolean value) {
allowIndeterminateProperty().set(value);
}
public final boolean isAllowIndeterminate() {
return allowIndeterminate == null ? false : allowIndeterminate.get();
}
public final BooleanProperty allowIndeterminateProperty() {
if (allowIndeterminate == null) {
allowIndeterminate =
new SimpleBooleanProperty(this, "allowIndeterminate");
}
return allowIndeterminate;
}
@Override public void fire() {
if (!isDisabled()) {
if (isAllowIndeterminate()) {
if (!isSelected() && !isIndeterminate()) {
setIndeterminate(true);
} else if (isSelected() && !isIndeterminate()) {
setSelected(false);
} else if (isIndeterminate()) {
setSelected(true);
setIndeterminate(false);
}
} else {
setSelected(!isSelected());
setIndeterminate(false);
}
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new CheckBoxSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "check-box";
private static final PseudoClass PSEUDO_CLASS_DETERMINATE =
PseudoClass.getPseudoClass("determinate");
private static final PseudoClass PSEUDO_CLASS_INDETERMINATE =
PseudoClass.getPseudoClass("indeterminate");
private static final PseudoClass PSEUDO_CLASS_SELECTED =
PseudoClass.getPseudoClass("selected");
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED: return isSelected();
case INDETERMINATE: return isIndeterminate();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
