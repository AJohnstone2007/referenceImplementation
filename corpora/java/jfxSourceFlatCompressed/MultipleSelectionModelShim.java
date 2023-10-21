package javafx.scene.control;
import javafx.collections.ObservableList;
public class MultipleSelectionModelShim {
public static ObservableList<Integer> getSelectedIndices(MultipleSelectionModel base) {
return base.getSelectedIndices();
}
public static MultipleSelectionModel
newInstance_from_class(
Class<? extends MultipleSelectionModel>modelClass,
Class<? extends Control>viewClass,
Control view) throws Exception {
return modelClass.getConstructor(viewClass).newInstance(view);
}
}
