package hello;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloToolBar extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("ToolBar");
final VBox box = new VBox(10);
final Scene scene = new Scene(box, 500, 500);
final ToolBar tb = new ToolBar();
tb.setOrientation(Orientation.HORIZONTAL);
tb.getItems().add(new Button("button 1"));
tb.getItems().add(new Separator());
tb.getItems().add(new Button("button 2"));
tb.getItems().add(new Region());
tb.getItems().add(new Button("button 3"));
final ToolBar tb2 = new ToolBar();
tb2.setOrientation(Orientation.VERTICAL);
tb2.getItems().add(new Button("button 1"));
tb2.getItems().add(new Separator());
tb2.getItems().add(new Button("button 2"));
tb2.getItems().add(new Region());
tb2.getItems().add(new Button("button 3"));
box.getChildren().add(tb);
HBox hbox = new HBox();
hbox.getChildren().add(tb2);
box.getChildren().add(hbox);
stage.setScene(scene);
stage.show();
}
}
