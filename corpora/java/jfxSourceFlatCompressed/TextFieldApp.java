package ensemble.samples.controls.text.textfield;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class TextFieldApp extends Application {
public Parent createContent() {
TextField text = new TextField("Text");
text.setMaxSize(140, TextField.USE_COMPUTED_SIZE);
return text;
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
