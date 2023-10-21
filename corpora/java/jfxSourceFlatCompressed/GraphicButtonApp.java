package ensemble.samples.controls.button.graphicbutton;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class GraphicButtonApp extends Application {
public Parent createContent() {
final String URL = "/ensemble/samples/shared-resources/icon-48x48.png";
final Image ICON_48 = new Image(getClass().getResourceAsStream(URL));
final ImageView imageView = new ImageView(ICON_48);
final Button button = new Button("Press", imageView);
return button;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
