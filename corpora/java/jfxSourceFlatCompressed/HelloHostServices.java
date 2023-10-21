package hello;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloHostServices extends Application {
@Override
public void start(Stage stage) {
final HostServices hs = getHostServices();
final Label codeBaseText = new Label();
final Label documentBaseText = new Label();
final Label showDocumentText = new Label();
stage.setTitle("Hello HostServices");
String userDir = System.getProperty("user.dir");
userDir = userDir.replace("\\", "/");
System.err.println("userDir = " + userDir);
if (!userDir.startsWith("/")) {
userDir = "/" + userDir;
}
String testDocBase = "file:" + userDir + "/";
System.err.println("testDocBase = " + testDocBase);
System.err.println("DocumentBase = " + hs.getDocumentBase());
System.err.println("testDocBase.equals(hs.getDocumentBase()) = "
+ testDocBase.equals(hs.getDocumentBase()));
Button getCodeBaseBtn = new Button("Get CodeBase");
getCodeBaseBtn.setOnAction(event -> codeBaseText.setText(
hs.getCodeBase().isEmpty() ? "EMPTY" : hs.getCodeBase()));
documentBaseText.setPrefWidth(400);
Button getDocumentBaseBtn = new Button("Get DocumentBase");
getDocumentBaseBtn.setOnAction(event -> documentBaseText.setText(hs.getDocumentBase()));
Button showDocmentBtn = new Button("Show Document");
showDocmentBtn.setOnAction(event
-> showDocument(hs, showDocumentText, "http://www.oracle.com/java/"));
VBox textBox = new VBox(15);
textBox.setFillWidth(false);
textBox.getChildren().addAll(codeBaseText, documentBaseText, showDocumentText);
VBox buttonBox = new VBox(5);
buttonBox.getChildren().addAll(
getCodeBaseBtn,
getDocumentBaseBtn,
showDocmentBtn);
HBox root = new HBox(7);
root.setPadding(new Insets(11, 12, 12, 11));
root.getChildren().addAll(buttonBox, textBox);
stage.setScene(new Scene(root, 600, 150));
stage.show();
}
private void showDocument(HostServices hs, Label showDocumentText, String urlStr) {
showDocumentText.setText(urlStr + " will be shown in the browser.");
hs.showDocument(urlStr);
}
public static void main(String[] args) {
Application.launch(args);
}
}
