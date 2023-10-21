package javafx.scene.control.skin;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
public class TableSkinShim {
public static <T> TableHeaderRow getTableHeaderRow(TableView<T> table) {
if (table.getSkin() instanceof TableViewSkinBase) {
return getTableHeaderRow((TableViewSkinBase) table.getSkin());
}
return null;
}
public static <T> TableHeaderRow getTableHeaderRow(TableViewSkinBase skin) {
return skin.getTableHeaderRow();
}
public static <T> TableColumnHeader getColumnHeaderFor(TableColumn<T, ?> column) {
TableView<T> table = column.getTableView();
TableHeaderRow tableHeader = getTableHeaderRow(table);
if (tableHeader != null) {
return tableHeader.getColumnHeaderFor(column);
}
return null;
}
public static VirtualFlow<?> getVirtualFlow(TableView<?> table) {
TableViewSkin<?> skin = (TableViewSkin<?>) table.getSkin();
return skin.getVirtualFlow();
}
public static VirtualFlow<?> getVirtualFlow(TreeTableView<?> table) {
TreeTableViewSkin<?> skin = (TreeTableViewSkin<?>) table.getSkin();
return skin.getVirtualFlow();
}
public static <T> boolean isFixedCellSizeEnabled(TableRow<T> tableRow) {
TableRowSkin<T> skin = (TableRowSkin<T>) tableRow.getSkin();
return skin.fixedCellSizeEnabled;
}
public static <T> boolean isFixedCellSizeEnabled(TreeTableRow<T> tableRow) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) tableRow.getSkin();
return skin.fixedCellSizeEnabled;
}
public static <T> boolean isDirty(TableRow<T> tableRow) {
TableRowSkin<T> skin = (TableRowSkin<T>) tableRow.getSkin();
return skin.isDirty();
}
public static <T> boolean isDirty(TreeTableRow<T> tableRow) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) tableRow.getSkin();
return skin.isDirty();
}
public static <T> void setDirty(TableRow<T> tableRow, boolean dirty) {
TableRowSkin<T> skin = (TableRowSkin<T>) tableRow.getSkin();
skin.setDirty(dirty);
}
public static <T> ObservableList<TableColumn<T, ?>> getVisibleLeafColumns(TableRow<T> tableRow) {
TableRowSkin<T> skin = (TableRowSkin<T>) tableRow.getSkin();
return skin.getVisibleLeafColumns();
}
public static <T> TableViewSkin<T> getTableViewSkin(TableRow<T> tableRow) {
TableRowSkin<T> skin = (TableRowSkin<T>) tableRow.getSkin();
return skin.getTableViewSkin();
}
public static <T> TreeTableViewSkin<T> getTableViewSkin(TreeTableRow<T> tableRow) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) tableRow.getSkin();
return skin.getTableViewSkin();
}
public static <T> TreeItem<T> getTreeItem(TreeTableRow<T> tableRow) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) tableRow.getSkin();
return skin.getTreeItem();
}
public static <T> ObjectProperty<Node> graphicProperty(TreeTableRow<T> tableRow) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) tableRow.getSkin();
return skin.graphicProperty();
}
public static <T> VirtualFlow<?> getVirtualFlow(TableRow<T> table) {
TableRowSkin<T> skin = (TableRowSkin<T>) table.getSkin();
return skin.getVirtualFlow();
}
public static <T> VirtualFlow<?> getVirtualFlow(TreeTableRow<T> table) {
TreeTableRowSkin<T> skin = (TreeTableRowSkin<T>) table.getSkin();
return skin.getVirtualFlow();
}
public static List<IndexedCell<?>> getCells(TableRow tableRow) {
TableRowSkin skin = (TableRowSkin) tableRow.getSkin();
return skin.cells;
}
public static List<IndexedCell<?>> getCells(TreeTableRow<?> tableRow) {
TreeTableRowSkin skin = (TreeTableRowSkin) tableRow.getSkin();
return skin.cells;
}
}
