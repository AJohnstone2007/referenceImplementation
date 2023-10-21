package javafx.scene.control.skin;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
public class NestedTableColumnHeader extends TableColumnHeader {
static final String DEFAULT_STYLE_CLASS = "nested-column-header";
private static final int DRAG_RECT_WIDTH = 4;
private static final String TABLE_COLUMN_KEY = "TableColumn";
private static final String TABLE_COLUMN_HEADER_KEY = "TableColumnHeader";
private ObservableList<? extends TableColumnBase> columns;
private TableColumnHeader label;
private ObservableList<TableColumnHeader> columnHeaders;
private ObservableList<TableColumnHeader> unmodifiableColumnHeaders;
private double lastX = 0.0F;
private double dragAnchorX = 0.0;
private Map<TableColumnBase<?,?>, Rectangle> dragRects = new WeakHashMap<>();
boolean updateColumns = true;
public NestedTableColumnHeader(final TableColumnBase tc) {
super(tc);
setFocusTraversable(false);
label = createTableColumnHeader(getTableColumn());
label.setTableHeaderRow(getTableHeaderRow());
label.setParentHeader(getParentHeader());
label.setNestedColumnHeader(this);
if (getTableColumn() != null) {
changeListenerHandler.registerChangeListener(getTableColumn().textProperty(), e ->
label.setVisible(getTableColumn().getText() != null && ! getTableColumn().getText().isEmpty()));
}
}
private final ListChangeListener<TableColumnBase> columnsListener = c -> {
setHeadersNeedUpdate();
};
private final WeakListChangeListener weakColumnsListener =
new WeakListChangeListener(columnsListener);
private static final EventHandler<MouseEvent> rectMousePressed = me -> {
Rectangle rect = (Rectangle) me.getSource();
TableColumnBase column = (TableColumnBase) rect.getProperties().get(TABLE_COLUMN_KEY);
NestedTableColumnHeader header = (NestedTableColumnHeader) rect.getProperties().get(TABLE_COLUMN_HEADER_KEY);
if (! header.isColumnResizingEnabled()) return;
if (header.getTableHeaderRow().columnDragLock) return;
if (me.isConsumed()) return;
me.consume();
if (me.getClickCount() == 2 && me.isPrimaryButtonDown()) {
TableHeaderRow tableHeader = header.getTableHeaderRow();
TableColumnHeader columnHeader = tableHeader.getColumnHeaderFor(column);
if (columnHeader != null) {
columnHeader.resizeColumnToFitContent(-1);
}
} else {
Rectangle innerRect = (Rectangle) me.getSource();
double startX = header.getTableHeaderRow().sceneToLocal(innerRect.localToScene(innerRect.getBoundsInLocal())).getMinX() + 2;
header.dragAnchorX = me.getSceneX();
header.columnResizingStarted(startX);
}
};
private static final EventHandler<MouseEvent> rectMouseDragged = me -> {
Rectangle rect = (Rectangle) me.getSource();
TableColumnBase column = (TableColumnBase) rect.getProperties().get(TABLE_COLUMN_KEY);
NestedTableColumnHeader header = (NestedTableColumnHeader) rect.getProperties().get(TABLE_COLUMN_HEADER_KEY);
if (! header.isColumnResizingEnabled()) return;
if (header.getTableHeaderRow().columnDragLock) return;
if (me.isConsumed()) return;
me.consume();
header.columnResizing(column, me);
};
private static final EventHandler<MouseEvent> rectMouseReleased = me -> {
Rectangle rect = (Rectangle) me.getSource();
TableColumnBase column = (TableColumnBase) rect.getProperties().get(TABLE_COLUMN_KEY);
NestedTableColumnHeader header = (NestedTableColumnHeader) rect.getProperties().get(TABLE_COLUMN_HEADER_KEY);
if (! header.isColumnResizingEnabled()) return;
if (header.getTableHeaderRow().columnDragLock) return;
if (me.isConsumed()) return;
me.consume();
header.columnResizingComplete(column, me);
};
private static final EventHandler<MouseEvent> rectCursorChangeListener = me -> {
Rectangle rect = (Rectangle) me.getSource();
TableColumnBase column = (TableColumnBase) rect.getProperties().get(TABLE_COLUMN_KEY);
NestedTableColumnHeader header = (NestedTableColumnHeader) rect.getProperties().get(TABLE_COLUMN_HEADER_KEY);
if (header.getTableHeaderRow().columnDragLock) return;
if (header.getCursor() == null) {
rect.setCursor(header.isColumnResizingEnabled() && rect.isHover() &&
column.isResizable() ? Cursor.H_RESIZE : null);
}
};
@Override void dispose() {
super.dispose();
if (label != null) {
label.dispose();
}
if (getColumns() != null) {
getColumns().removeListener(weakColumnsListener);
}
for (int i = 0; i < getColumnHeaders().size(); i++) {
TableColumnHeader header = getColumnHeaders().get(i);
header.dispose();
}
for (Rectangle rect : dragRects.values()) {
if (rect != null) {
rect.visibleProperty().unbind();
}
}
dragRects.clear();
getChildren().clear();
changeListenerHandler.dispose();
}
public final ObservableList<TableColumnHeader> getColumnHeaders() {
if (columnHeaders == null) {
columnHeaders = FXCollections.<TableColumnHeader>observableArrayList();
unmodifiableColumnHeaders = FXCollections.unmodifiableObservableList(columnHeaders);
}
return unmodifiableColumnHeaders;
}
@Override protected void layoutChildren() {
double w = getWidth() - snappedLeftInset() - snappedRightInset();
double h = getHeight() - snappedTopInset() - snappedBottomInset();
int labelHeight = 0;
if (label.isVisible() && getTableColumn() != null) {
labelHeight = (int) label.prefHeight(-1);
label.resize(w, labelHeight);
label.relocate(snappedLeftInset(), snappedTopInset());
}
double x = snappedLeftInset();
final double height = snapSizeY(h - labelHeight);
for (int i = 0, max = getColumnHeaders().size(); i < max; i++) {
TableColumnHeader n = getColumnHeaders().get(i);
if (! n.isVisible()) continue;
double prefWidth = n.prefWidth(height);
n.resize(prefWidth, height);
n.relocate(x, labelHeight + snappedTopInset());
x += prefWidth;
Rectangle dragRect = dragRects.get(n.getTableColumn());
if (dragRect != null) {
dragRect.setHeight(n.getDragRectHeight());
dragRect.relocate(x - DRAG_RECT_WIDTH / 2, snappedTopInset() + labelHeight);
}
}
}
@Override protected double computePrefWidth(double height) {
checkState();
double width = 0.0F;
if (getColumns() != null) {
for (TableColumnHeader c : getColumnHeaders()) {
if (c.isVisible()) {
width += c.computePrefWidth(height);
}
}
}
return width;
}
@Override protected double computePrefHeight(double width) {
checkState();
double height = 0.0F;
if (getColumnHeaders() != null) {
for (TableColumnHeader n : getColumnHeaders()) {
height = Math.max(height, n.prefHeight(-1));
}
}
double labelHeight = 0.0;
if (label.isVisible() && getTableColumn() != null) {
labelHeight = label.prefHeight(-1);
}
return height + labelHeight + snappedTopInset() + snappedBottomInset();
}
protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
return col == null || col.getColumns().isEmpty() || col == getTableColumn() ?
new TableColumnHeader(col) :
new NestedTableColumnHeader(col);
}
@Override void initStyleClasses() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
installTableColumnStyleClassListener();
}
@Override void setTableHeaderRow(TableHeaderRow header) {
super.setTableHeaderRow(header);
if (getTableSkin() != null) {
changeListenerHandler.registerChangeListener(TableSkinUtils.columnResizePolicyProperty(getTableSkin()), e -> updateContent());
}
label.setTableHeaderRow(header);
for (TableColumnHeader c : getColumnHeaders()) {
c.setTableHeaderRow(header);
}
}
@Override void setParentHeader(NestedTableColumnHeader parentHeader) {
super.setParentHeader(parentHeader);
label.setParentHeader(parentHeader);
}
ObservableList<? extends TableColumnBase> getColumns() {
return columns;
}
void setColumns(ObservableList<? extends TableColumnBase> newColumns) {
if (this.columns != null) {
this.columns.removeListener(weakColumnsListener);
}
this.columns = newColumns;
if (this.columns != null) {
this.columns.addListener(weakColumnsListener);
}
}
void updateTableColumnHeaders() {
if (getTableColumn() == null && getTableSkin() != null) {
setColumns(TableSkinUtils.getColumns(getTableSkin()));
} else if (getTableColumn() != null) {
setColumns(getTableColumn().getColumns());
}
if (getColumns().isEmpty()) {
for (int i = 0; i < getColumnHeaders().size(); i++) {
TableColumnHeader header = getColumnHeaders().get(i);
header.dispose();
}
NestedTableColumnHeader parentHeader = getParentHeader();
if (parentHeader != null) {
List<TableColumnHeader> parentColumnHeaders = parentHeader.getColumnHeaders();
int index = parentColumnHeaders.indexOf(this);
if (index >= 0 && index < parentColumnHeaders.size()) {
parentColumnHeaders.set(index, createColumnHeader(getTableColumn()));
}
} else {
columnHeaders.clear();
}
} else {
List<TableColumnHeader> oldHeaders = new ArrayList<>(getColumnHeaders());
List<TableColumnHeader> newHeaders = new ArrayList<>();
for (int i = 0; i < getColumns().size(); i++) {
TableColumnBase<?,?> column = getColumns().get(i);
if (column == null || ! column.isVisible()) continue;
boolean found = false;
for (int j = 0; j < oldHeaders.size(); j++) {
TableColumnHeader oldColumn = oldHeaders.get(j);
if (oldColumn.represents(column)) {
newHeaders.add(oldColumn);
found = true;
break;
}
}
if (!found) {
newHeaders.add(createColumnHeader(column));
}
}
columnHeaders.setAll(newHeaders);
oldHeaders.removeAll(newHeaders);
for (int i = 0; i < oldHeaders.size(); i++) {
oldHeaders.get(i).dispose();
}
}
updateContent();
for (TableColumnHeader header : getColumnHeaders()) {
header.applyCss();
}
}
boolean represents(TableColumnBase<?, ?> column) {
if (column.getColumns().isEmpty()) {
return false;
}
if (column != getTableColumn()) {
return false;
}
final int columnCount = column.getColumns().size();
final int headerCount = getColumnHeaders().size();
if (columnCount != headerCount) {
return false;
}
for (int i = 0; i < columnCount; i++) {
TableColumnBase<?,?> childColumn = column.getColumns().get(i);
TableColumnHeader childHeader = getColumnHeaders().get(i);
if (!childHeader.represents(childColumn)) {
return false;
}
}
return true;
}
@Override double getDragRectHeight() {
return label.prefHeight(-1);
}
void setHeadersNeedUpdate() {
updateColumns = true;
for (int i = 0; i < getColumnHeaders().size(); i++) {
TableColumnHeader header = getColumnHeaders().get(i);
if (header instanceof NestedTableColumnHeader) {
((NestedTableColumnHeader)header).setHeadersNeedUpdate();
}
}
requestLayout();
}
private void updateContent() {
final List<Node> content = new ArrayList<Node>();
content.add(label);
content.addAll(getColumnHeaders());
if (isColumnResizingEnabled()) {
rebuildDragRects();
content.addAll(dragRects.values());
}
getChildren().setAll(content);
}
private void rebuildDragRects() {
if (! isColumnResizingEnabled()) return;
getChildren().removeAll(dragRects.values());
for (Rectangle rect : dragRects.values()) {
rect.visibleProperty().unbind();
}
dragRects.clear();
List<? extends TableColumnBase> columns = getColumns();
if (columns == null) {
return;
}
boolean isConstrainedResize = false;
TableViewSkinBase tableSkin = getTableSkin();
Callback<ResizeFeaturesBase,Boolean> columnResizePolicy = TableSkinUtils.columnResizePolicyProperty(tableSkin).get();
if (columnResizePolicy != null) {
isConstrainedResize =
tableSkin instanceof TableViewSkin ? TableView.CONSTRAINED_RESIZE_POLICY.equals(columnResizePolicy) :
tableSkin instanceof TreeTableViewSkin ? TreeTableView.CONSTRAINED_RESIZE_POLICY.equals(columnResizePolicy) :
false;
}
if (isConstrainedResize && TableSkinUtils.getVisibleLeafColumns(tableSkin).size() == 1) {
return;
}
for (int col = 0; col < columns.size(); col++) {
if (isConstrainedResize && col == getColumns().size() - 1) {
break;
}
final TableColumnBase c = columns.get(col);
final Rectangle rect = new Rectangle();
rect.getProperties().put(TABLE_COLUMN_KEY, c);
rect.getProperties().put(TABLE_COLUMN_HEADER_KEY, this);
rect.setWidth(DRAG_RECT_WIDTH);
rect.setHeight(getHeight() - label.getHeight());
rect.setFill(Color.TRANSPARENT);
rect.visibleProperty().bind(c.visibleProperty().and(c.resizableProperty()));
rect.setOnMousePressed(rectMousePressed);
rect.setOnMouseDragged(rectMouseDragged);
rect.setOnMouseReleased(rectMouseReleased);
rect.setOnMouseEntered(rectCursorChangeListener);
rect.setOnMouseExited(rectCursorChangeListener);
dragRects.put(c, rect);
}
}
private void checkState() {
if (updateColumns) {
updateTableColumnHeaders();
updateColumns = false;
}
}
private TableColumnHeader createColumnHeader(TableColumnBase col) {
TableColumnHeader newCol = createTableColumnHeader(col);
newCol.setTableHeaderRow(getTableHeaderRow());
newCol.setParentHeader(this);
return newCol;
}
private boolean isColumnResizingEnabled() {
return true;
}
private void columnResizingStarted(double startX) {
setCursor(Cursor.H_RESIZE);
columnReorderLine.setLayoutX(startX);
}
private void columnResizing(TableColumnBase col, MouseEvent me) {
double draggedX = me.getSceneX() - dragAnchorX;
if (getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
draggedX = -draggedX;
}
double delta = draggedX - lastX;
boolean allowed = TableSkinUtils.resizeColumn(getTableSkin(), col, delta);
if (allowed) {
lastX = draggedX;
}
}
private void columnResizingComplete(TableColumnBase col, MouseEvent me) {
setCursor(null);
columnReorderLine.setTranslateX(0.0F);
columnReorderLine.setLayoutX(0.0F);
lastX = 0.0F;
}
}
