package javafx.scene.control;
public class TreeViewShim<T> {
public static <T> MultipleSelectionModel<TreeItem<T>>
get_TreeViewBitSetSelectionModel(TreeView treeView) {
return new TreeView.TreeViewBitSetSelectionModel<>(treeView);
}
public static Class get_TreeViewBitSetSelectionModel_class() {
return TreeView.TreeViewBitSetSelectionModel.class;
}
public static boolean is_TreeViewBitSetSelectionModel(Object o) {
return o instanceof TreeView.TreeViewBitSetSelectionModel;
}
public static <T> FocusModel<TreeItem<T>> get_TreeViewFocusModel(TreeView treeView) {
return new TreeView.TreeViewFocusModel(treeView);
}
public static TableView.TableViewSelectionModel<String>
newInstance_from_class(Class<? extends TableView.TableViewSelectionModel >modelClass,
TableView tableView) throws Exception {
return modelClass.getConstructor(TableView.class).newInstance(tableView);
}
}
