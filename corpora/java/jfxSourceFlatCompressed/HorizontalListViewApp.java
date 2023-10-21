package ensemble.samples.controls.listview.horizontallistview;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
public class HorizontalListViewApp extends Application {
public Parent createContent() {
Label[] rows = new Label[10];
for (int i = 0; i < 10; i++) {
if (i == 2) {
rows[i] = new Label("Long Row " + (i + 1));
} else {
rows[i] = new Label("Row " + (i + 1));
}
rows[i].setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
}
ListView horizontalListView = new ListView();
horizontalListView.setOrientation(Orientation.HORIZONTAL);
horizontalListView.setItems(FXCollections.observableArrayList(
rows[0], rows[1], rows[2], rows[3], rows[4], rows[5],
rows[6], rows[7], rows[8], rows[9]));
return horizontalListView;
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
