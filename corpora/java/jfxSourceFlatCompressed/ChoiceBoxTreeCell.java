package javafx.scene.control.cell;
import static javafx.scene.control.cell.CellUtils.createChoiceBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ChoiceBoxTreeCell<T> extends DefaultTreeCell<T> {
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
return list -> new ChoiceBoxTreeCell<T>(converter, items);
}
private final ObservableList<T> items;
private ChoiceBox<T> choiceBox;
private HBox hbox;
public ChoiceBoxTreeCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ChoiceBoxTreeCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ChoiceBoxTreeCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ChoiceBoxTreeCell(ObservableList<T> items) {
this(null, items);
}
public ChoiceBoxTreeCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("choice-box-tree-cell");
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
if (choiceBox == null) {
choiceBox = createChoiceBox(this, items, converterProperty());
}
if (hbox == null) {
hbox = new HBox(CellUtils.TREE_VIEW_HBOX_GRAPHIC_PADDING);
}
choiceBox.getSelectionModel().select(treeItem.getValue());
setText(null);
Node graphic = getTreeItemGraphic();
if (graphic != null) {
hbox.getChildren().setAll(graphic, choiceBox);
setGraphic(hbox);
} else {
setGraphic(choiceBox);
}
}
@Override public void cancelEdit() {
super.cancelEdit();
setText(getConverter().toString(getItem()));
setGraphic(null);
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), choiceBox);
};
private Node getTreeItemGraphic() {
TreeItem<T> treeItem = getTreeItem();
return treeItem == null ? null : treeItem.getGraphic();
}
}
