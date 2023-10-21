package javafx.scene.control;
public class ComboBoxShim {
public static <T> SingleSelectionModel<T> get_ComboBoxSelectionModel(ComboBox<T> cb) {
return new ComboBox.ComboBoxSelectionModel<>(cb);
}
public static boolean ComboBoxSelectionModel_isAssignableFrom(Class c) {
return ComboBox.ComboBoxSelectionModel.class.isAssignableFrom(c);
}
}
