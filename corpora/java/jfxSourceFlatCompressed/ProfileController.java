package ensemble.samples.language.fxml;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
public class ProfileController extends AnchorPane implements Initializable {
@FXML
private TextField user;
@FXML
private TextField phone;
@FXML
private TextField email;
@FXML
private TextArea address;
@FXML
private CheckBox subscribed;
@FXML
private Hyperlink logout;
@FXML
private Button save;
@FXML
private Label success;
private FXMLLoginDemoApp application;
public void setApp(FXMLLoginDemoApp application){
this.application = application;
User loggedUser = application.getLoggedUser();
user.setText(loggedUser.getId());
email.setText(loggedUser.getEmail());
phone.setText(loggedUser.getPhone());
if (loggedUser.getAddress() != null) {
address.setText(loggedUser.getAddress());
}
subscribed.setSelected(loggedUser.isSubscribed());
success.setOpacity(0);
}
@Override
public void initialize(URL location, ResourceBundle resources) {
}
public void processLogout(ActionEvent event) {
if (application == null){
return;
}
application.userLogout();
}
public void saveProfile(ActionEvent event) {
if (application == null){
animateMessage();
return;
}
User loggedUser = application.getLoggedUser();
loggedUser.setEmail(email.getText());
loggedUser.setPhone(phone.getText());
loggedUser.setSubscribed(subscribed.isSelected());
loggedUser.setAddress(address.getText());
animateMessage();
}
public void resetProfile(ActionEvent event){
if (application == null){
return;
}
email.setText("");
phone.setText("");
subscribed.setSelected(false);
address.setText("");
success.setOpacity(0.0);
}
private void animateMessage() {
FadeTransition ft = new FadeTransition(Duration.millis(1000), success);
ft.setFromValue(0.0);
ft.setToValue(1);
ft.play();
}
}
