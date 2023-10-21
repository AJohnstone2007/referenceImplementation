package com.sun.javafx.scene.control.behavior;
import javafx.collections.ObservableList;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
public class TreeTableRowBehavior<T> extends TableRowBehaviorBase<TreeTableRow<T>> {
public TreeTableRowBehavior(TreeTableRow<T> control) {
super(control);
}
@Override protected TableSelectionModel<TreeItem<T>> getSelectionModel() {
return getCellContainer().getSelectionModel();
}
@Override protected TableFocusModel<TreeItem<T>,?> getFocusModel() {
return getCellContainer().getFocusModel();
}
@Override protected TreeTableView<T> getCellContainer() {
return getNode().getTreeTableView();
}
@Override protected TablePositionBase<?> getFocusedCell() {
return getCellContainer().getFocusModel().getFocusedCell();
}
@Override protected ObservableList getVisibleLeafColumns() {
return getCellContainer().getVisibleLeafColumns();
}
@Override protected void edit(TreeTableRow<T> cell) {
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
}
