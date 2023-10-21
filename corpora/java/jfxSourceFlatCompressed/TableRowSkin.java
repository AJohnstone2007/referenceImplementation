package javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TableRowBehavior;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
public class TableRowSkin<T> extends TableRowSkinBase<T, TableRow<T>, TableCell<T,?>> {
private TableViewSkin<T> tableViewSkin;
private final BehaviorBase<TableRow<T>> behavior;
public TableRowSkin(TableRow<T> control) {
super(control);
behavior = new TableRowBehavior<>(control);
updateTableViewSkin();
registerChangeListener(control.tableViewProperty(), e -> {
updateTableViewSkin();
for (int i = 0, max = cells.size(); i < max; i++) {
Node n = cells.get(i);
if (n instanceof TableCell) {
((TableCell)n).updateTableView(getSkinnable().getTableView());
}
}
});
setupTreeTableViewListeners();
}
private void setupTreeTableViewListeners() {
TableView<T> tableView = getSkinnable().getTableView();
if (tableView == null) {
registerInvalidationListener(getSkinnable().tableViewProperty(), e -> {
unregisterInvalidationListeners(getSkinnable().tableViewProperty());
setupTreeTableViewListeners();
});
} else {
DoubleProperty fixedCellSizeProperty = tableView.fixedCellSizeProperty();
if (fixedCellSizeProperty != null) {
registerChangeListener(fixedCellSizeProperty, e -> {
fixedCellSize = fixedCellSizeProperty.get();
fixedCellSizeEnabled = fixedCellSize > 0;
});
fixedCellSize = fixedCellSizeProperty.get();
fixedCellSizeEnabled = fixedCellSize > 0;
registerChangeListener(getVirtualFlow().widthProperty(), e -> tableView.requestLayout());
}
}
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED_ITEMS: {
List<Node> selection = new ArrayList<>();
int index = getSkinnable().getIndex();
for (TablePosition<T,?> pos : getTableView().getSelectionModel().getSelectedCells()) {
if (pos.getRow() == index) {
TableColumn<T,?> column = pos.getTableColumn();
if (column == null) {
column = getTableView().getVisibleLeafColumn(0);
}
TableCell<T,?> cell = cellsMap.get(column).get();
if (cell != null) selection.add(cell);
}
return FXCollections.observableArrayList(selection);
}
}
case CELL_AT_ROW_COLUMN: {
int colIndex = (Integer)parameters[1];
TableColumn<T,?> column = getTableView().getVisibleLeafColumn(colIndex);
if (cellsMap.containsKey(column)) {
return cellsMap.get(column).get();
}
return null;
}
case FOCUS_ITEM: {
TableViewFocusModel<T> fm = getTableView().getFocusModel();
TablePosition<T,?> focusedCell = fm.getFocusedCell();
TableColumn<T,?> column = focusedCell.getTableColumn();
if (column == null) {
column = getTableView().getVisibleLeafColumn(0);
}
if (cellsMap.containsKey(column)) {
return cellsMap.get(column).get();
}
return null;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override protected TableCell<T, ?> createCell(TableColumnBase tcb) {
TableColumn tableColumn = (TableColumn<T,?>) tcb;
TableCell cell = (TableCell) tableColumn.getCellFactory().call(tableColumn);
cell.updateTableColumn(tableColumn);
cell.updateTableView(tableColumn.getTableView());
cell.updateTableRow(getSkinnable());
return cell;
}
@Override protected ObservableList<TableColumn<T, ?>> getVisibleLeafColumns() {
return getTableView() == null ? FXCollections.emptyObservableList() : getTableView().getVisibleLeafColumns();
}
@Override protected void updateCell(TableCell<T, ?> cell, TableRow<T> row) {
cell.updateTableRow(row);
}
@Override protected TableColumn<T, ?> getTableColumn(TableCell<T, ?> cell) {
return cell.getTableColumn();
}
private TableView<T> getTableView() {
return getSkinnable().getTableView();
}
private void updateTableViewSkin() {
TableView<T> tableView = getSkinnable().getTableView();
if (tableView != null && tableView.getSkin() instanceof TableViewSkin) {
tableViewSkin = (TableViewSkin)tableView.getSkin();
}
}
TableViewSkin<T> getTableViewSkin() {
return tableViewSkin;
}
}
