package hello;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
public class HelloTextField extends Application {
private double fontSize = Font.getDefault().getSize();
private Insets insets = new Insets(3, 5, 3, 5);
@Override public void start(Stage stage) {
final TextField textField = new TextField();
final PasswordField passwordField = new PasswordField();
Button setIllegalCharsBtn = new Button("Set Illegal Characters");
setIllegalCharsBtn.setOnAction(event -> textField.setText("Illegal characters here -->" + '\05' + '\07' + '\0' + "<--"));
Button increasePrefColumnCountBtn = new Button("Increase prefColumnCount");
increasePrefColumnCountBtn.setOnAction(event -> {
textField.setPrefColumnCount(textField.getPrefColumnCount() + 5);
passwordField.setPrefColumnCount(passwordField.getPrefColumnCount() + 5);
});
Button decreasePrefColumnCountBtn = new Button("Decrease prefColumnCount");
decreasePrefColumnCountBtn.setOnAction(event -> {
textField.setPrefColumnCount(textField.getPrefColumnCount() - 5);
passwordField.setPrefColumnCount(passwordField.getPrefColumnCount() - 5);
});
Button increaseFontSizeBtn = new Button("Increase Font Size");
increaseFontSizeBtn.setOnAction(event -> {
fontSize += 1;
textField.setStyle("-fx-font-size: " + fontSize + "pt");
passwordField.setStyle("-fx-font-size: " + fontSize + "pt");
});
Button decreaseFontSizeBtn = new Button("Decrease Font Size");
decreaseFontSizeBtn.setOnAction(event -> {
fontSize -= 1;
textField.setStyle("-fx-font-size: " + fontSize + "pt");
passwordField.setStyle("-fx-font-size: " + fontSize + "pt");
});
Button defaultBtn = new Button("Default Action");
defaultBtn.setDefaultButton(true);
defaultBtn.setOnAction(event -> System.out.println("Default Action"));
VBox fieldBox = new VBox(5);
fieldBox.setFillWidth(false);
fieldBox.getChildren().addAll(textField, passwordField);
VBox buttonBox = new VBox(5);
buttonBox.getChildren().addAll(
setIllegalCharsBtn,
increasePrefColumnCountBtn,
decreasePrefColumnCountBtn,
increaseFontSizeBtn,
decreaseFontSizeBtn,
defaultBtn);
HBox root = new HBox(7);
root.setPadding(new Insets(11, 12, 12, 11));
root.getChildren().addAll(fieldBox, buttonBox);
stage.setScene(new Scene(root, 800, 600));
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
