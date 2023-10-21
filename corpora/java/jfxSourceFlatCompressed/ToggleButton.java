package javafx.scene.control;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.css.StyleableProperty;
public class ToggleButton extends ButtonBase implements Toggle {
public ToggleButton() {
initialize();
}
public ToggleButton(String text) {
setText(text);
initialize();
}
public ToggleButton(String text, Node graphic) {
setText(text);
setGraphic(graphic);
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TOGGLE_BUTTON);
((StyleableProperty<Pos>)(WritableValue<Pos>)alignmentProperty()).applyStyle(null, Pos.CENTER);
setMnemonicParsing(true);
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
final boolean selected = get();
final ToggleGroup tg = getToggleGroup();
pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selected);
notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);
if (tg != null) {
if (selected) {
tg.selectToggle(ToggleButton.this);
} else if (tg.getSelectedToggle() == ToggleButton.this) {
tg.clearSelectedToggle();
}
}
}
@Override
public Object getBean() {
return ToggleButton.this;
}
@Override
public String getName() {
return "selected";
}
};
}
return selected;
}
private ObjectProperty<ToggleGroup> toggleGroup;
public final void setToggleGroup(ToggleGroup value) {
toggleGroupProperty().set(value);
}
public final ToggleGroup getToggleGroup() {
return toggleGroup == null ? null : toggleGroup.get();
}
public final ObjectProperty<ToggleGroup> toggleGroupProperty() {
if (toggleGroup == null) {
toggleGroup = new ObjectPropertyBase<ToggleGroup>() {
private ToggleGroup old;
private ChangeListener<Toggle> listener = (o, oV, nV) ->
ParentHelper.getTraversalEngine(ToggleButton.this).setOverriddenFocusTraversability(nV != null ? isSelected() : null);
@Override protected void invalidated() {
final ToggleGroup tg = get();
if (tg != null && !tg.getToggles().contains(ToggleButton.this)) {
if (old != null) {
old.getToggles().remove(ToggleButton.this);
}
tg.getToggles().add(ToggleButton.this);
final ParentTraversalEngine parentTraversalEngine = new ParentTraversalEngine(ToggleButton.this);
ParentHelper.setTraversalEngine(ToggleButton.this, parentTraversalEngine);
parentTraversalEngine.setOverriddenFocusTraversability(tg.getSelectedToggle() != null ? isSelected() : null);
tg.selectedToggleProperty().addListener(listener);
} else if (tg == null) {
old.selectedToggleProperty().removeListener(listener);
old.getToggles().remove(ToggleButton.this);
ParentHelper.setTraversalEngine(ToggleButton.this, null);
}
old = tg;
}
@Override
public Object getBean() {
return ToggleButton.this;
}
@Override
public String getName() {
return "toggleGroup";
}
};
}
return toggleGroup;
}
@Override public void fire() {
if (!isDisabled()) {
setSelected(!isSelected());
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new ToggleButtonSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "toggle-button";
private static final PseudoClass PSEUDO_CLASS_SELECTED =
PseudoClass.getPseudoClass("selected");
@Override protected Pos getInitialAlignment() {
return Pos.CENTER;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED: return isSelected();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
