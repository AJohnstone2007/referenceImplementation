package javafx.scene.control;
import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
public class TreeTablePosition<S,T> extends TablePositionBase<TreeTableColumn<S,T>> {
public TreeTablePosition(@NamedArg("treeTableView") TreeTableView<S> treeTableView, @NamedArg("row") int row, @NamedArg("tableColumn") TreeTableColumn<S,T> tableColumn) {
this(treeTableView, row, tableColumn, true);
}
TreeTablePosition(@NamedArg("treeTableView") TreeTableView<S> treeTableView, @NamedArg("row") int row, @NamedArg("tableColumn") TreeTableColumn<S,T> tableColumn, boolean doLookup) {
super(row, tableColumn);
this.controlRef = new WeakReference<>(treeTableView);
this.treeItemRef = new WeakReference<>(doLookup ?
(treeTableView != null ? treeTableView.getTreeItem(row) : null) : null);
nonFixedColumnIndex = treeTableView == null || tableColumn == null ? -1 : treeTableView.getVisibleLeafIndex(tableColumn);
}
TreeTablePosition(@NamedArg("treeTableView") TreeTablePosition<S, T> pos, @NamedArg("row") int row) {
super(row, pos.getTableColumn());
this.controlRef = new WeakReference<>(pos.getTreeTableView());
this.treeItemRef = new WeakReference<>(pos.getTreeItem());
nonFixedColumnIndex = pos.getColumn();
}
private final WeakReference<TreeTableView<S>> controlRef;
private final WeakReference<TreeItem<S>> treeItemRef;
int fixedColumnIndex = -1;
private final int nonFixedColumnIndex;
@Override public int getColumn() {
if (fixedColumnIndex > -1) {
return fixedColumnIndex;
}
return nonFixedColumnIndex;
}
public final TreeTableView<S> getTreeTableView() {
return controlRef.get();
}
@Override public final TreeTableColumn<S,T> getTableColumn() {
return super.getTableColumn();
}
public final TreeItem<S> getTreeItem() {
return treeItemRef.get();
}
}
