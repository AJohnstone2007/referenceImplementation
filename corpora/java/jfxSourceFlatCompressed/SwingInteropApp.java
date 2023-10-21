package ensemble.samples.language.swing;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
public class SwingInteropApp extends Application {
final SwingInteropService service = new SwingInteropService();
final Button button = new Button("Run SwingInterop");
public Parent createContent() {
button.setPrefSize(180, 45);
button.setOnAction((ActionEvent t) -> {
service.restart();
});
button.disableProperty().bind(service.bp);
return button;
}
@Override
public void stop() {
if (service.isRunning()) {
service.cancel();
}
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
