package hello;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
public class HelloWebView extends Application {
private static final String DEFAULT_URL = "http://www.oracle.com/java/";
private static final double NAVI_BAR_MIN_DIMENSION = 32.0;
private static final double PADDING_VALUE = 2.0;
private static final String buttonStyle = "-fx-font-weight: bold; -fx-font-size: 16px;";
private static final String goButtonUnicodeSymbol = "\u21B5";
private static final String stopButtonUnicodeSymbol = "\u2715";
private static final String backButtonUnicodeSymbol = "\u003C";
private static final String forwardButtonUnicodeSymbol = "\u003E";
private static final String reloadButtonUnicodeSymbol = "\u27F3";
@Override
public void start(Stage stage) {
List<String> args = getParameters().getRaw();
final String initialURL = args.size() > 0 ? args.get(0) : DEFAULT_URL;
final WebView webView = new WebView();
final WebEngine webEngine = webView.getEngine();
final TextField urlBox = new TextField();
urlBox.setMinHeight(NAVI_BAR_MIN_DIMENSION);
urlBox.setText(initialURL);
HBox.setHgrow(urlBox, Priority.ALWAYS);
urlBox.setOnAction(e -> webEngine.load(urlBox.getText()));
final Label bottomTitle = new Label();
bottomTitle.textProperty().bind(urlBox.textProperty());
final Button goStopButton = new Button(goButtonUnicodeSymbol);
goStopButton.setStyle(buttonStyle);
goStopButton.setOnAction(e -> webEngine.load(urlBox.getText()));
final Button backButton = new Button(backButtonUnicodeSymbol);
backButton.setStyle(buttonStyle);
backButton.setDisable(true);
backButton.setOnAction(e -> webEngine.getHistory().go(-1));
final Button forwardButton = new Button(forwardButtonUnicodeSymbol);
forwardButton.setStyle(buttonStyle);
forwardButton.setDisable(true);
forwardButton.setOnAction(e -> webEngine.getHistory().go(+1));
final Button reloadButton = new Button(reloadButtonUnicodeSymbol);
reloadButton.setStyle(buttonStyle);
reloadButton.setOnAction(e -> webEngine.reload());
final HBox naviBar = new HBox();
naviBar.getChildren().addAll(backButton, forwardButton, urlBox,
reloadButton, goStopButton);
naviBar.setPadding(new Insets(PADDING_VALUE));
final VBox root = new VBox();
root.getChildren().addAll(naviBar, webView, bottomTitle);
VBox.setVgrow(webView, Priority.ALWAYS);
webEngine.locationProperty().addListener((observable, oldValue, newValue) ->
urlBox.setText(newValue));
webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
if (newValue.compareTo(Worker.State.SUCCEEDED) < 0) {
bottomTitle.setVisible(true);
goStopButton.setText(stopButtonUnicodeSymbol);
goStopButton.setOnAction(e -> webEngine.getLoadWorker().cancel());
} else {
bottomTitle.setVisible(false);
goStopButton.setText(goButtonUnicodeSymbol);
goStopButton.setOnAction(e -> webEngine.load(urlBox.getText()));
}
});
webEngine.getHistory().currentIndexProperty().addListener((observable, oldValue, newValue) -> {
int length = webEngine.getHistory().getEntries().size();
backButton.setDisable((int)newValue == 0);
forwardButton.setDisable((int)newValue >= length - 1);
});
webEngine.load(initialURL);
Scene scene = new Scene(root);
stage.setScene(scene);
SimpleStringProperty titleProp = new SimpleStringProperty("HelloWebView" +
" (" + System.getProperty("java.version") + ") : ");
stage.titleProperty().bind(titleProp.concat(urlBox.textProperty()));
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}