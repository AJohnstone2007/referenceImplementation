package myapp6;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import myapp6.pkg3.AnnotatedController;
import myapp6.pkg3.CustomNode;
import myapp6.pkg3.SimpleController;
import static myapp6.Constants.*;
public class AppFXMLQualExported extends Application {
public static void main(String[] args) {
try {
Application.launch(args);
} catch (Throwable t) {
System.err.println("ERROR: caught unexpected exception: " + t);
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
private void doTestNone() throws IOException {
final URL fxmlURL = Util.getURL(SimpleController.class, "TestNone");
FXMLLoader loader = new FXMLLoader(fxmlURL);
Node fxmlRoot = loader.load();
Util.assertNotNull(fxmlRoot);
Util.assertTrue("fxmlRoot is not instance of StackPane", fxmlRoot instanceof StackPane);
Util.assertEquals("RootTestNone", fxmlRoot.getId());
}
private void doTestCustomNode() throws IOException {
final URL fxmlURL = Util.getURL(SimpleController.class, "TestCustomNode");
FXMLLoader loader = new FXMLLoader(fxmlURL);
try {
Node fxmlRoot = loader.load();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (LoadException ex) {
}
}
private void doTestSimple() throws IOException {
final URL fxmlURL = Util.getURL(SimpleController.class, "TestSimple");
FXMLLoader loader = new FXMLLoader(fxmlURL);
try {
Node fxmlRoot = loader.load();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (LoadException ex) {
}
}
private void doTestAnnotated() throws IOException {
final URL fxmlURL = Util.getURL(SimpleController.class, "TestAnnotated");
FXMLLoader loader = new FXMLLoader(fxmlURL);
try {
Node fxmlRoot = loader.load();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (LoadException ex) {
}
}
@Override
public void start(Stage stage) {
try {
doTestNone();
doTestCustomNode();
doTestSimple();
doTestAnnotated();
} catch (AssertionError ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_ASSERTION_FAILURE);
} catch (Error | Exception ex) {
System.err.println("ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
Platform.exit();
}
@Override public void stop() {
System.exit(ERROR_NONE);
}
}
