package javafx.scene.control.skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
public class TableColumnHeaderShim {
public static int getColumnIndex(TableColumnHeader colHeader) {
return colHeader == null ? -1 : colHeader.columnIndex;
}
public static void moveColumn(TableColumn col, TableColumnHeader colHeader, int newPos) {
colHeader.moveColumn(col, newPos);
}
public static int getSortPos(TableColumnHeader header) {
return header.sortPos;
}
public static boolean getTableHeaderRowColumnDragLock(TableColumnHeader header) {
return header.getTableHeaderRow().columnDragLock;
}
public static TableColumnHeader getColumnHeaderFor(TableHeaderRow header, final TableColumnBase<?,?> col) {
return header.getColumnHeaderFor(col);
}
public static void columnReorderingStarted(TableColumnHeader header, double dragOffset) {
header.columnReorderingStarted(dragOffset);
}
public static void columnReordering(TableColumnHeader header, double sceneX, double sceneY) {
header.columnReordering(sceneX, sceneY);
}
public static void columnReorderingComplete(TableColumnHeader header) {
header.columnReorderingComplete();
}
public static void resizeColumnToFitContent(TableColumnHeader header, int nbRows) {
header.resizeColumnToFitContent(nbRows);
}
}
