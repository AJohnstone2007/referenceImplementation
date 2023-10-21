package ensemble.samples.graphics2d.stopwatch;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class StopWatchApp extends Application {
private Watch watch;
public Parent createContent() {
watch = new Watch();
watch.setLayoutX(15);
watch.setLayoutY(20);
return watch;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
