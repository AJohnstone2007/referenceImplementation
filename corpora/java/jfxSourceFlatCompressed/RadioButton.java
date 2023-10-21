package javafx.scene.control;
import javafx.geometry.Pos;
import javafx.scene.control.skin.RadioButtonSkin;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
public class RadioButton extends ToggleButton {
public RadioButton() {
initialize();
}
public RadioButton(String text) {
setText(text);
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.RADIO_BUTTON);
((StyleableProperty<Pos>)(WritableValue<Pos>)alignmentProperty()).applyStyle(null, Pos.CENTER_LEFT);
}
@Override public void fire() {
if (getToggleGroup() == null || !isSelected()) {
super.fire();
}
}
@Override protected Skin<?> createDefaultSkin() {
return new RadioButtonSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "radio-button";
@Override protected Pos getInitialAlignment() {
return Pos.CENTER_LEFT;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED: return isSelected();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
