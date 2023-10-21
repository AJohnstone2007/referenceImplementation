package ensemble.samples.controls.listview.listviewcellfactory;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.Arrays;
public class ListViewCellFactoryApp extends Application {
public Parent createContent() {
final Number[] data = {
100.00, -12.34, 33.01,71.00, 23000.00, -6.00, 0, 42223.00, -12.05,
500.00, 430000.00, 1.00, -4.00, 1922.01, -90.00, 11111.00,
3901349.00, 12.00, -1.00, -2.00, 15.00, 47.50, 12.11
};
final ObservableList<Number> numbers =
FXCollections.<Number>observableArrayList(Arrays.asList(data));
final ListView<Number> listView = new ListView<>(numbers);
listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
listView.setCellFactory((ListView<java.lang.Number> list) ->
new MoneyFormatCell());
return listView;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
