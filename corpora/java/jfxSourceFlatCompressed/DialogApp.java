package ensemble.samples.controls.dialog;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class DialogApp extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
stage = primaryStage;
}
private AlertType type = AlertType.INFORMATION;
private Stage stage;
public void setAlertType(AlertType at) {
type = at;
}
protected Alert createAlert() {
Alert alert = new Alert(type, "");
alert.initModality(Modality.APPLICATION_MODAL);
alert.initOwner(stage);
alert.getDialogPane().setContentText(type + " text.");
alert.getDialogPane().setHeaderText(null);
alert.showAndWait()
.filter(response -> response == ButtonType.OK)
.ifPresent(response -> System.out.println("The alert was approved"));
return alert;
}
protected Dialog<ButtonType> createExceptionDialog(Throwable th) {
Dialog<ButtonType> dialog = new Dialog<ButtonType>();
dialog.setTitle("Program exception");
final DialogPane dialogPane = dialog.getDialogPane();
dialogPane.setContentText("Details of the problem:");
dialogPane.getButtonTypes().addAll(ButtonType.OK);
dialogPane.setContentText(th.getMessage());
dialog.initModality(Modality.APPLICATION_MODAL);
Label label = new Label("Exception stacktrace:");
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
th.printStackTrace(pw);
pw.close();
TextArea textArea = new TextArea(sw.toString());
textArea.setEditable(false);
textArea.setWrapText(true);
textArea.setMaxWidth(Double.MAX_VALUE);
textArea.setMaxHeight(Double.MAX_VALUE);
GridPane.setVgrow(textArea, Priority.ALWAYS);
GridPane.setHgrow(textArea, Priority.ALWAYS);
GridPane root = new GridPane();
root.setVisible(false);
root.setMaxWidth(Double.MAX_VALUE);
root.add(label, 0, 0);
root.add(textArea, 0, 1);
dialogPane.setExpandableContent(root);
dialog.showAndWait()
.filter(response -> response == ButtonType.OK)
.ifPresent(response -> System.out.println("The exception was approved"));
return dialog;
}
protected Dialog createTextInputDialog() {
TextInputDialog textInput = new TextInputDialog("");
textInput.setTitle("Text Input Dialog");
textInput.getDialogPane().setContentText("First Name:");
textInput.showAndWait()
.ifPresent(response -> {
if (response.isEmpty()) {
System.out.println("No name was inserted");
} else {
System.out.println("The first name is: " + response);
}
});
return textInput;
}
public Parent createContent() {
Group group = new Group();
HBox alertBox = new HBox(10);
VBox dialogCreators = new VBox(20);
ComboBox alert_types = new ComboBox<String>();
alert_types.getItems().addAll("Information", "Warning",
"Confirmation", "Error");
alert_types.setValue("Information");
Button create = new Button("Create Alert");
create.setTooltip(new Tooltip("Create an Alert Dialog"));
create.setOnAction(e ->
{
String type = (String) alert_types.getValue();
switch (type) {
case "Information":
setAlertType(AlertType.INFORMATION);
break;
case "Warning":
setAlertType(AlertType.WARNING);
break;
case "Confirmation":
setAlertType(AlertType.CONFIRMATION);
break;
case "Error":
setAlertType(AlertType.ERROR);
break;
}
createAlert();
});
alertBox.getChildren().addAll(alert_types, create);
Button exception = new Button("Create Exception Dialog");
exception.setTooltip(new Tooltip("Create an Exception Dialog"));
exception.setOnAction(e -> createExceptionDialog(new RuntimeException("oops")));
Button input = new Button("Create Text Input Dialog");
input.setTooltip(new Tooltip("Create an Text Input Dialog"));
input.setOnAction(e -> createTextInputDialog());
dialogCreators.getChildren().addAll(alertBox, exception, input);
group.getChildren().add(dialogCreators);
return group;
}
}
