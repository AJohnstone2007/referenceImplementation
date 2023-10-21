import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
public class DNDWebViewTest extends Application {
private static long time;
private static long initialTime;
@Override
public void start(Stage primaryStage) throws Exception {
final WebView webView = new WebView();
final Button offlineButton = new Button("Offline test");
final Button onlineButton = new Button("Online test");
offlineButton.setOnAction(e -> webView.getEngine().load(getClass().getResource("drag.html").toExternalForm()));
onlineButton.setOnAction(e -> webView.getEngine().load("https://openjdk.org"));
final Label instructions = new Label("Select a test and drag the images");
final Label readTime = new Label("");
webView.addEventHandler(DragEvent.DRAG_ENTERED, e -> {
time = System.currentTimeMillis();
initialTime = -1;
});
webView.addEventHandler(DragEvent.DRAG_OVER, e -> {
long newTime = System.currentTimeMillis();
if (initialTime == -1) {
initialTime = newTime - time;
}
readTime.setText("DND image read interval = " + (newTime - time) + " ms, initial delay = " + initialTime + " ms");
time = newTime;
});
VBox root = new VBox(20, instructions, new HBox(20, offlineButton, onlineButton), readTime, webView);
primaryStage.setScene(new Scene(root, 800, 600));
primaryStage.show();
}
}
