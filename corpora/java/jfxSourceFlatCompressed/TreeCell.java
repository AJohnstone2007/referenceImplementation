package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.TreeCellSkin;
import javafx.collections.WeakListChangeListener;
import java.lang.ref.WeakReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
public class TreeCell<T> extends IndexedCell<T> {
public TreeCell() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TREE_ITEM);
}
private final ListChangeListener<Integer> selectedListener = c -> {
updateSelection();
};
private final ChangeListener<MultipleSelectionModel<TreeItem<T>>> selectionModelPropertyListener = new ChangeListener<MultipleSelectionModel<TreeItem<T>>>() {
@Override public void changed(ObservableValue<? extends MultipleSelectionModel<TreeItem<T>>> observable,
MultipleSelectionModel<TreeItem<T>> oldValue,
MultipleSelectionModel<TreeItem<T>> newValue) {
if (oldValue != null) {
oldValue.getSelectedIndices().removeListener(weakSelectedListener);
}
if (newValue != null) {
newValue.getSelectedIndices().addListener(weakSelectedListener);
}
updateSelection();
}
};
private final InvalidationListener focusedListener = valueModel -> {
updateFocus();
};
private final ChangeListener<FocusModel<TreeItem<T>>> focusModelPropertyListener = new ChangeListener<FocusModel<TreeItem<T>>>() {
@Override public void changed(ObservableValue<? extends FocusModel<TreeItem<T>>> observable,
FocusModel<TreeItem<T>> oldValue,
FocusModel<TreeItem<T>> newValue) {
if (oldValue != null) {
oldValue.focusedIndexProperty().removeListener(weakFocusedListener);
}
if (newValue != null) {
newValue.focusedIndexProperty().addListener(weakFocusedListener);
}
updateFocus();
}
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
private boolean oldIsExpanded;
private final InvalidationListener treeItemExpandedInvalidationListener = new InvalidationListener() {
@Override public void invalidated(Observable o) {
boolean isExpanded = ((BooleanProperty)o).get();
pseudoClassStateChanged(EXPANDED_PSEUDOCLASS_STATE, isExpanded);
pseudoClassStateChanged(COLLAPSED_PSEUDOCLASS_STATE, !isExpanded);
if (isExpanded != oldIsExpanded) {
notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
}
oldIsExpanded = isExpanded;
}
};
private final InvalidationListener rootPropertyListener = observable -> {
updateItem(-1);
};
private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener<Integer>(selectedListener);
private final WeakChangeListener<MultipleSelectionModel<TreeItem<T>>> weakSelectionModelPropertyListener = new WeakChangeListener<MultipleSelectionModel<TreeItem<T>>>(selectionModelPropertyListener);
private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(focusedListener);
private final WeakChangeListener<FocusModel<TreeItem<T>>> weakFocusModelPropertyListener = new WeakChangeListener<FocusModel<TreeItem<T>>>(focusModelPropertyListener);
private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(editingListener);
private final WeakInvalidationListener weakLeafListener = new WeakInvalidationListener(leafListener);
private final WeakInvalidationListener weakTreeItemExpandedInvalidationListener =
new WeakInvalidationListener(treeItemExpandedInvalidationListener);
private final WeakInvalidationListener weakRootPropertyListener = new WeakInvalidationListener(rootPropertyListener);
private ReadOnlyObjectWrapper<TreeItem<T>> treeItem =
new ReadOnlyObjectWrapper<TreeItem<T>>(this, "treeItem") {
TreeItem<T> oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
oldValue.expandedProperty().removeListener(weakTreeItemExpandedInvalidationListener);
}
oldValue = get();
if (oldValue != null) {
oldIsExpanded = oldValue.isExpanded();
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
private ReadOnlyObjectWrapper<TreeView<T>> treeView = new ReadOnlyObjectWrapper<TreeView<T>>() {
private WeakReference<TreeView<T>> weakTreeViewRef;
@Override protected void invalidated() {
MultipleSelectionModel<TreeItem<T>> sm;
FocusModel<TreeItem<T>> fm;
if (weakTreeViewRef != null) {
TreeView<T> oldTreeView = weakTreeViewRef.get();
if (oldTreeView != null) {
sm = oldTreeView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().removeListener(weakSelectedListener);
}
fm = oldTreeView.getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().removeListener(weakFocusedListener);
}
oldTreeView.editingItemProperty().removeListener(weakEditingListener);
oldTreeView.focusModelProperty().removeListener(weakFocusModelPropertyListener);
oldTreeView.selectionModelProperty().removeListener(weakSelectionModelPropertyListener);
oldTreeView.rootProperty().removeListener(weakRootPropertyListener);
}
weakTreeViewRef = null;
}
TreeView<T> treeView = get();
if (treeView != null) {
sm = treeView.getSelectionModel();
if (sm != null) {
sm.getSelectedIndices().addListener(weakSelectedListener);
}
fm = treeView.getFocusModel();
if (fm != null) {
fm.focusedIndexProperty().addListener(weakFocusedListener);
}
treeView.editingItemProperty().addListener(weakEditingListener);
treeView.focusModelProperty().addListener(weakFocusModelPropertyListener);
treeView.selectionModelProperty().addListener(weakSelectionModelPropertyListener);
treeView.rootProperty().addListener(weakRootPropertyListener);
weakTreeViewRef = new WeakReference<TreeView<T>>(treeView);
}
updateItem(-1);
requestLayout();
}
@Override
public Object getBean() {
return TreeCell.this;
}
@Override
public String getName() {
return "treeView";
}
};
private void setTreeView(TreeView<T> value) { treeView.set(value); }
public final TreeView<T> getTreeView() { return treeView.get(); }
public final ReadOnlyObjectProperty<TreeView<T>> treeViewProperty() { return treeView.getReadOnlyProperty(); }
private TreeItem<T> treeItemAtStartEdit;
@Override public void startEdit() {
if (isEditing()) return;
final TreeView<T> tree = getTreeView();
if (! isEditable() || (tree != null && ! tree.isEditable())) {
return;
}
updateItem(-1);
super.startEdit();
if (!isEditing()) return;
treeItemAtStartEdit = getTreeItem();
if (tree != null) {
tree.fireEvent(new TreeView.EditEvent<T>(tree,
TreeView.<T>editStartEvent(),
treeItemAtStartEdit,
getItem(),
null));
tree.edit(treeItemAtStartEdit);
tree.requestFocus();
}
}
@Override public void commitEdit(T newValue) {
if (! isEditing()) return;
super.commitEdit(newValue);
final TreeItem<T> treeItem = getTreeItem();
final TreeView<T> tree = getTreeView();
if (tree != null) {
tree.fireEvent(new TreeView.EditEvent<T>(tree,
TreeView.<T>editCommitEvent(),
treeItem,
getItem(),
newValue));
}
updateItem(newValue, false);
if (tree != null) {
tree.edit(null);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(tree);
}
treeItemAtStartEdit = null;
}
@Override public void cancelEdit() {
if (! isEditing()) return;
TreeView<T> tree = getTreeView();
super.cancelEdit();
if (tree != null) {
TreeItem<T> editingItem = treeItemAtStartEdit;
T value = editingItem != null ? editingItem.getValue() : null;
if (updateEditingIndex) tree.edit(null);
ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(tree);
tree.fireEvent(new TreeView.EditEvent<T>(tree,
TreeView.<T>editCancelEvent(),
editingItem,
value,
null));
}
treeItemAtStartEdit = null;
}
@Override protected Skin<?> createDefaultSkin() {
return new TreeCellSkin<T>(this);
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
private boolean isFirstRun = true;
private void updateItem(int oldIndex) {
TreeView<T> tv = getTreeView();
if (tv == null) return;
int index = getIndex();
boolean valid = index >=0 && index < tv.getExpandedItemCount();
final boolean isEmpty = isEmpty();
final TreeItem<T> oldTreeItem = getTreeItem();
outer: if (valid) {
TreeItem<T> newTreeItem = tv.getTreeItem(index);
T newValue = newTreeItem == null ? null : newTreeItem.getValue();
T oldValue = oldTreeItem == null ? null : oldTreeItem.getValue();
if (oldIndex == index) {
if (!isItemChanged(oldValue, newValue)) {
break outer;
}
}
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
if (getIndex() == -1 || getTreeView() == null) return;
SelectionModel<TreeItem<T>> sm = getTreeView().getSelectionModel();
if (sm == null) {
updateSelected(false);
return;
}
boolean isSelected = sm.isSelected(getIndex());
if (isSelected() == isSelected) return;
updateSelected(isSelected);
}
private void updateFocus() {
if (getIndex() == -1 || getTreeView() == null) return;
FocusModel<TreeItem<T>> fm = getTreeView().getFocusModel();
if (fm == null) {
setFocused(false);
return;
}
setFocused(fm.isFocused(getIndex()));
}
private boolean updateEditingIndex = true;
private void updateEditing() {
final int index = getIndex();
final TreeView<T> tree = getTreeView();
final TreeItem<T> treeItem = getTreeItem();
final TreeItem<T> editItem = tree == null ? null : tree.getEditingItem();
final boolean editing = isEditing();
if (index == -1 || tree == null || treeItem == null) {
if (editing) {
doCancelEditing();
}
return;
}
final boolean match = treeItem.equals(editItem);
if (match && !editing) {
startEdit();
} else if (! match && editing) {
doCancelEditing();
}
}
private void doCancelEditing() {
try {
updateEditingIndex = false;
cancelEdit();
} finally {
updateEditingIndex = true;
}
}
public final void updateTreeView(TreeView<T> tree) {
setTreeView(tree);
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
private static final String DEFAULT_STYLE_CLASS = "tree-cell";
private static final PseudoClass EXPANDED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("expanded");
private static final PseudoClass COLLAPSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("collapsed");
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
TreeItem<T> treeItem = getTreeItem();
TreeView<T> treeView = getTreeView();
switch (attribute) {
case TREE_ITEM_PARENT: {
if (treeView == null) return null;
if (treeItem == null) return null;
TreeItem<T> parent = treeItem.getParent();
if (parent == null) return null;
int parentIndex = treeView.getRow(parent);
return treeView.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, parentIndex);
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
int childIndex = treeView.getRow(child);
return treeView.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, childIndex);
}
case LEAF: return treeItem == null ? true : treeItem.isLeaf();
case EXPANDED: return treeItem == null ? false : treeItem.isExpanded();
case INDEX: return getIndex();
case SELECTED: return isSelected();
case DISCLOSURE_LEVEL: {
return treeView == null ? 0 : treeView.getTreeItemLevel(treeItem);
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
case REQUEST_FOCUS: {
TreeView<T> treeView = getTreeView();
if (treeView != null) {
FocusModel<TreeItem<T>> fm = treeView.getFocusModel();
if (fm != null) {
fm.focus(getIndex());
}
}
break;
}
default: super.executeAccessibleAction(action);
}
}
}
