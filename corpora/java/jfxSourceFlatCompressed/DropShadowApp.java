package ensemble.samples.graphics2d.effects.dropshadow;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class DropShadowApp extends Application {
private DropShadow dropShadow = new DropShadow();
public Parent createContent() {
StackPane root = new StackPane();
Text sample = new Text(0, 40, "DropShadow Effect");
sample.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 36));
sample.setEffect(dropShadow);
root.setAlignment(Pos.CENTER);
root.getChildren().add(sample);
return root;
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
