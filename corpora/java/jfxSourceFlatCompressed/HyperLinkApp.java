package ensemble.samples.controls.hyperlink;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HyperLinkApp extends Application {
public Parent createContent() {
HBox hbox = new HBox(18);
hbox.setAlignment(Pos.CENTER);
VBox vbox = new VBox();
vbox.setSpacing(5);
vbox.setAlignment(Pos.CENTER_LEFT);
String IMAGE_48 = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(IMAGE_48));
ImageView iv = new ImageView(ICON_48);
Hyperlink h1 = new Hyperlink("Hyperlink");
h1.setPrefWidth(80);
h1.setMinWidth(Hyperlink.USE_PREF_SIZE);
Hyperlink h2 = new Hyperlink("Hyperlink with Image");
h2.setPrefWidth(200);
h2.setMinWidth(Hyperlink.USE_PREF_SIZE);
h2.setGraphic(iv);
vbox.getChildren().addAll(h1, h2);
hbox.getChildren().add(vbox);
return hbox;
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
