package javafx.scene.control.skin;
import javafx.scene.control.TableColumnBase;
public class TableHeaderRowShim {
public static TableColumnHeader getColumnHeaderFor(TableHeaderRow tr, final TableColumnBase<?,?> col) {
return tr.getColumnHeaderFor(col);
}
}
