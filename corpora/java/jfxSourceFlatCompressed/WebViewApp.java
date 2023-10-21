package ensemble.samples.controls.webview;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
public class WebViewApp extends Application {
public Parent createContent() {
WebView webView = new WebView();
final WebEngine webEngine = webView.getEngine();
final String DEFAULT_URL = "http://www.oracle.com/us/index.html";
webEngine.load(DEFAULT_URL);
final TextField locationField = new TextField(DEFAULT_URL);
final ChangeListener<String> changeListener =
(ObservableValue<? extends String> observable,
String oldValue, String newValue) -> {
locationField.setText(newValue);
};
webEngine.locationProperty().addListener(changeListener);
EventHandler<ActionEvent> goAction = (ActionEvent event) -> {
webEngine.load(locationField.getText().startsWith("http://")
? locationField.getText()
: "http://" + locationField.getText());
};
locationField.setOnAction(goAction);
Button goButton = new Button("Go");
goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
goButton.setDefaultButton(true);
goButton.setOnAction(goAction);
HBox hBox = new HBox(5);
hBox.getChildren().setAll(locationField, goButton);
HBox.setHgrow(locationField, Priority.ALWAYS);
final VBox vBox = new VBox(5);
vBox.getChildren().setAll(hBox, webView);
vBox.setPrefSize(800, 400);
VBox.setVgrow(webView, Priority.ALWAYS);
return vBox;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
