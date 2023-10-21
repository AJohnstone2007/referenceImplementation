package javafx.scene.control.skin;
import com.sun.javafx.scene.control.Properties;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.collections.WeakListChangeListener;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class TableViewSkinBase<M, S, C extends Control, I extends IndexedCell<M>, TC extends TableColumnBase<S,?>> extends VirtualContainerBase<C, I> {
private static final double GOLDEN_RATIO_MULTIPLIER = 0.618033987;
@SuppressWarnings("removal")
private static final boolean IS_PANNABLE =
AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("javafx.scene.control.skin.TableViewSkin.pannable"));
private final String EMPTY_TABLE_TEXT = ControlResources.getString("TableView.noContent");
private final String NO_COLUMNS_TEXT = ControlResources.getString("TableView.noColumns");
VirtualFlow<I> flow;
private boolean contentWidthDirty = true;
private Region columnReorderLine;
private Region columnReorderOverlay;
private TableHeaderRow tableHeaderRow;
private Callback<C, I> rowFactory;
private StackPane placeholderRegion;
private Label placeholderLabel;
private int visibleColCount;
boolean needCellsRecreated = true;
boolean needCellsReconfigured = false;
private int itemCount = -1;
private MapChangeListener<Object, Object> propertiesMapListener = c -> {
if (! c.wasAdded()) return;
if (Properties.REFRESH.equals(c.getKey())) {
refreshView();
getSkinnable().getProperties().remove(Properties.REFRESH);
} else if (Properties.RECREATE.equals(c.getKey())) {
needCellsRecreated = true;
refreshView();
getSkinnable().getProperties().remove(Properties.RECREATE);
}
};
private ListChangeListener<S> rowCountListener = c -> {
while (c.next()) {
if (c.wasReplaced()) {
itemCount = 0;
break;
} else if (c.getRemovedSize() == itemCount) {
itemCount = 0;
break;
}
}
if (getSkinnable() instanceof TableView) {
((TableView)getSkinnable()).edit(-1, null);
}
markItemCountDirty();
getSkinnable().requestLayout();
};
private ListChangeListener<TC> visibleLeafColumnsListener = c -> {
updateVisibleColumnCount();
while (c.next()) {
updateVisibleLeafColumnWidthListeners(c.getAddedSubList(), c.getRemoved());
}
};
private InvalidationListener widthListener = observable -> {
needCellsReconfigured = true;
if (getSkinnable() != null) {
getSkinnable().requestLayout();
}
};
private InvalidationListener itemsChangeListener;
private WeakListChangeListener<S> weakRowCountListener =
new WeakListChangeListener<>(rowCountListener);
private WeakListChangeListener<TC> weakVisibleLeafColumnsListener =
new WeakListChangeListener<>(visibleLeafColumnsListener);
private WeakInvalidationListener weakWidthListener =
new WeakInvalidationListener(widthListener);
private WeakInvalidationListener weakItemsChangeListener;
public TableViewSkinBase(final C control) {
super(control);
flow = getVirtualFlow();
flow.setPannable(IS_PANNABLE);
flow.getHbar().valueProperty().addListener(o -> horizontalScroll());
flow.getHbar().setUnitIncrement(15);
flow.getHbar().setBlockIncrement(TableColumnHeader.DEFAULT_COLUMN_WIDTH);
columnReorderLine = new Region();
columnReorderLine.getStyleClass().setAll("column-resize-line");
columnReorderLine.setManaged(false);
columnReorderLine.setVisible(false);
columnReorderOverlay = new Region();
columnReorderOverlay.getStyleClass().setAll("column-overlay");
columnReorderOverlay.setVisible(false);
columnReorderOverlay.setManaged(false);
tableHeaderRow = createTableHeaderRow();
tableHeaderRow.setFocusTraversable(false);
getChildren().addAll(tableHeaderRow, flow, columnReorderOverlay, columnReorderLine);
updateVisibleColumnCount();
updateVisibleLeafColumnWidthListeners(getVisibleLeafColumns(), FXCollections.<TC>emptyObservableList());
tableHeaderRow.reorderingProperty().addListener(valueModel -> {
getSkinnable().requestLayout();
});
getVisibleLeafColumns().addListener(weakVisibleLeafColumnsListener);
final ObjectProperty<ObservableList<S>> itemsProperty = TableSkinUtils.itemsProperty(this);
updateTableItems(null, itemsProperty.get());
itemsChangeListener = new InvalidationListener() {
private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>(itemsProperty.get());
@Override public void invalidated(Observable observable) {
ObservableList<S> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(itemsProperty.get());
updateTableItems(oldItems, itemsProperty.get());
}
};
weakItemsChangeListener = new WeakInvalidationListener(itemsChangeListener);
itemsProperty.addListener(weakItemsChangeListener);
final ObservableMap<Object, Object> properties = control.getProperties();
properties.remove(Properties.REFRESH);
properties.remove(Properties.RECREATE);
properties.addListener(propertiesMapListener);
control.addEventHandler(ScrollToEvent.<TC>scrollToColumn(), event -> {
scrollHorizontally(event.getScrollTarget());
});
InvalidationListener widthObserver = valueModel -> {
contentWidthDirty = true;
getSkinnable().requestLayout();
};
flow.widthProperty().addListener(widthObserver);
flow.getVbar().widthProperty().addListener(widthObserver);
final ObjectProperty<Callback<C, I>> rowFactoryProperty = TableSkinUtils.rowFactoryProperty(this);
registerChangeListener(rowFactoryProperty, e -> {
Callback<C, I> oldFactory = rowFactory;
rowFactory = rowFactoryProperty.get();
if (oldFactory != rowFactory) {
requestRebuildCells();
}
});
registerChangeListener(TableSkinUtils.placeholderProperty(this), e -> updatePlaceholderRegionVisibility());
registerChangeListener(flow.getVbar().visibleProperty(), e -> updateContentWidth());
}
@Override public void dispose() {
if (getSkinnable() == null) return;
final ObjectProperty<ObservableList<S>> itemsProperty = TableSkinUtils.itemsProperty(this);
getVisibleLeafColumns().removeListener(weakVisibleLeafColumnsListener);
itemsProperty.removeListener(weakItemsChangeListener);
getSkinnable().getProperties().removeListener(propertiesMapListener);
updateTableItems(itemsProperty.get(), null);
super.dispose();
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return 400;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double prefHeight = computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset);
List<? extends TC> cols = getVisibleLeafColumns();
if (cols == null || cols.isEmpty()) {
return prefHeight * GOLDEN_RATIO_MULTIPLIER;
}
double pw = leftInset + rightInset;
for (int i = 0, max = cols.size(); i < max; i++) {
TC tc = cols.get(i);
pw += Math.max(tc.getPrefWidth(), tc.getMinWidth());
}
return Math.max(pw, prefHeight * GOLDEN_RATIO_MULTIPLIER);
}
@Override protected void layoutChildren(final double x, double y,
final double w, final double h) {
C table = getSkinnable();
if (table == null) {
return;
}
super.layoutChildren(x, y, w, h);
if (needCellsRecreated) {
flow.recreateCells();
} else if (needCellsReconfigured) {
flow.reconfigureCells();
}
needCellsRecreated = false;
needCellsReconfigured = false;
final double baselineOffset = table.getLayoutBounds().getHeight() / 2;
double tableHeaderRowHeight = tableHeaderRow.prefHeight(-1);
layoutInArea(tableHeaderRow, x, y, w, tableHeaderRowHeight, baselineOffset,
HPos.CENTER, VPos.CENTER);
y += tableHeaderRowHeight;
double flowHeight = Math.floor(h - tableHeaderRowHeight);
if (getItemCount() == 0 || visibleColCount == 0) {
layoutInArea(placeholderRegion, x, y,
w, flowHeight,
baselineOffset, HPos.CENTER, VPos.CENTER);
} else {
layoutInArea(flow, x, y,
w, flowHeight,
baselineOffset, HPos.CENTER, VPos.CENTER);
}
if (tableHeaderRow.getReorderingRegion() != null) {
TableColumnHeader reorderingColumnHeader = tableHeaderRow.getReorderingRegion();
TableColumnBase reorderingColumn = reorderingColumnHeader.getTableColumn();
if (reorderingColumn != null) {
Node n = tableHeaderRow.getReorderingRegion();
double minX = tableHeaderRow.sceneToLocal(n.localToScene(n.getBoundsInLocal())).getMinX();
double overlayWidth = reorderingColumnHeader.getWidth();
if (minX < 0) {
overlayWidth += minX;
}
minX = minX < 0 ? 0 : minX;
if (minX + overlayWidth > w) {
overlayWidth = w - minX;
if (flow.getVbar().isVisible()) {
overlayWidth -= flow.getVbar().getWidth() - 1;
}
}
double contentAreaHeight = flowHeight;
if (flow.getHbar().isVisible()) {
contentAreaHeight -= flow.getHbar().getHeight();
}
columnReorderOverlay.resize(overlayWidth, contentAreaHeight);
columnReorderOverlay.setLayoutX(minX);
columnReorderOverlay.setLayoutY(tableHeaderRow.getHeight());
}
double cw = columnReorderLine.snappedLeftInset() + columnReorderLine.snappedRightInset();
double lineHeight = h - (flow.getHbar().isVisible() ? flow.getHbar().getHeight() - 1 : 0);
columnReorderLine.resizeRelocate(0, columnReorderLine.snappedTopInset(), cw, lineHeight);
}
columnReorderLine.setVisible(tableHeaderRow.isReordering());
columnReorderOverlay.setVisible(tableHeaderRow.isReordering());
checkContentWidthState();
}
protected TableHeaderRow createTableHeaderRow() {
return new TableHeaderRow(this);
}
protected TableHeaderRow getTableHeaderRow() {
return tableHeaderRow;
}
private TableSelectionModel<S> getSelectionModel() {
return TableSkinUtils.getSelectionModel(this);
}
private TableFocusModel<M,?> getFocusModel() {
return TableSkinUtils.getFocusModel(this);
}
private TablePositionBase<? extends TC> getFocusedCell() {
return TableSkinUtils.getFocusedCell(this);
}
private ObservableList<? extends TC> getVisibleLeafColumns() {
return TableSkinUtils.getVisibleLeafColumns(this);
}
@Override protected void updateItemCount() {
updatePlaceholderRegionVisibility();
int oldCount = itemCount;
int newCount = getItemCount();
itemCount = newCount;
if (itemCount == 0) {
flow.getHbar().setValue(0.0);
}
flow.setCellCount(newCount);
if (newCount == oldCount) {
needCellsReconfigured = true;
} else if (oldCount == 0) {
requestRebuildCells();
}
}
private void checkContentWidthState() {
if (contentWidthDirty || getItemCount() == 0) {
updateContentWidth();
contentWidthDirty = false;
}
}
void horizontalScroll() {
tableHeaderRow.updateScrollX();
}
protected void onFocusAboveCell() {
TableFocusModel<M, ?> fm = getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
protected void onFocusBelowCell() {
TableFocusModel<M, ?> fm = getFocusModel();
if (fm == null) return;
flow.scrollTo(fm.getFocusedIndex());
}
protected void onSelectAboveCell() {
SelectionModel<S> sm = getSelectionModel();
if (sm == null) return;
flow.scrollTo(sm.getSelectedIndex());
}
protected void onSelectBelowCell() {
SelectionModel<S> sm = getSelectionModel();
if (sm == null) return;
flow.scrollTo(sm.getSelectedIndex());
}
protected void onSelectLeftCell() {
scrollHorizontally();
}
protected void onSelectRightCell() {
scrollHorizontally();
}
protected void onFocusLeftCell() {
scrollHorizontally();
}
protected void onFocusRightCell() {
scrollHorizontally();
}
protected void onMoveToFirstCell() {
flow.scrollTo(0);
flow.setPosition(0);
}
protected void onMoveToLastCell() {
int endPos = getItemCount();
flow.scrollTo(endPos);
flow.setPosition(1);
}
private void updateTableItems(ObservableList<S> oldList, ObservableList<S> newList) {
if (oldList != null) {
oldList.removeListener(weakRowCountListener);
}
if (newList != null) {
newList.addListener(weakRowCountListener);
}
markItemCountDirty();
getSkinnable().requestLayout();
}
Region getColumnReorderLine() {
return columnReorderLine;
}
protected int onScrollPageDown(boolean isFocusDriven) {
TableSelectionModel<S> sm = getSelectionModel();
if (sm == null) return -1;
final int itemCount = getItemCount();
I lastVisibleCell = flow.getLastVisibleCellWithinViewport();
if (lastVisibleCell == null) return -1;
int lastVisibleCellIndex = lastVisibleCell.getIndex();
lastVisibleCellIndex = lastVisibleCellIndex >= itemCount ? itemCount - 1 : lastVisibleCellIndex;
boolean isSelected;
if (isFocusDriven) {
isSelected = lastVisibleCell.isFocused() || isCellFocused(lastVisibleCellIndex);
} else {
isSelected = lastVisibleCell.isSelected() || isCellSelected(lastVisibleCellIndex);
}
if (isSelected) {
boolean isLeadIndex = isLeadIndex(isFocusDriven, lastVisibleCellIndex);
if (isLeadIndex) {
flow.scrollToTop(lastVisibleCell);
I newLastVisibleCell = flow.getLastVisibleCellWithinViewport();
lastVisibleCell = newLastVisibleCell == null ? lastVisibleCell : newLastVisibleCell;
}
}
int newSelectionIndex = lastVisibleCell.getIndex();
newSelectionIndex = newSelectionIndex >= itemCount ? itemCount - 1 : newSelectionIndex;
flow.scrollTo(newSelectionIndex);
return newSelectionIndex;
}
protected int onScrollPageUp(boolean isFocusDriven) {
I firstVisibleCell = flow.getFirstVisibleCellWithinViewport();
if (firstVisibleCell == null) return -1;
int firstVisibleCellIndex = firstVisibleCell.getIndex();
boolean isSelected = false;
if (isFocusDriven) {
isSelected = firstVisibleCell.isFocused() || isCellFocused(firstVisibleCellIndex);
} else {
isSelected = firstVisibleCell.isSelected() || isCellSelected(firstVisibleCellIndex);
}
if (isSelected) {
boolean isLeadIndex = isLeadIndex(isFocusDriven, firstVisibleCellIndex);
if (isLeadIndex) {
flow.scrollToBottom(firstVisibleCell);
I newFirstVisibleCell = flow.getFirstVisibleCellWithinViewport();
firstVisibleCell = newFirstVisibleCell == null ? firstVisibleCell : newFirstVisibleCell;
}
}
int newSelectionIndex = firstVisibleCell.getIndex();
flow.scrollTo(newSelectionIndex);
return newSelectionIndex;
}
private boolean isLeadIndex(boolean isFocusDriven, int index) {
final TableSelectionModel<S> sm = getSelectionModel();
final FocusModel<M> fm = getFocusModel();
return (isFocusDriven && fm.getFocusedIndex() == index)
|| (! isFocusDriven && sm.getSelectedIndex() == index);
}
private void updateVisibleColumnCount() {
visibleColCount = getVisibleLeafColumns().size();
updatePlaceholderRegionVisibility();
requestRebuildCells();
}
private void updateVisibleLeafColumnWidthListeners(
List<? extends TC> added, List<? extends TC> removed) {
for (int i = 0, max = removed.size(); i < max; i++) {
TC tc = removed.get(i);
tc.widthProperty().removeListener(weakWidthListener);
}
for (int i = 0, max = added.size(); i < max; i++) {
TC tc = added.get(i);
tc.widthProperty().addListener(weakWidthListener);
}
requestRebuildCells();
}
final void updatePlaceholderRegionVisibility() {
boolean visible = visibleColCount == 0 || getItemCount() == 0;
if (visible) {
if (placeholderRegion == null) {
placeholderRegion = new StackPane();
placeholderRegion.getStyleClass().setAll("placeholder");
getChildren().add(placeholderRegion);
}
Node placeholderNode = TableSkinUtils.placeholderProperty(this).get();
if (placeholderNode == null) {
if (placeholderLabel == null) {
placeholderLabel = new Label();
}
String s = visibleColCount == 0 ? NO_COLUMNS_TEXT : EMPTY_TABLE_TEXT;
placeholderLabel.setText(s);
placeholderRegion.getChildren().setAll(placeholderLabel);
} else {
placeholderRegion.getChildren().setAll(placeholderNode);
}
}
flow.setVisible(! visible);
if (placeholderRegion != null) {
placeholderRegion.setVisible(visible);
}
}
private void updateContentWidth() {
double contentWidth = flow.getWidth();
if (flow.getVbar().isVisible()) {
contentWidth -= flow.getVbar().getWidth();
}
if (contentWidth <= 0) {
Control c = getSkinnable();
contentWidth = c.getWidth() - (snappedLeftInset() + snappedRightInset());
}
contentWidth = Math.max(0.0, contentWidth);
getSkinnable().getProperties().put("TableView.contentWidth", Math.floor(contentWidth));
}
private void refreshView() {
markItemCountDirty();
Control c = getSkinnable();
if (c != null) {
c.requestLayout();
}
}
public void scrollHorizontally() {
TableFocusModel<M, ?> fm = getFocusModel();
if (fm == null) return;
TC col = getFocusedCell().getTableColumn();
scrollHorizontally(col);
}
protected void scrollHorizontally(TC col) {
if (col == null || !col.isVisible()) return;
final Control control = getSkinnable();
TableColumnHeader header = tableHeaderRow.getColumnHeaderFor(col);
if (header == null || header.getWidth() <= 0) {
Platform.runLater(() -> scrollHorizontally(col));
return;
}
double start = 0;
for (TC c : getVisibleLeafColumns()) {
if (c.equals(col)) break;
start += c.getWidth();
}
double end = start + col.getWidth();
double headerWidth = control.getWidth() - snappedLeftInset() - snappedRightInset();
double pos = flow.getHbar().getValue();
double max = flow.getHbar().getMax();
double newPos;
if (start < pos && start >= 0) {
newPos = start;
} else {
double delta = start < 0 || end > headerWidth ? start - pos : 0;
newPos = pos + delta > max ? max : pos + delta;
}
flow.getHbar().setValue(newPos);
}
private boolean isCellSelected(int row) {
TableSelectionModel<S> sm = getSelectionModel();
if (sm == null) return false;
if (! sm.isCellSelectionEnabled()) return false;
int columnCount = getVisibleLeafColumns().size();
for (int col = 0; col < columnCount; col++) {
if (sm.isSelected(row, TableSkinUtils.getVisibleLeafColumn(this,col))) {
return true;
}
}
return false;
}
private boolean isCellFocused(int row) {
TableFocusModel<S,TC> fm = (TableFocusModel<S,TC>)(Object)getFocusModel();
if (fm == null) return false;
int columnCount = getVisibleLeafColumns().size();
for (int col = 0; col < columnCount; col++) {
if (fm.isFocused(row, TableSkinUtils.getVisibleLeafColumn(this,col))) {
return true;
}
}
return false;
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM: {
TableFocusModel<M,?> fm = getFocusModel();
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
case CELL_AT_ROW_COLUMN: {
int rowIndex = (Integer)parameters[0];
return flow.getPrivateCell(rowIndex);
}
case COLUMN_AT_INDEX: {
int index = (Integer)parameters[0];
TableColumnBase<S,?> column = TableSkinUtils.getVisibleLeafColumn(this,index);
return getTableHeaderRow().getColumnHeaderFor(column);
}
case HEADER: {
return getTableHeaderRow();
}
case VERTICAL_SCROLLBAR: return flow.getVbar();
case HORIZONTAL_SCROLLBAR: return flow.getHbar();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
