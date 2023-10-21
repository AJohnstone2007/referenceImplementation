package javafx.scene.control;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.css.PseudoClass;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
public class Cell<T> extends Labeled {
public Cell() {
setText(null);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
super.focusedProperty().addListener(new InvalidationListener() {
@Override public void invalidated(Observable property) {
pseudoClassStateChanged(PSEUDO_CLASS_FOCUSED, isFocused());
if (!isFocused() && isEditing()) {
cancelEdit();
}
}
});
pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, true);
}
private ObjectProperty<T> item = new SimpleObjectProperty<T>(this, "item");
public final ObjectProperty<T> itemProperty() { return item; }
public final void setItem(T value) { item.set(value); }
public final T getItem() { return item.get(); }
private ReadOnlyBooleanWrapper empty = new ReadOnlyBooleanWrapper(true) {
@Override protected void invalidated() {
final boolean active = get();
pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, active);
pseudoClassStateChanged(PSEUDO_CLASS_FILLED, !active);
}
@Override
public Object getBean() {
return Cell.this;
}
@Override
public String getName() {
return "empty";
}
};
public final ReadOnlyBooleanProperty emptyProperty() { return empty.getReadOnlyProperty(); }
private void setEmpty(boolean value) { empty.set(value); }
public final boolean isEmpty() { return empty.get(); }
private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper() {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, get());
}
@Override
public Object getBean() {
return Cell.this;
}
@Override
public String getName() {
return "selected";
}
};
public final ReadOnlyBooleanProperty selectedProperty() { return selected.getReadOnlyProperty(); }
void setSelected(boolean value) { selected.set(value); }
public final boolean isSelected() { return selected.get(); }
private ReadOnlyBooleanWrapper editing;
private void setEditing(boolean value) {
editingPropertyImpl().set(value);
}
public final boolean isEditing() {
return editing == null ? false : editing.get();
}
public final ReadOnlyBooleanProperty editingProperty() {
return editingPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper editingPropertyImpl() {
if (editing == null) {
editing = new ReadOnlyBooleanWrapper(this, "editing");
}
return editing;
}
private BooleanProperty editable;
public final void setEditable(boolean value) {
editableProperty().set(value);
}
public final boolean isEditable() {
return editable == null ? true : editable.get();
}
public final BooleanProperty editableProperty() {
if (editable == null) {
editable = new SimpleBooleanProperty(this, "editable", true);
}
return editable;
}
public void startEdit() {
if (isEditable() && !isEditing() && !isEmpty()) {
setEditing(true);
}
}
public void cancelEdit() {
if (isEditing()) {
setEditing(false);
}
}
public void commitEdit(T newValue) {
if (isEditing()) {
setEditing(false);
}
}
@Override protected void layoutChildren() {
if (itemDirty) {
updateItem(getItem(), isEmpty());
itemDirty = false;
}
super.layoutChildren();
}
protected void updateItem(T item, boolean empty) {
setItem(item);
setEmpty(empty);
if (empty && isSelected()) {
updateSelected(false);
}
}
public void updateSelected(boolean selected) {
if (selected && isEmpty()) return;
boolean wasSelected = isSelected();
setSelected(selected);
if (wasSelected != selected) {
markCellDirty();
}
}
protected boolean isItemChanged(T oldItem, T newItem) {
return oldItem != null ? !oldItem.equals(newItem) : newItem != null;
}
private boolean itemDirty = false;
private final void markCellDirty() {
itemDirty = true;
requestLayout();
}
private static final String DEFAULT_STYLE_CLASS = "cell";
private static final PseudoClass PSEUDO_CLASS_SELECTED =
PseudoClass.getPseudoClass("selected");
private static final PseudoClass PSEUDO_CLASS_FOCUSED =
PseudoClass.getPseudoClass("focused");
private static final PseudoClass PSEUDO_CLASS_EMPTY =
PseudoClass.getPseudoClass("empty");
private static final PseudoClass PSEUDO_CLASS_FILLED =
PseudoClass.getPseudoClass("filled");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
