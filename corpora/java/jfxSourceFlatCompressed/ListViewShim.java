package javafx.scene.control;
public class ListViewShim<T> {
public static <T> MultipleSelectionModel<T> getListViewBitSetSelectionModel(ListView listView) {
return new ListView.ListViewBitSetSelectionModel<>(listView);
}
public static Class get_ListViewBitSetSelectionModel_class() {
return ListView.ListViewBitSetSelectionModel.class;
}
public static <T> FocusModel<T> getListViewFocusModel(ListView view) {
return new ListView.ListViewFocusModel<>(view);
}
}
