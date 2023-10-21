package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
public class RadioMenuItem extends MenuItem implements Toggle {
public RadioMenuItem() {
this(null,null);
}
public RadioMenuItem(String text) {
this(text,null);
}
public RadioMenuItem(String text, Node graphic) {
super(text,graphic);
getStyleClass().add(DEFAULT_STYLE_CLASS);
}
private ObjectProperty<ToggleGroup> toggleGroup;
@Override public final void setToggleGroup(ToggleGroup value) {
toggleGroupProperty().set(value);
}
@Override public final ToggleGroup getToggleGroup() {
return toggleGroup == null ? null : toggleGroup.get();
}
@Override public final ObjectProperty<ToggleGroup> toggleGroupProperty() {
if (toggleGroup == null) {
toggleGroup = new ObjectPropertyBase<ToggleGroup>() {
private ToggleGroup old;
@Override protected void invalidated() {
if (old != null) {
old.getToggles().remove(RadioMenuItem.this);
}
old = get();
if (get() != null && !get().getToggles().contains(RadioMenuItem.this)) {
get().getToggles().add(RadioMenuItem.this);
}
}
@Override
public Object getBean() {
return RadioMenuItem.this;
}
@Override
public String getName() {
return "toggleGroup";
}
};
}
return toggleGroup;
}
private BooleanProperty selected;
@Override public final void setSelected(boolean value) {
selectedProperty().set(value);
}
@Override public final boolean isSelected() {
return selected == null ? false : selected.get();
}
@Override public final BooleanProperty selectedProperty() {
if (selected == null) {
selected = new BooleanPropertyBase() {
@Override protected void invalidated() {
if (getToggleGroup() != null) {
if (get()) {
getToggleGroup().selectToggle(RadioMenuItem.this);
} else if (getToggleGroup().getSelectedToggle() == RadioMenuItem.this) {
getToggleGroup().clearSelectedToggle();
}
}
if (isSelected()) {
getStyleClass().add(STYLE_CLASS_SELECTED);
} else {
getStyleClass().remove(STYLE_CLASS_SELECTED);
}
}
@Override
public Object getBean() {
return RadioMenuItem.this;
}
@Override
public String getName() {
return "selected";
}
};
}
return selected;
}
private static final String DEFAULT_STYLE_CLASS = "radio-menu-item";
private static final String STYLE_CLASS_SELECTED = "selected";
}
