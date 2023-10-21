package javafx.scene.control.skin;
import java.util.*;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
public class TableHeaderRow extends StackPane {
private final String MENU_SEPARATOR =
ControlResources.getString("TableView.nestedColumnControlMenuSeparator");
private final VirtualFlow flow;
final TableViewSkinBase<?,?,?,?,?> tableSkin;
private Map<TableColumnBase, CheckMenuItem> columnMenuItems = new HashMap<TableColumnBase, CheckMenuItem>();
private double scrollX;
private double tableWidth;
private Rectangle clip;
private TableColumnHeader reorderingRegion;
private StackPane dragHeader;
private final Label dragHeaderLabel = new Label();
private Region filler;
private Pane cornerRegion;
private ContextMenu columnPopupMenu;
boolean columnDragLock = false;
private InvalidationListener tableWidthListener = o -> updateTableWidth();
private InvalidationListener tablePaddingListener = o -> updateTableWidth();
private ListChangeListener visibleLeafColumnsListener = c -> getRootHeader().setHeadersNeedUpdate();
private final ListChangeListener tableColumnsListener = c -> {
while (c.next()) {
updateTableColumnListeners(c.getAddedSubList(), c.getRemoved());
}
};
private final InvalidationListener columnTextListener = observable -> {
TableColumnBase<?,?> column = (TableColumnBase<?,?>) ((StringProperty)observable).getBean();
CheckMenuItem menuItem = columnMenuItems.get(column);
if (menuItem != null) {
menuItem.setText(getText(column.getText(), column));
}
};
private final WeakInvalidationListener weakTableWidthListener =
new WeakInvalidationListener(tableWidthListener);
private final WeakInvalidationListener weakTablePaddingListener =
new WeakInvalidationListener(tablePaddingListener);
private final WeakListChangeListener weakVisibleLeafColumnsListener =
new WeakListChangeListener(visibleLeafColumnsListener);
private final WeakListChangeListener weakTableColumnsListener =
new WeakListChangeListener(tableColumnsListener);
private final WeakInvalidationListener weakColumnTextListener =
new WeakInvalidationListener(columnTextListener);
public TableHeaderRow(final TableViewSkinBase skin) {
this.tableSkin = skin;
this.flow = skin.flow;
getStyleClass().setAll("column-header-background");
clip = new Rectangle();
clip.setSmooth(false);
clip.heightProperty().bind(heightProperty());
setClip(clip);
updateTableWidth();
tableSkin.getSkinnable().widthProperty().addListener(weakTableWidthListener);
tableSkin.getSkinnable().paddingProperty().addListener(weakTablePaddingListener);
TableSkinUtils.getVisibleLeafColumns(skin).addListener(weakVisibleLeafColumnsListener);
columnPopupMenu = new ContextMenu();
updateTableColumnListeners(TableSkinUtils.getColumns(tableSkin), Collections.<TableColumnBase<?,?>>emptyList());
TableSkinUtils.getVisibleLeafColumns(skin).addListener(weakTableColumnsListener);
TableSkinUtils.getColumns(tableSkin).addListener(weakTableColumnsListener);
dragHeader = new StackPane();
dragHeader.setVisible(false);
dragHeader.getStyleClass().setAll("column-drag-header");
dragHeader.setManaged(false);
dragHeader.setMouseTransparent(true);
dragHeader.getChildren().add(dragHeaderLabel);
NestedTableColumnHeader rootHeader = createRootHeader();
setRootHeader(rootHeader);
rootHeader.setFocusTraversable(false);
rootHeader.setTableHeaderRow(this);
filler = new Region();
filler.getStyleClass().setAll("filler");
setOnMousePressed(e -> {
skin.getSkinnable().requestFocus();
});
final StackPane image = new StackPane();
image.setSnapToPixel(false);
image.getStyleClass().setAll("show-hide-column-image");
cornerRegion = new StackPane() {
@Override protected void layoutChildren() {
double imageWidth = image.snappedLeftInset() + image.snappedRightInset();
double imageHeight = image.snappedTopInset() + image.snappedBottomInset();
image.resize(imageWidth, imageHeight);
positionInArea(image, 0, 0, getWidth(), getHeight() - 3,
0, HPos.CENTER, VPos.CENTER);
}
};
cornerRegion.getStyleClass().setAll("show-hide-columns-button");
cornerRegion.getChildren().addAll(image);
BooleanProperty tableMenuButtonVisibleProperty = TableSkinUtils.tableMenuButtonVisibleProperty(skin);
if (tableMenuButtonVisibleProperty != null) {
cornerRegion.visibleProperty().bind(tableMenuButtonVisibleProperty);
};
cornerRegion.setOnMousePressed(me -> {
columnPopupMenu.show(cornerRegion, Side.BOTTOM, 0, 0);
me.consume();
});
getChildren().addAll(filler, rootHeader, cornerRegion, dragHeader);
}
private BooleanProperty reordering = new SimpleBooleanProperty(this, "reordering", false) {
@Override protected void invalidated() {
TableColumnHeader r = getReorderingRegion();
if (r != null) {
double dragHeaderHeight = r.getNestedColumnHeader() != null ?
r.getNestedColumnHeader().getHeight() :
getReorderingRegion().getHeight();
dragHeader.resize(dragHeader.getWidth(), dragHeaderHeight);
dragHeader.setTranslateY(getHeight() - dragHeaderHeight);
}
dragHeader.setVisible(isReordering());
}
};
public final void setReordering(boolean value) {
this.reordering.set(value);
}
public final boolean isReordering() {
return reordering.get();
}
public final BooleanProperty reorderingProperty() {
return reordering;
}
private ReadOnlyObjectWrapper<NestedTableColumnHeader> rootHeader = new ReadOnlyObjectWrapper<>(this, "rootHeader");
private final ReadOnlyObjectProperty<NestedTableColumnHeader> rootHeaderProperty() {
return rootHeader.getReadOnlyProperty();
}
public final NestedTableColumnHeader getRootHeader() {
return rootHeader.get();
}
private final void setRootHeader(NestedTableColumnHeader value) {
rootHeader.set(value);
}
@Override protected void layoutChildren() {
double x = scrollX;
double headerWidth = snapSizeX(getRootHeader().prefWidth(-1));
double prefHeight = getHeight() - snappedTopInset() - snappedBottomInset();
double cornerWidth = snapSizeX(flow.getVbar().prefWidth(-1));
getRootHeader().resizeRelocate(x, snappedTopInset(), headerWidth, prefHeight);
final Control control = tableSkin.getSkinnable();
if (control == null) {
return;
}
final BooleanProperty tableMenuButtonVisibleProperty = TableSkinUtils.tableMenuButtonVisibleProperty(tableSkin);
final double controlInsets = control.snappedLeftInset() + control.snappedRightInset();
double fillerWidth = tableWidth - headerWidth + filler.getInsets().getLeft() - controlInsets;
fillerWidth -= tableMenuButtonVisibleProperty != null && tableMenuButtonVisibleProperty.get() ? cornerWidth : 0;
filler.setVisible(fillerWidth > 0);
if (fillerWidth > 0) {
filler.resizeRelocate(x + headerWidth, snappedTopInset(), fillerWidth, prefHeight);
}
cornerRegion.resizeRelocate(tableWidth - cornerWidth, snappedTopInset(), cornerWidth, prefHeight);
}
@Override protected double computePrefWidth(double height) {
return getRootHeader().prefWidth(height);
}
@Override protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override protected double computePrefHeight(double width) {
double headerPrefHeight = getRootHeader().prefHeight(width);
headerPrefHeight = headerPrefHeight == 0.0 ? 24.0 : headerPrefHeight;
return snappedTopInset() + headerPrefHeight + snappedBottomInset();
}
protected void updateScrollX() {
scrollX = flow.getHbar().isVisible() ? -flow.getHbar().getValue() : 0.0F;
requestLayout();
layout();
}
protected void updateTableWidth() {
final Control c = tableSkin.getSkinnable();
if (c == null) {
this.tableWidth = 0;
} else {
Insets insets = c.getInsets() == null ? Insets.EMPTY : c.getInsets();
double padding = snapSizeX(insets.getLeft()) + snapSizeX(insets.getRight());
this.tableWidth = snapSizeX(c.getWidth()) - padding;
}
clip.setWidth(tableWidth);
}
protected NestedTableColumnHeader createRootHeader() {
return new NestedTableColumnHeader(null);
}
protected TableColumnHeader getReorderingRegion() {
return reorderingRegion;
}
void setReorderingColumn(TableColumnBase rc) {
dragHeaderLabel.setText(rc == null ? "" : rc.getText());
}
protected void setReorderingRegion(TableColumnHeader reorderingRegion) {
this.reorderingRegion = reorderingRegion;
if (reorderingRegion != null) {
dragHeader.resize(reorderingRegion.getWidth(), dragHeader.getHeight());
}
}
void setDragHeaderX(double dragHeaderX) {
dragHeader.setTranslateX(dragHeaderX);
}
TableColumnHeader getColumnHeaderFor(final TableColumnBase<?,?> col) {
if (col == null) return null;
List<TableColumnBase<?,?>> columnChain = new ArrayList<>();
columnChain.add(col);
TableColumnBase<?,?> parent = col.getParentColumn();
while (parent != null) {
columnChain.add(0, parent);
parent = parent.getParentColumn();
}
TableColumnHeader currentHeader = getRootHeader();
for (int depth = 0; depth < columnChain.size(); depth++) {
TableColumnBase<?,?> column = columnChain.get(depth);
currentHeader = getColumnHeaderFor(column, currentHeader);
}
return currentHeader;
}
private TableColumnHeader getColumnHeaderFor(final TableColumnBase<?,?> col, TableColumnHeader currentHeader) {
if (currentHeader instanceof NestedTableColumnHeader) {
List<TableColumnHeader> headers = ((NestedTableColumnHeader)currentHeader).getColumnHeaders();
for (int i = 0; i < headers.size(); i++) {
TableColumnHeader header = headers.get(i);
if (header.getTableColumn() == col) {
return header;
}
}
}
return null;
}
private void updateTableColumnListeners(List<? extends TableColumnBase<?,?>> added, List<? extends TableColumnBase<?,?>> removed) {
for (TableColumnBase tc : removed) {
remove(tc);
}
rebuildColumnMenu();
}
private void remove(TableColumnBase<?,?> col) {
if (col == null) return;
CheckMenuItem item = columnMenuItems.remove(col);
if (item != null) {
col.textProperty().removeListener(weakColumnTextListener);
item.selectedProperty().unbindBidirectional(col.visibleProperty());
columnPopupMenu.getItems().remove(item);
}
if (! col.getColumns().isEmpty()) {
for (TableColumnBase tc : col.getColumns()) {
remove(tc);
}
}
}
private void rebuildColumnMenu() {
columnPopupMenu.getItems().clear();
for (TableColumnBase<?,?> col : TableSkinUtils.getColumns(tableSkin)) {
if (col.getColumns().isEmpty()) {
createMenuItem(col);
} else {
List<TableColumnBase<?,?>> leafColumns = getLeafColumns(col);
for (TableColumnBase<?,?> _col : leafColumns) {
createMenuItem(_col);
}
}
}
}
private List<TableColumnBase<?,?>> getLeafColumns(TableColumnBase<?,?> col) {
List<TableColumnBase<?,?>> leafColumns = new ArrayList<>();
for (TableColumnBase<?,?> _col : col.getColumns()) {
if (_col.getColumns().isEmpty()) {
leafColumns.add(_col);
} else {
leafColumns.addAll(getLeafColumns(_col));
}
}
return leafColumns;
}
private void createMenuItem(TableColumnBase<?,?> col) {
CheckMenuItem item = columnMenuItems.get(col);
if (item == null) {
item = new CheckMenuItem();
columnMenuItems.put(col, item);
}
item.setText(getText(col.getText(), col));
col.textProperty().addListener(weakColumnTextListener);
item.setDisable(col.visibleProperty().isBound());
item.setSelected(col.isVisible());
final CheckMenuItem _item = item;
item.selectedProperty().addListener(o -> {
if (col.visibleProperty().isBound()) return;
col.setVisible(_item.isSelected());
});
col.visibleProperty().addListener(o -> _item.setSelected(col.isVisible()));
columnPopupMenu.getItems().add(item);
}
private String getText(String text, TableColumnBase col) {
String s = text;
TableColumnBase parentCol = col.getParentColumn();
while (parentCol != null) {
if (isColumnVisibleInHeader(parentCol, TableSkinUtils.getColumns(tableSkin))) {
s = parentCol.getText() + MENU_SEPARATOR + s;
}
parentCol = parentCol.getParentColumn();
}
return s;
}
private boolean isColumnVisibleInHeader(TableColumnBase col, List columns) {
if (col == null) return false;
for (int i = 0; i < columns.size(); i++) {
TableColumnBase column = (TableColumnBase) columns.get(i);
if (col.equals(column)) return true;
if (! column.getColumns().isEmpty()) {
boolean isVisible = isColumnVisibleInHeader(col, column.getColumns());
if (isVisible) return true;
}
}
return false;
}
}
