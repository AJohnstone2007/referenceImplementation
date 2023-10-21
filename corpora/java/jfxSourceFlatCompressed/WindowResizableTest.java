import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class WindowResizableTest extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
Button passButton = new Button("Pass");
Button failButton = new Button("Fail");
passButton.setOnAction(e -> this.quit());
failButton.setOnAction(e -> {
this.quit();
throw new AssertionError("The window is no longer resizable");
});
VBox rootNode = new VBox(5,
new Label("1. This is a MacOs specific test. If you run the test on some other Platform, please click Pass."),
new Label("2. Verify that the window is resizable in the beginning by dragging the edges of this Window."),
new Label("3. Click the green maximize button on window to enter full-screen mode and again click it to come back to normal mode."),
new Label("4. If the window is still resizable, click Pass otherwise Fail."),
new Label(""),
new HBox(10, passButton, failButton));
rootNode.setPadding(new Insets(8));
Scene scene = new Scene(rootNode);
primaryStage.setScene(scene);
primaryStage.setResizable(true);
primaryStage.show();
}
private void quit() {
Platform.exit();
}
public static void main(String[] args) {
Application.launch(args);
}
}
