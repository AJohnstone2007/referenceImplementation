package javafx.scene.control.cell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class CheckBoxListCell<T> extends ListCell<T> {
public static <T> Callback<ListView<T>, ListCell<T>> forListView(
final Callback<T, ObservableValue<Boolean>> getSelectedProperty) {
return forListView(getSelectedProperty, CellUtils.<T>defaultStringConverter());
}
public static <T> Callback<ListView<T>, ListCell<T>> forListView(
final Callback<T, ObservableValue<Boolean>> getSelectedProperty,
final StringConverter<T> converter) {
return list -> new CheckBoxListCell<T>(getSelectedProperty, converter);
}
private final CheckBox checkBox;
private ObservableValue<Boolean> booleanProperty;
public CheckBoxListCell() {
this(null);
}
public CheckBoxListCell(
final Callback<T, ObservableValue<Boolean>> getSelectedProperty) {
this(getSelectedProperty, CellUtils.<T>defaultStringConverter());
}
public CheckBoxListCell(
final Callback<T, ObservableValue<Boolean>> getSelectedProperty,
final StringConverter<T> converter) {
this.getStyleClass().add("check-box-list-cell");
setSelectedStateCallback(getSelectedProperty);
setConverter(converter);
this.checkBox = new CheckBox();
setAlignment(Pos.CENTER_LEFT);
setContentDisplay(ContentDisplay.LEFT);
setGraphic(null);
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
private ObjectProperty<Callback<T, ObservableValue<Boolean>>>
selectedStateCallback =
new SimpleObjectProperty<Callback<T, ObservableValue<Boolean>>>(
this, "selectedStateCallback");
public final ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
return selectedStateCallback;
}
public final void setSelectedStateCallback(Callback<T, ObservableValue<Boolean>> value) {
selectedStateCallbackProperty().set(value);
}
public final Callback<T, ObservableValue<Boolean>> getSelectedStateCallback() {
return selectedStateCallbackProperty().get();
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
if (! empty) {
StringConverter<T> c = getConverter();
Callback<T, ObservableValue<Boolean>> callback = getSelectedStateCallback();
if (callback == null) {
throw new NullPointerException(
"The CheckBoxListCell selectedStateCallbackProperty can not be null");
}
setGraphic(checkBox);
setText(c != null ? c.toString(item) : (item == null ? "" : item.toString()));
if (booleanProperty != null) {
checkBox.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
}
booleanProperty = callback.call(item);
if (booleanProperty != null) {
checkBox.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
}
} else {
setGraphic(null);
setText(null);
}
}
}
