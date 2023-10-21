package ensemble.samples.graphics2d.paints.color;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class ColorApp extends Application {
public Parent createContent() {
VBox vBox = new VBox();
vBox.setSpacing(10);
vBox.setAlignment(Pos.CENTER);
HBox hBox = new HBox();
hBox.setSpacing(6);
hBox.setAlignment(Pos.CENTER);
hBox.getChildren().addAll(
createRectangle(Color.hsb( 0.0, 1.0, 1.0)),
createRectangle(Color.hsb( 30.0, 1.0, 1.0)),
createRectangle(Color.hsb( 60.0, 1.0, 1.0)),
createRectangle(Color.hsb(120.0, 1.0, 1.0)),
createRectangle(Color.hsb(160.0, 1.0, 1.0)),
createRectangle(Color.hsb(200.0, 1.0, 1.0)),
createRectangle(Color.hsb(240.0, 1.0, 1.0)),
createRectangle(Color.hsb(280.0, 1.0, 1.0)),
createRectangle(Color.hsb(320.0, 1.0, 1.0))
);
HBox hBox2 = new HBox();
hBox2.setSpacing(6);
hBox2.setAlignment(Pos.CENTER);
hBox2.getChildren().addAll(
createRectangle(Color.hsb( 0.0, 0.5, 1.0)),
createRectangle(Color.hsb( 30.0, 0.5, 1.0)),
createRectangle(Color.hsb( 60.0, 0.5, 1.0)),
createRectangle(Color.hsb(120.0, 0.5, 1.0)),
createRectangle(Color.hsb(160.0, 0.5, 1.0)),
createRectangle(Color.hsb(200.0, 0.5, 1.0)),
createRectangle(Color.hsb(240.0, 0.5, 1.0)),
createRectangle(Color.hsb(280.0, 0.5, 1.0)),
createRectangle(Color.hsb(320.0, 0.5, 1.0))
);
HBox hBox3 = new HBox();
hBox3.setSpacing(6);
hBox3.setAlignment(Pos.CENTER);
hBox3.getChildren().addAll(
createRectangle(Color.hsb( 0.0, 1.0, 0.5)),
createRectangle(Color.hsb( 30.0, 1.0, 0.5)),
createRectangle(Color.hsb( 60.0, 1.0, 0.5)),
createRectangle(Color.hsb(120.0, 1.0, 0.5)),
createRectangle(Color.hsb(160.0, 1.0, 0.5)),
createRectangle(Color.hsb(200.0, 1.0, 0.5)),
createRectangle(Color.hsb(240.0, 1.0, 0.5)),
createRectangle(Color.hsb(280.0, 1.0, 0.5)),
createRectangle(Color.hsb(320.0, 1.0, 0.5))
);
HBox hBox4 = new HBox();
hBox4.setSpacing(6);
hBox4.setAlignment(Pos.CENTER);
hBox4.getChildren().addAll(
createRectangle(Color.BLACK),
createRectangle(Color.hsb(0, 0, 0.1)),
createRectangle(new Color(0.2, 0.2, 0.2, 1)),
createRectangle(Color.color(0.3, 0.3, 0.3)),
createRectangle(Color.rgb(102, 102, 102)),
createRectangle(Color.web("#777777")),
createRectangle(Color.gray(0.6)),
createRectangle(Color.grayRgb(179)),
createRectangle(Color.grayRgb(179, 0.5))
);
vBox.getChildren().addAll(hBox, hBox2, hBox3, hBox4);
return vBox;
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
