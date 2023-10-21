package javafx.scene.control;
public class TreeTableColumnShim {
public static <S> void setTreeTableView(TreeTableColumn ttc, TreeTableView<S> value) {
ttc.setTreeTableView(value);
}
}
