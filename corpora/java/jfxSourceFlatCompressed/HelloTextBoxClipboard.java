package hello;
import java.util.Set;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloTextBoxClipboard extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("TextBoxClipboard");
Scene scene = new Scene(new Group(), 600, 450);
scene.setFill(Color.GHOSTWHITE);
final Label copyFrom = new Label("Copy From: ");
final TextField copyFromText = new TextField("ABC 123");
final HBox cf = new HBox();
cf.getChildren().add(copyFrom);
cf.getChildren().add(copyFromText);
final Label copyTo = new Label("Copy To: ");
final TextField copyToText = new TextField();
final HBox ct = new HBox();
ct.getChildren().add(copyTo);
ct.getChildren().add(copyToText);
final HBox btns = new HBox();
final Button copy = new Button("Copy");
copy.setOnAction(e -> {
ClipboardContent content = new ClipboardContent();
content.putString(copyFromText.getText());
Clipboard.getSystemClipboard().setContent(content);
});
final Button paste = new Button("Paste");
paste.setOnAction(e -> {
Set<DataFormat> types = Clipboard.getSystemClipboard().getContentTypes();
for (DataFormat type: types) {
System.out.println("TYPE: " + type);
}
copyToText.setText(Clipboard.getSystemClipboard().getString());
});
btns.getChildren().add(copy);
btns.getChildren().add(paste);
final VBox vbox = new VBox();
vbox.setPadding(new Insets(30, 0, 0, 0));
vbox.setSpacing(25);
vbox.getChildren().add(btns);
vbox.getChildren().add(cf);
vbox.getChildren().add(ct);
scene.setRoot(vbox);
stage.setScene(scene);
stage.show();
}
}
