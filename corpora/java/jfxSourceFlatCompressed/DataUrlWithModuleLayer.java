package myapp7;
import java.lang.module.ModuleDescriptor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
public class DataUrlWithModuleLayer extends Application {
public static final int ERROR_OK = 0;
public static final int ERROR_ASSUMPTION_VIOLATED = 2;
public static final int ERROR_TIMEOUT = 3;
public static final int ERROR_TITLE_NOT_UPDATED = 4;
public static final int ERROR_UNEXPECTED_EXIT = 5;
@Override
public void start(Stage primaryStage) throws Exception {
Module module = Application.class.getModule();
if (module == null) {
System.err.println("Failure: Module for Application not found");
System.exit(ERROR_ASSUMPTION_VIOLATED);
}
if (! module.isNamed()) {
System.err.println("Failure: Expected named module");
System.exit(ERROR_ASSUMPTION_VIOLATED);
}
ModuleDescriptor moduleDesc = module.getDescriptor();
if (moduleDesc.isAutomatic()) {
System.err.println("Failure: Automatic module found");
System.exit(ERROR_ASSUMPTION_VIOLATED);
}
if (moduleDesc.isOpen()) {
System.err.println("Failure: Open module found");
System.exit(ERROR_ASSUMPTION_VIOLATED);
}
BorderPane root = new BorderPane();
Scene scene = new Scene(root);
WebView webview = new WebView();
root.setCenter(webview);
String checkJS = "document.getElementsByTagName(\"title\")[0].textContent='Executed'";
String checkJSEncoded = Base64.getEncoder().encodeToString(checkJS.getBytes(StandardCharsets.UTF_8));
String script = "<html>"
+ "<head>"
+ "<title>Armed</title>"
+ "</head>"
+ "<body>"
+ "<h1>Test for loading a data URL</h1>"
+ "<p>The test is successful, if the JVM does not crash with a SEGFAULT.</p>"
+ "<script src=\"data:application/javascript;base64," + checkJSEncoded + "\"></script>"
+ "</body>"
+ "</html>";
webview.getEngine().getLoadWorker().stateProperty().addListener(
new ChangeListener<State>() {
public void changed(ObservableValue ov, State oldState, State newState) {
if (newState == State.SUCCEEDED) {
String title = (String) webview.getEngine().executeScript("document.title");
if ("Executed".equals(title)) {
System.exit(ERROR_OK);
} else {
System.exit(ERROR_TITLE_NOT_UPDATED);
}
}
}
});
webview.getEngine().loadContent(script);
primaryStage.setScene(scene);
primaryStage.setWidth(1024);
primaryStage.setHeight(768);
primaryStage.show();
}
}
