package hello;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloTask extends Application {
private ProgressBar bar = new ProgressBar();
private Label label = new Label();
@Override
public void init() throws Exception {
Task<String> task = new Task<String>() {
@Override protected String call() throws Exception {
for (int i=0; i<100; i++) {
Thread.sleep(100);
updateProgress(i, 99);
}
return "Finished!";
}
};
bar.progressProperty().bind(task.progressProperty());
label.textProperty().bind(task.valueProperty());
Thread th = new Thread(task);
th.setDaemon(false);
th.start();
}
@Override
public void start(Stage stage) throws Exception {
VBox root = new VBox(15, bar, label);
root.setAlignment(Pos.CENTER);
Scene s = new Scene(root, 640, 480);
stage.setScene(s);
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
