package ensemble.samples.graphics2d.canvas;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class FireworksApp extends Application {
private final SanFranciscoFireworks sanFranciscoFireworks = new SanFranciscoFireworks();
public Parent createContent() {
return sanFranciscoFireworks;
}
public void play() {
sanFranciscoFireworks.play();
}
@Override
public void stop() {
sanFranciscoFireworks.stop();
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
