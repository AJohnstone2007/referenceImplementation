package javafx.scene.control.cell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import static javafx.scene.control.cell.CellUtils.createComboBox;
public class ComboBoxTreeCell<T> extends DefaultTreeCell<T> {
@SafeVarargs
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(T... items) {
return forTreeView(FXCollections.observableArrayList(items));
}
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final ObservableList<T> items) {
return forTreeView(null, items);
}
@SafeVarargs
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final StringConverter<T> converter,
final T... items) {
return forTreeView(converter, FXCollections.observableArrayList(items));
}
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final StringConverter<T> converter,
final ObservableList<T> items) {
return list -> new ComboBoxTreeCell<T>(converter, items);
}
private final ObservableList<T> items;
private ComboBox<T> comboBox;
private HBox hbox;
public ComboBoxTreeCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ComboBoxTreeCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ComboBoxTreeCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ComboBoxTreeCell(ObservableList<T> items) {
this(null, items);
}
public ComboBoxTreeCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("combo-box-tree-cell");
this.items = items;
setConverter(converter != null ? converter : CellUtils.<T>defaultStringConverter());
}
private ObjectProperty<StringConverter<T>> converter =
new SimpleObjectProperty<StringConverter<T>>(this, "converter");
public final ObjectProperty<StringConverter<T>> converterProperty() {
return converter;
}
public final void setConverter(StringConverter<T> value) {
converterProperty().set(value);
}
public final StringConverter<T> getConverter() {
return converterProperty().get();
}
private BooleanProperty comboBoxEditable =
new SimpleBooleanProperty(this, "comboBoxEditable");
public final BooleanProperty comboBoxEditableProperty() {
return comboBoxEditable;
}
public final void setComboBoxEditable(boolean value) {
comboBoxEditableProperty().set(value);
}
public final boolean isComboBoxEditable() {
return comboBoxEditableProperty().get();
}
public ObservableList<T> getItems() {
return items;
}
@Override public void startEdit() {
TreeItem<T> treeItem = getTreeItem();
if (treeItem == null) {
return;
}
super.startEdit();
if (!isEditing()) {
return;
}
if (comboBox == null) {
comboBox = createComboBox(this, items, converterProperty());
comboBox.editableProperty().bind(comboBoxEditableProperty());
}
if (hbox == null) {
hbox = new HBox(CellUtils.TREE_VIEW_HBOX_GRAPHIC_PADDING);
}
comboBox.getSelectionModel().select(treeItem.getValue());
setText(null);
Node graphic = CellUtils.getGraphic(treeItem);
if (graphic != null) {
hbox.getChildren().setAll(graphic, comboBox);
setGraphic(hbox);
} else {
setGraphic(comboBox);
}
}
@Override public void cancelEdit() {
super.cancelEdit();
setText(getConverter().toString(getItem()));
setGraphic(null);
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
Node graphic = CellUtils.getGraphic(getTreeItem());
CellUtils.updateItem(this, getConverter(), hbox, graphic, comboBox);
};
}
