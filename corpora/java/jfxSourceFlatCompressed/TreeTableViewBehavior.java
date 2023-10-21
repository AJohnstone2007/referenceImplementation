package com.sun.javafx.scene.control.behavior;
import static javafx.scene.input.KeyCode.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.util.Callback;
public class TreeTableViewBehavior<T> extends TableViewBehaviorBase<TreeTableView<T>, TreeItem<T>, TreeTableColumn<T, ?>> {
private final ChangeListener<TreeTableView.TreeTableViewSelectionModel<T>> selectionModelListener =
(observable, oldValue, newValue) -> {
if (oldValue != null) {
oldValue.getSelectedCells().removeListener(weakSelectedCellsListener);
}
if (newValue != null) {
newValue.getSelectedCells().addListener(weakSelectedCellsListener);
}
};
private final WeakChangeListener<TreeTableView.TreeTableViewSelectionModel<T>> weakSelectionModelListener =
new WeakChangeListener<>(selectionModelListener);
public TreeTableViewBehavior(TreeTableView<T> control) {
super(control);
InputMap<TreeTableView<T>> expandCollapseInputMap = new InputMap<>(control);
expandCollapseInputMap.getMappings().addAll(
new InputMap.KeyMapping(LEFT, e -> rtl(control, this::expandRow, this::collapseRow)),
new InputMap.KeyMapping(KP_LEFT, e -> rtl(control, this::expandRow, this::collapseRow)),
new InputMap.KeyMapping(RIGHT, e -> rtl(control, this::collapseRow, this::expandRow)),
new InputMap.KeyMapping(KP_RIGHT, e -> rtl(control, this::collapseRow, this::expandRow)),
new InputMap.KeyMapping(MULTIPLY, e -> expandAll()),
new InputMap.KeyMapping(ADD, e -> expandRow()),
new InputMap.KeyMapping(SUBTRACT, e -> collapseRow())
);
addDefaultChildMap(getInputMap(), expandCollapseInputMap);
control.selectionModelProperty().addListener(weakSelectionModelListener);
if (getSelectionModel() != null) {
control.getSelectionModel().getSelectedCells().addListener(selectedCellsListener);
}
}
@Override protected int getItemCount() {
return getNode().getExpandedItemCount();
}
@Override protected TableFocusModel getFocusModel() {
return getNode().getFocusModel();
}
@Override protected TableSelectionModel<TreeItem<T>> getSelectionModel() {
return getNode().getSelectionModel();
}
@Override protected ObservableList<TreeTablePosition<T,?>> getSelectedCells() {
return getNode().getSelectionModel().getSelectedCells();
}
@Override protected TablePositionBase getFocusedCell() {
return getNode().getFocusModel().getFocusedCell();
}
@Override protected int getVisibleLeafIndex(TableColumnBase tc) {
return getNode().getVisibleLeafIndex((TreeTableColumn)tc);
}
@Override protected TreeTableColumn getVisibleLeafColumn(int index) {
return getNode().getVisibleLeafColumn(index);
}
@Override protected boolean isControlEditable() {
return getNode().isEditable();
}
@Override protected void editCell(int row, TableColumnBase tc) {
getNode().edit(row, (TreeTableColumn)tc);
}
@Override protected ObservableList<TreeTableColumn<T,?>> getVisibleLeafColumns() {
return getNode().getVisibleLeafColumns();
}
@Override protected TablePositionBase<TreeTableColumn<T, ?>>
getTablePosition(int row, TableColumnBase<TreeItem<T>, ?> tc) {
return new TreeTablePosition(getNode(), row, (TreeTableColumn)tc);
}
@Override protected void selectAllToFocus(boolean setAnchorToFocusIndex) {
if (getNode().getEditingCell() != null) return;
super.selectAllToFocus(setAnchorToFocusIndex);
}
private void rightArrowPressed() {
if (getNode().getSelectionModel().isCellSelectionEnabled()) {
if (isRTL()) {
selectLeftCell();
} else {
selectRightCell();
}
} else {
expandRow();
}
}
private void leftArrowPressed() {
if (getNode().getSelectionModel().isCellSelectionEnabled()) {
if (isRTL()) {
selectRightCell();
} else {
selectLeftCell();
}
} else {
collapseRow();
}
}
private void expandRow() {
Callback<TreeItem<T>, Integer> getIndex = p -> getNode().getRow(p);
TreeViewBehavior.expandRow(getNode().getSelectionModel(), getIndex);
}
private void expandAll() {
TreeViewBehavior.expandAll(getNode().getRoot());
}
private void collapseRow() {
TreeTableView<T> control = getNode();
TreeViewBehavior.collapseRow(control.getSelectionModel(), control.getRoot(), control.isShowRoot());
}
}
