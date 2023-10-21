package ensemble.samples.language.fxml;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
public class FXMLLoginDemoApp extends Application {
private Group root = new Group();
private User loggedUser;
private final double MINIMUM_WINDOW_WIDTH = 390.0;
private final double MINIMUM_WINDOW_HEIGHT = 500.0;
public Parent createContent() {
gotoLogin();
return root;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
public User getLoggedUser() {
return loggedUser;
}
public boolean userLogging(String userId, String password){
if (Authenticator.validate(userId, password)) {
loggedUser = User.of(userId);
gotoProfile();
return true;
} else {
return false;
}
}
void userLogout(){
loggedUser = null;
gotoLogin();
}
private void gotoProfile() {
try {
ProfileController profile =
(ProfileController)replaceSceneContent("Profile.fxml");
profile.setApp(this);
} catch (Exception ex) {
Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
}
}
private void gotoLogin() {
try {
LoginController login =
(LoginController)replaceSceneContent("Login.fxml");
login.setApp(this);
} catch (Exception ex) {
Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
}
}
private Initializable replaceSceneContent(String fxml) throws Exception {
FXMLLoader loader = new FXMLLoader();
InputStream in = FXMLLoginDemoApp.class.getResourceAsStream(fxml);
loader.setBuilderFactory(new JavaFXBuilderFactory());
loader.setLocation(FXMLLoginDemoApp.class.getResource(fxml));
AnchorPane page;
try {
page = (AnchorPane) loader.load(in);
} finally {
in.close();
}
root.getChildren().removeAll();
root.getChildren().addAll(page);
return (Initializable)loader.getController();
}
}
