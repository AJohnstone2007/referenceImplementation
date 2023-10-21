package ensemble.samples.scenegraph.stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class StageApp extends Application {
public Parent createContent() {
Button button = new Button("Create a Stage");
button.setStyle("-fx-font-size: 24;");
button.setDefaultButton(true);
button.setOnAction((ActionEvent t) -> {
final Stage stage = new Stage();
Group rootGroup = new Group();
Scene scene = new Scene(rootGroup, 200, 200, Color.WHITESMOKE);
stage.setScene(scene);
stage.setTitle("New stage");
stage.centerOnScreen();
stage.show();
Text text = new Text(20, 110, "JavaFX");
text.setFill(Color.DODGERBLUE);
text.setEffect(new Lighting());
text.setFont(Font.font(Font.getDefault().getFamily(), 50));
rootGroup.getChildren().add(text);
});
return button;
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
