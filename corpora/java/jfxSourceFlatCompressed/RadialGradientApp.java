package ensemble.samples.graphics2d.paints.radialgradient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class RadialGradientApp extends Application {
public Parent createContent() {
RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
new Stop(0, Color.DODGERBLUE),
new Stop(1, Color.BLACK)
});
Circle circle1 = new Circle(45, 45, 40, gradient1);
RadialGradient gradient2 = new RadialGradient(20, 1, 0.5, 0.5, 0.6, true, CycleMethod.NO_CYCLE, new Stop[]{
new Stop(0, Color.TRANSPARENT),
new Stop(0.5, Color.DARKGRAY),
new Stop(0.64, Color.WHITESMOKE),
new Stop(0.65, Color.YELLOW),
new Stop(1, Color.GOLD)
});
Circle circle2 = new Circle(145, 45, 40, gradient2);
HBox hb = new HBox(10);
hb.setAlignment(Pos.CENTER);
hb.getChildren().addAll(circle1, circle2);
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
