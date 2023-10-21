package javafx.scene.control.cell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
public class TextFieldTreeTableCell<S,T> extends TreeTableCell<S,T> {
public static <S> Callback<TreeTableColumn<S,String>, TreeTableCell<S,String>> forTreeTableColumn() {
return forTreeTableColumn(new DefaultStringConverter());
}
public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
final StringConverter<T> converter) {
return list -> new TextFieldTreeTableCell<S,T>(converter);
}
private TextField textField;
public TextFieldTreeTableCell() {
this(null);
}
public TextFieldTreeTableCell(StringConverter<T> converter) {
this.getStyleClass().add("text-field-tree-table-cell");
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
if (textField == null) {
textField = CellUtils.createTextField(this, getConverter());
}
CellUtils.startEdit(this, getConverter(), null, null, textField);
}
@Override public void cancelEdit() {
super.cancelEdit();
CellUtils.cancelEdit(this, getConverter(), null);
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
CellUtils.updateItem(this, getConverter(), null, null, textField);
}
}
