package javafx.scene.control.cell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
public class TextFieldTreeCell<T> extends DefaultTreeCell<T> {
public static Callback<TreeView<String>, TreeCell<String>> forTreeView() {
return forTreeView(new DefaultStringConverter());
}
public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
final StringConverter<T> converter) {
return list -> new TextFieldTreeCell<T>(converter);
}
private TextField textField;
private HBox hbox;
public TextFieldTreeCell() {
this(null);
}
public TextFieldTreeCell(StringConverter<T> converter) {
this.getStyleClass().add("text-field-tree-cell");
setConverter(converter);
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
@Override public void startEdit() {
super.startEdit();
if (!isEditing()) {
return;
}
StringConverter<T> converter = getConverter();
if (textField == null) {
textField = CellUtils.createTextField(this, converter);
}
if (hbox == null) {
hbox = new HBox(CellUtils.TREE_VIEW_HBOX_GRAPHIC_PADDING);
}
CellUtils.startEdit(this, converter, hbox, getTreeItemGraphic(), textField);
}
@Override public void cancelEdit() {
super.cancelEdit();
CellUtils.cancelEdit(this, getConverter(), getTreeItemGraphic());
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
}
private Node getTreeItemGraphic() {
TreeItem<T> treeItem = getTreeItem();
return treeItem == null ? null : treeItem.getGraphic();
}
}
