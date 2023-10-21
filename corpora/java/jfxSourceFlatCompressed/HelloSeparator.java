package hello;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
public class HelloSeparator extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello Separator");
Scene scene = new Scene(new Group(), 200, 600);
int offsetX = 0;
int offsetY = 0;
final List<Separator> separators = new ArrayList<Separator>();
for(int i = 0; i < 10; i++) {
Separator s = new Separator();
separators.add(s);
s.setLayoutX(25 + offsetX);
s.setLayoutY(40 + offsetY);
offsetY += 45;
offsetX += 5;
((Group)scene.getRoot()).getChildren().add(s);
}
ToggleButton toggle = new ToggleButton("Horizontal Slider");
toggle.setSelected(true);
toggle.selectedProperty().addListener(ov -> {
for (Separator s : separators) {
s.setOrientation(s.getOrientation() == Orientation.VERTICAL ?
Orientation.HORIZONTAL : Orientation.VERTICAL);
}
});
((Group)scene.getRoot()).getChildren().add(toggle);
stage.setScene(scene);
stage.show();
}
}
