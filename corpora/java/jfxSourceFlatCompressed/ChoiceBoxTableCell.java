package javafx.scene.control.cell;
import static javafx.scene.control.cell.CellUtils.createChoiceBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ChoiceBoxTableCell<S,T> extends TableCell<S,T> {
@SafeVarargs
public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(final T... items) {
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
return list -> new ChoiceBoxTableCell<S,T>(converter, items);
}
private final ObservableList<T> items;
private ChoiceBox<T> choiceBox;
public ChoiceBoxTableCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ChoiceBoxTableCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ChoiceBoxTableCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ChoiceBoxTableCell(ObservableList<T> items) {
this(null, items);
}
public ChoiceBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("choice-box-table-cell");
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
super.startEdit();
if (!isEditing()) {
return;
}
if (choiceBox == null) {
choiceBox = createChoiceBox(this, items, converterProperty());
}
choiceBox.getSelectionModel().select(getItem());
setText(null);
setGraphic(choiceBox);
}
@Override public void cancelEdit() {
super.cancelEdit();
setText(getConverter().toString(getItem()));
setGraphic(null);
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
CellUtils.updateItem(this, getConverter(), null, null, choiceBox);
}
}
