package hello;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
public class HelloRectangle3D extends Application {
@Override public void start(Stage stage) throws Exception {
final double sceneWidth = 640;
final double sceneHeight = 480;
Rectangle background = new Rectangle((sceneWidth - 300) / 2, (sceneHeight - 300) / 2, 300, 300);
background.setFill(Color.PURPLE);
background.setRotationAxis(Rotate.Y_AXIS);
background.setRotate(10);
final Rectangle rect = rect((sceneWidth - 200) / 2, (sceneHeight - 200) / 2, 200, 200, Color.LIME);
rect.setRotationAxis(Rotate.Y_AXIS);
rect.setCache(true);
rect.setOnMouseClicked(event -> {
if (event.isShiftDown()) {
rect.setStrokeWidth(Math.random() * 10);
} else {
rect.setFill(new Color(Math.random(), Math.random(), Math.random(), 1));
}
});
Group stack = new Group(background, rect);
Scene scene = new Scene(stack, sceneWidth, sceneHeight, true);
scene.setCamera(new PerspectiveCamera());
scene.setFill(Color.BLACK);
stage.setScene(scene);
stage.setTitle("HelloRectangle3D");
stage.show();
RotateTransition tx = new RotateTransition(Duration.seconds(20), rect);
tx.setToAngle(360);
tx.setCycleCount(RotateTransition.INDEFINITE);
tx.setInterpolator(Interpolator.LINEAR);
tx.play();
}
private Rectangle rect(double x, double y, double width, double height, Color color) {
Rectangle rect = new Rectangle(x, y, width, height);
rect.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, color), new Stop(1, color.darker().darker())));
rect.setArcHeight(42);
rect.setArcWidth(42);
rect.setStroke(Color.WHITE);
rect.setStrokeWidth(5);
rect.setStrokeType(StrokeType.OUTSIDE);
return rect;
}
public static void main(String[] args) {
launch(args);
}
}
