package javafx.scene.control.cell;
import static javafx.scene.control.cell.CellUtils.createComboBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ComboBoxListCell<T> extends ListCell<T> {
@SafeVarargs
public static <T> Callback<ListView<T>, ListCell<T>> forListView(final T... items) {
return forListView(FXCollections.observableArrayList(items));
}
@SafeVarargs
public static <T> Callback<ListView<T>, ListCell<T>> forListView(
final StringConverter<T> converter,
final T... items) {
return forListView(converter, FXCollections.observableArrayList(items));
}
public static <T> Callback<ListView<T>, ListCell<T>> forListView(
final ObservableList<T> items) {
return forListView(null, items);
}
public static <T> Callback<ListView<T>, ListCell<T>> forListView(
final StringConverter<T> converter,
final ObservableList<T> items) {
return list -> new ComboBoxListCell<T>(converter, items);
}
private final ObservableList<T> items;
private ComboBox<T> comboBox;
public ComboBoxListCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ComboBoxListCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ComboBoxListCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ComboBoxListCell(ObservableList<T> items) {
this(null, items);
}
public ComboBoxListCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("combo-box-list-cell");
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
