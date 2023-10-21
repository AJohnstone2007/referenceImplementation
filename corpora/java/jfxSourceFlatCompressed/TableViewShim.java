package javafx.scene.control;
public class TableViewShim {
public static Class get_TableViewArrayListSelectionModel_class() {
return TableView.TableViewArrayListSelectionModel.class;
}
public static <S> TableView.TableViewSelectionModel<S> get_TableViewArrayListSelectionModel(TableView table) {
return new TableView.TableViewArrayListSelectionModel<>(table);
}
}
