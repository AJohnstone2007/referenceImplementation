package ensemble.samples.language.fxml;
import java.awt.Color;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
public class LoginController extends AnchorPane implements Initializable {
@FXML
TextField userId;
@FXML
PasswordField password;
@FXML
Button login;
@FXML
Label errorMessage;
private FXMLLoginDemoApp application;
public void setApp(FXMLLoginDemoApp application){
this.application = application;
}
@Override
public void initialize(URL location, ResourceBundle resources) {
errorMessage.setText("");
userId.setPromptText("demo");
password.setPromptText("demo");
}
public void processLogin(ActionEvent event) {
if (application == null){
errorMessage.setText("Hello " + userId.getText());
} else {
if (!application.userLogging(userId.getText(), password.getText())){
errorMessage.setText("Username/Password is incorrect");
}
}
}
}
