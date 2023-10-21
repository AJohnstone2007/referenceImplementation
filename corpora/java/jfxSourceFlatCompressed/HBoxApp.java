package ensemble.samples.layout.hbox;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class HBoxApp extends Application {
public Parent createContent() {
Label label = new Label("Test:");
TextField tb = new TextField();
Button button = new Button("Button");
HBox hbox = new HBox(5);
hbox.getChildren().addAll(label, tb, button);
hbox.setAlignment(Pos.CENTER);
return hbox;
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
