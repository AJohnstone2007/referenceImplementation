package com.sun.javafx.scene.control.behavior;
import javafx.collections.ObservableList;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
public class TableRowBehavior<T> extends TableRowBehaviorBase<TableRow<T>> {
public TableRowBehavior(TableRow<T> control) {
super(control);
}
@Override protected TableSelectionModel<T> getSelectionModel() {
return getCellContainer().getSelectionModel();
}
@Override protected TablePositionBase<?> getFocusedCell() {
return getCellContainer().getFocusModel().getFocusedCell();
}
@Override protected FocusModel<T> getFocusModel() {
return getCellContainer().getFocusModel();
}
@Override protected ObservableList getVisibleLeafColumns() {
return getCellContainer().getVisibleLeafColumns();
}
@Override protected TableView<T> getCellContainer() {
return getNode().getTableView();
}
@Override protected void edit(TableRow<T> cell) {
}
}
