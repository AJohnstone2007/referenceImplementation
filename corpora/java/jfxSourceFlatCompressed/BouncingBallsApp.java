package ensemble.samples.graphics2d.bouncingballs;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
public class BouncingBallsApp extends Application {
private BallsScreen ballsscreen;
public Parent createContent() {
ballsscreen = new BallsScreen();
ballsscreen.setLayoutX(15);
ballsscreen.setLayoutY(20);
final BallsPane pane = ballsscreen.getPane();
Button resetButton = new Button("Reset");
resetButton.setOnAction((ActionEvent event) -> {
pane.resetBalls();
});
VBox vb = new VBox(10);
vb.getChildren().addAll(resetButton, ballsscreen);
vb.setPadding(new Insets(15, 24, 15, 24));
vb.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
vb.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
return vb;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
