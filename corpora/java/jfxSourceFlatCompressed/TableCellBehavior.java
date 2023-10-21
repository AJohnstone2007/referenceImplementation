package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
public class TableCellBehavior<S,T> extends TableCellBehaviorBase<S, T, TableColumn<S,?>, TableCell<S, T>> {
public TableCellBehavior(TableCell<S,T> control) {
super(control);
}
@Override protected TableView<S> getCellContainer() {
return getNode().getTableView();
}
@Override protected TableColumn<S,T> getTableColumn() {
return getNode().getTableColumn();
}
@Override protected int getItemCount() {
return getCellContainer().getItems().size();
}
@Override protected TableView.TableViewSelectionModel<S> getSelectionModel() {
return getCellContainer().getSelectionModel();
}
@Override protected TableViewFocusModel<S> getFocusModel() {
return getCellContainer().getFocusModel();
}
@Override protected TablePositionBase getFocusedCell() {
return getCellContainer().getFocusModel().getFocusedCell();
}
@Override protected boolean isTableRowSelected() {
return getNode().getTableRow().isSelected();
}
@Override protected int getVisibleLeafIndex(TableColumnBase tc) {
return getCellContainer().getVisibleLeafIndex((TableColumn) tc);
}
@Override protected void focus(int row, TableColumnBase tc) {
getFocusModel().focus(row, (TableColumn)tc);
}
@Override protected void edit(TableCell<S,T> cell) {
if (cell == null) {
getCellContainer().edit(-1, null);
} else {
getCellContainer().edit(cell.getIndex(), cell.getTableColumn());
}
}
}
