package javafx.scene.control;
public class ChoiceBoxShim {
public static <T> SingleSelectionModel<T> get_ChoiceBoxSelectionModel(ChoiceBox<T> cb) {
return new ChoiceBox.ChoiceBoxSelectionModel<T>(cb);
}
public static boolean ChoiceBoxSelectionModel_isAssignableFrom(Class c) {
return ChoiceBox.ChoiceBoxSelectionModel.class.isAssignableFrom(c);
}
}
