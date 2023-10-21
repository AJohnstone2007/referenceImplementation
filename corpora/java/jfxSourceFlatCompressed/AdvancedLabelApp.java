package ensemble.samples.controls.text.advancedlabel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class AdvancedLabelApp extends Application {
public Parent createContent() {
String URL = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(URL));
ImageView imageView = new ImageView(ICON_48);
Label above = new Label("Image above", imageView);
above.setContentDisplay(ContentDisplay.TOP);
imageView = new ImageView(ICON_48);
Label right = new Label("Image on the right", imageView);
right.setContentDisplay(ContentDisplay.RIGHT);
imageView = new ImageView(ICON_48);
Label below = new Label("Image below", imageView);
below.setContentDisplay(ContentDisplay.BOTTOM);
imageView = new ImageView(ICON_48);
Label left = new Label("Image on the left", imageView);
left.setContentDisplay(ContentDisplay.LEFT);
imageView = new ImageView(ICON_48);
Label centered = new Label("Image centered", imageView);
centered.setContentDisplay(ContentDisplay.CENTER);
final VBox box = new VBox(2);
box.setAlignment(Pos.CENTER);
box.getChildren().addAll(above, right, below, left, centered);
return box;
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
