package ensemble.samples.scenegraph.advancedstage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class AdvancedStageApp extends Application {
private double initX;
private double initY;
public Parent createContent() {
Button button = new Button("Create a Stage");
button.setStyle("-fx-font-size: 24;");
button.setDefaultButton(true);
button.setOnAction((ActionEvent t) -> {
final Stage stage = new Stage(StageStyle.TRANSPARENT);
Group rootGroup = new Group();
Scene scene = new Scene(rootGroup, 200, 200, Color.TRANSPARENT);
stage.setScene(scene);
stage.centerOnScreen();
stage.show();
Circle dragger = new Circle(100, 100, 100);
dragger.setFill(new RadialGradient(-0.3, 135, 0.5, 0.5, 1, true,
CycleMethod.NO_CYCLE,
new Stop[] {
new Stop(0, Color.DARKGRAY),
new Stop(1, Color.BLACK)
}));
rootGroup.setOnMousePressed((MouseEvent me) -> {
initX = me.getScreenX() - stage.getX();
initY = me.getScreenY() - stage.getY();
});
rootGroup.setOnMouseDragged((MouseEvent me) -> {
stage.setX(me.getScreenX() - initX);
stage.setY(me.getScreenY() - initY);
});
Button close = new Button("Close me");
close.setOnAction((ActionEvent event) -> {
stage.close();
});
Button min = new Button("Minimize me");
min.setOnAction((ActionEvent event) -> {
stage.setIconified(true);
});
Text text = new Text("JavaFX");
text.setFill(Color.WHITESMOKE);
text.setEffect(new Lighting());
text.setBoundsType(TextBoundsType.VISUAL);
text.setFont(Font.font(Font.getDefault().getFamily(), 50));
VBox vBox = new VBox();
vBox.setSpacing(10);
vBox.setPadding(new Insets(60, 0, 0, 20));
vBox.setAlignment(Pos.TOP_CENTER);
vBox.getChildren().addAll(text, min, close);
rootGroup.getChildren().addAll(dragger, vBox);
});
return button;
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
