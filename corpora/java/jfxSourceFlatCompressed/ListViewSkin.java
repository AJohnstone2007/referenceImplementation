package javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.scene.control.Properties;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.WeakListChangeListener;
import javafx.collections.WeakMapChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import com.sun.javafx.scene.control.behavior.ListViewBehavior;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
public class ListViewSkin<T> extends VirtualContainerBase<ListView<T>, ListCell<T>> {
@SuppressWarnings("removal")
private static final boolean IS_PANNABLE =
AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("javafx.scene.control.skin.ListViewSkin.pannable"));
private static final String EMPTY_LIST_TEXT = ControlResources.getString("ListView.noContent");
private final VirtualFlow<ListCell<T>> flow;
private StackPane placeholderRegion;
private Node placeholderNode;
private ObservableList<T> listViewItems;
private boolean needCellsRebuilt = true;
private boolean needCellsReconfigured = false;
private int itemCount = -1;
private ListViewBehavior<T> behavior;
private MapChangeListener<Object, Object> propertiesMapListener = c -> {
if (! c.wasAdded()) return;
if (Properties.RECREATE.equals(c.getKey())) {
needCellsRebuilt = true;
getSkinnable().requestLayout();
getSkinnable().getProperties().remove(Properties.RECREATE);
}
};
private WeakMapChangeListener<Object, Object> weakPropertiesMapListener =
new WeakMapChangeListener<>(propertiesMapListener);
private final ListChangeListener<T> listViewItemsListener = new ListChangeListener<T>() {
@Override public void onChanged(Change<? extends T> c) {
while (c.next()) {
if (c.wasReplaced()) {
for (int i = c.getFrom(); i < c.getTo(); i++) {
flow.setCellDirty(i);
}
break;
} else if (c.getRemovedSize() == itemCount) {
itemCount = 0;
break;
}
}
getSkinnable().edit(-1);
markItemCountDirty();
getSkinnable().requestLayout();
}
};
private final WeakListChangeListener<T> weakListViewItemsListener =
new WeakListChangeListener<T>(listViewItemsListener);
private final InvalidationListener itemsChangeListener = observable -> updateListViewItems();
private WeakInvalidationListener
weakItemsChangeListener = new WeakInvalidationListener(itemsChangeListener);
public ListViewSkin(final ListView<T> control) {
super(control);
behavior = new ListViewBehavior<>(control);
behavior.setOnFocusPreviousRow(() -> onFocusPreviousCell());
behavior.setOnFocusNextRow(() -> onFocusNextCell());
behavior.setOnMoveToFirstCell(() -> onMoveToFirstCell());
behavior.setOnMoveToLastCell(() -> onMoveToLastCell());
behavior.setOnSelectPreviousRow(() -> onSelectPreviousCell());
behavior.setOnSelectNextRow(() -> onSelectNextCell());
behavior.setOnScrollPageDown(this::onScrollPageDown);
behavior.setOnScrollPageUp(this::onScrollPageUp);
updateListViewItems();
flow = getVirtualFlow();
flow.setId("virtual-flow");
flow.setPannable(IS_PANNABLE);
flow.setVertical(control.getOrientation() == Orientation.VERTICAL);
flow.setCellFactory(flow -> createCell());
flow.setFixedCellSize(control.getFixedCellSize());
getChildren().add(flow);
EventHandler<MouseEvent> ml = event -> {
if (control.isFocusTraversable()) {
control.requestFocus();
}
};
flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
updateItemCount();
control.itemsProperty().addListener(weakItemsChangeListener);
final ObservableMap<Object, Object> properties = control.getProperties();
properties.remove(Properties.RECREATE);
properties.addListener(weakPropertiesMapListener);
registerChangeListener(control.itemsProperty(), o -> updateListViewItems());
registerChangeListener(control.orientationProperty(), o ->
flow.setVertical(control.getOrientation() == Orientation.VERTICAL)
);
registerChangeListener(control.cellFactoryProperty(), o -> flow.recreateCells());
registerChangeListener(control.parentProperty(), o -> {
if (control.getParent() != null && control.isVisible()) {
control.requestLayout();
}
});
registerChangeListener(control.placeholderProperty(), o -> updatePlaceholderRegionVisibility());
registerChangeListener(control.fixedCellSizeProperty(), o ->
flow.setFixedCellSize(control.getFixedCellSize())
);
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().getProperties().removeListener(weakPropertiesMapListener);
getSkinnable().itemsProperty().removeListener(weakItemsChangeListener);
if (listViewItems != null) {
listViewItems.removeListener(weakListViewItemsListener);
listViewItems = null;
}
getChildren().remove(flow);
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
super.layoutChildren(x, y, w, h);
if (needCellsRebuilt) {
flow.rebuildCells();
} else if (needCellsReconfigured) {
flow.reconfigureCells();
}
needCellsRebuilt = false;
needCellsReconfigured = false;
if (getItemCount() == 0) {
if (placeholderRegion != null) {
placeholderRegion.setVisible(w > 0 && h > 0);
placeholderRegion.resizeRelocate(x, y, w, h);
}
} else {
flow.resizeRelocate(x, y, w, h);
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
checkState();
if (getItemCount() == 0) {
if (placeholderRegion == null) {
updatePlaceholderRegionVisibility();
}
if (placeholderRegion != null) {
return placeholderRegion.prefWidth(height) + leftInset + rightInset;
}
}
return computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset) * 0.618033987;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return 400;
}
@Override protected int getItemCount() {
return itemCount;
}
@Override protected void updateItemCount() {
if (flow == null) return;
int oldCount = itemCount;
int newCount = listViewItems == null ? 0 : listViewItems.size();
itemCount = newCount;
flow.setCellCount(newCount);
updatePlaceholderRegionVisibility();
if (newCount == oldCount) {
needCellsReconfigured = true;
} else if (oldCount == 0) {
requestRebuildCells();
}
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM: {
FocusModel<?> fm = getSkinnable().getFocusModel();
int focusedIndex = fm.getFocusedIndex();
if (focusedIndex == -1) {
if (placeholderRegion != null && placeholderRegion.isVisible()) {
return placeholderRegion.getChildren().get(0);
}
if (getItemCount() > 0) {
focusedIndex = 0;
} else {
return null;
}
}
return flow.getPrivateCell(focusedIndex);
}
case ITEM_COUNT: return getItemCount();
case ITEM_AT_INDEX: {
Integer rowIndex = (Integer)parameters[0];
if (rowIndex == null) return null;
if (0 <= rowIndex && rowIndex < getItemCount()) {
return flow.getPrivateCell(rowIndex);
}
return null;
}
case SELECTED_ITEMS: {
MultipleSelectionModel<T> sm = getSkinnable().getSelectionModel();
ObservableList<Integer> indices = sm.getSelectedIndices();
List<Node> selection = new ArrayList<>(indices.size());
for (int i : indices) {
ListCell<T> row = flow.getPrivateCell(i);
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
if (item instanceof ListCell) {
@SuppressWarnings("unchecked")
ListCell<T> cell = (ListCell<T>)item;
flow.scrollTo(cell.getIndex());
}
break;
}
case SET_SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<Node> items = (ObservableList<Node>)parameters[0];
if (items != null) {
MultipleSelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm != null) {
sm.clearSelection();
for (Node item : items) {
if (item instanceof ListCell) {
@SuppressWarnings("unchecked")
ListCell<T> cell = (ListCell<T>)item;
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
private ListCell<T> createCell() {
ListCell<T> cell;
if (getSkinnable().getCellFactory() != null) {
cell = getSkinnable().getCellFactory().call(getSkinnable());
} else {
cell = createDefaultCellImpl();
}
cell.updateListView(getSkinnable());
return cell;
}
private void updateListViewItems() {
if (listViewItems != null) {
listViewItems.removeListener(weakListViewItemsListener);
}
this.listViewItems = getSkinnable().getItems();
if (listViewItems != null) {
listViewItems.addListener(weakListViewItemsListener);
}
markItemCountDirty();
getSkinnable().requestLayout();
}
private final void updatePlaceholderRegionVisibility() {
boolean visible = getItemCount() == 0;
if (visible) {
placeholderNode = getSkinnable().getPlaceholder();
if (placeholderNode == null && (EMPTY_LIST_TEXT != null && ! EMPTY_LIST_TEXT.isEmpty())) {
placeholderNode = new Label();
((Label)placeholderNode).setText(EMPTY_LIST_TEXT);
}
if (placeholderNode != null) {
if (placeholderRegion == null) {
placeholderRegion = new StackPane();
placeholderRegion.getStyleClass().setAll("placeholder");
getChildren().add(placeholderRegion);
}
placeholderRegion.getChildren().setAll(placeholderNode);
}
}
flow.setVisible(!visible);
if (placeholderRegion != null) {
placeholderRegion.setVisible(visible);
}
}
private static <T> ListCell<T> createDefaultCellImpl() {
return new ListCell<T>() {
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setText(null);
setGraphic(null);
} else if (item instanceof Node) {
setText(null);
Node currentNode = getGraphic();
Node newNode = (Node) item;
if (currentNode == null || ! currentNode.equals(newNode)) {
setGraphic(newNode);
}
} else {
setText(item == null ? "null" : item.toString());
setGraphic(null);
}
}
};
}
private void onFocusPreviousCell() {
FocusModel<T> fm = getSkinnable().getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
private void onFocusNextCell() {
FocusModel<T> fm = getSkinnable().getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
private void onSelectPreviousCell() {
SelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm == null) return;
int pos = sm.getSelectedIndex();
flow.scrollTo(pos);
IndexedCell<T> cell = flow.getFirstVisibleCell();
if (cell == null || pos < cell.getIndex()) {
flow.setPosition(pos / (double) getItemCount());
}
}
private void onSelectNextCell() {
SelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm == null) return;
int pos = sm.getSelectedIndex();
flow.scrollTo(pos);
ListCell<T> cell = flow.getLastVisibleCell();
if (cell == null || cell.getIndex() < pos) {
flow.setPosition(pos / (double) getItemCount());
}
}
private void onMoveToFirstCell() {
flow.scrollTo(0);
flow.setPosition(0);
}
private void onMoveToLastCell() {
int endPos = getItemCount() - 1;
flow.scrollTo(endPos);
flow.setPosition(1);
}
private int onScrollPageDown(boolean isFocusDriven) {
ListCell<T> lastVisibleCell = flow.getLastVisibleCellWithinViewport();
if (lastVisibleCell == null) return -1;
final SelectionModel<T> sm = getSkinnable().getSelectionModel();
final FocusModel<T> fm = getSkinnable().getFocusModel();
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
ListCell<T> newLastVisibleCell = flow.getLastVisibleCellWithinViewport();
lastVisibleCell = newLastVisibleCell == null ? lastVisibleCell : newLastVisibleCell;
}
} else {
}
int newSelectionIndex = lastVisibleCell.getIndex();
flow.scrollTo(lastVisibleCell);
return newSelectionIndex;
}
private int onScrollPageUp(boolean isFocusDriven) {
ListCell<T> firstVisibleCell = flow.getFirstVisibleCellWithinViewport();
if (firstVisibleCell == null) return -1;
final SelectionModel<T> sm = getSkinnable().getSelectionModel();
final FocusModel<T> fm = getSkinnable().getFocusModel();
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
ListCell<T> newFirstVisibleCell = flow.getFirstVisibleCellWithinViewport();
firstVisibleCell = newFirstVisibleCell == null ? firstVisibleCell : newFirstVisibleCell;
}
} else {
}
int newSelectionIndex = firstVisibleCell.getIndex();
flow.scrollTo(firstVisibleCell);
return newSelectionIndex;
}
}
