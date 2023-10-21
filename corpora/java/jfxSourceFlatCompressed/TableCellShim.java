package javafx.scene.control;
public class TableCellShim<S,T> extends TableCell<S,T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
public static <S, T> void set_lockItemOnEdit(TableCell<S, T> tc, boolean b) {
tc.lockItemOnEdit = b;
}
public static <S, T> TablePosition<S, T> getEditingCellAtStartEdit(TableCell<S, T> cell) {
return cell.getEditingCellAtStartEdit();
}
}
