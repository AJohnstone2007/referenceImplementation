package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.collections.FXCollections;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.behavior.TreeTableRowBehavior;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class TreeTableRowSkin<T> extends TableRowSkinBase<TreeItem<T>, TreeTableRow<T>, TreeTableCell<T,?>> {
private TreeItem<?> treeItem;
private boolean disclosureNodeDirty = true;
private Node graphic;
private final BehaviorBase<TreeTableRow<T>> behavior;
private TreeTableViewSkin treeTableViewSkin;
private boolean childrenDirty = false;
public TreeTableRowSkin(TreeTableRow<T> control) {
super(control);
behavior = new TreeTableRowBehavior<>(control);
updateTreeItem();
updateTableViewSkin();
registerChangeListener(control.treeTableViewProperty(), e -> updateTableViewSkin());
registerChangeListener(control.indexProperty(), e -> updateCells = true);
registerChangeListener(control.treeItemProperty(), e -> {
updateTreeItem();
});
setupTreeTableViewListeners();
}
private void setupTreeTableViewListeners() {
TreeTableView<T> treeTableView = getSkinnable().getTreeTableView();
if (treeTableView == null) {
registerInvalidationListener(getSkinnable().treeTableViewProperty(), e -> {
unregisterInvalidationListeners(getSkinnable().treeTableViewProperty());
setupTreeTableViewListeners();
});
} else {
registerChangeListener(treeTableView.treeColumnProperty(), e -> {
isDirty = true;
getSkinnable().requestLayout();
});
DoubleProperty fixedCellSizeProperty = getTreeTableView().fixedCellSizeProperty();
if (fixedCellSizeProperty != null) {
registerChangeListener(fixedCellSizeProperty, e -> {
fixedCellSize = fixedCellSizeProperty.get();
fixedCellSizeEnabled = fixedCellSize > 0;
});
fixedCellSize = fixedCellSizeProperty.get();
fixedCellSizeEnabled = fixedCellSize > 0;
registerChangeListener(getVirtualFlow().widthProperty(), e -> treeTableView.requestLayout());
}
}
}
private void updateTreeItemGraphic() {
disclosureNodeDirty = true;
getSkinnable().requestLayout();
}
private DoubleProperty indent = null;
public final void setIndent(double value) { indentProperty().set(value); }
public final double getIndent() { return indent == null ? 10.0 : indent.get(); }
public final DoubleProperty indentProperty() {
if (indent == null) {
indent = new StyleableDoubleProperty(10.0) {
@Override public Object getBean() {
return TreeTableRowSkin.this;
}
@Override public String getName() {
return "indent";
}
@Override public CssMetaData<TreeTableRow<?>,Number> getCssMetaData() {
return TreeTableRowSkin.StyleableProperties.INDENT;
}
};
}
return indent;
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void updateChildren() {
super.updateChildren();
updateDisclosureNodeAndGraphic();
if (childrenDirty) {
childrenDirty = false;
if (cells.isEmpty()) {
getChildren().clear();
} else {
getChildren().addAll(cells);
}
}
}
@Override protected void layoutChildren(double x, double y, double w, double h) {
if (disclosureNodeDirty) {
updateDisclosureNodeAndGraphic();
disclosureNodeDirty = false;
}
Node disclosureNode = getDisclosureNode();
if (disclosureNode != null && disclosureNode.getScene() == null) {
updateDisclosureNodeAndGraphic();
}
super.layoutChildren(x, y, w, h);
}
@Override protected TreeTableCell<T, ?> createCell(TableColumnBase tcb) {
TreeTableColumn tableColumn = (TreeTableColumn<T,?>) tcb;
TreeTableCell cell = (TreeTableCell) tableColumn.getCellFactory().call(tableColumn);
cell.updateTableColumn(tableColumn);
cell.updateTreeTableView(tableColumn.getTreeTableView());
return cell;
}
@Override void updateCells(boolean resetChildren) {
super.updateCells(resetChildren);
if (resetChildren) {
childrenDirty = true;
updateChildren();
}
}
@Override boolean isIndentationRequired() {
return true;
}
@Override TableColumnBase getTreeColumn() {
return getTreeTableView().getTreeColumn();
}
@Override int getIndentationLevel(TreeTableRow<T> control) {
return getTreeTableView().getTreeItemLevel(control.getTreeItem());
}
@Override double getIndentationPerLevel() {
return getIndent();
}
@Override Node getDisclosureNode() {
return getSkinnable().getDisclosureNode();
}
@Override boolean isDisclosureNodeVisible() {
return getDisclosureNode() != null && treeItem != null && ! treeItem.isLeaf();
}
@Override boolean isShowRoot() {
return getTreeTableView().isShowRoot();
}
@Override protected ObservableList<TreeTableColumn<T, ?>> getVisibleLeafColumns() {
return getTreeTableView() == null ? FXCollections.emptyObservableList() : getTreeTableView().getVisibleLeafColumns();
}
@Override protected void updateCell(TreeTableCell<T, ?> cell, TreeTableRow<T> row) {
cell.updateTableRow(row);
}
@Override protected TreeTableColumn<T, ?> getTableColumn(TreeTableCell cell) {
return cell.getTableColumn();
}
@Override protected ObjectProperty<Node> graphicProperty() {
if (treeItem == null) return null;
return treeItem.graphicProperty();
}
private void updateTreeItem() {
unregisterInvalidationListeners(graphicProperty());
treeItem = getSkinnable().getTreeItem();
registerInvalidationListener(graphicProperty(), e -> updateTreeItemGraphic());
}
private TreeTableView<T> getTreeTableView() {
return getSkinnable().getTreeTableView();
}
private void updateDisclosureNodeAndGraphic() {
if (getSkinnable().isEmpty()) {
getChildren().remove(graphic);
return;
}
ObjectProperty<Node> graphicProperty = graphicProperty();
Node newGraphic = graphicProperty == null ? null : graphicProperty.get();
if (newGraphic != null) {
if (newGraphic != graphic) {
getChildren().remove(graphic);
}
if (! getChildren().contains(newGraphic)) {
getChildren().add(newGraphic);
graphic = newGraphic;
}
}
Node disclosureNode = getSkinnable().getDisclosureNode();
if (disclosureNode != null) {
boolean disclosureVisible = treeItem != null && ! treeItem.isLeaf();
disclosureNode.setVisible(disclosureVisible);
if (! disclosureVisible) {
getChildren().remove(disclosureNode);
} else if (disclosureNode.getParent() == null) {
getChildren().add(disclosureNode);
disclosureNode.toFront();
} else {
disclosureNode.toBack();
}
if (disclosureNode.getScene() != null) {
disclosureNode.applyCss();
}
}
}
private void updateTableViewSkin() {
TreeTableView<T> tableView = getSkinnable().getTreeTableView();
if (tableView != null && tableView.getSkin() instanceof TreeTableViewSkin) {
treeTableViewSkin = (TreeTableViewSkin)tableView.getSkin();
}
}
TreeTableViewSkin<T> getTableViewSkin() {
return treeTableViewSkin;
}
TreeItem<T> getTreeItem() {
return (TreeItem<T>) treeItem;
}
private static class StyleableProperties {
private static final CssMetaData<TreeTableRow<?>,Number> INDENT =
new CssMetaData<TreeTableRow<?>,Number>("-fx-indent",
SizeConverter.getInstance(), 10.0) {
@Override public boolean isSettable(TreeTableRow<?> n) {
DoubleProperty p = ((TreeTableRowSkin<?>) n.getSkin()).indentProperty();
return p == null || !p.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TreeTableRow<?> n) {
final TreeTableRowSkin<?> skin = (TreeTableRowSkin<?>) n.getSkin();
return (StyleableProperty<Number>)(WritableValue<Number>)skin.indentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(CellSkinBase.getClassCssMetaData());
styleables.add(INDENT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
final TreeTableView<T> treeTableView = getSkinnable().getTreeTableView();
switch (attribute) {
case SELECTED_ITEMS: {
List<Node> selection = new ArrayList<>();
int index = getSkinnable().getIndex();
for (TreeTablePosition<T,?> pos : treeTableView.getSelectionModel().getSelectedCells()) {
if (pos.getRow() == index) {
TreeTableColumn<T,?> column = pos.getTableColumn();
if (column == null) {
column = treeTableView.getVisibleLeafColumn(0);
}
TreeTableCell<T,?> cell = cellsMap.get(column).get();
if (cell != null) selection.add(cell);
}
return FXCollections.observableArrayList(selection);
}
}
case CELL_AT_ROW_COLUMN: {
int colIndex = (Integer)parameters[1];
TreeTableColumn<T,?> column = treeTableView.getVisibleLeafColumn(colIndex);
if (cellsMap.containsKey(column)) {
return cellsMap.get(column).get();
}
return null;
}
case FOCUS_ITEM: {
TreeTableView.TreeTableViewFocusModel<T> fm = treeTableView.getFocusModel();
TreeTablePosition<T,?> focusedCell = fm.getFocusedCell();
TreeTableColumn<T,?> column = focusedCell.getTableColumn();
if (column == null) {
column = treeTableView.getVisibleLeafColumn(0);
}
if (cellsMap.containsKey(column)) {
return cellsMap.get(column).get();
}
return null;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
