package hello;
import static javafx.scene.paint.Color.GHOSTWHITE;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloRadioButton extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
VBox vbox = new VBox(20);
HBox sides = new HBox(14);
vbox.setAlignment(Pos.CENTER);
sides.setAlignment(Pos.CENTER);
VBox quotes = new VBox(10);
quotes.setAlignment(Pos.CENTER_LEFT);
ToggleGroup toggle = new ToggleGroup();
RadioButton button1 = new RadioButton("No, *I* am your father");
button1.setToggleGroup(toggle);
button1.setSelected(true);
RadioButton button2 = new RadioButton("Nooooooooo!");
button2.setLayoutY(40);
button2.setToggleGroup(toggle);
quotes.getChildren().addAll(button1, button2);
Button clear = new Button("Clear Selection");
sides.getChildren().add(new Label("Select Side:"));
ToggleGroup sideGroup = new ToggleGroup();
for (final Side side : Side.class.getEnumConstants()) {
final RadioButton rb = new RadioButton(side.toString());
rb.setToggleGroup(sideGroup);
if (side == Side.BOTTOM) {
rb.setSelected(true);
}
sides.getChildren().add(rb);
}
clear.setOnAction(e -> {
Toggle tog = sideGroup.getSelectedToggle();
if (tog != null) {
RadioButton rb = (RadioButton)tog;
rb.setSelected(false);
}
});
vbox.getChildren().addAll(quotes, clear, sides);
vbox.setLayoutX(20);
vbox.setLayoutY(20);
Group group = new Group();
Scene scene = new Scene(group);
scene.setFill(GHOSTWHITE);
group.getChildren().add(vbox);
stage.setTitle("Hello RadioButton");
stage.setWidth(450);
stage.setHeight(300);
stage.setScene(scene);
stage.setResizable(false);
stage.show();
}
}
