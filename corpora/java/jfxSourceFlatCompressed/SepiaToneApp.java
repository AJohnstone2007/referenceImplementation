package ensemble.samples.graphics2d.effects.sepiatone;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class SepiaToneApp extends Application {
private SepiaTone sepiaTone = new SepiaTone();
public Parent createContent() {
String URL = "/ensemble/samples/shared-resources/boat.jpg";
Image BOAT = new Image(getClass().getResourceAsStream(URL));
ImageView sample = new ImageView(BOAT);
sepiaTone.setLevel(0.5d);
sample.setEffect(sepiaTone);
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
