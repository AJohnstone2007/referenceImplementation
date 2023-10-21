package com.sun.javafx.scene.control.behavior;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
public class TreeTableCellBehavior<S,T> extends TableCellBehaviorBase<TreeItem<S>, T, TreeTableColumn<S, ?>, TreeTableCell<S,T>> {
public TreeTableCellBehavior(TreeTableCell<S,T> control) {
super(control);
}
@Override protected TreeTableView<S> getCellContainer() {
return getNode().getTreeTableView();
}
@Override protected TreeTableColumn<S,T> getTableColumn() {
return getNode().getTableColumn();
}
@Override protected int getItemCount() {
return getCellContainer().getExpandedItemCount();
}
@Override protected TreeTableView.TreeTableViewSelectionModel<S> getSelectionModel() {
return getCellContainer().getSelectionModel();
}
@Override protected TreeTableView.TreeTableViewFocusModel<S> getFocusModel() {
return getCellContainer().getFocusModel();
}
@Override protected TablePositionBase getFocusedCell() {
return getCellContainer().getFocusModel().getFocusedCell();
}
@Override protected boolean isTableRowSelected() {
return getNode().getTableRow().isSelected();
}
@Override protected int getVisibleLeafIndex(TableColumnBase tc) {
return getCellContainer().getVisibleLeafIndex((TreeTableColumn)tc);
}
@Override protected void focus(int row, TableColumnBase tc) {
getFocusModel().focus(row, (TreeTableColumn)tc);
}
@Override protected void edit(TreeTableCell<S,T> cell) {
if (cell == null) {
getCellContainer().edit(-1, null);
} else {
getCellContainer().edit(cell.getIndex(), cell.getTableColumn());
}
}
@Override protected boolean handleDisclosureNode(double x, double y) {
final TreeItem<S> treeItem = getNode().getTableRow().getTreeItem();
final TreeTableView<S> treeTableView = getNode().getTreeTableView();
final TreeTableColumn<S,T> column = getTableColumn();
final TreeTableColumn<S,?> treeColumn = treeTableView.getTreeColumn() == null ?
treeTableView.getVisibleLeafColumn(0) : treeTableView.getTreeColumn();
if (column == treeColumn) {
final Node disclosureNode = getNode().getTableRow().getDisclosureNode();
if (disclosureNode != null && disclosureNode.isVisible()) {
double startX = 0;
for (TreeTableColumn<S,?> tc : treeTableView.getVisibleLeafColumns()) {
if (tc == treeColumn) break;
startX += tc.getWidth();
}
final double endX = disclosureNode.getBoundsInParent().getMaxX();
if (x < (endX - startX)) {
if (treeItem != null) {
treeItem.setExpanded(!treeItem.isExpanded());
}
return true;
}
}
}
return false;
}
@Override
protected void handleClicks(MouseButton button, int clickCount, boolean isAlreadySelected) {
TreeItem<S> treeItem = getNode().getTableRow().getTreeItem();
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
}
