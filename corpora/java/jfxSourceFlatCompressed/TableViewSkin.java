package javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import com.sun.javafx.scene.control.behavior.TableViewBehavior;
public class TableViewSkin<T> extends TableViewSkinBase<T, T, TableView<T>, TableRow<T>, TableColumn<T, ?>> {
private final TableViewBehavior<T> behavior;
public TableViewSkin(final TableView<T> control) {
super(control);
behavior = new TableViewBehavior<>(control);
flow.setFixedCellSize(control.getFixedCellSize());
flow.setCellFactory(flow -> createCell());
EventHandler<MouseEvent> ml = event -> {
if (control.isFocusTraversable()) {
control.requestFocus();
}
};
flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
behavior.setOnFocusPreviousRow(() -> onFocusAboveCell());
behavior.setOnFocusNextRow(() -> onFocusBelowCell());
behavior.setOnMoveToFirstCell(() -> onMoveToFirstCell());
behavior.setOnMoveToLastCell(() -> onMoveToLastCell());
behavior.setOnScrollPageDown(isFocusDriven -> onScrollPageDown(isFocusDriven));
behavior.setOnScrollPageUp(isFocusDriven -> onScrollPageUp(isFocusDriven));
behavior.setOnSelectPreviousRow(() -> onSelectAboveCell());
behavior.setOnSelectNextRow(() -> onSelectBelowCell());
behavior.setOnSelectLeftCell(() -> onSelectLeftCell());
behavior.setOnSelectRightCell(() -> onSelectRightCell());
behavior.setOnFocusLeftCell(() -> onFocusLeftCell());
behavior.setOnFocusRightCell(() -> onFocusRightCell());
registerChangeListener(control.fixedCellSizeProperty(), e -> flow.setFixedCellSize(getSkinnable().getFixedCellSize()));
updateItemCount();
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED_ITEMS: {
List<Node> selection = new ArrayList<>();
TableViewSelectionModel<T> sm = getSkinnable().getSelectionModel();
for (TablePosition<T,?> pos : sm.getSelectedCells()) {
TableRow<T> row = flow.getPrivateCell(pos.getRow());
if (row != null) selection.add(row);
}
return FXCollections.observableArrayList(selection);
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_ITEM: {
Node item = (Node)parameters[0];
if (item instanceof TableCell) {
@SuppressWarnings("unchecked")
TableCell<T, ?> cell = (TableCell<T, ?>)item;
flow.scrollTo(cell.getIndex());
}
break;
}
case SET_SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<Node> items = (ObservableList<Node>)parameters[0];
if (items != null) {
TableSelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm != null) {
sm.clearSelection();
for (Node item : items) {
if (item instanceof TableCell) {
@SuppressWarnings("unchecked")
TableCell<T, ?> cell = (TableCell<T, ?>)item;
sm.select(cell.getIndex(), cell.getTableColumn());
}
}
}
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
private TableRow<T> createCell() {
TableRow<T> cell;
TableView<T> tableView = getSkinnable();
if (tableView.getRowFactory() != null) {
cell = tableView.getRowFactory().call(tableView);
} else {
cell = new TableRow<T>();
}
cell.updateTableView(tableView);
return cell;
}
@Override protected int getItemCount() {
TableView<T> tableView = getSkinnable();
return tableView.getItems() == null ? 0 : tableView.getItems().size();
}
@Override void horizontalScroll() {
super.horizontalScroll();
if (getSkinnable().getFixedCellSize() > 0) {
flow.requestCellLayout();
}
}
}
