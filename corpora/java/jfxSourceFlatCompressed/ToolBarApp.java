package ensemble.samples.controls.toolbar.toolbar;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
public class ToolBarApp extends Application {
public Parent createContent() {
ToolBar toolbar = new ToolBar();
toolbar.getItems().add(new Button("Home"));
toolbar.getItems().add(new Button("Options"));
toolbar.getItems().add(new Button("Help"));
return toolbar;
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
