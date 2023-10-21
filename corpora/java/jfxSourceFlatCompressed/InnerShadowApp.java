package ensemble.samples.graphics2d.effects.innershadow;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class InnerShadowApp extends Application {
private InnerShadow innerShadow = new InnerShadow();
private Text sample = new Text(0, 100, "Shadow");
public Parent createContent() {
StackPane root = new StackPane();
sample.setFont(Font.font("Arial Black", 80));
sample.setFill(Color.web("#BBBBBB"));
innerShadow.setRadius(5d);
innerShadow.setOffsetX(2);
innerShadow.setOffsetY(2);
sample.setEffect(innerShadow);
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
