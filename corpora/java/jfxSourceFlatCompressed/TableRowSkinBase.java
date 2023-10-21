package javafx.scene.control.skin;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import com.sun.javafx.PlatformUtil;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import com.sun.javafx.tk.Toolkit;
public abstract class TableRowSkinBase<T,
C extends IndexedCell ,
R extends IndexedCell> extends CellSkinBase<C> {
private static boolean IS_STUB_TOOLKIT = Toolkit.getToolkit().toString().contains("StubToolkit");
private static boolean DO_ANIMATIONS = ! IS_STUB_TOOLKIT && ! PlatformUtil.isEmbedded();
private static final Duration FADE_DURATION = Duration.millis(200);
static final Map<TableColumnBase<?,?>, Double> maxDisclosureWidthMap = new WeakHashMap<>();
private static final int DEFAULT_FULL_REFRESH_COUNTER = 100;
WeakHashMap<TableColumnBase, Reference<R>> cellsMap;
final List<R> cells = new ArrayList<>();
private int fullRefreshCounter = DEFAULT_FULL_REFRESH_COUNTER;
boolean isDirty = false;
boolean updateCells = false;
double fixedCellSize;
boolean fixedCellSizeEnabled;
public TableRowSkinBase(C control) {
super(control);
getSkinnable().setPickOnBounds(false);
recreateCells();
updateCells(true);
registerListChangeListener(getVisibleLeafColumns(), c -> updateLeafColumns());
registerInvalidationListener(control.itemProperty(), o -> requestCellUpdate());
registerChangeListener(control.indexProperty(), e -> {
if (getSkinnable().isEmpty()) {
requestCellUpdate();
}
});
}
private void updateLeafColumns() {
isDirty = true;
getSkinnable().requestLayout();
}
protected abstract R createCell(TableColumnBase<T,?> tc);
protected abstract void updateCell(R cell, C row);
protected abstract TableColumnBase<T,?> getTableColumn(R cell);
protected abstract ObservableList<? extends TableColumnBase > getVisibleLeafColumns();
protected ObjectProperty<Node> graphicProperty() {
return null;
}
@Override protected void layoutChildren(double x, final double y, final double w, final double h) {
checkState();
if (cellsMap.isEmpty()) return;
ObservableList<? extends TableColumnBase> visibleLeafColumns = getVisibleLeafColumns();
if (visibleLeafColumns.isEmpty()) {
super.layoutChildren(x,y,w,h);
return;
}
C control = getSkinnable();
double leftMargin = 0;
double disclosureWidth = 0;
double graphicWidth = 0;
boolean indentationRequired = isIndentationRequired();
boolean disclosureVisible = isDisclosureNodeVisible();
int indentationColumnIndex = 0;
Node disclosureNode = null;
if (indentationRequired) {
TableColumnBase<?,?> treeColumn = getTreeColumn();
indentationColumnIndex = treeColumn == null ? 0 : visibleLeafColumns.indexOf(treeColumn);
indentationColumnIndex = indentationColumnIndex < 0 ? 0 : indentationColumnIndex;
int indentationLevel = getIndentationLevel(control);
if (! isShowRoot()) indentationLevel--;
final double indentationPerLevel = getIndentationPerLevel();
leftMargin = indentationLevel * indentationPerLevel;
final double defaultDisclosureWidth = maxDisclosureWidthMap.containsKey(treeColumn) ?
maxDisclosureWidthMap.get(treeColumn) : 0;
disclosureWidth = defaultDisclosureWidth;
disclosureNode = getDisclosureNode();
if (disclosureNode != null) {
disclosureNode.setVisible(disclosureVisible);
if (disclosureVisible) {
disclosureWidth = disclosureNode.prefWidth(h);
if (disclosureWidth > defaultDisclosureWidth) {
maxDisclosureWidthMap.put(treeColumn, disclosureWidth);
final VirtualFlow<C> flow = getVirtualFlow();
final int thisIndex = getSkinnable().getIndex();
for (int i = 0; i < flow.cells.size(); i++) {
C cell = flow.cells.get(i);
if (cell == null || cell.isEmpty()) continue;
cell.requestLayout();
cell.layout();
}
}
}
}
}
double width;
double height;
final double verticalPadding = snappedTopInset() + snappedBottomInset();
final double horizontalPadding = snappedLeftInset() + snappedRightInset();
final double controlHeight = control.getHeight();
int index = control.getIndex();
if (index < 0 ) return;
for (int column = 0, max = cells.size(); column < max; column++) {
R tableCell = cells.get(column);
TableColumnBase<T, ?> tableColumn = getTableColumn(tableCell);
boolean isVisible = true;
if (fixedCellSizeEnabled) {
isVisible = isColumnPartiallyOrFullyVisible(tableColumn);
height = fixedCellSize;
} else {
height = Math.max(controlHeight, tableCell.prefHeight(-1));
height = snapSizeY(height) - snapSizeY(verticalPadding);
}
if (isVisible) {
if (fixedCellSizeEnabled && tableCell.getParent() == null) {
getChildren().add(tableCell);
}
width = tableCell.prefWidth(height) - snapSizeX(horizontalPadding);
final boolean centreContent = h <= 24.0;
final StyleOrigin origin = ((StyleableObjectProperty<?>) tableCell.alignmentProperty()).getStyleOrigin();
if (! centreContent && origin == null) {
tableCell.setAlignment(Pos.TOP_LEFT);
}
if (indentationRequired && column == indentationColumnIndex) {
if (disclosureVisible) {
double ph = disclosureNode.prefHeight(disclosureWidth);
if (width > 0 && width < (disclosureWidth + leftMargin)) {
fadeOut(disclosureNode);
} else {
fadeIn(disclosureNode);
disclosureNode.resize(disclosureWidth, ph);
disclosureNode.relocate(x + leftMargin,
centreContent ? (h / 2.0 - ph / 2.0) :
(y + tableCell.getPadding().getTop()));
disclosureNode.toFront();
}
}
ObjectProperty<Node> graphicProperty = graphicProperty();
Node graphic = graphicProperty == null ? null : graphicProperty.get();
if (graphic != null) {
graphicWidth = graphic.prefWidth(-1) + 3;
double ph = graphic.prefHeight(graphicWidth);
if (width > 0 && width < disclosureWidth + leftMargin + graphicWidth) {
fadeOut(graphic);
} else {
fadeIn(graphic);
graphic.relocate(x + leftMargin + disclosureWidth,
centreContent ? (h / 2.0 - ph / 2.0) :
(y + tableCell.getPadding().getTop()));
graphic.toFront();
}
}
}
tableCell.resize(width, height);
tableCell.relocate(x, snappedTopInset());
tableCell.requestLayout();
} else {
width = snapSizeX(tableCell.prefWidth(-1)) - snapSizeX(horizontalPadding);
if (fixedCellSizeEnabled) {
getChildren().remove(tableCell);
}
}
x += width;
}
}
int getIndentationLevel(C control) {
return 0;
}
double getIndentationPerLevel() {
return 0;
}
boolean isIndentationRequired() {
return false;
}
TableColumnBase getTreeColumn() {
return null;
}
Node getDisclosureNode() {
return null;
}
boolean isDisclosureNodeVisible() {
return false;
}
boolean isShowRoot() {
return true;
}
void updateCells(boolean resetChildren) {
if (resetChildren) {
if (fullRefreshCounter == 0) {
recreateCells();
}
fullRefreshCounter--;
}
final boolean cellsEmpty = cells.isEmpty();
cells.clear();
final C skinnable = getSkinnable();
final int skinnableIndex = skinnable.getIndex();
final List<? extends TableColumnBase > visibleLeafColumns = getVisibleLeafColumns();
for (int i = 0, max = visibleLeafColumns.size(); i < max; i++) {
TableColumnBase<T,?> col = visibleLeafColumns.get(i);
R cell = null;
if (cellsMap.containsKey(col)) {
cell = cellsMap.get(col).get();
if (cell == null) {
cellsMap.remove(col);
}
}
if (cell == null) {
cell = createCellAndCache(col);
}
updateCell(cell, skinnable);
cell.updateIndex(skinnableIndex);
cells.add(cell);
}
if (fixedCellSizeEnabled) {
List<Node> toRemove = new ArrayList<>();
for (Node cell : getChildren()) {
if (!(cell instanceof IndexedCell)) continue;
TableColumnBase<T, ?> tableColumn = getTableColumn((R) cell);
if (!getVisibleLeafColumns().contains(tableColumn)) {
toRemove.add(cell);
}
}
getChildren().removeAll(toRemove);
} else if (resetChildren || cellsEmpty) {
getChildren().setAll(cells);
}
}
VirtualFlow<C> getVirtualFlow() {
Parent p = getSkinnable();
while (p != null) {
if (p instanceof VirtualFlow) {
return (VirtualFlow<C>) p;
}
p = p.getParent();
}
return null;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double prefWidth = 0.0;
for (R cell : cells) {
prefWidth += cell.prefWidth(height);
}
return prefWidth;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
if (fixedCellSizeEnabled) {
return fixedCellSize;
}
checkState();
if (getCellSize() < DEFAULT_CELL_SIZE) {
return getCellSize();
}
double prefHeight = 0.0f;
final int count = cells.size();
for (int i=0; i<count; i++) {
final R tableCell = cells.get(i);
prefHeight = Math.max(prefHeight, tableCell.prefHeight(-1));
}
double ph = Math.max(prefHeight, Math.max(getCellSize(), getSkinnable().minHeight(-1)));
return ph;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
if (fixedCellSizeEnabled) {
return fixedCellSize;
}
checkState();
if (getCellSize() < DEFAULT_CELL_SIZE) {
return getCellSize();
}
double minHeight = 0.0f;
final int count = cells.size();
for (int i = 0; i < count; i++) {
final R tableCell = cells.get(i);
minHeight = Math.max(minHeight, tableCell.minHeight(-1));
}
return minHeight;
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
if (fixedCellSizeEnabled) {
return fixedCellSize;
}
return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
}
final void checkState() {
if (isDirty) {
updateCells(true);
isDirty = false;
updateCells = false;
} else if (updateCells) {
updateCells(false);
updateCells = false;
}
}
boolean isDirty() {
return isDirty;
}
void setDirty(boolean dirty) {
isDirty = dirty;
}
private boolean isColumnPartiallyOrFullyVisible(TableColumnBase col) {
if (col == null || !col.isVisible()) return false;
final VirtualFlow<?> virtualFlow = getVirtualFlow();
double scrollX = virtualFlow == null ? 0.0 : virtualFlow.getHbar().getValue();
double start = 0;
final ObservableList<? extends TableColumnBase> visibleLeafColumns = getVisibleLeafColumns();
for (int i = 0, max = visibleLeafColumns.size(); i < max; i++) {
TableColumnBase<?,?> c = visibleLeafColumns.get(i);
if (c.equals(col)) break;
start += c.getWidth();
}
double end = start + col.getWidth();
final Insets padding = getSkinnable().getPadding();
double headerWidth = getSkinnable().getWidth() - padding.getLeft() + padding.getRight();
return (start >= scrollX || end > scrollX) && (start < (headerWidth + scrollX) || end <= (headerWidth + scrollX));
}
private void requestCellUpdate() {
updateCells = true;
getSkinnable().requestLayout();
final int newIndex = getSkinnable().getIndex();
for (int i = 0, max = cells.size(); i < max; i++) {
cells.get(i).updateIndex(newIndex);
}
}
private void recreateCells() {
if (cellsMap != null) {
Collection<Reference<R>> cells = cellsMap.values();
Iterator<Reference<R>> cellsIter = cells.iterator();
while (cellsIter.hasNext()) {
Reference<R> cellRef = cellsIter.next();
R cell = cellRef.get();
if (cell != null) {
cell.updateIndex(-1);
cell.getSkin().dispose();
cell.setSkin(null);
}
}
cellsMap.clear();
}
ObservableList<? extends TableColumnBase > columns = getVisibleLeafColumns();
cellsMap = new WeakHashMap<>(columns.size());
fullRefreshCounter = DEFAULT_FULL_REFRESH_COUNTER;
getChildren().clear();
for (TableColumnBase col : columns) {
if (cellsMap.containsKey(col)) {
continue;
}
createCellAndCache(col);
}
}
private R createCellAndCache(TableColumnBase<T,?> col) {
R cell = createCell(col);
cellsMap.put(col, new WeakReference<>(cell));
return cell;
}
private void fadeOut(final Node node) {
if (node.getOpacity() < 1.0) return;
if (! DO_ANIMATIONS) {
node.setOpacity(0);
return;
}
final FadeTransition fader = new FadeTransition(FADE_DURATION, node);
fader.setToValue(0.0);
fader.play();
}
private void fadeIn(final Node node) {
if (node.getOpacity() > 0.0) return;
if (! DO_ANIMATIONS) {
node.setOpacity(1);
return;
}
final FadeTransition fader = new FadeTransition(FADE_DURATION, node);
fader.setToValue(1.0);
fader.play();
}
}
