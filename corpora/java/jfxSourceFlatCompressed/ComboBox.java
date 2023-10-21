package javafx.scene.control;
import com.sun.javafx.scene.control.FakeFocusTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.lang.ref.WeakReference;
public class ComboBox<T> extends ComboBoxBase<T> {
private static <T> StringConverter<T> defaultStringConverter() {
return new StringConverter<T>() {
@Override public String toString(T t) {
return t == null ? null : t.toString();
}
@Override public T fromString(String string) {
return (T) string;
}
};
}
public ComboBox() {
this(FXCollections.<T>observableArrayList());
}
public ComboBox(ObservableList<T> items) {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.COMBO_BOX);
setItems(items);
setSelectionModel(new ComboBoxSelectionModel<T>(this));
valueProperty().addListener((ov, t, t1) -> {
if (getItems() == null) return;
SelectionModel<T> sm = getSelectionModel();
if (sm == null) return;
int index = getItems().indexOf(t1);
if (index == -1) {
Runnable r = () -> {
sm.setSelectedIndex(-1);
sm.setSelectedItem(t1);
};
if (sm instanceof ComboBoxSelectionModel) {
((ComboBoxSelectionModel)sm).doAtomic(r);
} else {
r.run();
}
} else {
T selectedItem = sm.getSelectedItem();
if (selectedItem == null || ! selectedItem.equals(getValue())) {
sm.clearAndSelect(index);
}
}
});
editableProperty().addListener(o -> {
if (!isEditable()) {
if (getItems() != null && !getItems().contains(getValue())) {
SingleSelectionModel<T> selectionModel = getSelectionModel();
if (selectionModel != null) {
selectionModel.clearSelection();
}
}
}
});
focusedProperty().addListener(o -> {
if (!isFocused()) {
commitValue();
}
});
}
private ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<ObservableList<T>>(this, "items");
public final void setItems(ObservableList<T> value) { itemsProperty().set(value); }
public final ObservableList<T> getItems() {return items.get(); }
public ObjectProperty<ObservableList<T>> itemsProperty() { return items; }
public ObjectProperty<StringConverter<T>> converterProperty() { return converter; }
private ObjectProperty<StringConverter<T>> converter =
new SimpleObjectProperty<StringConverter<T>>(this, "converter", ComboBox.<T>defaultStringConverter());
public final void setConverter(StringConverter<T> value) { converterProperty().set(value); }
public final StringConverter<T> getConverter() {return converterProperty().get(); }
private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory =
new SimpleObjectProperty<Callback<ListView<T>, ListCell<T>>>(this, "cellFactory");
public final void setCellFactory(Callback<ListView<T>, ListCell<T>> value) { cellFactoryProperty().set(value); }
public final Callback<ListView<T>, ListCell<T>> getCellFactory() {return cellFactoryProperty().get(); }
public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() { return cellFactory; }
public ObjectProperty<ListCell<T>> buttonCellProperty() { return buttonCell; }
private ObjectProperty<ListCell<T>> buttonCell =
new SimpleObjectProperty<ListCell<T>>(this, "buttonCell");
public final void setButtonCell(ListCell<T> value) { buttonCellProperty().set(value); }
public final ListCell<T> getButtonCell() {return buttonCellProperty().get(); }
private ObjectProperty<SingleSelectionModel<T>> selectionModel = new SimpleObjectProperty<SingleSelectionModel<T>>(this, "selectionModel") {
private SingleSelectionModel<T> oldSM = null;
@Override protected void invalidated() {
if (oldSM != null) {
oldSM.selectedItemProperty().removeListener(selectedItemListener);
}
SingleSelectionModel<T> sm = get();
oldSM = sm;
if (sm != null) {
sm.selectedItemProperty().addListener(selectedItemListener);
}
}
};
public final void setSelectionModel(SingleSelectionModel<T> value) { selectionModel.set(value); }
public final SingleSelectionModel<T> getSelectionModel() { return selectionModel.get(); }
public final ObjectProperty<SingleSelectionModel<T>> selectionModelProperty() { return selectionModel; }
private IntegerProperty visibleRowCount
= new SimpleIntegerProperty(this, "visibleRowCount", 10);
public final void setVisibleRowCount(int value) { visibleRowCount.set(value); }
public final int getVisibleRowCount() { return visibleRowCount.get(); }
public final IntegerProperty visibleRowCountProperty() { return visibleRowCount; }
private TextField textField;
private ReadOnlyObjectWrapper<TextField> editor;
public final TextField getEditor() {
return editorProperty().get();
}
public final ReadOnlyObjectProperty<TextField> editorProperty() {
if (editor == null) {
editor = new ReadOnlyObjectWrapper<>(this, "editor");
textField = new FakeFocusTextField();
editor.set(textField);
}
return editor.getReadOnlyProperty();
}
private ObjectProperty<Node> placeholder;
public final ObjectProperty<Node> placeholderProperty() {
if (placeholder == null) {
placeholder = new SimpleObjectProperty<Node>(this, "placeholder");
}
return placeholder;
}
public final void setPlaceholder(Node value) {
placeholderProperty().set(value);
}
public final Node getPlaceholder() {
return placeholder == null ? null : placeholder.get();
}
@Override protected Skin<?> createDefaultSkin() {
return new ComboBoxListViewSkin<T>(this);
}
public final void commitValue() {
if (!isEditable()) return;
String text = getEditor().getText();
StringConverter<T> converter = getConverter();
if (converter != null) {
T value = converter.fromString(text);
setValue(value);
}
}
public final void cancelEdit() {
if (!isEditable()) return;
final T committedValue = getValue();
StringConverter<T> converter = getConverter();
if (converter != null) {
String valueString = converter.toString(committedValue);
getEditor().setText(valueString);
}
}
private ChangeListener<T> selectedItemListener = new ChangeListener<T>() {
@Override public void changed(ObservableValue<? extends T> ov, T t, T t1) {
if (wasSetAllCalled && t1 == null) {
} else {
updateValue(t1);
}
wasSetAllCalled = false;
}
};
private void updateValue(T newValue) {
if (! valueProperty().isBound()) {
setValue(newValue);
}
}
private static final String DEFAULT_STYLE_CLASS = "combo-box";
private boolean wasSetAllCalled = false;
private int previousItemCount = -1;
static class ComboBoxSelectionModel<T> extends SingleSelectionModel<T> {
private final ComboBox<T> comboBox;
private boolean atomic = false;
private void doAtomic(Runnable r) {
atomic = true;
r.run();
atomic = false;
}
public ComboBoxSelectionModel(final ComboBox<T> cb) {
if (cb == null) {
throw new NullPointerException("ComboBox can not be null");
}
this.comboBox = cb;
this.comboBox.previousItemCount = getItemCount();
selectedIndexProperty().addListener(valueModel -> {
if (atomic) return;
setSelectedItem(getModelItem(getSelectedIndex()));
});
itemsObserver = new InvalidationListener() {
private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(comboBox.getItems());
@Override public void invalidated(Observable observable) {
ObservableList<T> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(comboBox.getItems());
updateItemsObserver(oldItems, comboBox.getItems());
comboBox.previousItemCount = getItemCount();
}
};
this.comboBox.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
if (comboBox.getItems() != null) {
this.comboBox.getItems().addListener(weakItemsContentObserver);
}
}
private final ListChangeListener<T> itemsContentObserver = new ListChangeListener<T>() {
@Override public void onChanged(Change<? extends T> c) {
if (comboBox.getItems() == null || comboBox.getItems().isEmpty()) {
setSelectedIndex(-1);
} else if (getSelectedIndex() == -1 && getSelectedItem() != null) {
int newIndex = comboBox.getItems().indexOf(getSelectedItem());
if (newIndex != -1) {
setSelectedIndex(newIndex);
}
}
int shift = 0;
while (c.next()) {
comboBox.wasSetAllCalled = comboBox.previousItemCount == c.getRemovedSize();
if (c.wasReplaced()) {
} else if (c.wasAdded() || c.wasRemoved()) {
if (c.getFrom() <= getSelectedIndex() && getSelectedIndex()!= -1) {
shift += c.wasAdded() ? c.getAddedSize() : -c.getRemovedSize();
}
}
}
if (shift != 0) {
clearAndSelect(getSelectedIndex() + shift);
} else if (comboBox.wasSetAllCalled && getSelectedIndex() >= 0 && getSelectedItem() != null) {
T selectedItem = getSelectedItem();
for (int i = 0; i < comboBox.getItems().size(); i++) {
if (selectedItem.equals(comboBox.getItems().get(i))) {
comboBox.setValue(null);
setSelectedItem(null);
setSelectedIndex(i);
break;
}
}
}
comboBox.previousItemCount = getItemCount();
}
};
private final InvalidationListener itemsObserver;
private WeakListChangeListener<T> weakItemsContentObserver =
new WeakListChangeListener<T>(itemsContentObserver);
private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
if (oldList != null) {
oldList.removeListener(weakItemsContentObserver);
}
if (newList != null) {
newList.addListener(weakItemsContentObserver);
}
int newValueIndex = -1;
if (newList != null) {
T value = comboBox.getValue();
if (value != null) {
newValueIndex = newList.indexOf(value);
}
}
setSelectedIndex(newValueIndex);
}
@Override protected T getModelItem(int index) {
final ObservableList<T> items = comboBox.getItems();
if (items == null) return null;
if (index < 0 || index >= items.size()) return null;
return items.get(index);
}
@Override protected int getItemCount() {
final ObservableList<T> items = comboBox.getItems();
return items == null ? 0 : items.size();
}
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch(attribute) {
case TEXT:
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
Object title = super.queryAccessibleAttribute(attribute, parameters);
if (title != null) return title;
StringConverter<T> converter = getConverter();
if (converter == null) {
return getValue() != null ? getValue().toString() : "";
}
return converter.toString(getValue());
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
