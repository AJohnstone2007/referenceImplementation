package a11y;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class HelloSimpleListView extends Application {
public void start(Stage stage) {
stage.setTitle(getClass().getSimpleName());
ListView<String> listView = new ListView<>();
ObservableList<String> list = FXCollections.observableArrayList();
for (int i=0; i<128; i++) {
list.add("JavaFX item " + i);
}
listView.setItems(list);
listView.getSelectionModel().selectedIndexProperty().addListener(new InvalidationListener() {
public void invalidated(Observable ov) {
System.out.println("SelectedIndex: " + listView.getSelectionModel().getSelectedIndex());
}
});
listView.setPlaceholder(new Text("place holder for emptyness"));
ToggleButton button = new ToggleButton("empty");
button.setOnAction(t-> {
if (list.size() == 0) {
for (int i=0; i<128; i++) {
list.add("JavaFX item " + i);
}
} else {
list.setAll();
}
});
ToggleButton multi = new ToggleButton("multi");
listView.getSelectionModel().selectionModeProperty().bind(new When(multi.selectedProperty()).then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));
VBox group = new VBox(listView, button, multi);
stage.setScene(new Scene(group, 800, 600));
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
