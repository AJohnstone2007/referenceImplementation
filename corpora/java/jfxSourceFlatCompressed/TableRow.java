package javafx.scene.control;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.collections.WeakListChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.skin.TableRowSkin;
public class TableRow<T> extends IndexedCell<T> {
public TableRow() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TABLE_ROW);
}
private ListChangeListener<Integer> selectedListener = c -> {
updateSelection();
};
private final InvalidationListener focusedListener = valueModel -> {
updateFocus();
};
private final InvalidationListener editingListener = valueModel -> {
updateEditing();
};
private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener<>(selectedListener);
private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(focusedListener);
private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(editingListener);
private ReadOnlyObjectWrapper<TableView<T>> tableView;
private void setTableView(TableView<T> value) {
tableViewPropertyImpl().set(value);
}
public final TableView<T> getTableView() {
return tableView == null ? null : tableView.get();
}
public final ReadOnlyObjectProperty<TableView<T>> tableViewProperty() {
return tableViewPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TableView<T>> tableViewPropertyImpl() {
if (tableView == null) {
tableView = new ReadOnlyObjectWrapper<TableView<T>>() {
private WeakReference<TableView<T>> weakTableViewRef;
@Override protected void invalidated() {
TableView.TableViewSelectionModel<T> sm;
TableViewFocusModel<T> fm;
if (weakTableViewRef != null) {
TableView<T> oldTableView = weakTableViewRef.get();
if (oldTableView != null) {
sm = oldTableView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().removeListener(weakSelectedListener);
}
fm = oldTableView.getFocusModel();
if (fm != null) {
fm.focusedCellProperty().removeListener(weakFocusedListener);
}
oldTableView.editingCellProperty().removeListener(weakEditingListener);
}
weakTableViewRef = null;
}
TableView<T> tableView = getTableView();
if (tableView != null) {
sm = tableView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().addListener(weakSelectedListener);
}
fm = tableView.getFocusModel();
if (fm != null) {
fm.focusedCellProperty().addListener(weakFocusedListener);
}
tableView.editingCellProperty().addListener(weakEditingListener);
weakTableViewRef = new WeakReference<TableView<T>>(get());
}
}
@Override
public Object getBean() {
return TableRow.this;
}
@Override
public String getName() {
return "tableView";
}
};
}
return tableView;
}
@Override protected Skin<?> createDefaultSkin() {
return new TableRowSkin<>(this);
}
@Override void indexChanged(int oldIndex, int newIndex) {
super.indexChanged(oldIndex, newIndex);
updateItem(oldIndex);
updateSelection();
updateFocus();
}
private boolean isFirstRun = true;
private void updateItem(int oldIndex) {
TableView<T> tv = getTableView();
if (tv == null || tv.getItems() == null) return;
final List<T> items = tv.getItems();
final int itemCount = items == null ? -1 : items.size();
final int newIndex = getIndex();
boolean valid = newIndex >= 0 && newIndex < itemCount;
final T oldValue = getItem();
final boolean isEmpty = isEmpty();
outer: if (valid) {
final T newValue = items.get(newIndex);
if (oldIndex == newIndex) {
if (!isItemChanged(oldValue, newValue)) {
break outer;
}
}
updateItem(newValue, false);
} else {
if ((!isEmpty && oldValue != null) || isFirstRun) {
updateItem(null, true);
isFirstRun = false;
}
}
}
private void updateSelection() {
if (getIndex() == -1) return;
TableView<T> table = getTableView();
boolean isSelected = table != null &&
table.getSelectionModel() != null &&
! table.getSelectionModel().isCellSelectionEnabled() &&
table.getSelectionModel().isSelected(getIndex());
updateSelected(isSelected);
}
private void updateFocus() {
if (getIndex() == -1) return;
TableView<T> table = getTableView();
if (table == null) return;
TableView.TableViewSelectionModel<T> sm = table.getSelectionModel();
TableView.TableViewFocusModel<T> fm = table.getFocusModel();
if (sm == null || fm == null) return;
boolean isFocused = ! sm.isCellSelectionEnabled() && fm.isFocused(getIndex());
setFocused(isFocused);
}
private void updateEditing() {
if (getIndex() == -1) return;
TableView<T> table = getTableView();
if (table == null) return;
TableView.TableViewSelectionModel<T> sm = table.getSelectionModel();
if (sm == null || sm.isCellSelectionEnabled()) return;
TablePosition<T,?> editCell = table.getEditingCell();
if (editCell != null && editCell.getTableColumn() != null) {
return;
}
boolean rowMatch = editCell == null ? false : editCell.getRow() == getIndex();
if (! isEditing() && rowMatch) {
startEdit();
} else if (isEditing() && ! rowMatch) {
cancelEdit();
}
}
public final void updateTableView(TableView<T> tv) {
setTableView(tv);
}
private static final String DEFAULT_STYLE_CLASS = "table-row-cell";
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case INDEX: return getIndex();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
