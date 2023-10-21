package ensemble.samples.controls.text.simplelabel;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class SimpleLabelApp extends Application {
public Parent createContent() {
String URL = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(URL));
ImageView imageView = new ImageView(ICON_48);
Label label = new Label("Label with a graphic on the left.", imageView);
label.setContentDisplay(ContentDisplay.LEFT);
return label;
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
