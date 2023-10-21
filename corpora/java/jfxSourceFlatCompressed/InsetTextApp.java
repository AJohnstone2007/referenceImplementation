package ensemble.samples.controls.text.insettext;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
public class InsetTextApp extends Application {
public Parent createContent() {
final String insetTextCss =
getClass().getResource("InsetText.css").toExternalForm();
final Label label = new Label("Label styled as a bar");
label.setId("label1");
label.getStylesheets().add(insetTextCss);
return label;
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
