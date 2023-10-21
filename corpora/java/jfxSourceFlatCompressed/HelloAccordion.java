package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloAccordion extends Application {
@Override public void start(Stage stage) {
TitledPane t1 = new TitledPane();
t1.setId("Label 1");
t1.setText("Label 1");
t1.setContent(new Button("This is Button 1\n\nAnd there were a few empty lines just there!"));
TitledPane t2 = new TitledPane();
t2.setId("Label 2");
t2.setText("Label 2");
t2.setContent(new Label("This is Label 2\n\nAnd there were a few empty lines just there!"));
TitledPane t3 = new TitledPane();
t3.setId("Label 3");
t3.setText("Label 3");
t3.setContent(new Button("This is Button 3\n\nAnd there were a few empty lines just there!"));
Accordion accordion = new Accordion();
accordion.getPanes().add(t1);
accordion.getPanes().add(t2);
accordion.getPanes().add(t3);
stage.setTitle("Accordion Sample");
final VBox root = new VBox(20);
root.setFillWidth(false);
Scene scene = new Scene(root, 500, 500);
root.getChildren().add(accordion);
root.getChildren().add(new Button("This button changes it's layout when Accordion is used"));
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
