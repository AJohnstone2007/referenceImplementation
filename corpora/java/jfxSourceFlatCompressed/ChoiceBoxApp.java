package ensemble.samples.controls.choicebox;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
public class ChoiceBoxApp extends Application {
public Parent createContent() {
ChoiceBox cb = new ChoiceBox();
cb.getItems().addAll("Dog", "Cat", "Horse");
cb.getSelectionModel().selectFirst();
return cb;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
