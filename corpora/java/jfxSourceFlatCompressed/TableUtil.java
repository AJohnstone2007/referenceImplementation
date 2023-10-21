package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
class TableUtil {
private TableUtil() {
}
static void removeTableColumnListener(List<? extends TableColumnBase> list,
final InvalidationListener columnVisibleObserver,
final InvalidationListener columnSortableObserver,
final InvalidationListener columnSortTypeObserver,
final InvalidationListener columnComparatorObserver) {
if (list == null) return;
for (TableColumnBase col : list) {
col.visibleProperty().removeListener(columnVisibleObserver);
col.sortableProperty().removeListener(columnSortableObserver);
col.comparatorProperty().removeListener(columnComparatorObserver);
if (col instanceof TableColumn) {
((TableColumn)col).sortTypeProperty().removeListener(columnSortTypeObserver);
} else if (col instanceof TreeTableColumn) {
((TreeTableColumn)col).sortTypeProperty().removeListener(columnSortTypeObserver);
}
removeTableColumnListener(col.getColumns(),
columnVisibleObserver,
columnSortableObserver,
columnSortTypeObserver,
columnComparatorObserver);
}
}
static void addTableColumnListener(List<? extends TableColumnBase> list,
final InvalidationListener columnVisibleObserver,
final InvalidationListener columnSortableObserver,
final InvalidationListener columnSortTypeObserver,
final InvalidationListener columnComparatorObserver) {
if (list == null) return;
for (TableColumnBase col : list) {
col.visibleProperty().addListener(columnVisibleObserver);
col.sortableProperty().addListener(columnSortableObserver);
col.comparatorProperty().addListener(columnComparatorObserver);
if (col instanceof TableColumn) {
((TableColumn)col).sortTypeProperty().addListener(columnSortTypeObserver);
} else if (col instanceof TreeTableColumn) {
((TreeTableColumn)col).sortTypeProperty().addListener(columnSortTypeObserver);
}
addTableColumnListener(col.getColumns(),
columnVisibleObserver,
columnSortableObserver,
columnSortTypeObserver,
columnComparatorObserver);
}
}
static void removeColumnsListener(List<? extends TableColumnBase> list, ListChangeListener cl) {
if (list == null) return;
for (TableColumnBase col : list) {
col.getColumns().removeListener(cl);
removeColumnsListener(col.getColumns(), cl);
}
}
static void addColumnsListener(List<? extends TableColumnBase> list, ListChangeListener cl) {
if (list == null) return;
for (TableColumnBase col : list) {
col.getColumns().addListener(cl);
addColumnsListener(col.getColumns(), cl);
}
}
static void handleSortFailure(ObservableList<? extends TableColumnBase> sortOrder,
SortEventType sortEventType, final Object... supportInfo) {
if (sortEventType == SortEventType.COLUMN_SORT_TYPE_CHANGE) {
final TableColumnBase changedColumn = (TableColumnBase) supportInfo[0];
revertSortType(changedColumn);
} else if (sortEventType == SortEventType.SORT_ORDER_CHANGE) {
ListChangeListener.Change change = (ListChangeListener.Change) supportInfo[0];
final List toRemove = new ArrayList();
final List toAdd = new ArrayList();
while (change.next()) {
if (change.wasAdded()) {
toRemove.addAll(change.getAddedSubList());
}
if (change.wasRemoved()) {
toAdd.addAll(change.getRemoved());
}
}
sortOrder.removeAll(toRemove);
sortOrder.addAll(toAdd);
} else if (sortEventType == SortEventType.COLUMN_SORTABLE_CHANGE) {
} else if (sortEventType == SortEventType.COLUMN_COMPARATOR_CHANGE) {
}
}
private static void revertSortType(TableColumnBase changedColumn) {
if (changedColumn instanceof TableColumn) {
TableColumn tableColumn = (TableColumn)changedColumn;
final TableColumn.SortType sortType = tableColumn.getSortType();
if (sortType == TableColumn.SortType.ASCENDING) {
tableColumn.setSortType(null);
} else if (sortType == TableColumn.SortType.DESCENDING) {
tableColumn.setSortType(TableColumn.SortType.ASCENDING);
} else if (sortType == null) {
tableColumn.setSortType(TableColumn.SortType.DESCENDING);
}
} else if (changedColumn instanceof TreeTableColumn) {
TreeTableColumn tableColumn = (TreeTableColumn)changedColumn;
final TreeTableColumn.SortType sortType = tableColumn.getSortType();
if (sortType == TreeTableColumn.SortType.ASCENDING) {
tableColumn.setSortType(null);
} else if (sortType == TreeTableColumn.SortType.DESCENDING) {
tableColumn.setSortType(TreeTableColumn.SortType.ASCENDING);
} else if (sortType == null) {
tableColumn.setSortType(TreeTableColumn.SortType.DESCENDING);
}
}
}
static enum SortEventType {
SORT_ORDER_CHANGE,
COLUMN_SORT_TYPE_CHANGE,
COLUMN_SORTABLE_CHANGE,
COLUMN_COMPARATOR_CHANGE
}
static boolean constrainedResize(ResizeFeaturesBase prop,
boolean isFirstRun,
double tableWidth,
List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
TableColumnBase<?,?> column = prop.getColumn();
double delta = prop.getDelta();
boolean isShrinking;
double target;
double totalLowerBound = 0;
double totalUpperBound = 0;
if (tableWidth == 0) return false;
double colWidth = 0;
for (TableColumnBase<?,?> col : visibleLeafColumns) {
colWidth += col.getWidth();
}
if (Math.abs(colWidth - tableWidth) > 1) {
isShrinking = colWidth > tableWidth;
target = tableWidth;
if (isFirstRun) {
for (TableColumnBase<?,?> col : visibleLeafColumns) {
totalLowerBound += col.getMinWidth();
totalUpperBound += col.getMaxWidth();
}
totalUpperBound = totalUpperBound == Double.POSITIVE_INFINITY ?
Double.MAX_VALUE :
(totalUpperBound == Double.NEGATIVE_INFINITY ? Double.MIN_VALUE : totalUpperBound);
for (TableColumnBase col : visibleLeafColumns) {
double lowerBound = col.getMinWidth();
double upperBound = col.getMaxWidth();
double newSize;
if (Math.abs(totalLowerBound - totalUpperBound) < .0000001) {
newSize = lowerBound;
} else {
double f = (target - totalLowerBound) / (totalUpperBound - totalLowerBound);
newSize = Math.round(lowerBound + f * (upperBound - lowerBound));
}
double remainder = resize(col, newSize - col.getWidth());
target -= newSize + remainder;
totalLowerBound -= lowerBound;
totalUpperBound -= upperBound;
}
isFirstRun = false;
} else {
double actualDelta = tableWidth - colWidth;
List<? extends TableColumnBase<?,?>> cols = visibleLeafColumns;
resizeColumns(cols, actualDelta);
}
}
if (column == null) {
return false;
}
isShrinking = delta < 0;
TableColumnBase<?,?> leafColumn = column;
while (leafColumn.getColumns().size() > 0) {
leafColumn = leafColumn.getColumns().get(leafColumn.getColumns().size() - 1);
}
int colPos = visibleLeafColumns.indexOf(leafColumn);
int endColPos = visibleLeafColumns.size() - 1;
double remainingDelta = delta;
while (endColPos > colPos && remainingDelta != 0) {
TableColumnBase<?,?> resizingCol = visibleLeafColumns.get(endColPos);
endColPos--;
if (! resizingCol.isResizable()) continue;
TableColumnBase<?,?> shrinkingCol = isShrinking ? leafColumn : resizingCol;
TableColumnBase<?,?> growingCol = !isShrinking ? leafColumn : resizingCol;
if (growingCol.getWidth() > growingCol.getPrefWidth()) {
List<? extends TableColumnBase> seq = visibleLeafColumns.subList(colPos + 1, endColPos + 1);
for (int i = seq.size() - 1; i >= 0; i--) {
TableColumnBase<?,?> c = seq.get(i);
if (c.getWidth() < c.getPrefWidth()) {
growingCol = c;
break;
}
}
}
double sdiff = Math.min(Math.abs(remainingDelta), shrinkingCol.getWidth() - shrinkingCol.getMinWidth());
double delta1 = resize(shrinkingCol, -sdiff);
double delta2 = resize(growingCol, sdiff);
remainingDelta += isShrinking ? sdiff : -sdiff;
}
return remainingDelta == 0;
}
static double resize(TableColumnBase column, double delta) {
if (delta == 0) return 0.0F;
if (! column.isResizable()) return delta;
final boolean isShrinking = delta < 0;
final List<TableColumnBase<?,?>> resizingChildren = getResizableChildren(column, isShrinking);
if (resizingChildren.size() > 0) {
return resizeColumns(resizingChildren, delta);
} else {
double newWidth = column.getWidth() + delta;
if (newWidth > column.getMaxWidth()) {
column.doSetWidth(column.getMaxWidth());
return newWidth - column.getMaxWidth();
} else if (newWidth < column.getMinWidth()) {
column.doSetWidth(column.getMinWidth());
return newWidth - column.getMinWidth();
} else {
column.doSetWidth(newWidth);
return 0.0F;
}
}
}
private static List<TableColumnBase<?,?>> getResizableChildren(TableColumnBase<?,?> column, boolean isShrinking) {
if (column == null || column.getColumns().isEmpty()) {
return Collections.emptyList();
}
List<TableColumnBase<?,?>> tablecolumns = new ArrayList<TableColumnBase<?,?>>();
for (TableColumnBase c : column.getColumns()) {
if (! c.isVisible()) continue;
if (! c.isResizable()) continue;
if (isShrinking && c.getWidth() > c.getMinWidth()) {
tablecolumns.add(c);
} else if (!isShrinking && c.getWidth() < c.getMaxWidth()) {
tablecolumns.add(c);
}
}
return tablecolumns;
}
private static double resizeColumns(List<? extends TableColumnBase<?,?>> columns, double delta) {
final int columnCount = columns.size();
double colDelta = delta / columnCount;
double remainingDelta = delta;
int col = 0;
boolean isClean = true;
for (TableColumnBase<?,?> childCol : columns) {
col++;
double leftOverDelta = resize(childCol, colDelta);
remainingDelta = remainingDelta - colDelta + leftOverDelta;
if (leftOverDelta != 0) {
isClean = false;
colDelta = remainingDelta / (columnCount - col);
}
}
return isClean ? 0.0 : remainingDelta;
}
}
