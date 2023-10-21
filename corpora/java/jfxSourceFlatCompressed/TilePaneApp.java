package ensemble.samples.layout.tilepane;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
public class TilePaneApp extends Application {
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(245, 100);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
TilePane tilePane = new TilePane();
tilePane.setPrefColumns(2);
tilePane.setAlignment(Pos.CENTER);
String IMAGE = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(IMAGE));
Button[] buttons = new Button[6];
for (int j = 0; j < buttons.length; j++) {
buttons[j] = new Button("button" + (j + 1), new ImageView(ICON_48));
tilePane.getChildren().add(buttons[j]);
}
root.getChildren().add(tilePane);
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
