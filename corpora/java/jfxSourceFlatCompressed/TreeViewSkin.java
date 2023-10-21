package javafx.scene.control.skin;
import com.sun.javafx.scene.control.Properties;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.scene.control.behavior.TreeViewBehavior;
public class TreeViewSkin<T> extends VirtualContainerBase<TreeView<T>, TreeCell<T>> {
@SuppressWarnings("removal")
private static final boolean IS_PANNABLE =
AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("javafx.scene.control.skin.TreeViewSkin.pannable"));
private final VirtualFlow<TreeCell<T>> flow;
private WeakReference<TreeItem<T>> weakRoot;
private final TreeViewBehavior<T> behavior;
private MapChangeListener<Object, Object> propertiesMapListener = c -> {
if (! c.wasAdded()) return;
if (Properties.RECREATE.equals(c.getKey())) {
requestRebuildCells();
getSkinnable().getProperties().remove(Properties.RECREATE);
}
};
private EventHandler<TreeModificationEvent<T>> rootListener = e -> {
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
getSkinnable().edit(null);
};
private WeakEventHandler<TreeModificationEvent<T>> weakRootListener;
public TreeViewSkin(final TreeView control) {
super(control);
behavior = new TreeViewBehavior<>(control);
flow = getVirtualFlow();
flow.setPannable(IS_PANNABLE);
flow.setCellFactory(this::createCell);
flow.setFixedCellSize(control.getFixedCellSize());
getChildren().add(flow);
setRoot(getSkinnable().getRoot());
EventHandler<MouseEvent> ml = event -> {
if (control.isFocusTraversable()) {
control.requestFocus();
}
};
flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
final ObservableMap<Object, Object> properties = control.getProperties();
properties.remove(Properties.RECREATE);
properties.addListener(propertiesMapListener);
behavior.setOnFocusPreviousRow(() -> { onFocusPreviousCell(); });
behavior.setOnFocusNextRow(() -> { onFocusNextCell(); });
behavior.setOnMoveToFirstCell(() -> { onMoveToFirstCell(); });
behavior.setOnMoveToLastCell(() -> { onMoveToLastCell(); });
behavior.setOnScrollPageDown(this::onScrollPageDown);
behavior.setOnScrollPageUp(this::onScrollPageUp);
behavior.setOnSelectPreviousRow(() -> { onSelectPreviousCell(); });
behavior.setOnSelectNextRow(() -> { onSelectNextCell(); });
registerChangeListener(control.rootProperty(), e -> setRoot(getSkinnable().getRoot()));
registerChangeListener(control.showRootProperty(), e -> {
if (! getSkinnable().isShowRoot() && getRoot() != null) {
getRoot().setExpanded(true);
}
updateItemCount();
});
registerChangeListener(control.cellFactoryProperty(), e -> flow.recreateCells());
registerChangeListener(control.fixedCellSizeProperty(), e -> flow.setFixedCellSize(getSkinnable().getFixedCellSize()));
updateItemCount();
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().getProperties().removeListener(propertiesMapListener);
setRoot(null);
getChildren().remove(flow);
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset) * 0.618033987;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return 400;
}
@Override protected void layoutChildren(final double x, final double y, final double w, final double h) {
super.layoutChildren(x, y, w, h);
flow.resizeRelocate(x, y, w, h);
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM: {
FocusModel<?> fm = getSkinnable().getFocusModel();
int focusedIndex = fm.getFocusedIndex();
if (focusedIndex == -1) {
if (getItemCount() > 0) {
focusedIndex = 0;
} else {
return null;
}
}
return flow.getPrivateCell(focusedIndex);
}
case ROW_AT_INDEX: {
final int rowIndex = (Integer)parameters[0];
return rowIndex < 0 ? null : flow.getPrivateCell(rowIndex);
}
case SELECTED_ITEMS: {
MultipleSelectionModel<TreeItem<T>> sm = getSkinnable().getSelectionModel();
ObservableList<Integer> indices = sm.getSelectedIndices();
List<Node> selection = new ArrayList<>(indices.size());
for (int i : indices) {
TreeCell<T> row = flow.getPrivateCell(i);
if (row != null) selection.add(row);
}
return FXCollections.observableArrayList(selection);
}
case VERTICAL_SCROLLBAR: return flow.getVbar();
case HORIZONTAL_SCROLLBAR: return flow.getHbar();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_ITEM: {
Node item = (Node)parameters[0];
if (item instanceof TreeCell) {
@SuppressWarnings("unchecked")
TreeCell<T> cell = (TreeCell<T>)item;
flow.scrollTo(cell.getIndex());
}
break;
}
case SET_SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<Node> items = (ObservableList<Node>)parameters[0];
if (items != null) {
MultipleSelectionModel<TreeItem<T>> sm = getSkinnable().getSelectionModel();
if (sm != null) {
sm.clearSelection();
for (Node item : items) {
if (item instanceof TreeCell) {
@SuppressWarnings("unchecked")
TreeCell<T> cell = (TreeCell<T>)item;
sm.select(cell.getIndex());
}
}
}
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
private TreeCell<T> createCell(VirtualFlow<TreeCell<T>> flow) {
final TreeCell<T> cell;
if (getSkinnable().getCellFactory() != null) {
cell = getSkinnable().getCellFactory().call(getSkinnable());
} else {
cell = createDefaultCellImpl();
}
if (cell.getDisclosureNode() == null) {
final StackPane disclosureNode = new StackPane();
disclosureNode.getStyleClass().setAll("tree-disclosure-node");
final StackPane disclosureNodeArrow = new StackPane();
disclosureNodeArrow.getStyleClass().setAll("arrow");
disclosureNode.getChildren().add(disclosureNodeArrow);
cell.setDisclosureNode(disclosureNode);
}
cell.updateTreeView(getSkinnable());
return cell;
}
private TreeItem<T> getRoot() {
return weakRoot == null ? null : weakRoot.get();
}
private void setRoot(TreeItem<T> newRoot) {
if (getRoot() != null && weakRootListener != null) {
getRoot().removeEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootListener);
}
weakRoot = new WeakReference<>(newRoot);
if (getRoot() != null) {
weakRootListener = new WeakEventHandler<>(rootListener);
getRoot().addEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootListener);
}
updateItemCount();
}
@Override protected int getItemCount() {
return getSkinnable().getExpandedItemCount();
}
@Override protected void updateItemCount() {
int newCount = getItemCount();
requestRebuildCells();
flow.setCellCount(newCount);
getSkinnable().requestLayout();
}
private TreeCell<T> createDefaultCellImpl() {
return new TreeCell<T>() {
private HBox hbox;
private WeakReference<TreeItem<T>> treeItemRef;
private InvalidationListener treeItemGraphicListener = observable -> {
updateDisplay(getItem(), isEmpty());
};
private InvalidationListener treeItemListener = new InvalidationListener() {
@Override public void invalidated(Observable observable) {
TreeItem<T> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
if (oldTreeItem != null) {
oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
}
TreeItem<T> newTreeItem = getTreeItem();
if (newTreeItem != null) {
newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
treeItemRef = new WeakReference<TreeItem<T>>(newTreeItem);
}
}
};
private WeakInvalidationListener weakTreeItemGraphicListener =
new WeakInvalidationListener(treeItemGraphicListener);
private WeakInvalidationListener weakTreeItemListener =
new WeakInvalidationListener(treeItemListener);
{
treeItemProperty().addListener(weakTreeItemListener);
if (getTreeItem() != null) {
getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
}
}
private void updateDisplay(T item, boolean empty) {
if (item == null || empty) {
hbox = null;
setText(null);
setGraphic(null);
} else {
TreeItem<T> treeItem = getTreeItem();
Node graphic = treeItem == null ? null : treeItem.getGraphic();
if (graphic != null) {
if (item instanceof Node) {
setText(null);
if (hbox == null) {
hbox = new HBox(3);
}
hbox.getChildren().setAll(graphic, (Node)item);
setGraphic(hbox);
} else {
hbox = null;
setText(item.toString());
setGraphic(graphic);
}
} else {
hbox = null;
if (item instanceof Node) {
setText(null);
setGraphic((Node)item);
} else {
setText(item.toString());
setGraphic(null);
}
}
}
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
updateDisplay(item, empty);
}
};
}
private void onFocusPreviousCell() {
FocusModel<TreeItem<T>> fm = getSkinnable().getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
private void onFocusNextCell() {
FocusModel<TreeItem<T>> fm = getSkinnable().getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
private void onSelectPreviousCell() {
int row = getSkinnable().getSelectionModel().getSelectedIndex();
flow.scrollTo(row);
}
private void onSelectNextCell() {
int row = getSkinnable().getSelectionModel().getSelectedIndex();
flow.scrollTo(row);
}
private void onMoveToFirstCell() {
flow.scrollTo(0);
flow.setPosition(0);
}
private void onMoveToLastCell() {
flow.scrollTo(getItemCount());
flow.setPosition(1);
}
private int onScrollPageDown(boolean isFocusDriven) {
TreeCell<T> lastVisibleCell = flow.getLastVisibleCellWithinViewport();
if (lastVisibleCell == null) return -1;
final SelectionModel<TreeItem<T>> sm = getSkinnable().getSelectionModel();
final FocusModel<TreeItem<T>> fm = getSkinnable().getFocusModel();
if (sm == null || fm == null) return -1;
int lastVisibleCellIndex = lastVisibleCell.getIndex();
boolean isSelected = false;
if (isFocusDriven) {
isSelected = lastVisibleCell.isFocused() || fm.isFocused(lastVisibleCellIndex);
} else {
isSelected = lastVisibleCell.isSelected() || sm.isSelected(lastVisibleCellIndex);
}
if (isSelected) {
boolean isLeadIndex = (isFocusDriven && fm.getFocusedIndex() == lastVisibleCellIndex)
|| (! isFocusDriven && sm.getSelectedIndex() == lastVisibleCellIndex);
if (isLeadIndex) {
flow.scrollToTop(lastVisibleCell);
TreeCell<T> newLastVisibleCell = flow.getLastVisibleCellWithinViewport();
lastVisibleCell = newLastVisibleCell == null ? lastVisibleCell : newLastVisibleCell;
}
} else {
}
int newSelectionIndex = lastVisibleCell.getIndex();
flow.scrollTo(lastVisibleCell);
return newSelectionIndex;
}
private int onScrollPageUp(boolean isFocusDriven) {
TreeCell<T> firstVisibleCell = flow.getFirstVisibleCellWithinViewport();
if (firstVisibleCell == null) return -1;
final SelectionModel<TreeItem<T>> sm = getSkinnable().getSelectionModel();
final FocusModel<TreeItem<T>> fm = getSkinnable().getFocusModel();
if (sm == null || fm == null) return -1;
int firstVisibleCellIndex = firstVisibleCell.getIndex();
boolean isSelected = false;
if (isFocusDriven) {
isSelected = firstVisibleCell.isFocused() || fm.isFocused(firstVisibleCellIndex);
} else {
isSelected = firstVisibleCell.isSelected() || sm.isSelected(firstVisibleCellIndex);
}
if (isSelected) {
boolean isLeadIndex = (isFocusDriven && fm.getFocusedIndex() == firstVisibleCellIndex)
|| (! isFocusDriven && sm.getSelectedIndex() == firstVisibleCellIndex);
if (isLeadIndex) {
flow.scrollToBottom(firstVisibleCell);
TreeCell<T> newFirstVisibleCell = flow.getFirstVisibleCellWithinViewport();
firstVisibleCell = newFirstVisibleCell == null ? firstVisibleCell : newFirstVisibleCell;
}
} else {
}
int newSelectionIndex = firstVisibleCell.getIndex();
flow.scrollTo(firstVisibleCell);
return newSelectionIndex;
}
}
