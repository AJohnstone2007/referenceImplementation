package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.TreeTableCellSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.collections.WeakListChangeListener;
import java.lang.ref.WeakReference;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView.TreeTableViewFocusModel;
public class TreeTableCell<S,T> extends IndexedCell<T> {
public TreeTableCell() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TREE_TABLE_CELL);
updateColumnIndex();
}
boolean lockItemOnEdit = false;
private boolean itemDirty = false;
private ListChangeListener<TreeTablePosition<S,?>> selectedListener = c -> {
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
private ListChangeListener<TreeTableColumn<S,?>> visibleLeafColumnsListener = c -> {
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
private final InvalidationListener rootPropertyListener = observable -> {
updateItem(-1);
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
private final WeakListChangeListener<TreeTablePosition<S,?>> weakSelectedListener =
new WeakListChangeListener<TreeTablePosition<S,?>>(selectedListener);
private final WeakInvalidationListener weakFocusedListener =
new WeakInvalidationListener(focusedListener);
private final WeakInvalidationListener weaktableRowUpdateObserver =
new WeakInvalidationListener(tableRowUpdateObserver);
private final WeakInvalidationListener weakEditingListener =
new WeakInvalidationListener(editingListener);
private final WeakListChangeListener<TreeTableColumn<S,?>> weakVisibleLeafColumnsListener =
new WeakListChangeListener<TreeTableColumn<S,?>>(visibleLeafColumnsListener);
private final WeakListChangeListener<String> weakColumnStyleClassListener =
new WeakListChangeListener<String>(columnStyleClassListener);
private final WeakInvalidationListener weakColumnStyleListener =
new WeakInvalidationListener(columnStyleListener);
private final WeakInvalidationListener weakColumnIdListener =
new WeakInvalidationListener(columnIdListener);
private final WeakInvalidationListener weakRootPropertyListener =
new WeakInvalidationListener(rootPropertyListener);
private ReadOnlyObjectWrapper<TreeTableColumn<S,T>> tableColumn =
new ReadOnlyObjectWrapper<TreeTableColumn<S,T>>(this, "tableColumn") {
@Override protected void invalidated() {
updateColumnIndex();
}
};
public final ReadOnlyObjectProperty<TreeTableColumn<S,T>> tableColumnProperty() { return tableColumn.getReadOnlyProperty(); }
private void setTableColumn(TreeTableColumn<S,T> value) { tableColumn.set(value); }
public final TreeTableColumn<S,T> getTableColumn() { return tableColumn.get(); }
private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableView;
private void setTreeTableView(TreeTableView<S> value) {
treeTableViewPropertyImpl().set(value);
}
public final TreeTableView<S> getTreeTableView() {
return treeTableView == null ? null : treeTableView.get();
}
public final ReadOnlyObjectProperty<TreeTableView<S>> treeTableViewProperty() {
return treeTableViewPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableViewPropertyImpl() {
if (treeTableView == null) {
treeTableView = new ReadOnlyObjectWrapper<TreeTableView<S>>(this, "treeTableView") {
private WeakReference<TreeTableView<S>> weakTableViewRef;
@Override protected void invalidated() {
TreeTableView.TreeTableViewSelectionModel<S> sm;
TreeTableView.TreeTableViewFocusModel<S> fm;
if (weakTableViewRef != null) {
TreeTableView<S> oldTableView = weakTableViewRef.get();
if (oldTableView != null) {
sm = oldTableView.getSelectionModel();
if (sm != null) {
sm.getSelectedCells().removeListener(weakSelectedListener);
}
fm = oldTableView.getFocusModel();
if (fm != null) {
fm.focusedCellProperty().removeListener(weakFocusedListener);
}
oldTableView.editingCellProperty().removeListener(weakEditingListener);
oldTableView.getVisibleLeafColumns().removeListener(weakVisibleLeafColumnsListener);
oldTableView.rootProperty().removeListener(weakRootPropertyListener);
}
}
TreeTableView<S> newTreeTableView = get();
if (newTreeTableView != null) {
sm = newTreeTableView.getSelectionModel();
if (sm != null) {
sm.getSelectedCells().addListener(weakSelectedListener);
}
fm = newTreeTableView.getFocusModel();
if (fm != null) {
fm.focusedCellProperty().addListener(weakFocusedListener);
}
newTreeTableView.editingCellProperty().addListener(weakEditingListener);
newTreeTableView.getVisibleLeafColumns().addListener(weakVisibleLeafColumnsListener);
newTreeTableView.rootProperty().addListener(weakRootPropertyListener);
weakTableViewRef = new WeakReference<TreeTableView<S>>(newTreeTableView);
}
updateColumnIndex();
}
};
}
return treeTableView;
}
private ReadOnlyObjectWrapper<TreeTableRow<S>> tableRow =
new ReadOnlyObjectWrapper<TreeTableRow<S>>(this, "tableRow");
private void setTableRow(TreeTableRow<S> value) { tableRow.set(value); }
public final TreeTableRow<S> getTableRow() { return tableRow.get(); }
public final ReadOnlyObjectProperty<TreeTableRow<S>> tableRowProperty() {
return tableRow.getReadOnlyProperty();
}
@Deprecated(since = "17")
public final TreeTableRow<S> getTreeTableRow() { return getTableRow(); }
private TreeTablePosition<S, T> editingCellAtStartEdit = null;
TreeTablePosition<S, T> getEditingCellAtStartEdit() {
return editingCellAtStartEdit;
}
@Override public void startEdit() {
if (isEditing()) return;
final TreeTableView<S> table = getTreeTableView();
final TreeTableColumn<S,T> column = getTableColumn();
final TreeTableRow<S> row = getTableRow();
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
editingCellAtStartEdit = new TreeTablePosition<>(table, getIndex(), column);
if (column != null) {
CellEditEvent<S, T> editEvent = new CellEditEvent<>(
table,
editingCellAtStartEdit,
TreeTableColumn.<S,T>editStartEvent(),
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
final TreeTableView<S> table = getTreeTableView();
if (getTableColumn() != null) {
CellEditEvent<S,T> editEvent = new CellEditEvent<S,T>(
table,
editingCellAtStartEdit,
TreeTableColumn.<S,T>editCommitEvent(),
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
final TreeTableView<S> table = getTreeTableView();
if (table != null) {
if (updateEditingIndex) table.edit(-1, null);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(table);
}
if (getTableColumn() != null) {
CellEditEvent<S,T> editEvent = new CellEditEvent<S,T>(
table,
editingCellAtStartEdit,
TreeTableColumn.<S,T>editCancelEvent(),
null
);
Event.fireEvent(getTableColumn(), editEvent);
}
}
@Override public void updateSelected(boolean selected) {
if (getTableRow() == null || getTableRow().isEmpty()) return;
setSelected(selected);
}
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
private boolean isLastVisibleColumn = false;
private int columnIndex = -1;
private void updateColumnIndex() {
final TreeTableView<S> tv = getTreeTableView();
TreeTableColumn<S,T> tc = getTableColumn();
columnIndex = tv == null || tc == null ? -1 : tv.getVisibleLeafIndex(tc);
isLastVisibleColumn = getTableColumn() != null &&
columnIndex != -1 &&
columnIndex == tv.getVisibleLeafColumns().size() - 1;
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
final TreeTableView<S> tv = getTreeTableView();
if (getIndex() == -1 || tv == null) return;
TreeTableView.TreeTableViewSelectionModel<S> sm = tv.getSelectionModel();
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
final TreeTableView<S> tv = getTreeTableView();
if (getIndex() == -1 || tv == null) return;
TreeTableView.TreeTableViewFocusModel<S> fm = tv.getFocusModel();
if (fm == null) {
setFocused(false);
return;
}
setFocused(fm.isFocused(getIndex(), getTableColumn()));
}
private void updateEditing() {
final TreeTableView<S> tv = getTreeTableView();
if (getIndex() == -1 || tv == null) {
if (isEditing()) {
doCancelEdit();
}
return;
}
TreeTablePosition<S,?> editCell = tv.getEditingCell();
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
private boolean match(TreeTablePosition pos) {
return pos != null && pos.getRow() == getIndex() && pos.getTableColumn() == getTableColumn();
}
private boolean isInCellSelectionMode() {
TreeTableView<S> tv = getTreeTableView();
if (tv == null) return false;
TreeTableView.TreeTableViewSelectionModel<S> sm = tv.getSelectionModel();
return sm != null && sm.isCellSelectionEnabled();
}
private ObservableValue<T> currentObservableValue = null;
private boolean isFirstRun = true;
private WeakReference<S> oldRowItemRef;
private void updateItem(int oldIndex) {
if (currentObservableValue != null) {
currentObservableValue.removeListener(weaktableRowUpdateObserver);
}
final TreeTableView<S> tableView = getTreeTableView();
final TreeTableColumn<S,T> tableColumn = getTableColumn();
final int itemCount = tableView == null ? -1 : getTreeTableView().getExpandedItemCount();
final int index = getIndex();
final boolean isEmpty = isEmpty();
final T oldValue = getItem();
final TreeTableRow<S> tableRow = getTableRow();
final S rowItem = tableRow == null ? null : tableRow.getItem();
final boolean indexExceedsItemCount = index >= itemCount;
outer: if (indexExceedsItemCount ||
index < 0 ||
columnIndex < 0 ||
!isVisible() ||
tableColumn == null ||
!tableColumn.isVisible() ||
tableView.getRoot() == null) {
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
public final void updateTreeTableView(TreeTableView<S> tv) {
setTreeTableView(tv);
}
public final void updateTableRow(TreeTableRow<S> row) {
this.setTableRow(row);
}
public final void updateTableColumn(TreeTableColumn<S,T> column) {
TreeTableColumn<S,T> oldCol = getTableColumn();
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
setTableColumn(column);
if (column != null) {
getStyleClass().addAll(column.getStyleClass());
column.getStyleClass().addListener(weakColumnStyleClassListener);
column.idProperty().addListener(weakColumnIdListener);
column.styleProperty().addListener(weakColumnStyleListener);
possiblySetId(column.getId());
possiblySetStyle(column.getStyle());
}
}
@Deprecated(since = "17")
public final void updateTreeTableRow(TreeTableRow<S> row) {
updateTableRow(row);
}
@Deprecated(since = "17")
public final void updateTreeTableColumn(TreeTableColumn<S,T> column) {
updateTableColumn(column);
}
private static final String DEFAULT_STYLE_CLASS = "tree-table-cell";
private static final PseudoClass PSEUDO_CLASS_LAST_VISIBLE =
PseudoClass.getPseudoClass("last-visible");
@Override protected Skin<?> createDefaultSkin() {
return new TreeTableCellSkin<S,T>(this);
}
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
TreeTableView<S> treeTableView = getTreeTableView();
if (treeTableView != null) {
TreeTableViewFocusModel<S> fm = treeTableView.getFocusModel();
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
