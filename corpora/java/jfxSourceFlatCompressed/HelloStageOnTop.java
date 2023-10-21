package hello;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloStageOnTop extends Application{
public static final String ENABLE_ON_TOP = "Enable on top";
public static final String DISABLE_ON_TOP = "Disable on top";
@Override
public void start(Stage primaryStage) throws Exception {
Button button = new Button("Open Root stage with child");
CheckBox box = new CheckBox("Root is always on top");
box.setSelected(true);
VBox root = new VBox(15, box, button);
root.setPadding(new Insets(20));
Scene scene = new Scene(root);
button.setOnAction(event -> createNewStage(0, null, box.isSelected()));
primaryStage.setScene(scene);
primaryStage.show();
}
private void createNewStage(int level, Stage owner, boolean onTop) {
Stage stage = new Stage();
stage.initOwner(owner);
stage.setTitle(level == 0 ? "Root" : "Child " + level);
stage.setAlwaysOnTop(onTop);
VBox root = new VBox(15);
root.setPadding(new Insets(20));
Scene scene = new Scene(root);
stage.setScene(scene);
ToggleButton onTopButton = new ToggleButton(onTop ? DISABLE_ON_TOP : ENABLE_ON_TOP);
onTopButton.setSelected(onTop);
stage.alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> {
onTopButton.setSelected(newValue);
onTopButton.setText(newValue ? DISABLE_ON_TOP : ENABLE_ON_TOP);
});
onTopButton.setOnAction(event -> stage.setAlwaysOnTop(!stage.isAlwaysOnTop()));
CheckBox box = new CheckBox("Child stage always on top");
box.setSelected(true);
Button newStageButton = new Button("Open child stage");
newStageButton.setOnAction(event -> createNewStage(level + 1, stage, box.isSelected()));
root.getChildren().addAll(onTopButton, box, newStageButton);
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
