package com.sun.javafx.scene.control.behavior;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import com.sun.javafx.scene.control.skin.Utils;
public class TableViewBehavior<T> extends TableViewBehaviorBase<TableView<T>, T, TableColumn<T, ?>> {
private final ChangeListener<TableViewSelectionModel<T>> selectionModelListener =
(observable, oldValue, newValue) -> {
if (oldValue != null) {
oldValue.getSelectedCells().removeListener(weakSelectedCellsListener);
}
if (newValue != null) {
newValue.getSelectedCells().addListener(weakSelectedCellsListener);
}
};
private final WeakChangeListener<TableViewSelectionModel<T>> weakSelectionModelListener =
new WeakChangeListener<TableViewSelectionModel<T>>(selectionModelListener);
private TwoLevelFocusBehavior tlFocus;
public TableViewBehavior(TableView<T> control) {
super(control);
control.selectionModelProperty().addListener(weakSelectionModelListener);
TableViewSelectionModel<T> sm = control.getSelectionModel();
if (sm != null) {
sm.getSelectedCells().addListener(selectedCellsListener);
}
if (Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusBehavior(control);
}
}
@Override public void dispose() {
if (tlFocus != null) tlFocus.dispose();
super.dispose();
}
@Override protected int getItemCount() {
return getNode().getItems() == null ? 0 : getNode().getItems().size();
}
@Override protected TableFocusModel getFocusModel() {
return getNode().getFocusModel();
}
@Override protected TableSelectionModel<T> getSelectionModel() {
return getNode().getSelectionModel();
}
@Override protected ObservableList<TablePosition> getSelectedCells() {
return getNode().getSelectionModel().getSelectedCells();
}
@Override protected TablePositionBase getFocusedCell() {
return getNode().getFocusModel().getFocusedCell();
}
@Override protected int getVisibleLeafIndex(TableColumnBase tc) {
return getNode().getVisibleLeafIndex((TableColumn)tc);
}
@Override protected TableColumn<T,?> getVisibleLeafColumn(int index) {
return getNode().getVisibleLeafColumn(index);
}
@Override protected boolean isControlEditable() {
return getNode().isEditable();
}
@Override protected void editCell(int row, TableColumnBase tc) {
getNode().edit(row, (TableColumn)tc);
}
@Override protected ObservableList<TableColumn<T,?>> getVisibleLeafColumns() {
return getNode().getVisibleLeafColumns();
}
@Override protected TablePositionBase<TableColumn<T, ?>>
getTablePosition(int row, TableColumnBase<T, ?> tc) {
return new TablePosition(getNode(), row, (TableColumn)tc);
}
@Override protected void selectAllToFocus(boolean setAnchorToFocusIndex) {
if (getNode().getEditingCell() != null) return;
super.selectAllToFocus(setAnchorToFocusIndex);
}
}
