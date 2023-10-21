package ensemble.samples.layout.borderpane;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class BorderPaneApp extends Application {
public Parent createContent() {
BorderPane borderPane = new BorderPane();
ToolBar toolbar = new ToolBar();
toolbar.getItems().add(new Button("Home"));
toolbar.getItems().add(new Button("Options"));
toolbar.getItems().add(new Button("Help"));
borderPane.setTop(toolbar);
Label label1 = new Label("Left hand");
Button leftButton = new Button("left");
VBox leftVbox = new VBox();
leftVbox.getChildren().addAll(label1, leftButton);
borderPane.setLeft(leftVbox);
Label rightlabel1 = new Label("Right hand");
Button rightButton = new Button("right");
VBox rightVbox = new VBox();
rightVbox.getChildren().addAll(rightlabel1, rightButton);
borderPane.setRight(rightVbox);
Label centerLabel = new Label("Center area.");
centerLabel.setWrapText(true);
String IMAGE = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(IMAGE));
ImageView imageView = new ImageView(ICON_48);
AnchorPane centerAP = new AnchorPane();
AnchorPane.setTopAnchor(centerLabel, Double.valueOf(5));
AnchorPane.setLeftAnchor(centerLabel, Double.valueOf(20));
AnchorPane.setTopAnchor(imageView, Double.valueOf(40));
AnchorPane.setLeftAnchor(imageView, Double.valueOf(30));
centerAP.getChildren().addAll(centerLabel, imageView);
borderPane.setCenter(centerAP);
Label bottomLabel = new Label("At the bottom.");
borderPane.setBottom(bottomLabel);
return borderPane;
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
