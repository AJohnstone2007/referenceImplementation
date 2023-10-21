package hello;
import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class HelloModality extends Application {
static final int offset = 25;
static int counter = 0;
public static void main(String[] args) {
Application.launch(args);
}
Scene createScene(final Stage stage) {
Group root = new Group();
int xyOffset = offset * counter++;
if (xyOffset > 200) xyOffset = 0;
Scene scene = new Scene(root, 600 - xyOffset, 450 - xyOffset);
final CheckBox checker = new CheckBox("Owner");
checker.setSelected(true);
checker.setLayoutX(25);
checker.setLayoutY(40);
root.getChildren().add(checker);
final ToggleGroup modalityGroup = new ToggleGroup();
final RadioButton noneButton = new RadioButton("NONE");
noneButton.setSelected(true);
noneButton.setLayoutX(155);
noneButton.setLayoutY(40);
noneButton.setToggleGroup(modalityGroup);
final RadioButton windowButton = new RadioButton("WINDOW_MODAL");
windowButton.setMnemonicParsing(false);
windowButton.setLayoutX(155);
windowButton.setLayoutY(60);
windowButton.setToggleGroup(modalityGroup);
final RadioButton applicationButton = new RadioButton("APPLICATION_MODAL");
applicationButton.setMnemonicParsing(false);
applicationButton.setLayoutX(155);
applicationButton.setLayoutY(80);
applicationButton.setToggleGroup(modalityGroup);
root.getChildren().add(noneButton);
root.getChildren().add(windowButton);
root.getChildren().add(applicationButton);
final ToggleGroup styleGroup = new ToggleGroup();
final RadioButton sdButton = new RadioButton("DECORATED");
sdButton.setSelected(true);
sdButton.setLayoutX(325);
sdButton.setLayoutY(40);
sdButton.setToggleGroup(styleGroup);
final RadioButton sudButton = new RadioButton("UNDECORATED");
sudButton.setLayoutX(325);
sudButton.setLayoutY(60);
sudButton.setToggleGroup(styleGroup);
final RadioButton stButton = new RadioButton("TRANSPARENT");
stButton.setLayoutX(325);
stButton.setLayoutY(80);
stButton.setToggleGroup(styleGroup);
final RadioButton suButton = new RadioButton("UTILITY");
suButton.setLayoutX(325);
suButton.setLayoutY(100);
suButton.setToggleGroup(styleGroup);
root.getChildren().add(sdButton);
root.getChildren().add(sudButton);
root.getChildren().add(stButton);
root.getChildren().add(suButton);
Button button = new Button("Create Dialog");
button.setLayoutX(100);
button.setLayoutY(200);
button.setOnAction(new EventHandler<ActionEvent>() {
@Override public void handle(ActionEvent e) {
final Stage dialog = new Stage();
boolean owned = checker.isSelected();
dialog.initOwner(owned ? stage : null);
Modality modality;
if (applicationButton.isSelected()) {
modality = Modality.APPLICATION_MODAL;
} else if (windowButton.isSelected()) {
modality = Modality.WINDOW_MODAL;
} else {
modality = Modality.NONE;
}
dialog.initModality(modality);
StageStyle stageStyle;
if (suButton.isSelected()) {
stageStyle = StageStyle.UTILITY;
} else if (sudButton.isSelected()) {
stageStyle = StageStyle.UNDECORATED;
} else if (stButton.isSelected()) {
stageStyle = StageStyle.TRANSPARENT;
} else {
stageStyle = StageStyle.DECORATED;
}
dialog.initStyle(stageStyle);
dialog.initModality(modality);
dialog.setTitle("Owner: " + (owned ? stage : null) + " * Modality: "
+ modality.toString() + " * Style: " + stageStyle.toString());
Scene dialogScene = createScene(dialog);
Group dialogRoot = (Group) dialogScene.getRoot();
Button dialogButton = new Button("Dismiss");
dialogButton.setLayoutX(275);
dialogButton.setLayoutY(200);
dialogButton.setOnAction(new EventHandler<ActionEvent>() {
@Override public void handle(ActionEvent e) {
dialog.hide();
}
});
dialogRoot.getChildren().add(dialogButton);
dialog.setScene(dialogScene);
dialog.show();
}
});
root.getChildren().add(button);
Button button2 = new Button("Click Me");
button2.setLayoutX(200);
button2.setLayoutY(200);
button2.setOnAction(new EventHandler<ActionEvent>() {
@Override public void handle(ActionEvent e) {
System.out.println("Event: " + e);
}
});
root.getChildren().add(button2);
final CheckBox ownedFileChooser = new CheckBox("Owned file chooser");
ownedFileChooser.setSelected(true);
ownedFileChooser.setLayoutX(25);
ownedFileChooser.setLayoutY(240);
root.getChildren().add(ownedFileChooser);
Button button3 = new Button("File Chooser");
button3.setLayoutX(200);
button3.setLayoutY(240);
button3.setOnAction(new EventHandler<ActionEvent>() {
@Override public void handle(ActionEvent e) {
FileChooser fc = new FileChooser();
File f = fc.showOpenDialog(ownedFileChooser.isSelected() ? stage : null);
System.err.println("Selected file: " + f);
}
});
root.getChildren().add(button3);
return scene;
}
@Override public void start(final Stage primaryStage) {
primaryStage.setTitle("HelloModality");
Scene scene = createScene(primaryStage);
primaryStage.setScene(scene);
primaryStage.show();
}
}
