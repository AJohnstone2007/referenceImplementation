package fx83dfeatures;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class ParallelNearAndFarClipTest extends Application {
private static final double OFFSET_PERCENT = 0.01;
private static final int WIDTH = 500;
private static final int HEIGHT = 500;
private static final double NEAR = 0.1;
private static final double FAR = 100.0;
private Scene createClipPlanes(Stage stage) {
stage.setTitle("Parallel Near and Far Clip Test");
final double tanOfHalfFOV = Math.tan(Math.toRadians(15));
final double halfHeight = HEIGHT / 2;
final double focalLength = halfHeight / tanOfHalfFOV;
final double eyePositionZ = -1.0 * focalLength;
final double nearClipDistance = focalLength * NEAR + eyePositionZ;
final double farClipDistance = focalLength * FAR + eyePositionZ;
final double nearClipDistanceOffset = Math.abs(nearClipDistance * OFFSET_PERCENT);
final double farClipDistanceOffset = Math.abs(farClipDistance * OFFSET_PERCENT);
System.out.println("In scene coordinate: focalLength = " + focalLength
+ ", nearClipDistance = " + nearClipDistance
+ ", nearClipDistanceOffset = " + nearClipDistanceOffset
+ ", farClipDistance = " + farClipDistance
+ ", farClipDistanceOffset = " + farClipDistanceOffset);
Rectangle insideRect = new Rectangle(200, 200, Color.GREEN);
insideRect.setLayoutX(150);
insideRect.setLayoutY(150);
insideRect.setId("Green");
Rectangle insideNearClip = new Rectangle(50, 50, Color.BLUE);
insideNearClip.setLayoutX(225);
insideNearClip.setLayoutY(225);
insideNearClip.setTranslateZ(nearClipDistance + nearClipDistanceOffset);
insideNearClip.setId("Blue");
Rectangle outsideNearClip = new Rectangle(100, 100, Color.YELLOW);
outsideNearClip.setLayoutX(200);
outsideNearClip.setLayoutY(200);
outsideNearClip.setTranslateZ(nearClipDistance - nearClipDistanceOffset);
outsideNearClip.setId("Yellow");
Rectangle insideFarClip = new Rectangle(300, 300, Color.RED);
insideFarClip.setTranslateX(100);
insideFarClip.setTranslateY(100);
insideFarClip.setTranslateZ(farClipDistance - farClipDistanceOffset);
insideFarClip.setId("Red");
Rectangle outsideFarClip = new Rectangle(400, 400, Color.CYAN);
outsideFarClip.setTranslateX(50);
outsideFarClip.setTranslateY(50);
outsideFarClip.setTranslateZ(farClipDistance + farClipDistanceOffset);
outsideFarClip.setId("Cyan");
Group root = new Group();
root.getChildren().addAll(outsideFarClip, insideFarClip, insideRect, insideNearClip, outsideNearClip);
Scene scene = new Scene(root, WIDTH, HEIGHT, false);
scene.setOnMousePressed(event -> System.out.println("Clicked: " + event.getTarget()));
return scene;
}
@Override public void start(Stage stage) {
Scene scene = createClipPlanes(stage);
scene.setFill(Color.GRAY);
stage.setScene(scene);
stage.sizeToScene();
stage.setResizable(false);
stage.show();
System.out.println("You should expect to see 3 overlapping squares in"
+ " the order of Blue is on top of Green and Green is on top Red.");
}
public static void main(String[] args) {
Application.launch(args);
}
}
