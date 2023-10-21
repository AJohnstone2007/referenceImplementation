package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.NamedArg;
public class TablePosition<S,T> extends TablePositionBase<TableColumn<S,T>> {
public TablePosition(@NamedArg("tableView") TableView<S> tableView, @NamedArg("row") int row, @NamedArg("tableColumn") TableColumn<S,T> tableColumn) {
super(row, tableColumn);
this.controlRef = new WeakReference<>(tableView);
List<S> items = tableView != null ? tableView.getItems() : null;
this.itemRef = new WeakReference<>(
items != null && row >= 0 && row < items.size() ? items.get(row) : null);
nonFixedColumnIndex = tableView == null || tableColumn == null ? -1 : tableView.getVisibleLeafIndex(tableColumn);
}
private final WeakReference<TableView<S>> controlRef;
private final WeakReference<S> itemRef;
int fixedColumnIndex = -1;
private final int nonFixedColumnIndex;
@Override public int getColumn() {
if (fixedColumnIndex > -1) {
return fixedColumnIndex;
}
return nonFixedColumnIndex;
}
public final TableView<S> getTableView() {
return controlRef.get();
}
@Override public final TableColumn<S,T> getTableColumn() {
return super.getTableColumn();
}
final S getItem() {
return itemRef == null ? null : itemRef.get();
}
@Override public String toString() {
return "TablePosition [ row: " + getRow() + ", column: " + getTableColumn() + ", "
+ "tableView: " + getTableView() + " ]";
}
}
