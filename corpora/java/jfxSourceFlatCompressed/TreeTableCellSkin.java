package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import java.util.Map;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
public class TreeTableCellSkin<S,T> extends TableCellSkinBase<TreeItem<S>, T, TreeTableCell<S,T>> {
private final BehaviorBase<TreeTableCell<S,T>> behavior;
public TreeTableCellSkin(TreeTableCell<S,T> control) {
super(control);
behavior = new TreeTableCellBehavior<>(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override public ReadOnlyObjectProperty<TreeTableColumn<S,T>> tableColumnProperty() {
return getSkinnable().tableColumnProperty();
}
@Override
protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset,
double leftInset) {
return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + calculateIndentation();
}
@Override
protected void layoutChildren(double x, double y, double w, double h) {
double indentation = calculateIndentation();
x += indentation;
w -= indentation;
super.layoutChildren(x, y, w, h);
}
@Override
protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
double leftInset) {
if (isDeferToParentForPrefWidth) {
return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + calculateIndentation();
}
return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
}
@Override
protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
double leftInset) {
width -= calculateIndentation();
return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
}
private double calculateIndentation() {
double indentation = 0;
final double height = getCellSize();
TreeTableCell<S,T> cell = getSkinnable();
TreeTableColumn<S,T> tableColumn = cell.getTableColumn();
if (tableColumn == null) return indentation;
TreeTableView<S> treeTable = cell.getTreeTableView();
if (treeTable == null) return indentation;
int columnIndex = treeTable.getVisibleLeafIndex(tableColumn);
TreeTableColumn<S,?> treeColumn = treeTable.getTreeColumn();
if ((treeColumn == null && columnIndex != 0) || (treeColumn != null && !tableColumn.equals(treeColumn))) {
return indentation;
}
TreeTableRow<S> treeTableRow = cell.getTableRow();
if (treeTableRow == null) return indentation;
TreeItem<S> treeItem = treeTableRow.getTreeItem();
if (treeItem == null) return indentation;
int nodeLevel = treeTable.getTreeItemLevel(treeItem);
if (!treeTable.isShowRoot()) nodeLevel--;
double indentPerLevel = 10;
if (treeTableRow.getSkin() instanceof TreeTableRowSkin) {
indentPerLevel = ((TreeTableRowSkin<?>)treeTableRow.getSkin()).getIndentationPerLevel();
}
indentation += nodeLevel * indentPerLevel;
Map<TableColumnBase<?,?>, Double> mdwp = TableRowSkinBase.maxDisclosureWidthMap;
indentation += mdwp.containsKey(treeColumn) ? mdwp.get(treeColumn) : 0;
Node graphic = treeItem.getGraphic();
indentation += graphic == null ? 0 : graphic.prefWidth(height);
return indentation;
}
}
