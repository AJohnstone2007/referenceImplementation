package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.TreeTableRowSkin;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.TreeTableView.TreeTableViewFocusModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
public class TreeTableRow<T> extends IndexedCell<T> {
public TreeTableRow() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TREE_TABLE_ROW);
}
private final ListChangeListener<Integer> selectedListener = c -> {
updateSelection();
};
private final InvalidationListener focusedListener = valueModel -> {
updateFocus();
};
private final InvalidationListener editingListener = valueModel -> {
updateEditing();
};
private final InvalidationListener leafListener = new InvalidationListener() {
@Override public void invalidated(Observable valueModel) {
TreeItem<T> treeItem = getTreeItem();
if (treeItem != null) {
requestLayout();
}
}
};
private boolean oldExpanded;
private final InvalidationListener treeItemExpandedInvalidationListener = o -> {
final boolean expanded = ((BooleanProperty)o).get();
pseudoClassStateChanged(EXPANDED_PSEUDOCLASS_STATE, expanded);
pseudoClassStateChanged(COLLAPSED_PSEUDOCLASS_STATE, !expanded);
if (expanded != oldExpanded) {
notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
}
oldExpanded = expanded;
};
private final WeakListChangeListener<Integer> weakSelectedListener =
new WeakListChangeListener<Integer>(selectedListener);
private final WeakInvalidationListener weakFocusedListener =
new WeakInvalidationListener(focusedListener);
private final WeakInvalidationListener weakEditingListener =
new WeakInvalidationListener(editingListener);
private final WeakInvalidationListener weakLeafListener =
new WeakInvalidationListener(leafListener);
private final WeakInvalidationListener weakTreeItemExpandedInvalidationListener =
new WeakInvalidationListener(treeItemExpandedInvalidationListener);
private ReadOnlyObjectWrapper<TreeItem<T>> treeItem =
new ReadOnlyObjectWrapper<TreeItem<T>>(this, "treeItem") {
TreeItem<T> oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
oldValue.expandedProperty().removeListener(weakTreeItemExpandedInvalidationListener);
}
oldValue = get();
if (oldValue != null) {
oldExpanded = oldValue.isExpanded();
oldValue.expandedProperty().addListener(weakTreeItemExpandedInvalidationListener);
weakTreeItemExpandedInvalidationListener.invalidated(oldValue.expandedProperty());
}
}
};
private void setTreeItem(TreeItem<T> value) {
treeItem.set(value);
}
public final TreeItem<T> getTreeItem() { return treeItem.get(); }
public final ReadOnlyObjectProperty<TreeItem<T>> treeItemProperty() { return treeItem.getReadOnlyProperty(); }
private ObjectProperty<Node> disclosureNode = new SimpleObjectProperty<Node>(this, "disclosureNode");
public final void setDisclosureNode(Node value) { disclosureNodeProperty().set(value); }
public final Node getDisclosureNode() { return disclosureNode.get(); }
public final ObjectProperty<Node> disclosureNodeProperty() { return disclosureNode; }
private ReadOnlyObjectWrapper<TreeTableView<T>> treeTableView = new ReadOnlyObjectWrapper<TreeTableView<T>>(this, "treeTableView") {
private WeakReference<TreeTableView<T>> weakTreeTableViewRef;
@Override protected void invalidated() {
TreeTableViewSelectionModel<T> sm;
TreeTableViewFocusModel<T> fm;
if (weakTreeTableViewRef != null) {
TreeTableView<T> oldTreeTableView = weakTreeTableViewRef.get();
if (oldTreeTableView != null) {
sm = oldTreeTableView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().removeListener(weakSelectedListener);
}
fm = oldTreeTableView.getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().removeListener(weakFocusedListener);
}
oldTreeTableView.editingCellProperty().removeListener(weakEditingListener);
}
weakTreeTableViewRef = null;
}
if (get() != null) {
sm = get().getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().addListener(weakSelectedListener);
}
fm = get().getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().addListener(weakFocusedListener);
}
get().editingCellProperty().addListener(weakEditingListener);
weakTreeTableViewRef = new WeakReference<TreeTableView<T>>(get());
}
updateItem();
requestLayout();
}
};
private void setTreeTableView(TreeTableView<T> value) { treeTableView.set(value); }
public final TreeTableView<T> getTreeTableView() { return treeTableView.get(); }
public final ReadOnlyObjectProperty<TreeTableView<T>> treeTableViewProperty() { return treeTableView.getReadOnlyProperty(); }
@Override void indexChanged(int oldIndex, int newIndex) {
index = getIndex();
updateItem();
updateSelection();
updateFocus();
}
@Override public void startEdit() {
final TreeTableView<T> treeTable = getTreeTableView();
if (! isEditable() || (treeTable != null && ! treeTable.isEditable())) {
return;
}
super.startEdit();
if (treeTable != null) {
treeTable.fireEvent(new TreeTableView.EditEvent<T>(treeTable,
TreeTableView.<T>editStartEvent(),
getTreeItem(),
getItem(),
null));
treeTable.requestFocus();
}
}
@Override public void commitEdit(T newValue) {
if (! isEditing()) return;
final TreeItem<T> treeItem = getTreeItem();
final TreeTableView<T> treeTable = getTreeTableView();
if (treeTable != null) {
treeTable.fireEvent(new TreeTableView.EditEvent<T>(treeTable,
TreeTableView.<T>editCommitEvent(),
treeItem,
getItem(),
newValue));
}
if (treeItem != null) {
treeItem.setValue(newValue);
updateTreeItem(treeItem);
updateItem(newValue, false);
}
super.commitEdit(newValue);
if (treeTable != null) {
treeTable.edit(-1, null);
treeTable.requestFocus();
}
}
@Override public void cancelEdit() {
if (! isEditing()) return;
TreeTableView<T> treeTable = getTreeTableView();
if (treeTable != null) {
treeTable.fireEvent(new TreeTableView.EditEvent<T>(treeTable,
TreeTableView.<T>editCancelEvent(),
getTreeItem(),
getItem(),
null));
}
super.cancelEdit();
if (treeTable != null) {
treeTable.edit(-1, null);
treeTable.requestFocus();
}
}
private int index = -1;
private boolean isFirstRun = true;
private void updateItem() {
TreeTableView<T> tv = getTreeTableView();
if (tv == null) return;
boolean valid = index >=0 && index < tv.getExpandedItemCount();
final TreeItem<T> oldTreeItem = getTreeItem();
final boolean isEmpty = isEmpty();
if (valid) {
final TreeItem<T> newTreeItem = tv.getTreeItem(index);
final T newValue = newTreeItem == null ? null : newTreeItem.getValue();
updateTreeItem(newTreeItem);
updateItem(newValue, false);
} else {
if ((!isEmpty && oldTreeItem != null) || isFirstRun) {
updateTreeItem(null);
updateItem(null, true);
isFirstRun = false;
}
}
}
private void updateSelection() {
if (isEmpty()) return;
if (index == -1 || getTreeTableView() == null) return;
if (getTreeTableView().getSelectionModel() == null) return;
boolean isSelected = getTreeTableView().getSelectionModel().isSelected(index);
if (isSelected() == isSelected) return;
updateSelected(isSelected);
}
private void updateFocus() {
if (getIndex() == -1 || getTreeTableView() == null) return;
if (getTreeTableView().getFocusModel() == null) return;
setFocused(getTreeTableView().getFocusModel().isFocused(getIndex()));
}
private void updateEditing() {
if (getIndex() == -1 || getTreeTableView() == null || getTreeItem() == null) return;
final TreeTablePosition<T,?> editingCell = getTreeTableView().getEditingCell();
if (editingCell != null && editingCell.getTableColumn() != null) {
return;
}
final TreeItem<T> editItem = editingCell == null ? null : editingCell.getTreeItem();
if (! isEditing() && getTreeItem().equals(editItem)) {
startEdit();
} else if (isEditing() && ! getTreeItem().equals(editItem)) {
cancelEdit();
}
}
public final void updateTreeTableView(TreeTableView<T> treeTable) {
setTreeTableView(treeTable);
}
public final void updateTreeItem(TreeItem<T> treeItem) {
TreeItem<T> _treeItem = getTreeItem();
if (_treeItem != null) {
_treeItem.leafProperty().removeListener(weakLeafListener);
}
setTreeItem(treeItem);
if (treeItem != null) {
treeItem.leafProperty().addListener(weakLeafListener);
}
}
private static final String DEFAULT_STYLE_CLASS = "tree-table-row-cell";
private static final PseudoClass EXPANDED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("expanded");
private static final PseudoClass COLLAPSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("collapsed");
@Override protected Skin<?> createDefaultSkin() {
return new TreeTableRowSkin<T>(this);
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
final TreeItem<T> treeItem = getTreeItem();
final TreeTableView<T> treeTableView = getTreeTableView();
switch (attribute) {
case TREE_ITEM_PARENT: {
if (treeItem == null) return null;
TreeItem<T> parent = treeItem.getParent();
if (parent == null) return null;
int parentIndex = treeTableView.getRow(parent);
return treeTableView.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, parentIndex);
}
case TREE_ITEM_COUNT: {
if (treeItem == null) return 0;
if (!treeItem.isExpanded()) return 0;
return treeItem.getChildren().size();
}
case TREE_ITEM_AT_INDEX: {
if (treeItem == null) return null;
if (!treeItem.isExpanded()) return null;
int index = (Integer)parameters[0];
if (index >= treeItem.getChildren().size()) return null;
TreeItem<T> child = treeItem.getChildren().get(index);
if (child == null) return null;
int childIndex = treeTableView.getRow(child);
return treeTableView.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, childIndex);
}
case LEAF: return treeItem == null ? true : treeItem.isLeaf();
case EXPANDED: return treeItem == null ? false : treeItem.isExpanded();
case INDEX: return getIndex();
case DISCLOSURE_LEVEL: {
return treeTableView == null ? 0 : treeTableView.getTreeItemLevel(treeItem);
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case EXPAND: {
TreeItem<T> treeItem = getTreeItem();
if (treeItem != null) treeItem.setExpanded(true);
break;
}
case COLLAPSE: {
TreeItem<T> treeItem = getTreeItem();
if (treeItem != null) treeItem.setExpanded(false);
break;
}
default: super.executeAccessibleAction(action);
}
}
}
