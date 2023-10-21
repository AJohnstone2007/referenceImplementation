package javafx.scene.control.cell;
import static javafx.scene.control.cell.CellUtils.createComboBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ComboBoxTableCell<S,T> extends TableCell<S,T> {
@SafeVarargs
public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
final T... items) {
return forTableColumn(null, items);
}
@SafeVarargs
public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
final StringConverter<T> converter,
final T... items) {
return forTableColumn(converter, FXCollections.observableArrayList(items));
}
public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
final ObservableList<T> items) {
return forTableColumn(null, items);
}
public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
final StringConverter<T> converter,
final ObservableList<T> items) {
return list -> new ComboBoxTableCell<S,T>(converter, items);
}
private final ObservableList<T> items;
private ComboBox<T> comboBox;
public ComboBoxTableCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ComboBoxTableCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ComboBoxTableCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ComboBoxTableCell(ObservableList<T> items) {
this(null, items);
}
public ComboBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("combo-box-table-cell");
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
super.startEdit();
if (!isEditing()) {
return;
}
if (comboBox == null) {
comboBox = createComboBox(this, items, converterProperty());
comboBox.editableProperty().bind(comboBoxEditableProperty());
}
comboBox.getSelectionModel().select(getItem());
setText(null);
setGraphic(comboBox);
}
@Override public void cancelEdit() {
super.cancelEdit();
setText(getConverter().toString(getItem()));
setGraphic(null);
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
CellUtils.updateItem(this, getConverter(), null, null, comboBox);
}
}
