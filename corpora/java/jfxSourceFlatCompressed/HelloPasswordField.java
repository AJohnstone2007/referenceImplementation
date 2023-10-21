package a11y;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloPasswordField extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
Label label = new Label("secret name:");
PasswordField pf = new PasswordField();
label.setLabelFor(pf);
pf.setText("senha");
Button button = new Button("push");
Scene scene = new Scene(new VBox(label, pf, button), 300, 300);
stage.setScene(scene);
stage.show();
}
}
