package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.input.MouseButton;
public abstract class TableCellBehaviorBase<S, T, TC extends TableColumnBase<S, ?>, C extends IndexedCell<T>> extends CellBehaviorBase<C> {
public TableCellBehaviorBase(C control) {
super(control);
}
protected abstract TableColumnBase<S, T> getTableColumn();
protected abstract int getItemCount();
protected abstract TableSelectionModel<S> getSelectionModel();
protected abstract TableFocusModel<S,TC> getFocusModel();
protected abstract TablePositionBase getFocusedCell();
protected abstract boolean isTableRowSelected();
protected abstract int getVisibleLeafIndex(TableColumnBase<S,T> tc);
protected abstract void focus(int row, TableColumnBase<S,T> tc);
protected void doSelect(final double x, final double y, final MouseButton button,
final int clickCount, final boolean shiftDown, final boolean shortcutDown) {
final C tableCell = getNode();
if (! tableCell.contains(x, y)) return;
final Control tableView = getCellContainer();
if (tableView == null) return;
int count = getItemCount();
if (tableCell.getIndex() >= count) return;
TableSelectionModel<S> sm = getSelectionModel();
if (sm == null) return;
final boolean selected = isSelected();
final int row = tableCell.getIndex();
final int column = getColumn();
final TableColumnBase<S,T> tableColumn = getTableColumn();
TableFocusModel fm = getFocusModel();
if (fm == null) return;
TablePositionBase focusedCell = getFocusedCell();
if (handleDisclosureNode(x, y)) {
return;
}
if (shiftDown) {
if (! hasNonDefaultAnchor(tableView)) {
setAnchor(tableView, focusedCell, false);
}
} else {
removeAnchor(tableView);
}
if (button == MouseButton.PRIMARY || (button == MouseButton.SECONDARY && !selected)) {
if (sm.getSelectionMode() == SelectionMode.SINGLE) {
simpleSelect(button, clickCount, shortcutDown);
} else {
if (shortcutDown) {
if (selected) {
sm.clearSelection(row, tableColumn);
fm.focus(row, tableColumn);
} else {
sm.select(row, tableColumn);
}
} else if (shiftDown) {
final TablePositionBase anchor = getAnchor(tableView, focusedCell);
final int anchorRow = anchor.getRow();
final boolean asc = anchorRow < row;
sm.clearSelection();
int minRow = Math.min(anchorRow, row);
int maxRow = Math.max(anchorRow, row);
TableColumnBase<S,T> minColumn = anchor.getColumn() < column ? anchor.getTableColumn() : tableColumn;
TableColumnBase<S,T> maxColumn = anchor.getColumn() >= column ? anchor.getTableColumn() : tableColumn;
if (asc) {
sm.selectRange(minRow, minColumn, maxRow, maxColumn);
} else {
sm.selectRange(maxRow, minColumn, minRow, maxColumn);
}
} else {
simpleSelect(button, clickCount, shortcutDown);
}
}
}
}
protected void simpleSelect(MouseButton button, int clickCount, boolean shortcutDown) {
final TableSelectionModel<S> sm = getSelectionModel();
final int row = getNode().getIndex();
final TableColumnBase<S,T> column = getTableColumn();
boolean isAlreadySelected = sm.isSelected(row, column);
if (isAlreadySelected && shortcutDown) {
sm.clearSelection(row, column);
getFocusModel().focus(row, (TC) column);
isAlreadySelected = false;
} else {
sm.clearAndSelect(row, column);
}
handleClicks(button, clickCount, isAlreadySelected);
}
private int getColumn() {
if (getSelectionModel().isCellSelectionEnabled()) {
TableColumnBase<S,T> tc = getTableColumn();
return getVisibleLeafIndex(tc);
}
return -1;
}
@Override
protected boolean isSelected() {
TableSelectionModel<S> sm = getSelectionModel();
if (sm == null) return false;
if (sm.isCellSelectionEnabled()) {
final C cell = getNode();
return cell.isSelected();
} else {
return isTableRowSelected();
}
}
}
