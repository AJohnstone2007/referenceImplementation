package ensemble.samples.graphics2d.images.imagecreation;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class ImageCreationApp extends Application {
public Parent createContent() {
String URL = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(URL));
ImageView sample1 = new ImageView(ICON_48);
Image JV0H = new Image("https://java.com/images/jv0h.jpg",
400, 100, true, true);
ImageView sample2 = new ImageView(JV0H);
return new Group(new VBox(10, sample1, sample2));
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
