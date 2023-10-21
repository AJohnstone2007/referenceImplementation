package ensemble.samples.graphics3d.cube;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
public class CubeApp extends Application {
private Timeline animation;
public Parent createContent() {
Cube c = new Cube(1, Color.RED);
c.rx.setAngle(45);
c.ry.setAngle(45);
Cube c2 = new Cube(1, Color.GREEN);
c2.setTranslateX(2);
c2.rx.setAngle(45);
c2.ry.setAngle(45);
Cube c3 = new Cube(1, Color.ORANGE);
c3.setTranslateX(-2);
c3.rx.setAngle(45);
c3.ry.setAngle(45);
animation = new Timeline();
animation.getKeyFrames().addAll(
new KeyFrame(Duration.ZERO,
new KeyValue(c.ry.angleProperty(), 0d),
new KeyValue(c2.rx.angleProperty(), 0d),
new KeyValue(c3.rz.angleProperty(), 0d)),
new KeyFrame(Duration.seconds(1),
new KeyValue(c.ry.angleProperty(), 360d),
new KeyValue(c2.rx.angleProperty(), 360d),
new KeyValue(c3.rz.angleProperty(), 360d)));
animation.setCycleCount(Timeline.INDEFINITE);
PerspectiveCamera camera = new PerspectiveCamera(true);
camera.getTransforms().add(new Translate(0, 0, -10));
Group root = new Group();
root.getChildren().addAll(c, c2, c3);
SubScene subScene = new SubScene(root, 640, 480,
true, SceneAntialiasing.BALANCED);
subScene.setCamera(camera);
return new Group(subScene);
}
public void play() {
animation.play();
}
@Override
public void stop() {
animation.pause();
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
