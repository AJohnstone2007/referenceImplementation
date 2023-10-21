package ensemble.samples.controls.checkbox;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class CheckBoxApp extends Application {
public Parent createContent() {
VBox vbox = new VBox();
vbox.setSpacing(10);
CheckBox cb1 = new CheckBox("Simple checkbox");
CheckBox cb2 = new CheckBox("Three-state checkbox");
cb2.setAllowIndeterminate(true);
cb2.setIndeterminate(false);
CheckBox cb3 = new CheckBox("Disabled");
cb3.setSelected(true);
cb3.setDisable(true);
vbox.getChildren().addAll(cb1, cb2, cb3);
vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
return vbox;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
