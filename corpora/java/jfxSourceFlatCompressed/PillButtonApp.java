package ensemble.samples.controls.button.pillbutton;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class PillButtonApp extends Application {
public Parent createContent() {
ToggleButton tb1 = new ToggleButton("Left");
tb1.setPrefSize(76, 45);
tb1.getStyleClass().add("left-pill");
ToggleButton tb2 = new ToggleButton("Center");
tb2.setPrefSize(76, 45);
tb2.getStyleClass().add("center-pill");
ToggleButton tb3 = new ToggleButton("Right");
tb3.setPrefSize(76, 45);
tb3.getStyleClass().add("right-pill");
final ToggleGroup group = new ToggleGroup();
tb1.setToggleGroup(group);
tb2.setToggleGroup(group);
tb3.setToggleGroup(group);
group.selectToggle(tb1);
final ChangeListener<Toggle> listener =
(ObservableValue<? extends Toggle> observable,
Toggle old, Toggle now) -> {
if (now == null) {
group.selectToggle(old);
}
};
group.selectedToggleProperty().addListener(listener);
final String pillButtonCss =
getClass().getResource("PillButton.css").toExternalForm();
final HBox hBox = new HBox();
hBox.setAlignment(Pos.CENTER);
hBox.getChildren().addAll(tb1, tb2, tb3);
hBox.getStylesheets().add(pillButtonCss);
return hBox;
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
