package javafx.scene.control;
public class TreeTableCellShim<S,T> extends TreeTableCell<S,T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
public static <S, T> void set_lockItemOnEdit(TreeTableCell<S, T> tc, boolean b) {
tc.lockItemOnEdit = b;
}
public static <S, T> TreeTablePosition<S, T> getEditingCellAtStartEdit(TreeTableCell<S, T> cell) {
return cell.getEditingCellAtStartEdit();
}
}
