package hello.dialog.fxml;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class FXMLSampleDialog extends Application {
@FXML private DialogPane dialogPane;
@FXML private ButtonType helpButtonType;
@FXML private TextField firstNameField;
@FXML private TextField lastNameField;
@FXML private TextField emailField;
public static void main(String[] args) {
launch(args);
}
@Override public void start(Stage primaryStage) {
showAndWait();
}
public void showAndWait() {
try {
FXMLLoader.load(getClass().getResource("FXMLSampleDialog.fxml"));
} catch (Exception e) {
e.printStackTrace();
}
}
@FXML private void initialize() {
Dialog<Person> dialog = new Dialog<>();
dialog.setDialogPane(dialogPane);
dialog.setResultConverter(buttonType -> {
return buttonType == ButtonType.OK ?
new Person(firstNameField.getText(),
lastNameField.getText(),
emailField.getText()) : null;
});
Node helpButton = dialogPane.lookupButton(helpButtonType);
((Button)helpButton).addEventFilter(ActionEvent.ACTION, event -> {
System.out.println("It's ok to ask for help!");
event.consume();
});
dialog.showAndWait().ifPresent(result -> System.out.println("Result is " + result));
}
public static class Person {
public String firstName;
public String lastName;
public String email;
public Person(String firstName, String lastName, String email) {
this.firstName = firstName;
this.lastName = lastName;
this.email = email;
}
@Override public String toString() {
return "Person [firstName: " + firstName + ", lastName: " + lastName + ", email: " + email + "]";
}
}
}
