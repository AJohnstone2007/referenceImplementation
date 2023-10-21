package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.skin.TableCellSkin;
import javafx.collections.WeakListChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn.CellEditEvent;
public class TableCell<S,T> extends IndexedCell<T> {
public TableCell() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TABLE_CELL);
updateColumnIndex();
}
boolean lockItemOnEdit = false;
private boolean itemDirty = false;
private ListChangeListener<TablePosition> selectedListener = c -> {
while (c.next()) {
if (c.wasAdded() || c.wasRemoved()) {
updateSelection();
}
}
};
private final InvalidationListener focusedListener = value -> {
updateFocus();
};
private final InvalidationListener tableRowUpdateObserver = value -> {
itemDirty = true;
requestLayout();
};
private final InvalidationListener editingListener = value -> {
updateEditing();
};
private ListChangeListener<TableColumn<S,?>> visibleLeafColumnsListener = c -> {
updateColumnIndex();
};
private ListChangeListener<String> columnStyleClassListener = c -> {
while (c.next()) {
if (c.wasRemoved()) {
getStyleClass().removeAll(c.getRemoved());
}
if (c.wasAdded()) {
getStyleClass().addAll(c.getAddedSubList());
}
}
};
private final InvalidationListener columnStyleListener = value -> {
if (getTableColumn() != null) {
possiblySetStyle(getTableColumn().getStyle());
}
};
private final InvalidationListener columnIdListener = value -> {
if (getTableColumn() != null) {
possiblySetId(getTableColumn().getId());
}
};
private final WeakListChangeListener<TablePosition> weakSelectedListener =
new WeakListChangeListener<>(selectedListener);
private final WeakInvalidationListener weakFocusedListener =
new WeakInvalidationListener(focusedListener);
private final WeakInvalidationListener weaktableRowUpdateObserver =
new WeakInvalidationListener(tableRowUpdateObserver);
private final WeakInvalidationListener weakEditingListener =
new WeakInvalidationListener(editingListener);
private final WeakInvalidationListener weakColumnStyleListener =
new WeakInvalidationListener(columnStyleListener);
private final WeakInvalidationListener weakColumnIdListener =
new WeakInvalidationListener(columnIdListener);
private final WeakListChangeListener<TableColumn<S,?>> weakVisibleLeafColumnsListener =
new WeakListChangeListener<>(visibleLeafColumnsListener);
private final WeakListChangeListener<String> weakColumnStyleClassListener =
new WeakListChangeListener<String>(columnStyleClassListener);
private ReadOnlyObjectWrapper<TableColumn<S,T>> tableColumn = new ReadOnlyObjectWrapper<TableColumn<S,T>>() {
@Override protected void invalidated() {
updateColumnIndex();
}
@Override public Object getBean() {
return TableCell.this;
}
@Override public String getName() {
return "tableColumn";
}
};
public final ReadOnlyObjectProperty<TableColumn<S,T>> tableColumnProperty() { return tableColumn.getReadOnlyProperty(); }
private void setTableColumn(TableColumn<S,T> value) { tableColumn.set(value); }
public final TableColumn<S,T> getTableColumn() { return tableColumn.get(); }
private ReadOnlyObjectWrapper<TableView<S>> tableView;
private void setTableView(TableView<S> value) {
tableViewPropertyImpl().set(value);
}
public final TableView<S> getTableView() {
return tableView == null ? null : tableView.get();
}
public final ReadOnlyObjectProperty<TableView<S>> tableViewProperty() {
return tableViewPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TableView<S>> tableViewPropertyImpl() {
if (tableView == null) {
tableView = new ReadOnlyObjectWrapper<TableView<S>>() {
private WeakReference<TableView<S>> weakTableViewRef;
@Override protected void invalidated() {
TableView.TableViewSelectionModel<S> sm;
TableViewFocusModel<S> fm;
if (weakTableViewRef != null) {
cleanUpTableViewListeners(weakTableViewRef.get());
}
if (get() != null) {
sm = get().getSelectionModel();
if (sm != null) {
sm.getSelectedCells().addListener(weakSelectedListener);
}
fm = get().getFocusModel();
if (fm != null) {
fm.focusedCellProperty().addListener(weakFocusedListener);
}
get().editingCellProperty().addListener(weakEditingListener);
get().getVisibleLeafColumns().addListener(weakVisibleLeafColumnsListener);
weakTableViewRef = new WeakReference<TableView<S>>(get());
}
updateColumnIndex();
}
@Override public Object getBean() {
return TableCell.this;
}
@Override public String getName() {
return "tableView";
}
};
}
return tableView;
}
private ReadOnlyObjectWrapper<TableRow<S>> tableRow = new ReadOnlyObjectWrapper<>(this, "tableRow");
private void setTableRow(TableRow<S> value) { tableRow.set(value); }
public final TableRow<S> getTableRow() { return tableRow.get(); }
public final ReadOnlyObjectProperty<TableRow<S>> tableRowProperty() { return tableRow; }
private TablePosition<S, T> editingCellAtStartEdit;
TablePosition<S, T> getEditingCellAtStartEdit() {
return editingCellAtStartEdit;
}
@Override public void startEdit() {
if (isEditing()) return;
final TableView<S> table = getTableView();
final TableColumn<S,T> column = getTableColumn();
final TableRow<S> row = getTableRow();
if (!isEditable() ||
(table != null && !table.isEditable()) ||
(column != null && !column.isEditable()) ||
(row != null && !row.isEditable())) {
return;
}
if (! lockItemOnEdit) {
updateItem(-1);
}
super.startEdit();
if (!isEditing()) return;
editingCellAtStartEdit = new TablePosition<>(table, getIndex(), column);
if (column != null) {
CellEditEvent<S,?> editEvent = new CellEditEvent<>(
table,
editingCellAtStartEdit,
TableColumn.editStartEvent(),
null
);
Event.fireEvent(column, editEvent);
}
if (table != null) {
table.edit(editingCellAtStartEdit.getRow(), editingCellAtStartEdit.getTableColumn());
}
}
@Override public void commitEdit(T newValue) {
if (!isEditing()) return;
super.commitEdit(newValue);
final TableView<S> table = getTableView();
if (getTableColumn() != null) {
CellEditEvent<S, T> editEvent = new CellEditEvent<>(
table,
editingCellAtStartEdit,
TableColumn.editCommitEvent(),
newValue
);
Event.fireEvent(getTableColumn(), editEvent);
}
updateItem(newValue, false);
if (table != null) {
table.edit(-1, null);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(table);
}
}
@Override public void cancelEdit() {
if (!isEditing()) return;
super.cancelEdit();
final TableView<S> table = getTableView();
if (table != null) {
if (updateEditingIndex) table.edit(-1, null);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(table);
}
if (getTableColumn() != null) {
CellEditEvent<S,?> editEvent = new CellEditEvent<>(
table,
editingCellAtStartEdit,
TableColumn.editCancelEvent(),
null
);
Event.fireEvent(getTableColumn(), editEvent);
}
}
@Override public void updateSelected(boolean selected) {
if (getTableRow() == null || getTableRow().isEmpty()) return;
setSelected(selected);
}
@Override protected Skin<?> createDefaultSkin() {
return new TableCellSkin<S,T>(this);
}
private void cleanUpTableViewListeners(TableView<S> tableView) {
if (tableView != null) {
TableView.TableViewSelectionModel<S> sm = tableView.getSelectionModel();
if (sm != null) {
sm.getSelectedCells().removeListener(weakSelectedListener);
}
TableViewFocusModel<S> fm = tableView.getFocusModel();
if (fm != null) {
fm.focusedCellProperty().removeListener(weakFocusedListener);
}
tableView.editingCellProperty().removeListener(weakEditingListener);
tableView.getVisibleLeafColumns().removeListener(weakVisibleLeafColumnsListener);
}
}
@Override void indexChanged(int oldIndex, int newIndex) {
super.indexChanged(oldIndex, newIndex);
updateItem(oldIndex);
updateSelection();
updateFocus();
updateEditing();
}
private boolean isLastVisibleColumn = false;
private int columnIndex = -1;
private void updateColumnIndex() {
TableView<S> tv = getTableView();
TableColumn<S,T> tc = getTableColumn();
columnIndex = tv == null || tc == null ? -1 : tv.getVisibleLeafIndex(tc);
isLastVisibleColumn = getTableColumn() != null &&
columnIndex != -1 &&
columnIndex == getTableView().getVisibleLeafColumns().size() - 1;
pseudoClassStateChanged(PSEUDO_CLASS_LAST_VISIBLE, isLastVisibleColumn);
}
private void updateSelection() {
if (isEmpty()) return;
final boolean isSelected = isSelected();
if (! isInCellSelectionMode()) {
if (isSelected) {
updateSelected(false);
}
return;
}
final TableView<S> tableView = getTableView();
if (getIndex() == -1 || tableView == null) return;
TableSelectionModel<S> sm = tableView.getSelectionModel();
if (sm == null) {
updateSelected(false);
return;
}
boolean isSelectedNow = sm.isSelected(getIndex(), getTableColumn());
if (isSelected == isSelectedNow) return;
updateSelected(isSelectedNow);
}
private void updateFocus() {
final boolean isFocused = isFocused();
if (! isInCellSelectionMode()) {
if (isFocused) {
setFocused(false);
}
return;
}
final TableView<S> tableView = getTableView();
final TableRow<S> tableRow = getTableRow();
final int index = getIndex();
if (index == -1 || tableView == null || tableRow == null) return;
final TableViewFocusModel<S> fm = tableView.getFocusModel();
if (fm == null) {
setFocused(false);
return;
}
setFocused(fm.isFocused(index, getTableColumn()));
}
private void updateEditing() {
if (getIndex() == -1 || getTableView() == null) {
if (isEditing()) {
doCancelEdit();
}
return;
}
TablePosition<S,?> editCell = getTableView().getEditingCell();
boolean match = match(editCell);
if (match && ! isEditing()) {
startEdit();
} else if (! match && isEditing()) {
doCancelEdit();
}
}
private void doCancelEdit() {
try {
updateEditingIndex = false;
cancelEdit();
} finally {
updateEditingIndex = true;
}
}
private boolean updateEditingIndex = true;
private boolean match(TablePosition<S,?> pos) {
return pos != null && pos.getRow() == getIndex() && pos.getTableColumn() == getTableColumn();
}
private boolean isInCellSelectionMode() {
TableView<S> tableView = getTableView();
if (tableView == null) return false;
TableSelectionModel<S> sm = tableView.getSelectionModel();
return sm != null && sm.isCellSelectionEnabled();
}
private ObservableValue<T> currentObservableValue = null;
private boolean isFirstRun = true;
private WeakReference<S> oldRowItemRef;
private void updateItem(int oldIndex) {
if (currentObservableValue != null) {
currentObservableValue.removeListener(weaktableRowUpdateObserver);
}
final TableView<S> tableView = getTableView();
final List<S> items = tableView == null ? FXCollections.<S>emptyObservableList() : tableView.getItems();
final TableColumn<S,T> tableColumn = getTableColumn();
final int itemCount = items == null ? -1 : items.size();
final int index = getIndex();
final boolean isEmpty = isEmpty();
final T oldValue = getItem();
final TableRow<S> tableRow = getTableRow();
final S rowItem = tableRow == null ? null : tableRow.getItem();
final boolean indexExceedsItemCount = index >= itemCount;
outer: if (indexExceedsItemCount ||
index < 0 ||
columnIndex < 0 ||
!isVisible() ||
tableColumn == null ||
!tableColumn.isVisible()) {
if ((!isEmpty && oldValue != null) || isFirstRun || indexExceedsItemCount) {
updateItem(null, true);
isFirstRun = false;
}
return;
} else {
currentObservableValue = tableColumn.getCellObservableValue(index);
final T newValue = currentObservableValue == null ? null : currentObservableValue.getValue();
if (oldIndex == index) {
if (!isItemChanged(oldValue, newValue)) {
S oldRowItem = oldRowItemRef != null ? oldRowItemRef.get() : null;
if (oldRowItem != null && oldRowItem.equals(rowItem)) {
break outer;
}
}
}
updateItem(newValue, false);
}
oldRowItemRef = new WeakReference<>(rowItem);
if (currentObservableValue == null) {
return;
}
currentObservableValue.addListener(weaktableRowUpdateObserver);
}
@Override protected void layoutChildren() {
if (itemDirty) {
updateItem(-1);
itemDirty = false;
}
super.layoutChildren();
}
public final void updateTableView(TableView tv) {
setTableView(tv);
}
public final void updateTableRow(TableRow tableRow) {
this.setTableRow(tableRow);
}
public final void updateTableColumn(TableColumn col) {
TableColumn<S,T> oldCol = getTableColumn();
if (oldCol != null) {
oldCol.getStyleClass().removeListener(weakColumnStyleClassListener);
getStyleClass().removeAll(oldCol.getStyleClass());
oldCol.idProperty().removeListener(weakColumnIdListener);
oldCol.styleProperty().removeListener(weakColumnStyleListener);
String id = getId();
String style = getStyle();
if (id != null && id.equals(oldCol.getId())) {
setId(null);
}
if (style != null && style.equals(oldCol.getStyle())) {
setStyle("");
}
}
setTableColumn(col);
if (col != null) {
getStyleClass().addAll(col.getStyleClass());
col.getStyleClass().addListener(weakColumnStyleClassListener);
col.idProperty().addListener(weakColumnIdListener);
col.styleProperty().addListener(weakColumnStyleListener);
possiblySetId(col.getId());
possiblySetStyle(col.getStyle());
}
}
private static final String DEFAULT_STYLE_CLASS = "table-cell";
private static final PseudoClass PSEUDO_CLASS_LAST_VISIBLE =
PseudoClass.getPseudoClass("last-visible");
private void possiblySetId(String idCandidate) {
if (getId() == null || getId().isEmpty()) {
setId(idCandidate);
}
}
private void possiblySetStyle(String styleCandidate) {
if (getStyle() == null || getStyle().isEmpty()) {
setStyle(styleCandidate);
}
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case ROW_INDEX: return getIndex();
case COLUMN_INDEX: return columnIndex;
case SELECTED: return isInCellSelectionMode() ? isSelected() : getTableRow().isSelected();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case REQUEST_FOCUS: {
TableView<S> tableView = getTableView();
if (tableView != null) {
TableViewFocusModel<S> fm = tableView.getFocusModel();
if (fm != null) {
fm.focus(getIndex(), getTableColumn());
}
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
}
