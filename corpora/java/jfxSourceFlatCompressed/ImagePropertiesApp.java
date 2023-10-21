package ensemble.samples.graphics2d.images.imageproperties;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class ImagePropertiesApp extends Application {
public Parent createContent() {
String resource = "/ensemble/samples/shared-resources/sanfran.jpg";
String url = getClass().getResource(resource).toString();
ImageView sample1 = new ImageView(new Image(url, 30, 70, false, true));
ImageView sample2 = new ImageView(new Image(url));
sample2.setFitWidth(200);
sample2.setPreserveRatio(true);
ImageView sample3 = new ImageView(new Image(url));
sample3.setFitHeight(20);
sample3.setPreserveRatio(true);
ImageView sample4 = new ImageView(new Image(url));
sample4.setFitWidth(40);
sample4.setFitHeight(80);
sample4.setPreserveRatio(false);
sample4.setSmooth(true);
ImageView sample5 = new ImageView(new Image(url));
sample5.setFitHeight(60);
sample5.setPreserveRatio(true);
Rectangle2D rectangle2D = new Rectangle2D(50, 200, 120, 60);
sample5.setViewport(rectangle2D);
HBox hBox = new HBox();
hBox.setSpacing(10);
hBox.getChildren().addAll(sample1, sample3, sample4, sample5);
VBox vb = new VBox(10);
vb.setAlignment(Pos.CENTER);
vb.getChildren().addAll(hBox, sample2);
vb.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
vb.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
return vb;
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
