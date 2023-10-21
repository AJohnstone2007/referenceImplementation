package hello;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloControls extends Application {
private static String[] hellos = new String[] {
"Hello World",
"\u00D0\u00BF\u00D1\u0080\u00D0\u00B8\u00D0\u00B2\u00D0\u00B5\u00D1\u0082 \u00D0\u00BC\u00D0\u00B8\u00D1\u0080",
"Hola Mundo"
};
@Override public void start(Stage stage) {
stage.setTitle("Hello Controls");
Scene scene = new Scene(new Group(), 600, 450);
scene.setFill(Color.CHOCOLATE);
int offset = 0;
for(String hello : hellos) {
offset += 40;
Button button = new Button();
button.setLayoutX(25);
button.setLayoutY(offset);
button.setText(hello);
((Group)scene.getRoot()).getChildren().add(button);
}
stage.setScene(scene);
stage.show();
offset += 40;
Slider slider = new Slider();
slider.setLayoutX(25);
slider.setLayoutY(offset);
slider.setShowTickMarks(true);
slider.setShowTickLabels(true);
((Group)scene.getRoot()).getChildren().add(slider);
offset += 40;
Slider vslider = new Slider();
vslider.setOrientation(Orientation.VERTICAL);
vslider.setLayoutX(25);
vslider.setLayoutY(offset);
((Group)scene.getRoot()).getChildren().add(vslider);
}
public static void main(String[] args) {
Application.launch(args);
}
}
