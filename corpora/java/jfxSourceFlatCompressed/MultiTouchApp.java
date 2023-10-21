package ensemble.samples.scenegraph.events.multitouch;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MultiTouchApp extends Application {
private MultiTouchPane multiTouchPane;
public Parent createContent() {
multiTouchPane = new MultiTouchPane();
multiTouchPane.setPrefSize(800, 400);
return multiTouchPane;
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
