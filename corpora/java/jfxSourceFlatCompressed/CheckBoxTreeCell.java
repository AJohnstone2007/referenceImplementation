package javafx.scene.control.cell;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class CheckBoxTreeCell<T> extends DefaultTreeCell<T> {
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView() {
Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty =
item -> {
if (item instanceof CheckBoxTreeItem<?>) {
return ((CheckBoxTreeItem<?>)item).selectedProperty();
}
return null;
};
return forTreeView(getSelectedProperty,
CellUtils.<T>defaultTreeItemStringConverter());
}
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final Callback<TreeItem<T>,
ObservableValue<Boolean>> getSelectedProperty) {
return forTreeView(getSelectedProperty, CellUtils.<T>defaultTreeItemStringConverter());
}
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
final StringConverter<TreeItem<T>> converter) {
return tree -> new CheckBoxTreeCell<T>(getSelectedProperty, converter);
}
private final CheckBox checkBox;
private ObservableValue<Boolean> booleanProperty;
private BooleanProperty indeterminateProperty;
public CheckBoxTreeCell() {
this(item -> {
if (item instanceof CheckBoxTreeItem<?>) {
return ((CheckBoxTreeItem<?>)item).selectedProperty();
}
return null;
});
}
public CheckBoxTreeCell(
final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty) {
this(getSelectedProperty, CellUtils.<T>defaultTreeItemStringConverter(), null);
}
public CheckBoxTreeCell(
final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
final StringConverter<TreeItem<T>> converter) {
this(getSelectedProperty, converter, null);
}
private CheckBoxTreeCell(
final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
final StringConverter<TreeItem<T>> converter,
final Callback<TreeItem<T>, ObservableValue<Boolean>> getIndeterminateProperty) {
this.getStyleClass().add("check-box-tree-cell");
setSelectedStateCallback(getSelectedProperty);
setConverter(converter);
this.checkBox = new CheckBox();
this.checkBox.setAllowIndeterminate(false);
setGraphic(null);
}
private ObjectProperty<StringConverter<TreeItem<T>>> converter =
new SimpleObjectProperty<StringConverter<TreeItem<T>>>(this, "converter");
public final ObjectProperty<StringConverter<TreeItem<T>>> converterProperty() {
return converter;
}
public final void setConverter(StringConverter<TreeItem<T>> value) {
converterProperty().set(value);
}
public final StringConverter<TreeItem<T>> getConverter() {
return converterProperty().get();
}
private ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>>
selectedStateCallback =
new SimpleObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>>(
this, "selectedStateCallback");
public final ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
return selectedStateCallback;
}
public final void setSelectedStateCallback(Callback<TreeItem<T>, ObservableValue<Boolean>> value) {
selectedStateCallbackProperty().set(value);
}
public final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedStateCallback() {
return selectedStateCallbackProperty().get();
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setText(null);
setGraphic(null);
} else {
StringConverter<TreeItem<T>> c = getConverter();
TreeItem<T> treeItem = getTreeItem();
setText(c != null ? c.toString(treeItem) : (treeItem == null ? "" : treeItem.toString()));
checkBox.setGraphic(treeItem == null ? null : treeItem.getGraphic());
setGraphic(checkBox);
if (booleanProperty != null) {
checkBox.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
}
if (indeterminateProperty != null) {
checkBox.indeterminateProperty().unbindBidirectional(indeterminateProperty);
}
if (treeItem instanceof CheckBoxTreeItem) {
CheckBoxTreeItem<T> cbti = (CheckBoxTreeItem<T>) treeItem;
booleanProperty = cbti.selectedProperty();
checkBox.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
indeterminateProperty = cbti.indeterminateProperty();
checkBox.indeterminateProperty().bindBidirectional(indeterminateProperty);
} else {
Callback<TreeItem<T>, ObservableValue<Boolean>> callback = getSelectedStateCallback();
if (callback == null) {
throw new NullPointerException(
"The CheckBoxTreeCell selectedStateCallbackProperty can not be null");
}
booleanProperty = callback.call(treeItem);
if (booleanProperty != null) {
checkBox.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
}
}
}
}
@Override void updateDisplay(T item, boolean empty) {
}
}
