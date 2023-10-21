package ensemble.samples.scenegraph.events.keystrokemotion;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class KeyStrokeMotionApp extends Application {
private LettersPane lettersPane;
public Parent createContent() {
lettersPane = new LettersPane();
return lettersPane;
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
