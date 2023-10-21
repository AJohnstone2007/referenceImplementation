package ensemble.samples.scenegraph.nodeproperties;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class NodePropertiesApp extends Application {
private Rectangle rectA;
private Rectangle rectB;
private Rectangle rectC;
public Parent createContent() {
rectA = new Rectangle(50, 50, Color.LIGHTSALMON);
rectA.setTranslateX(10);
rectB = new Rectangle(50, 50, Color.LIGHTGREEN);
rectB.setLayoutX(20);
rectB.setLayoutY(10);
rectC = new Rectangle(50, 50, Color.DODGERBLUE);
rectC.setX(30);
rectC.setY(20);
rectC.setOpacity(0.8);
Pane root = new Pane(rectA, rectB, rectC);
root.setPrefSize(130, 100);
root.setMinSize(130, 100);
root.setMaxSize(130, 100);
return root;
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
