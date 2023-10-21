package test.com.sun.javafx.scene.control.infrastructure;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableColumnHeaderShim;
public class TableColumnHeaderUtil {
public static int getColumnIndex(TableColumn col) {
TableColumnHeader colHeader = VirtualFlowTestUtils.getTableColumnHeader(col.getTableView(), col);
return TableColumnHeaderShim.getColumnIndex(colHeader);
}
public static void moveColumn(TableColumn col, int newPos) {
TableColumnHeader colHeader = VirtualFlowTestUtils.getTableColumnHeader(col.getTableView(), col);
TableColumnHeaderShim.moveColumn(col, colHeader, newPos);
}
public static void moveColumn(TableColumn col, int dragOffset, int x) {
TableColumnHeader colHeader = VirtualFlowTestUtils.getTableColumnHeader(col.getTableView(), col);
TableColumnHeaderShim.columnReorderingStarted(colHeader, dragOffset);
TableColumnHeaderShim.columnReordering(colHeader, x, 0);
TableColumnHeaderShim.columnReorderingComplete(colHeader);
}
}
