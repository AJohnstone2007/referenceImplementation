package hello;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
public class HelloBorderPane extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello BorderPane");
BorderPane borderpane = new BorderPane();
borderpane.setPadding(new Insets(10,10,10,10));
ToolBar toolbar = new ToolBar();
toolbar.getItems().addAll(new Button("Insert"), new Button("Delete"));
borderpane.setTop(toolbar);
FlowPane flow = new FlowPane();
flow.setHgap(4);
flow.setVgap(10);
flow.setPrefWrapLength(400);
InnerShadow shadow = new InnerShadow();
for (int r = 70; r > 3; r -= 4) {
Circle circle = new Circle();
circle.setEffect(shadow);
circle.setRadius(r);
circle.setFill(Color.RED);
flow.getChildren().add(circle);
}
borderpane.setCenter(flow);
borderpane.setBottom(new Label("My status is idle right now"));
borderpane.setLeft(new Separator());
borderpane.setRight(new Separator());
Scene scene = new Scene(borderpane, 500, 500);
stage.setScene(scene);
stage.show();
}
}
