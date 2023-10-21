package ensemble.samples.graphics2d.effects.gaussianblur;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class GaussianBlurApp extends Application {
private static final Image ICON_48 = new Image(GaussianBlurApp.class.getResourceAsStream("/ensemble/samples/shared-resources/icon-48x48.png"));
private GaussianBlur gaussianBlur = new GaussianBlur();
public Parent createContent() {
StackPane root = new StackPane();
ImageView sample = new ImageView(ICON_48);
gaussianBlur.setRadius(8d);
sample.setEffect(gaussianBlur);
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
