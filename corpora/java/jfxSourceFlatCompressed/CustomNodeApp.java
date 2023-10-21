package ensemble.samples.scenegraph.customnode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
public class CustomNodeApp extends Application {
public Parent createContent() {
VBox vbox = new VBox();
MyNode myNode = new MyNode("MyNode");
MyNode parent = new MyNode("Parent");
Polygon arrow = createUMLArrow();
Label extend = new Label("<<extends>>");
extend.setStyle("-fx-padding: 0 0 0 -1em;");
vbox.getChildren().addAll(parent, arrow, myNode);
vbox.setAlignment(Pos.CENTER);
HBox hbox = new HBox();
hbox.setAlignment(Pos.CENTER);
hbox.setPadding(new Insets(10));
hbox.getChildren().addAll(vbox, extend);
return hbox;
}
public static Polygon createUMLArrow() {
Polygon polygon = new Polygon(new double[]{
7.5, 0,
15, 15,
7.51, 15,
7.51, 40,
7.49, 40,
7.49, 15,
0, 15
});
polygon.setFill(Color.WHITE);
polygon.setStroke(Color.BLACK);
return polygon;
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
