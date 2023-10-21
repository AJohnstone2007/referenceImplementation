package hello;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class HelloTiger extends Application {
@Override
public void start(Stage stage) throws Exception {
final double sceneWidth = 1024;
final double sceneHeight = 768;
Rectangle background = new Rectangle(0, 0, sceneWidth, sceneHeight);
Tiger tiger = new Tiger();
Group tigerGroup = new Group(tiger);
tigerGroup.setTranslateX(400);
tigerGroup.setTranslateY(200);
Group root = new Group(background, tigerGroup);
Scene scene = new Scene(root, sceneWidth, sceneHeight);
stage.setScene(scene);
stage.show();
FillTransition tx = new FillTransition(Duration.seconds(5), background, Color.BLACK, Color.RED);
tx.setCycleCount(FillTransition.INDEFINITE);
tx.setAutoReverse(true);
RotateTransition rot = new RotateTransition(Duration.seconds(5), tigerGroup);
rot.setCycleCount(RotateTransition.INDEFINITE);
rot.setToAngle(360);
rot.play();
}
public static void main(String[] args) {
launch(args);
}
}