package ensemble.samples.controls.text.searchbox;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class SearchBoxApp extends Application {
public Parent createContent() {
final String searchBoxCss =
getClass().getResource("SearchBox.css").toExternalForm();
final VBox vbox = new VBox();
vbox.getStylesheets().add(searchBoxCss);
vbox.setPrefWidth(200);
vbox.setMaxWidth(Control.USE_PREF_SIZE);
vbox.getChildren().add(new SearchBox());
return vbox;
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
