package javafx.scene.control.skin;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.TreeTableViewBackingList;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.event.WeakEventHandler;
import javafx.scene.control.*;
import com.sun.javafx.scene.control.behavior.TreeTableViewBehavior;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
public class TreeTableViewSkin<T> extends TableViewSkinBase<T, TreeItem<T>, TreeTableView<T>, TreeTableRow<T>, TreeTableColumn<T,?>> {
TreeTableViewBackingList<T> tableBackingList;
ObjectProperty<ObservableList<TreeItem<T>>> tableBackingListProperty;
private WeakReference<TreeItem<T>> weakRootRef;
private final TreeTableViewBehavior<T> behavior;
private EventHandler<TreeItem.TreeModificationEvent<T>> rootListener = e -> {
if (e.wasAdded() && e.wasRemoved() && e.getAddedSize() == e.getRemovedSize()) {
markItemCountDirty();
getSkinnable().requestLayout();
} else if (e.getEventType().equals(TreeItem.valueChangedEvent())) {
requestRebuildCells();
} else {
EventType<?> eventType = e.getEventType();
while (eventType != null) {
if (eventType.equals(TreeItem.<T>expandedItemCountChangeEvent())) {
markItemCountDirty();
getSkinnable().requestLayout();
break;
}
eventType = eventType.getSuperType();
}
}
getSkinnable().edit(-1, null);
};
private WeakEventHandler<TreeModificationEvent<T>> weakRootListener;
public TreeTableViewSkin(final TreeTableView<T> control) {
super(control);
behavior = new TreeTableViewBehavior<>(control);
flow.setFixedCellSize(control.getFixedCellSize());
flow.setCellFactory(flow -> createCell());
setRoot(getSkinnable().getRoot());
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
registerChangeListener(control.rootProperty(), e -> {
getSkinnable().edit(-1, null);
setRoot(getSkinnable().getRoot());
});
registerChangeListener(control.showRootProperty(), e -> {
if (! getSkinnable().isShowRoot() && getRoot() != null) {
getRoot().setExpanded(true);
}
updateItemCount();
});
registerChangeListener(control.rowFactoryProperty(), e -> flow.recreateCells());
registerChangeListener(control.expandedItemCountProperty(), e -> markItemCountDirty());
registerChangeListener(control.fixedCellSizeProperty(), e -> flow.setFixedCellSize(getSkinnable().getFixedCellSize()));
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case ROW_AT_INDEX: {
final int rowIndex = (Integer)parameters[0];
return rowIndex < 0 ? null : flow.getPrivateCell(rowIndex);
}
case SELECTED_ITEMS: {
List<Node> selection = new ArrayList<>();
TreeTableView.TreeTableViewSelectionModel<T> sm = getSkinnable().getSelectionModel();
for (TreeTablePosition<T,?> pos : sm.getSelectedCells()) {
TreeTableRow<T> row = flow.getPrivateCell(pos.getRow());
if (row != null) selection.add(row);
}
return FXCollections.observableArrayList(selection);
}
case FOCUS_ITEM:
case CELL_AT_ROW_COLUMN:
case COLUMN_AT_INDEX:
case HEADER:
case VERTICAL_SCROLLBAR:
case HORIZONTAL_SCROLLBAR:
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_ITEM: {
Node item = (Node)parameters[0];
if (item instanceof TreeTableCell) {
@SuppressWarnings("unchecked")
TreeTableCell<T, ?> cell = (TreeTableCell<T, ?>)item;
flow.scrollTo(cell.getIndex());
}
break;
}
case SET_SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<Node> items = (ObservableList<Node>)parameters[0];
if (items != null) {
TreeTableView.TreeTableViewSelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm != null) {
sm.clearSelection();
for (Node item : items) {
if (item instanceof TreeTableCell) {
@SuppressWarnings("unchecked")
TreeTableCell<T, ?> cell = (TreeTableCell<T, ?>)item;
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
private TreeTableRow<T> createCell() {
TreeTableRow<T> cell;
TreeTableView<T> treeTableView = getSkinnable();
if (treeTableView.getRowFactory() != null) {
cell = treeTableView.getRowFactory().call(treeTableView);
} else {
cell = new TreeTableRow<T>();
}
if (cell.getDisclosureNode() == null) {
final StackPane disclosureNode = new StackPane();
disclosureNode.getStyleClass().setAll("tree-disclosure-node");
disclosureNode.setMouseTransparent(true);
final StackPane disclosureNodeArrow = new StackPane();
disclosureNodeArrow.getStyleClass().setAll("arrow");
disclosureNode.getChildren().add(disclosureNodeArrow);
cell.setDisclosureNode(disclosureNode);
}
cell.updateTreeTableView(treeTableView);
return cell;
}
private TreeItem<T> getRoot() {
return weakRootRef == null ? null : weakRootRef.get();
}
private void setRoot(TreeItem<T> newRoot) {
if (getRoot() != null && weakRootListener != null) {
getRoot().removeEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootListener);
}
weakRootRef = new WeakReference<>(newRoot);
if (getRoot() != null) {
weakRootListener = new WeakEventHandler<>(rootListener);
getRoot().addEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootListener);
}
updateItemCount();
}
@Override protected int getItemCount() {
return getSkinnable().getExpandedItemCount();
}
@Override void horizontalScroll() {
super.horizontalScroll();
if (getSkinnable().getFixedCellSize() > 0) {
flow.requestCellLayout();
}
}
@Override protected void updateItemCount() {
updatePlaceholderRegionVisibility();
tableBackingList.resetSize();
int oldCount = flow.getCellCount();
int newCount = getItemCount();
flow.setCellCount(newCount);
if (newCount != oldCount) {
} else {
needCellsReconfigured = true;
}
}
}
