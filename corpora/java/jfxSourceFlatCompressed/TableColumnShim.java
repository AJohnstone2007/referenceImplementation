package javafx.scene.control;
public class TableColumnShim {
public static void setTableView(TableColumn tc, TableView tv) {
tc.setTableView(tv);
}
public static final double DEFAULT_WIDTH = TableColumnBase.DEFAULT_WIDTH;
public static final double DEFAULT_MIN_WIDTH = TableColumnBase.DEFAULT_MIN_WIDTH;
public static final double DEFAULT_MAX_WIDTH = TableColumnBase.DEFAULT_MAX_WIDTH;
}
