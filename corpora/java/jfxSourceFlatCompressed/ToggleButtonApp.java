package ensemble.samples.controls.togglebutton;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
public class ToggleButtonApp extends Application {
public Parent createContent() {
final Label label = new Label();
label.setStyle("-fx-font-size: 2em;");
label.setAlignment(Pos.CENTER);
ToggleGroup group = new ToggleGroup();
final ToggleButton cat = new ToggleButton("Cat");
final ToggleButton dog = new ToggleButton("Dog");
final ToggleButton horse = new ToggleButton("Horse");
cat.setMinSize(72, 40);
dog.setMinSize(72, 40);
horse.setMinSize(72, 40);
cat.setToggleGroup(group);
dog.setToggleGroup(group);
horse.setToggleGroup(group);
final ChangeListener<Toggle> changeListener =
(ObservableValue<? extends Toggle> observable,
Toggle oldValue, Toggle selectedToggle) -> {
if (selectedToggle != null) {
label.setText(((ToggleButton) selectedToggle).getText());
} else {
label.setText("...");
}
};
group.selectedToggleProperty().addListener(changeListener);
group.selectToggle(cat);
GridPane.setConstraints(cat, 0, 0);
GridPane.setConstraints(dog, 1, 0);
GridPane.setConstraints(horse, 2, 0);
GridPane.setConstraints(label, 0, 1, 3, 1, HPos.CENTER, VPos.BASELINE);
final GridPane grid = new GridPane();
grid.setVgap(20);
grid.setHgap(12);
grid.getChildren().addAll(cat, dog, horse, label);
grid.setAlignment(Pos.CENTER);
return grid;
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
