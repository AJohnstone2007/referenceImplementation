package ensemble.samples.controls.listview.simplelistview;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import java.util.Arrays;
public class SimpleListViewApp extends Application {
public Parent createContent() {
final String[] strings = {
"Row 1", "Row 2", "Long Row 3", "Row 4", "Row 5", "Row 6", "Row 7",
"Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14",
"Row 15", "Row 16", "Row 17", "Row 18", "Row 19", "Row 20"
};
final ObservableList<String> stringList =
FXCollections.<String>observableArrayList(Arrays.asList(strings));
final ListView<String> listView = new ListView<String>(stringList);
listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
