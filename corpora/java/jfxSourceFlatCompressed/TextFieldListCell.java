package javafx.scene.control.cell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
public class TextFieldListCell<T> extends ListCell<T> {
public static Callback<ListView<String>, ListCell<String>> forListView() {
return forListView(new DefaultStringConverter());
}
public static <T> Callback<ListView<T>, ListCell<T>> forListView(final StringConverter<T> converter) {
return list -> new TextFieldListCell<T>(converter);
}
private TextField textField;
public TextFieldListCell() {
this(null);
}
public TextFieldListCell(StringConverter<T> converter) {
this.getStyleClass().add("text-field-list-cell");
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
