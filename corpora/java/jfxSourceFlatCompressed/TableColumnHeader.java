package javafx.scene.control.skin;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.TableColumnBaseHelper;
import com.sun.javafx.scene.control.TreeTableViewBackingList;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static com.sun.javafx.scene.control.TableColumnSortTypeWrapper.getSortTypeName;
import static com.sun.javafx.scene.control.TableColumnSortTypeWrapper.getSortTypeProperty;
import static com.sun.javafx.scene.control.TableColumnSortTypeWrapper.isAscending;
import static com.sun.javafx.scene.control.TableColumnSortTypeWrapper.isDescending;
import static com.sun.javafx.scene.control.TableColumnSortTypeWrapper.setSortType;
public class TableColumnHeader extends Region {
static final String DEFAULT_STYLE_CLASS = "column-header";
static final double DEFAULT_COLUMN_WIDTH = 80.0F;
private boolean autoSizeComplete = false;
private double dragOffset;
private NestedTableColumnHeader nestedColumnHeader;
private TableHeaderRow tableHeaderRow;
private NestedTableColumnHeader parentHeader;
Label label;
int sortPos = -1;
private Region arrow;
private Label sortOrderLabel;
private HBox sortOrderDots;
private Node sortArrow;
private boolean isSortColumn;
private boolean isSizeDirty = false;
boolean isLastVisibleColumn = false;
int columnIndex = -1;
private int newColumnPos;
Region columnReorderLine;
public TableColumnHeader(final TableColumnBase tc) {
setTableColumn(tc);
setFocusTraversable(false);
initStyleClasses();
initUI();
changeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
changeListenerHandler.registerChangeListener(sceneProperty(), e -> updateScene());
if (getTableColumn() != null) {
changeListenerHandler.registerChangeListener(tc.idProperty(), e -> setId(tc.getId()));
changeListenerHandler.registerChangeListener(tc.styleProperty(), e -> setStyle(tc.getStyle()));
changeListenerHandler.registerChangeListener(tc.widthProperty(), e -> {
isSizeDirty = true;
requestLayout();
});
changeListenerHandler.registerChangeListener(tc.visibleProperty(), e -> setVisible(getTableColumn().isVisible()));
changeListenerHandler.registerChangeListener(tc.sortNodeProperty(), e -> updateSortGrid());
changeListenerHandler.registerChangeListener(tc.sortableProperty(), e -> {
if (TableSkinUtils.getSortOrder(getTableSkin()).contains(getTableColumn())) {
NestedTableColumnHeader root = getTableHeaderRow().getRootHeader();
updateAllHeaders(root);
}
});
changeListenerHandler.registerChangeListener(tc.textProperty(), e -> label.setText(tc.getText()));
changeListenerHandler.registerChangeListener(tc.graphicProperty(), e -> label.setGraphic(tc.getGraphic()));
setId(tc.getId());
setStyle(tc.getStyle());
setAccessibleRole(AccessibleRole.TABLE_COLUMN);
}
}
final LambdaMultiplePropertyChangeListenerHandler changeListenerHandler;
private ListChangeListener<TableColumnBase<?,?>> sortOrderListener = c -> {
updateSortPosition();
};
private ListChangeListener<TableColumnBase<?,?>> visibleLeafColumnsListener = c -> {
updateColumnIndex();
updateSortPosition();
};
private ListChangeListener<String> styleClassListener = c -> {
while (c.next()) {
if (c.wasRemoved()) {
getStyleClass().removeAll(c.getRemoved());
}
if (c.wasAdded()) {
getStyleClass().addAll(c.getAddedSubList());
}
}
};
private WeakListChangeListener<TableColumnBase<?,?>> weakSortOrderListener =
new WeakListChangeListener<TableColumnBase<?,?>>(sortOrderListener);
private final WeakListChangeListener<TableColumnBase<?,?>> weakVisibleLeafColumnsListener =
new WeakListChangeListener<TableColumnBase<?,?>>(visibleLeafColumnsListener);
private final WeakListChangeListener<String> weakStyleClassListener =
new WeakListChangeListener<String>(styleClassListener);
private static final EventHandler<MouseEvent> mousePressedHandler = me -> {
TableColumnHeader header = (TableColumnHeader) me.getSource();
TableColumnBase tableColumn = header.getTableColumn();
ContextMenu menu = tableColumn.getContextMenu();
if (menu != null && menu.isShowing()) {
menu.hide();
}
if (me.isConsumed()) return;
me.consume();
header.getTableHeaderRow().columnDragLock = true;
header.getTableSkin().getSkinnable().requestFocus();
if (me.isPrimaryButtonDown() && header.isColumnReorderingEnabled()) {
header.columnReorderingStarted(me.getX());
}
};
private static final EventHandler<MouseEvent> mouseDraggedHandler = me -> {
if (me.isConsumed()) return;
me.consume();
TableColumnHeader header = (TableColumnHeader) me.getSource();
if (me.isPrimaryButtonDown() && header.isColumnReorderingEnabled()) {
header.columnReordering(me.getSceneX(), me.getSceneY());
}
};
private static final EventHandler<MouseEvent> mouseReleasedHandler = me -> {
TableColumnHeader header = (TableColumnHeader) me.getSource();
header.getTableHeaderRow().columnDragLock = false;
if (me.isPopupTrigger()) return;
if (me.isConsumed()) return;
me.consume();
if (header.getTableHeaderRow().isReordering() && header.isColumnReorderingEnabled()) {
header.columnReorderingComplete();
} else if (me.isStillSincePress()) {
header.sortColumn(me.isShiftDown());
}
};
private static final EventHandler<ContextMenuEvent> contextMenuRequestedHandler = me -> {
TableColumnHeader header = (TableColumnHeader) me.getSource();
TableColumnBase tableColumn = header.getTableColumn();
ContextMenu menu = tableColumn.getContextMenu();
if (menu != null) {
menu.show(header, me.getScreenX(), me.getScreenY());
me.consume();
}
};
private DoubleProperty size;
private final double getSize() {
return size == null ? 20.0 : size.doubleValue();
}
private final DoubleProperty sizeProperty() {
if (size == null) {
size = new StyleableDoubleProperty(20) {
@Override
protected void invalidated() {
double value = get();
if (value <= 0) {
if (isBound()) {
unbind();
}
set(20);
throw new IllegalArgumentException("Size cannot be 0 or negative");
}
}
@Override public Object getBean() {
return TableColumnHeader.this;
}
@Override public String getName() {
return "size";
}
@Override public CssMetaData<TableColumnHeader,Number> getCssMetaData() {
return StyleableProperties.SIZE;
}
};
}
return size;
}
private ReadOnlyObjectWrapper<TableColumnBase<?,?>> tableColumn = new ReadOnlyObjectWrapper<>(this, "tableColumn");
private final void setTableColumn(TableColumnBase<?,?> column) {
tableColumn.set(column);
}
public final TableColumnBase<?,?> getTableColumn() {
return tableColumn.get();
}
public final ReadOnlyObjectProperty<TableColumnBase<?,?>> tableColumnProperty() {
return tableColumn.getReadOnlyProperty();
}
@Override protected void layoutChildren() {
if (isSizeDirty) {
resize(getTableColumn().getWidth(), getHeight());
isSizeDirty = false;
}
double sortWidth = 0;
double w = snapSizeX(getWidth()) - (snappedLeftInset() + snappedRightInset());
double h = getHeight() - (snappedTopInset() + snappedBottomInset());
double x = w;
if (arrow != null) {
arrow.setMaxSize(arrow.prefWidth(-1), arrow.prefHeight(-1));
}
if (sortArrow != null && sortArrow.isVisible()) {
sortWidth = sortArrow.prefWidth(-1);
x -= sortWidth;
sortArrow.resize(sortWidth, sortArrow.prefHeight(-1));
positionInArea(sortArrow, x, snappedTopInset(),
sortWidth, h, 0, HPos.CENTER, VPos.CENTER);
}
if (label != null) {
double labelWidth = w - sortWidth;
label.resizeRelocate(snappedLeftInset(), 0, labelWidth, getHeight());
}
}
@Override protected double computePrefWidth(double height) {
if (getNestedColumnHeader() != null) {
double width = getNestedColumnHeader().prefWidth(height);
if (getTableColumn() != null) {
TableColumnBaseHelper.setWidth(getTableColumn(), width);
}
return width;
} else if (getTableColumn() != null && getTableColumn().isVisible()) {
return snapSizeX(getTableColumn().getWidth());
}
return 0;
}
@Override protected double computeMinHeight(double width) {
return label == null ? 0 : label.minHeight(width);
}
@Override protected double computePrefHeight(double width) {
if (getTableColumn() == null) return 0;
return Math.max(getSize(), label.prefHeight(-1));
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case INDEX: return getIndex(getTableColumn());
case TEXT: return getTableColumn() != null ? getTableColumn().getText() : null;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
void initStyleClasses() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
installTableColumnStyleClassListener();
}
void installTableColumnStyleClassListener() {
TableColumnBase tc = getTableColumn();
if (tc != null) {
getStyleClass().addAll(tc.getStyleClass());
tc.getStyleClass().addListener(weakStyleClassListener);
}
}
NestedTableColumnHeader getNestedColumnHeader() { return nestedColumnHeader; }
void setNestedColumnHeader(NestedTableColumnHeader nch) { nestedColumnHeader = nch; }
protected TableHeaderRow getTableHeaderRow() {
return tableHeaderRow;
}
void setTableHeaderRow(TableHeaderRow thr) {
tableHeaderRow = thr;
updateTableSkin();
}
private void updateTableSkin() {
TableViewSkinBase<?,?,?,?,?> tableSkin = getTableSkin();
if (tableSkin == null) return;
updateColumnIndex();
this.columnReorderLine = tableSkin.getColumnReorderLine();
if (getTableColumn() != null) {
updateSortPosition();
TableSkinUtils.getSortOrder(tableSkin).addListener(weakSortOrderListener);
TableSkinUtils.getVisibleLeafColumns(tableSkin).addListener(weakVisibleLeafColumnsListener);
}
}
protected TableViewSkinBase<?, ?, ?, ?, ?> getTableSkin() {
return tableHeaderRow == null ? null : tableHeaderRow.tableSkin;
}
NestedTableColumnHeader getParentHeader() { return parentHeader; }
void setParentHeader(NestedTableColumnHeader ph) { parentHeader = ph; }
private void updateAllHeaders(TableColumnHeader header) {
if (header instanceof NestedTableColumnHeader) {
List<TableColumnHeader> children = ((NestedTableColumnHeader)header).getColumnHeaders();
for (int i = 0; i < children.size(); i++) {
updateAllHeaders(children.get(i));
}
} else {
header.updateSortPosition();
}
}
private void updateScene() {
final int n = 30;
if (! autoSizeComplete) {
if (getTableColumn() == null || getTableColumn().getWidth() != DEFAULT_COLUMN_WIDTH || getScene() == null) {
return;
}
doColumnAutoSize(n);
autoSizeComplete = true;
}
}
void dispose() {
TableViewSkinBase tableSkin = getTableSkin();
if (tableSkin != null) {
TableSkinUtils.getVisibleLeafColumns(tableSkin).removeListener(weakVisibleLeafColumnsListener);
TableSkinUtils.getSortOrder(tableSkin).removeListener(weakSortOrderListener);
}
changeListenerHandler.dispose();
}
private boolean isSortingEnabled() {
return true;
}
private boolean isColumnReorderingEnabled() {
return !Properties.IS_TOUCH_SUPPORTED && TableSkinUtils.getVisibleLeafColumns(getTableSkin()).size() > 1;
}
private void initUI() {
if (getTableColumn() == null) return;
setOnMousePressed(mousePressedHandler);
setOnMouseDragged(mouseDraggedHandler);
setOnDragDetected(event -> event.consume());
setOnContextMenuRequested(contextMenuRequestedHandler);
setOnMouseReleased(mouseReleasedHandler);
label = new Label();
label.setText(getTableColumn().getText());
label.setGraphic(getTableColumn().getGraphic());
label.setVisible(getTableColumn().isVisible());
if (isSortingEnabled()) {
updateSortGrid();
}
}
private void doColumnAutoSize(int cellsToMeasure) {
double prefWidth = getTableColumn().getPrefWidth();
if (prefWidth == DEFAULT_COLUMN_WIDTH) {
resizeColumnToFitContent(cellsToMeasure);
}
}
protected void resizeColumnToFitContent(int maxRows) {
TableColumnBase<?, ?> tc = getTableColumn();
if (!tc.isResizable()) return;
Object control = this.getTableSkin().getSkinnable();
if (control instanceof TableView) {
resizeColumnToFitContent((TableView) control, (TableColumn) tc, this.getTableSkin(), maxRows);
} else if (control instanceof TreeTableView) {
resizeColumnToFitContent((TreeTableView) control, (TreeTableColumn) tc, this.getTableSkin(), maxRows);
}
}
private <T,S> void resizeColumnToFitContent(TableView<T> tv, TableColumn<T, S> tc, TableViewSkinBase tableSkin, int maxRows) {
List<?> items = tv.getItems();
if (items == null || items.isEmpty()) return;
Callback cellFactory = tc.getCellFactory();
if (cellFactory == null) return;
TableCell<T,?> cell = (TableCell<T, ?>) cellFactory.call(tc);
if (cell == null) return;
cell.getProperties().put(Properties.DEFER_TO_PARENT_PREF_WIDTH, Boolean.TRUE);
double padding = 10;
Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
if (n instanceof Region) {
Region r = (Region) n;
padding = r.snappedLeftInset() + r.snappedRightInset();
}
Callback<TableView<T>, TableRow<T>> rowFactory = tv.getRowFactory();
TableRow<T> tableRow = createMeasureRow(tv, tableSkin, rowFactory);
((SkinBase<?>) tableRow.getSkin()).getChildren().add(cell);
int rows = maxRows == -1 ? items.size() : Math.min(items.size(), maxRows);
double maxWidth = 0;
for (int row = 0; row < rows; row++) {
tableRow.updateIndex(row);
cell.updateTableColumn(tc);
cell.updateTableView(tv);
cell.updateTableRow(tableRow);
cell.updateIndex(row);
if ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null) {
tableRow.applyCss();
maxWidth = Math.max(maxWidth, cell.prefWidth(-1));
tableSkin.getChildren().remove(cell);
}
}
cell.updateIndex(-1);
TableColumnHeader header = tableSkin.getTableHeaderRow().getColumnHeaderFor(tc);
double headerTextWidth = Utils.computeTextWidth(header.label.getFont(), tc.getText(), -1);
Node graphic = header.label.getGraphic();
double headerGraphicWidth = graphic == null ? 0 : graphic.prefWidth(-1) + header.label.getGraphicTextGap();
double headerWidth = headerTextWidth + headerGraphicWidth + 10 + header.snappedLeftInset() + header.snappedRightInset();
maxWidth = Math.max(maxWidth, headerWidth);
maxWidth += padding;
if (tv.getColumnResizePolicy() == TableView.CONSTRAINED_RESIZE_POLICY && tv.getWidth() > 0) {
if (maxWidth > tc.getMaxWidth()) {
maxWidth = tc.getMaxWidth();
}
int size = tc.getColumns().size();
if (size > 0) {
TableColumnHeader columnHeader = getTableHeaderRow().getColumnHeaderFor(tc.getColumns().get(size - 1));
if (columnHeader != null) {
columnHeader.resizeColumnToFitContent(maxRows);
}
return;
}
TableSkinUtils.resizeColumn(tableSkin, tc, Math.round(maxWidth - tc.getWidth()));
} else {
TableColumnBaseHelper.setWidth(tc, maxWidth);
}
}
private <T> TableRow<T> createMeasureRow(TableView<T> tv, TableViewSkinBase tableSkin,
Callback<TableView<T>, TableRow<T>> rowFactory) {
TableRow<T> tableRow = rowFactory != null ? rowFactory.call(tv) : new TableRow<>();
tableSkin.getChildren().add(tableRow);
tableRow.applyCss();
if (!(tableRow.getSkin() instanceof SkinBase<?>)) {
tableSkin.getChildren().remove(tableRow);
tableRow = createMeasureRow(tv, tableSkin, null);
}
return tableRow;
}
private <T,S> void resizeColumnToFitContent(TreeTableView<T> ttv, TreeTableColumn<T, S> tc, TableViewSkinBase tableSkin, int maxRows) {
List<?> items = new TreeTableViewBackingList(ttv);
if (items == null || items.isEmpty()) return;
Callback cellFactory = tc.getCellFactory();
if (cellFactory == null) return;
TreeTableCell<T,S> cell = (TreeTableCell) cellFactory.call(tc);
if (cell == null) return;
cell.getProperties().put(Properties.DEFER_TO_PARENT_PREF_WIDTH, Boolean.TRUE);
double padding = 10;
Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
if (n instanceof Region) {
Region r = (Region) n;
padding = r.snappedLeftInset() + r.snappedRightInset();
}
Callback<TreeTableView<T>, TreeTableRow<T>> rowFactory = ttv.getRowFactory();
TreeTableRow<T> treeTableRow = createMeasureRow(ttv, tableSkin, rowFactory);
((SkinBase<?>) treeTableRow.getSkin()).getChildren().add(cell);
int rows = maxRows == -1 ? items.size() : Math.min(items.size(), maxRows);
double maxWidth = 0;
for (int row = 0; row < rows; row++) {
treeTableRow.updateIndex(row);
treeTableRow.updateTreeItem(ttv.getTreeItem(row));
cell.updateTableColumn(tc);
cell.updateTreeTableView(ttv);
cell.updateTableRow(treeTableRow);
cell.updateIndex(row);
if ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null) {
treeTableRow.applyCss();
double w = cell.prefWidth(-1);
maxWidth = Math.max(maxWidth, w);
tableSkin.getChildren().remove(cell);
}
}
cell.updateIndex(-1);
TableColumnHeader header = tableSkin.getTableHeaderRow().getColumnHeaderFor(tc);
double headerTextWidth = Utils.computeTextWidth(header.label.getFont(), tc.getText(), -1);
Node graphic = header.label.getGraphic();
double headerGraphicWidth = graphic == null ? 0 : graphic.prefWidth(-1) + header.label.getGraphicTextGap();
double headerWidth = headerTextWidth + headerGraphicWidth + 10 + header.snappedLeftInset() + header.snappedRightInset();
maxWidth = Math.max(maxWidth, headerWidth);
maxWidth += padding;
if (ttv.getColumnResizePolicy() == TreeTableView.CONSTRAINED_RESIZE_POLICY && ttv.getWidth() > 0) {
if (maxWidth > tc.getMaxWidth()) {
maxWidth = tc.getMaxWidth();
}
int size = tc.getColumns().size();
if (size > 0) {
TableColumnHeader columnHeader = getTableHeaderRow().getColumnHeaderFor(tc.getColumns().get(size - 1));
if (columnHeader != null) {
columnHeader.resizeColumnToFitContent(maxRows);
}
return;
}
TableSkinUtils.resizeColumn(tableSkin, tc, Math.round(maxWidth - tc.getWidth()));
} else {
TableColumnBaseHelper.setWidth(tc, maxWidth);
}
}
private <T> TreeTableRow<T> createMeasureRow(TreeTableView<T> ttv, TableViewSkinBase tableSkin,
Callback<TreeTableView<T>, TreeTableRow<T>> rowFactory) {
TreeTableRow<T> treeTableRow = rowFactory != null ? rowFactory.call(ttv) : new TreeTableRow<>();
tableSkin.getChildren().add(treeTableRow);
treeTableRow.applyCss();
if (!(treeTableRow.getSkin() instanceof SkinBase<?>)) {
tableSkin.getChildren().remove(treeTableRow);
treeTableRow = createMeasureRow(ttv, tableSkin, null);
}
return treeTableRow;
}
private void updateSortPosition() {
this.sortPos = ! getTableColumn().isSortable() ? -1 : getSortPosition();
updateSortGrid();
}
private void updateSortGrid() {
if (this instanceof NestedTableColumnHeader) return;
getChildren().clear();
getChildren().add(label);
if (! isSortingEnabled()) return;
isSortColumn = sortPos != -1;
if (! isSortColumn) {
if (sortArrow != null) {
sortArrow.setVisible(false);
}
return;
}
int visibleLeafIndex = TableSkinUtils.getVisibleLeafIndex(getTableSkin(), getTableColumn());
if (visibleLeafIndex == -1) return;
final int sortColumnCount = getVisibleSortOrderColumnCount();
boolean showSortOrderDots = sortPos <= 3 && sortColumnCount > 1;
Node _sortArrow = null;
if (getTableColumn().getSortNode() != null) {
_sortArrow = getTableColumn().getSortNode();
getChildren().add(_sortArrow);
} else {
GridPane sortArrowGrid = new GridPane();
_sortArrow = sortArrowGrid;
sortArrowGrid.setPadding(new Insets(0, 3, 0, 0));
getChildren().add(sortArrowGrid);
if (arrow == null) {
arrow = new Region();
arrow.getStyleClass().setAll("arrow");
arrow.setVisible(true);
arrow.setRotate(isAscending(getTableColumn()) ? 180.0F : 0.0F);
changeListenerHandler.registerChangeListener(getSortTypeProperty(getTableColumn()), e -> {
updateSortGrid();
if (arrow != null) {
arrow.setRotate(isAscending(getTableColumn()) ? 180 : 0.0);
}
});
}
arrow.setVisible(isSortColumn);
if (sortPos > 2) {
if (sortOrderLabel == null) {
sortOrderLabel = new Label();
sortOrderLabel.getStyleClass().add("sort-order");
}
sortOrderLabel.setText("" + (sortPos + 1));
sortOrderLabel.setVisible(sortColumnCount > 1);
sortArrowGrid.add(arrow, 1, 1);
GridPane.setHgrow(arrow, Priority.NEVER);
GridPane.setVgrow(arrow, Priority.NEVER);
sortArrowGrid.add(sortOrderLabel, 2, 1);
} else if (showSortOrderDots) {
if (sortOrderDots == null) {
sortOrderDots = new HBox(0);
sortOrderDots.getStyleClass().add("sort-order-dots-container");
}
boolean isAscending = isAscending(getTableColumn());
int arrowRow = isAscending ? 1 : 2;
int dotsRow = isAscending ? 2 : 1;
sortArrowGrid.add(arrow, 1, arrowRow);
GridPane.setHalignment(arrow, HPos.CENTER);
sortArrowGrid.add(sortOrderDots, 1, dotsRow);
updateSortOrderDots(sortPos);
} else {
sortArrowGrid.add(arrow, 1, 1);
GridPane.setHgrow(arrow, Priority.NEVER);
GridPane.setVgrow(arrow, Priority.ALWAYS);
}
}
sortArrow = _sortArrow;
if (sortArrow != null) {
sortArrow.setVisible(isSortColumn);
}
requestLayout();
}
private void updateSortOrderDots(int sortPos) {
double arrowWidth = arrow.prefWidth(-1);
sortOrderDots.getChildren().clear();
for (int i = 0; i <= sortPos; i++) {
Region r = new Region();
r.getStyleClass().add("sort-order-dot");
String sortTypeName = getSortTypeName(getTableColumn());
if (sortTypeName != null && ! sortTypeName.isEmpty()) {
r.getStyleClass().add(sortTypeName.toLowerCase(Locale.ROOT));
}
sortOrderDots.getChildren().add(r);
if (i < sortPos) {
Region spacer = new Region();
double lp = sortPos == 1 ? 1 : 0;
spacer.setPadding(new Insets(0, 1, 0, lp));
sortOrderDots.getChildren().add(spacer);
}
}
sortOrderDots.setAlignment(Pos.TOP_CENTER);
sortOrderDots.setMaxWidth(arrowWidth);
}
void moveColumn(TableColumnBase column, final int newColumnPos) {
if (column == null || newColumnPos < 0) return;
ObservableList<TableColumnBase<?,?>> columns = getColumns(column);
final int columnsCount = columns.size();
final int currentPos = columns.indexOf(column);
int actualNewColumnPos = newColumnPos;
final int requiredVisibleColumns = actualNewColumnPos;
int visibleColumnsSeen = 0;
for (int i = 0; i < columnsCount; i++) {
if (visibleColumnsSeen == (requiredVisibleColumns + 1)) {
break;
}
if (columns.get(i).isVisible()) {
visibleColumnsSeen++;
} else {
actualNewColumnPos++;
}
}
if (actualNewColumnPos >= columnsCount) {
actualNewColumnPos = columnsCount - 1;
} else if (actualNewColumnPos < 0) {
actualNewColumnPos = 0;
}
if (actualNewColumnPos == currentPos) return;
List<TableColumnBase<?,?>> tempList = new ArrayList<>(columns);
tempList.remove(column);
tempList.add(actualNewColumnPos, column);
columns.setAll(tempList);
}
private ObservableList<TableColumnBase<?,?>> getColumns(TableColumnBase column) {
return column.getParentColumn() == null ?
TableSkinUtils.getColumns(getTableSkin()) :
column.getParentColumn().getColumns();
}
private int getIndex(TableColumnBase<?,?> column) {
if (column == null) return -1;
ObservableList<? extends TableColumnBase<?,?>> columns = getColumns(column);
int index = -1;
for (int i = 0; i < columns.size(); i++) {
TableColumnBase<?,?> _column = columns.get(i);
if (! _column.isVisible()) continue;
index++;
if (column.equals(_column)) break;
}
return index;
}
private void updateColumnIndex() {
TableColumnBase tc = getTableColumn();
TableViewSkinBase tableSkin = getTableSkin();
columnIndex = tableSkin == null || tc == null ? -1 :TableSkinUtils.getVisibleLeafIndex(tableSkin,tc);
isLastVisibleColumn = getTableColumn() != null &&
columnIndex != -1 &&
columnIndex == TableSkinUtils.getVisibleLeafColumns(tableSkin).size() - 1;
pseudoClassStateChanged(PSEUDO_CLASS_LAST_VISIBLE, isLastVisibleColumn);
}
private void sortColumn(final boolean addColumn) {
if (! isSortingEnabled()) return;
if (getTableColumn() == null || getTableColumn().getColumns().size() != 0 || getTableColumn().getComparator() == null || !getTableColumn().isSortable()) return;
final ObservableList<TableColumnBase<?,?>> sortOrder = TableSkinUtils.getSortOrder(getTableSkin());
if (addColumn) {
if (!isSortColumn) {
setSortType(getTableColumn(), TableColumn.SortType.ASCENDING);
sortOrder.add(getTableColumn());
} else if (isAscending(getTableColumn())) {
setSortType(getTableColumn(), TableColumn.SortType.DESCENDING);
} else {
int i = sortOrder.indexOf(getTableColumn());
if (i != -1) {
sortOrder.remove(i);
}
}
} else {
if (isSortColumn && sortOrder.size() == 1) {
if (isAscending(getTableColumn())) {
setSortType(getTableColumn(), TableColumn.SortType.DESCENDING);
} else {
sortOrder.remove(getTableColumn());
}
} else if (isSortColumn) {
if (isAscending(getTableColumn())) {
setSortType(getTableColumn(), TableColumn.SortType.DESCENDING);
} else if (isDescending(getTableColumn())) {
setSortType(getTableColumn(), TableColumn.SortType.ASCENDING);
}
List<TableColumnBase<?,?>> sortOrderCopy = new ArrayList<TableColumnBase<?,?>>(sortOrder);
sortOrderCopy.remove(getTableColumn());
sortOrderCopy.add(0, getTableColumn());
sortOrder.setAll(getTableColumn());
} else {
setSortType(getTableColumn(), TableColumn.SortType.ASCENDING);
sortOrder.setAll(getTableColumn());
}
}
}
private int getSortPosition() {
if (getTableColumn() == null) {
return -1;
}
final List<TableColumnBase> sortOrder = getVisibleSortOrderColumns();
int pos = 0;
for (int i = 0; i < sortOrder.size(); i++) {
TableColumnBase _tc = sortOrder.get(i);
if (getTableColumn().equals(_tc)) {
return pos;
}
pos++;
}
return -1;
}
private List<TableColumnBase> getVisibleSortOrderColumns() {
final ObservableList<TableColumnBase<?,?>> sortOrder = TableSkinUtils.getSortOrder(getTableSkin());
List<TableColumnBase> visibleSortOrderColumns = new ArrayList<>();
for (int i = 0; i < sortOrder.size(); i++) {
TableColumnBase _tc = sortOrder.get(i);
if (_tc == null || ! _tc.isSortable() || ! _tc.isVisible()) {
continue;
}
visibleSortOrderColumns.add(_tc);
}
return visibleSortOrderColumns;
}
private int getVisibleSortOrderColumnCount() {
return getVisibleSortOrderColumns().size();
}
void columnReorderingStarted(double dragOffset) {
if (! getTableColumn().isReorderable()) return;
this.dragOffset = dragOffset;
getTableHeaderRow().setReorderingColumn(getTableColumn());
getTableHeaderRow().setReorderingRegion(this);
}
void columnReordering(double sceneX, double sceneY) {
if (! getTableColumn().isReorderable()) return;
getTableHeaderRow().setReordering(true);
TableColumnHeader hoverHeader = null;
final double x = getParentHeader().sceneToLocal(sceneX, sceneY).getX();
double dragX = getTableSkin().getSkinnable().sceneToLocal(sceneX, sceneY).getX() - dragOffset;
getTableHeaderRow().setDragHeaderX(dragX);
double startX = 0;
double endX = 0;
double headersWidth = 0;
newColumnPos = 0;
for (TableColumnHeader header : getParentHeader().getColumnHeaders()) {
if (! header.isVisible()) continue;
double headerWidth = header.prefWidth(-1);
headersWidth += headerWidth;
startX = header.getBoundsInParent().getMinX();
endX = startX + headerWidth;
if (x >= startX && x < endX) {
hoverHeader = header;
break;
}
newColumnPos++;
}
if (hoverHeader == null) {
newColumnPos = x > headersWidth ? (getParentHeader().getColumns().size() - 1) : 0;
return;
}
double midPoint = startX + (endX - startX) / 2;
boolean beforeMidPoint = x <= midPoint;
int currentPos = getIndex(getTableColumn());
newColumnPos += newColumnPos > currentPos && beforeMidPoint ?
-1 : (newColumnPos < currentPos && !beforeMidPoint ? 1 : 0);
double lineX = getTableHeaderRow().sceneToLocal(hoverHeader.localToScene(hoverHeader.getBoundsInLocal())).getMinX();
lineX = lineX + ((beforeMidPoint) ? (0) : (hoverHeader.getWidth()));
if (lineX >= -0.5 && lineX <= getTableSkin().getSkinnable().getWidth()) {
columnReorderLine.setTranslateX(lineX);
columnReorderLine.setVisible(true);
}
getTableHeaderRow().setReordering(true);
}
void columnReorderingComplete() {
if (! getTableColumn().isReorderable()) return;
moveColumn(getTableColumn(), newColumnPos);
columnReorderLine.setTranslateX(0.0F);
columnReorderLine.setLayoutX(0.0F);
newColumnPos = 0;
getTableHeaderRow().setReordering(false);
columnReorderLine.setVisible(false);
getTableHeaderRow().setReorderingColumn(null);
getTableHeaderRow().setReorderingRegion(null);
dragOffset = 0.0F;
}
double getDragRectHeight() {
return getHeight();
}
boolean represents(TableColumnBase<?, ?> column) {
if (!column.getColumns().isEmpty()) {
return false;
}
return column == getTableColumn();
}
private static final PseudoClass PSEUDO_CLASS_LAST_VISIBLE =
PseudoClass.getPseudoClass("last-visible");
private static class StyleableProperties {
private static final CssMetaData<TableColumnHeader,Number> SIZE =
new CssMetaData<TableColumnHeader,Number>("-fx-size",
SizeConverter.getInstance(), 20.0) {
@Override
public boolean isSettable(TableColumnHeader n) {
return n.size == null || !n.size.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TableColumnHeader n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.sizeProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(SIZE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
}
