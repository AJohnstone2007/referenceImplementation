package ensemble.samples.graphics2d.paints.lineargradient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class LinearGradientApp extends Application {
public Parent createContent() {
Rectangle rect1 = new Rectangle(0,0,80,80);
LinearGradient gradient1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[] {
new Stop(0, Color.DODGERBLUE),
new Stop(1, Color.BLACK)
});
rect1.setFill(gradient1);
Rectangle rect2 = new Rectangle(0,0,80,80);
LinearGradient gradient2 = new LinearGradient(0, 0, 0, 0.5, true, CycleMethod.REFLECT, new Stop[] {
new Stop(0, Color.DODGERBLUE),
new Stop(0.1, Color.BLACK),
new Stop(1, Color.DODGERBLUE)
});
rect2.setFill(gradient2);
HBox hb = new HBox(10);
hb.setAlignment(Pos.CENTER);
hb.getChildren().addAll(rect1, rect2);
return hb;
}
private Rectangle createRectangle(Color color) {
Rectangle rect1 = new Rectangle(0, 45, 20, 20);
rect1.setFill(color);
return rect1;
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
