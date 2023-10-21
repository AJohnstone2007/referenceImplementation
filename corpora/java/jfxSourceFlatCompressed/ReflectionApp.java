package ensemble.samples.graphics2d.effects.reflection;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class ReflectionApp extends Application {
private Reflection reflection = new Reflection();
public Parent createContent() {
String BOAT = "/ensemble/samples/shared-resources/boat.jpg";
Image image = new Image(getClass().getResourceAsStream(BOAT));
ImageView sample = new ImageView(image);
sample.setPreserveRatio(true);
sample.setEffect(reflection);
return new Group(sample);
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
