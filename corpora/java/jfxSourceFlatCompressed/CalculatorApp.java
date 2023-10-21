package ensemble.samples.graphics2d.calc;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
public class CalculatorApp extends Application {
public Parent createContent() {
final Calculator calculator = new Calculator();
return calculator;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
final Scene scene = new Scene(new Group());
scene.setRoot(createContent());
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
