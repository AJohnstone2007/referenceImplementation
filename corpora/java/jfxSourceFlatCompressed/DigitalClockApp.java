package ensemble.samples.graphics2d.digitalclock;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
public class DigitalClockApp extends Application {
public static final String IMAGE =
"/ensemble/samples/shared-resources/DigitalClock-background.png";
private Clock clock;
public Parent createContent() {
Group root = new Group();
String url = getClass().getResource(IMAGE).toExternalForm();
ImageView background = new ImageView(new Image(url));
clock = new Clock(Color.ORANGERED, Color.rgb(50, 50, 50));
clock.setLayoutX(45);
clock.setLayoutY(186);
clock.getTransforms().add(new Scale(0.83f, 0.83f, 0, 0));
root.getChildren().addAll(background, clock);
return root;
}
public void play() {
clock.play();
}
@Override public void stop() {
clock.stop();
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
