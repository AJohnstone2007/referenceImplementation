package javafx.scene.control;
public class TreeTableViewShim {
public static Class get_TreeTableViewArrayListSelectionModel_class() {
return TreeTableView.TreeTableViewArrayListSelectionModel.class;
}
public static boolean instanceof_TreeTableViewArrayListSelectionModel(Object o) {
return o instanceof TreeTableView.TreeTableViewArrayListSelectionModel;
}
public static <S> TableSelectionModel<TreeItem<S>>
get_TreeTableViewArrayListSelectionModel(TreeTableView<S> treeTableView) {
return new TreeTableView.TreeTableViewArrayListSelectionModel<>(treeTableView);
}
public static <S> void setSelectionModel(TreeTableView<S> treeTableView,
TableSelectionModel<TreeItem<S>> model)
{
treeTableView.setSelectionModel((TreeTableView.TreeTableViewSelectionModel)model);
}
public static double get_contentWidth(TreeTableView ttv) {
return ttv.contentWidth;
}
}
