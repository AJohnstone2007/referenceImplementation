package javafx.scene.control;
import java.lang.ref.WeakReference;
public abstract class TablePositionBase<TC extends TableColumnBase> {
protected TablePositionBase(int row, TC tableColumn) {
this.row = row;
this.tableColumnRef = new WeakReference<TC>(tableColumn);
}
private final int row;
private final WeakReference<TC> tableColumnRef;
public int getRow() {
return row;
}
public abstract int getColumn();
public TC getTableColumn() {
return tableColumnRef.get();
}
@Override public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
@SuppressWarnings("unchecked")
final TablePositionBase other = (TablePositionBase) obj;
if (this.row != other.row) {
return false;
}
TC tableColumn = getTableColumn();
TableColumnBase otherTableColumn = other.getTableColumn();
if (tableColumn != otherTableColumn && (tableColumn == null || !tableColumn.equals(otherTableColumn))) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 5;
hash = 79 * hash + this.row;
TableColumnBase tableColumn = getTableColumn();
hash = 79 * hash + (tableColumn != null ? tableColumn.hashCode() : 0);
return hash;
}
}
