package com.sun.javafx.scene.control.behavior;
import javafx.scene.Node;
import javafx.scene.control.FocusModel;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
public class TreeCellBehavior<T> extends CellBehaviorBase<TreeCell<T>> {
public TreeCellBehavior(final TreeCell<T> control) {
super(control);
}
@Override
protected MultipleSelectionModel<TreeItem<T>> getSelectionModel() {
return getCellContainer().getSelectionModel();
}
@Override
protected FocusModel<TreeItem<T>> getFocusModel() {
return getCellContainer().getFocusModel();
}
@Override
protected TreeView<T> getCellContainer() {
return getNode().getTreeView();
}
@Override
protected void edit(TreeCell<T> cell) {
TreeItem<T> treeItem = cell == null ? null : cell.getTreeItem();
getCellContainer().edit(treeItem);
}
@Override
protected void handleClicks(MouseButton button, int clickCount, boolean isAlreadySelected) {
TreeItem<T> treeItem = getNode().getTreeItem();
if (button == MouseButton.PRIMARY) {
if (clickCount == 1 && isAlreadySelected) {
edit(getNode());
} else if (clickCount == 1) {
edit(null);
} else if (clickCount == 2 && treeItem.isLeaf()) {
edit(getNode());
} else if (clickCount % 2 == 0) {
treeItem.setExpanded(! treeItem.isExpanded());
}
}
}
@Override protected boolean handleDisclosureNode(double x, double y) {
TreeCell<T> treeCell = getNode();
Node disclosureNode = treeCell.getDisclosureNode();
if (disclosureNode != null) {
if (disclosureNode.getBoundsInParent().contains(x, y)) {
if (treeCell.getTreeItem() != null) {
treeCell.getTreeItem().setExpanded(! treeCell.getTreeItem().isExpanded());
}
return true;
}
}
return false;
}
}
