package a11y;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloSimpleCheckBox extends Application {
@Override public void start(Stage stage) {
CheckBox cbox = new CheckBox("Choose this item");
cbox.setIndeterminate(true);
cbox.setAllowIndeterminate(true);
Label label = new Label();
label.textProperty().bind(
Bindings.when(cbox.indeterminateProperty()).
then("The check box is indeterminate").
otherwise(
Bindings.when(cbox.selectedProperty()).
then("The check box is selected").
otherwise("The check box is not selected"))
);
VBox vbox = new VBox(7);
vbox.setAlignment(Pos.CENTER);
vbox.getChildren().addAll(label, cbox, new Button("OK"));
Scene scene = new Scene(vbox, 400, 400);
scene.setFill(Color.SKYBLUE);
stage.setTitle("Hello CheckBox");
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
