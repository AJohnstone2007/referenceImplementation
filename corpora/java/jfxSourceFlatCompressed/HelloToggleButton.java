package hello;
import static javafx.scene.paint.Color.GHOSTWHITE;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloToggleButton extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
final ToggleGroup group = new ToggleGroup();
group.selectedToggleProperty().addListener(ov -> System.out.println("UserData for selected Toggle: " +
(group.getSelectedToggle() == null ? "****** no selected toggle******" :
group.getSelectedToggle().getUserData())));
ToggleButton button1 = new ToggleButton("No, *I* am your father");
button1.setUserData("Button 1");
button1.setToggleGroup(group);
button1.setSelected(true);
ToggleButton button2 = new ToggleButton("Nooooooooo!");
button2.setUserData("Button 2");
button2.setLayoutY(40);
button2.setToggleGroup(group);
ToggleButton button3 = new ToggleButton("No, *I* am your father");
button3.setToggleGroup(group);
ToggleButton button4 = new ToggleButton("Nooooooooo!");
button4.setToggleGroup(group);
VBox vbox = new VBox(10, button1, button2, button3, button4);
Scene scene = new Scene(vbox);
stage.setScene(scene);
stage.show();
}
}
