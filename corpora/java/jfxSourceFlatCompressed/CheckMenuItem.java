package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.scene.Node;
public class CheckMenuItem extends MenuItem {
public CheckMenuItem() {
this(null,null);
}
public CheckMenuItem(String text) {
this(text,null);
}
public CheckMenuItem(String text, Node graphic) {
super(text,graphic);
getStyleClass().add(DEFAULT_STYLE_CLASS);
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
get();
if (isSelected()) {
getStyleClass().add(STYLE_CLASS_SELECTED);
} else {
getStyleClass().remove(STYLE_CLASS_SELECTED);
}
}
@Override
public Object getBean() {
return CheckMenuItem.this;
}
@Override
public String getName() {
return "selected";
}
};
}
return selected;
}
private static final String DEFAULT_STYLE_CLASS = "check-menu-item";
private static final String STYLE_CLASS_SELECTED = "selected";
}
