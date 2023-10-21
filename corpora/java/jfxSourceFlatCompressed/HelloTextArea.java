package hello;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloTextArea extends Application {
@Override
public void start(Stage stage) {
stage.setTitle("Hello TextArea");
stage.setWidth(800);
stage.setHeight(600);
Scene scene = new Scene(new Group());
scene.setFill(Color.GHOSTWHITE);
FlowPane root = new FlowPane();
root.setOrientation(Orientation.VERTICAL);
scene.setRoot(root);
root.setPadding(new Insets(8, 8, 8, 8));
root.setVgap(8);
TextField textField = new TextField("abcdefghijklmnop");
textField.setPrefColumnCount(8);
root.getChildren().add(textField);
TextArea textArea = new TextArea();
textArea.setPrefColumnCount(24);
root.getChildren().add(textArea);
textArea.textProperty().addListener((observable, oldValue, newValue) -> {
});
ToggleButton wrapButton = new ToggleButton("Wrap Text");
textArea.wrapTextProperty().bind(wrapButton.selectedProperty());
wrapButton.setSelected(true);
root.getChildren().add(wrapButton);
ToggleButton editableButton = new ToggleButton("Editable");
textArea.editableProperty().bind(editableButton.selectedProperty());
editableButton.setSelected(true);
root.getChildren().add(editableButton);
wrapButton.requestFocus();
for (int i = 0; i < 20; i++) {
textArea.insertText(textArea.getLength(), i + ". Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n");
}
textArea.selectRange(0, 0);
stage.setScene(scene);
stage.show();
textArea.requestFocus();
}
public static void main(String[] args) {
Application.launch(HelloTextArea.class, args);
}
}
