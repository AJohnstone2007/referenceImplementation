package javafx.scene.control.cell;
import static javafx.scene.control.cell.CellUtils.createChoiceBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ChoiceBoxTreeTableCell<S,T> extends TreeTableCell<S,T> {
@SafeVarargs
public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(final T... items) {
return forTreeTableColumn(null, items);
}
@SafeVarargs
public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
final StringConverter<T> converter,
final T... items) {
return forTreeTableColumn(converter, FXCollections.observableArrayList(items));
}
public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
final ObservableList<T> items) {
return forTreeTableColumn(null, items);
}
public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
final StringConverter<T> converter,
final ObservableList<T> items) {
return list -> new ChoiceBoxTreeTableCell<S,T>(converter, items);
}
private final ObservableList<T> items;
private ChoiceBox<T> choiceBox;
public ChoiceBoxTreeTableCell() {
this(FXCollections.<T>observableArrayList());
}
@SafeVarargs
public ChoiceBoxTreeTableCell(T... items) {
this(FXCollections.observableArrayList(items));
}
@SafeVarargs
public ChoiceBoxTreeTableCell(StringConverter<T> converter, T... items) {
this(converter, FXCollections.observableArrayList(items));
}
public ChoiceBoxTreeTableCell(ObservableList<T> items) {
this(null, items);
}
public ChoiceBoxTreeTableCell(StringConverter<T> converter, ObservableList<T> items) {
this.getStyleClass().add("choice-box-tree-table-cell");
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
