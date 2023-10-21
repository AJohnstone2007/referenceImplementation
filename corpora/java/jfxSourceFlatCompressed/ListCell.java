package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.ListCellSkin;
public class ListCell<T> extends IndexedCell<T> {
public ListCell() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.LIST_ITEM);
}
private final InvalidationListener editingListener = value -> {
updateEditing();
};
private boolean updateEditingIndex = true;
private final ListChangeListener<Integer> selectedListener = c -> {
updateSelection();
};
private final ChangeListener<MultipleSelectionModel<T>> selectionModelPropertyListener = new ChangeListener<MultipleSelectionModel<T>>() {
@Override
public void changed(
ObservableValue<? extends MultipleSelectionModel<T>> observable,
MultipleSelectionModel<T> oldValue,
MultipleSelectionModel<T> newValue) {
if (oldValue != null) {
oldValue.getSelectedIndices().removeListener(weakSelectedListener);
}
if (newValue != null) {
newValue.getSelectedIndices().addListener(weakSelectedListener);
}
updateSelection();
}
};
private final ListChangeListener<T> itemsListener = c -> {
boolean doUpdate = false;
while (c.next()) {
final int currentIndex = getIndex();
final ListView<T> lv = getListView();
final List<T> items = lv == null ? null : lv.getItems();
final int itemCount = items == null ? 0 : items.size();
final boolean indexAfterChangeFromIndex = currentIndex >= c.getFrom();
final boolean indexBeforeChangeToIndex = currentIndex < c.getTo() || currentIndex == itemCount;
final boolean indexInRange = indexAfterChangeFromIndex && indexBeforeChangeToIndex;
doUpdate = indexInRange || (indexAfterChangeFromIndex && !c.wasReplaced() && (c.wasRemoved() || c.wasAdded()));
}
if (doUpdate) {
updateItem(-1);
}
};
private final InvalidationListener itemsPropertyListener = new InvalidationListener() {
private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(null);
@Override public void invalidated(Observable observable) {
ObservableList<T> oldItems = weakItemsRef.get();
if (oldItems != null) {
oldItems.removeListener(weakItemsListener);
}
ListView<T> listView = getListView();
ObservableList<T> items = listView == null ? null : listView.getItems();
weakItemsRef = new WeakReference<>(items);
if (items != null) {
items.addListener(weakItemsListener);
}
updateItem(-1);
}
};
private final InvalidationListener focusedListener = value -> {
updateFocus();
};
private final ChangeListener<FocusModel<T>> focusModelPropertyListener = new ChangeListener<FocusModel<T>>() {
@Override public void changed(ObservableValue<? extends FocusModel<T>> observable,
FocusModel<T> oldValue,
FocusModel<T> newValue) {
if (oldValue != null) {
oldValue.focusedIndexProperty().removeListener(weakFocusedListener);
}
if (newValue != null) {
newValue.focusedIndexProperty().addListener(weakFocusedListener);
}
updateFocus();
}
};
private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(editingListener);
private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener<Integer>(selectedListener);
private final WeakChangeListener<MultipleSelectionModel<T>> weakSelectionModelPropertyListener = new WeakChangeListener<MultipleSelectionModel<T>>(selectionModelPropertyListener);
private final WeakListChangeListener<T> weakItemsListener = new WeakListChangeListener<T>(itemsListener);
private final WeakInvalidationListener weakItemsPropertyListener = new WeakInvalidationListener(itemsPropertyListener);
private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(focusedListener);
private final WeakChangeListener<FocusModel<T>> weakFocusModelPropertyListener = new WeakChangeListener<FocusModel<T>>(focusModelPropertyListener);
private ReadOnlyObjectWrapper<ListView<T>> listView = new ReadOnlyObjectWrapper<ListView<T>>(this, "listView") {
private WeakReference<ListView<T>> weakListViewRef = new WeakReference<ListView<T>>(null);
@Override protected void invalidated() {
final ListView<T> currentListView = get();
final ListView<T> oldListView = weakListViewRef.get();
if (currentListView == oldListView) return;
if (oldListView != null) {
final MultipleSelectionModel<T> sm = oldListView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().removeListener(weakSelectedListener);
}
final FocusModel<T> fm = oldListView.getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().removeListener(weakFocusedListener);
}
final ObservableList<T> items = oldListView.getItems();
if (items != null) {
items.removeListener(weakItemsListener);
}
oldListView.editingIndexProperty().removeListener(weakEditingListener);
oldListView.itemsProperty().removeListener(weakItemsPropertyListener);
oldListView.focusModelProperty().removeListener(weakFocusModelPropertyListener);
oldListView.selectionModelProperty().removeListener(weakSelectionModelPropertyListener);
}
if (currentListView != null) {
final MultipleSelectionModel<T> sm = currentListView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().addListener(weakSelectedListener);
}
final FocusModel<T> fm = currentListView.getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().addListener(weakFocusedListener);
}
final ObservableList<T> items = currentListView.getItems();
if (items != null) {
items.addListener(weakItemsListener);
}
currentListView.editingIndexProperty().addListener(weakEditingListener);
currentListView.itemsProperty().addListener(weakItemsPropertyListener);
currentListView.focusModelProperty().addListener(weakFocusModelPropertyListener);
currentListView.selectionModelProperty().addListener(weakSelectionModelPropertyListener);
weakListViewRef = new WeakReference<ListView<T>>(currentListView);
}
updateItem(-1);
updateSelection();
updateFocus();
requestLayout();
}
};
private void setListView(ListView<T> value) { listView.set(value); }
public final ListView<T> getListView() { return listView.get(); }
public final ReadOnlyObjectProperty<ListView<T>> listViewProperty() { return listView.getReadOnlyProperty(); }
@Override void indexChanged(int oldIndex, int newIndex) {
super.indexChanged(oldIndex, newIndex);
if (isEditing() && newIndex == oldIndex) {
} else {
updateItem(oldIndex);
updateSelection();
updateFocus();
updateEditing();
}
}
@Override protected Skin<?> createDefaultSkin() {
return new ListCellSkin<T>(this);
}
private int indexAtStartEdit;
@Override public void startEdit() {
if (isEditing()) return;
final ListView<T> list = getListView();
if (!isEditable() || (list != null && ! list.isEditable())) {
return;
}
super.startEdit();
if (!isEditing()) return;
indexAtStartEdit = getIndex();
if (list != null) {
list.fireEvent(new ListView.EditEvent<T>(list,
ListView.<T>editStartEvent(),
null,
indexAtStartEdit));
list.edit(indexAtStartEdit);
list.requestFocus();
}
}
@Override public void commitEdit(T newValue) {
if (! isEditing()) return;
super.commitEdit(newValue);
ListView<T> list = getListView();
if (list != null) {
list.fireEvent(new ListView.EditEvent<T>(list,
ListView.<T>editCommitEvent(),
newValue,
list.getEditingIndex()));
}
updateItem(newValue, false);
if (list != null) {
list.edit(-1);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(list);
}
}
@Override public void cancelEdit() {
if (! isEditing()) return;
super.cancelEdit();
ListView<T> list = getListView();
if (list != null) {
if (updateEditingIndex) list.edit(-1);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(list);
list.fireEvent(new ListView.EditEvent<T>(list,
ListView.<T>editCancelEvent(),
null,
indexAtStartEdit));
}
}
private boolean firstRun = true;
private void updateItem(int oldIndex) {
final ListView<T> lv = getListView();
final List<T> items = lv == null ? null : lv.getItems();
final int index = getIndex();
final int itemCount = items == null ? -1 : items.size();
boolean valid = items != null && index >=0 && index < itemCount;
final T oldValue = getItem();
final boolean isEmpty = isEmpty();
outer: if (valid) {
final T newValue = items.get(index);
if (oldIndex == index) {
if (!isItemChanged(oldValue, newValue)) {
break outer;
}
}
updateItem(newValue, false);
} else {
if (!isEmpty || firstRun) {
updateItem(null, true);
firstRun = false;
}
}
}
public final void updateListView(ListView<T> listView) {
setListView(listView);
}
private void updateSelection() {
if (isEmpty()) return;
int index = getIndex();
ListView<T> listView = getListView();
if (index == -1 || listView == null) return;
SelectionModel<T> sm = listView.getSelectionModel();
if (sm == null) {
updateSelected(false);
return;
}
boolean isSelected = sm.isSelected(index);
if (isSelected() == isSelected) return;
updateSelected(isSelected);
}
private void updateFocus() {
int index = getIndex();
ListView<T> listView = getListView();
if (index == -1 || listView == null) return;
FocusModel<T> fm = listView.getFocusModel();
if (fm == null) {
setFocused(false);
return;
}
setFocused(fm.isFocused(index));
}
private void updateEditing() {
final int index = getIndex();
final ListView<T> list = getListView();
final int editIndex = list == null ? -1 : list.getEditingIndex();
final boolean editing = isEditing();
final boolean match = (list != null) && (index != -1) && (index == editIndex);
if (match && !editing) {
startEdit();
} else if (!match && editing) {
try {
updateEditingIndex = false;
cancelEdit();
} finally {
updateEditingIndex = true;
}
}
}
private static final String DEFAULT_STYLE_CLASS = "list-cell";
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case INDEX: return getIndex();
case SELECTED: return isSelected();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case REQUEST_FOCUS: {
ListView<T> listView = getListView();
if (listView != null) {
FocusModel<T> fm = listView.getFocusModel();
if (fm != null) {
fm.focus(getIndex());
}
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
}
