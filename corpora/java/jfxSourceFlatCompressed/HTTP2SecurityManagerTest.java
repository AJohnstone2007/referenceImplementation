import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
@SuppressWarnings("removal")
public class HTTP2SecurityManagerTest {
public static class MainWindow extends Application {
@Override
public void start(Stage stage) throws Exception {
VBox instructions = new VBox(
new Label(" This test loads a web page with a security manager set,"),
new Label(" and a Policy that grants AllPermission."),
new Label(""),
new Label(" Check the console output for an AccessControllException:"),
new Label(" Click 'Pass' if there is NO exception"),
new Label(" Click 'Fail' if an AccessControlException is logged")
);
Button passButton = new Button("Pass");
passButton.setOnAction(e -> {
Platform.exit();
});
Button failButton = new Button("Fail");
failButton.setOnAction(e -> {
Platform.exit();
throw new AssertionError("Unexpected AccessControlException");
});
HBox buttonBox = new HBox(20, passButton, failButton);
WebView webView = new WebView();
webView.getEngine().load("https://www.oracle.com/java/");
VBox root = new VBox(10, buttonBox, instructions, webView);
Scene scene = new Scene(root);
stage.setScene(scene);
stage.show();
}
}
public static void main(String[] args) {
Policy.setPolicy(new Policy() {
@Override
public PermissionCollection getPermissions(ProtectionDomain domain) {
Permissions permissions = new Permissions();
permissions.add(new AllPermission());
return permissions;
}
});
System.setSecurityManager(new SecurityManager());
Application.launch(MainWindow.class);
}
}
