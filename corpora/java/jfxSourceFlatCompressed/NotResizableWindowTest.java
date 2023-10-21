import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class NotResizableWindowTest extends Application{
@Override
public void start(Stage primaryStage) throws Exception {
Button openDialogButton = new Button("Press this button");
Button passButton = new Button("Pass");
Button failButton = new Button("Fail");
openDialogButton.setOnAction((e)->{
Dialog<ButtonType> dialog = new Dialog<>();
dialog.initOwner(primaryStage);
dialog.setContentText("Press Close button in dialog");
dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
dialog.show();
});
passButton.setOnAction((e)->{
quit();
});
failButton.setOnAction((e)->{
quit();
throw new AssertionError("The window buttons are not same");
});
VBox root = new VBox(8,
new Label("Check window button state before and after clicking dialog button"),
new Label("If the state is the same as before, Press Pass otherwise Fail"),
openDialogButton,passButton,failButton);
root.setPadding(new Insets(8));
Scene scene = new Scene(root);
primaryStage.setScene(scene);
primaryStage.setResizable(false);
primaryStage.show();
}
public static void quit() {
Platform.exit();
}
public static void main(String[] args) {
Application.launch(args);
}
}
