package hello;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloTooltip extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
Button button1 = new Button("Cut");
button1.setTooltip(new Tooltip("Tooltip Button 1"));
Button button2 = new Button("Copy");
button2.setTooltip(new Tooltip("Tooltip Button 2"));
Button button3 = new Button("Paste");
button3.setTooltip(new Tooltip("Tooltip Button 3"));
Button button4 = new Button("WrapTooltip");
Tooltip t = new Tooltip("This is a long tooltip with wrapText set to true; and width set to 80. So should wrap!");
t.setPrefWidth(80);
t.setWrapText(true);
button4.setTooltip(t);
HBox hbox = new HBox(5);
hbox.getChildren().addAll(button1, button2, button3, button4);
Scene scene = new Scene(hbox, 400, 300);
scene.setFill(Color.CHOCOLATE);
stage.setScene(scene);
stage.setTitle("Hello Tooltip");
stage.show();
}
}
