package hello;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
public class HelloSanity extends Application {
public static void main(String[] args) {
launch(args);
}
public void start(final Stage primaryStage) {
Screen screen = Screen.getPrimary();
Rectangle2D bounds = screen.getVisualBounds();
primaryStage.setX(0);
primaryStage.setY(0);
final VBox mainBox = new VBox(30);
mainBox.setAlignment(Pos.CENTER);
final Scene globalScene = new Scene(new Group(),bounds.getWidth(), bounds.getHeight());
final TestBuilder builder = TestBuilder.getInstance();
Label welcome = new Label("Welcome to Hello Sanity");
Button bControls = new Button("Controls");
bControls.setOnAction(e -> builder.controlTest(globalScene, mainBox));
Button bTabs = new Button("Tabs and Menus");
bTabs.setOnAction(e -> builder.menusTest(globalScene, mainBox, primaryStage));
Button bWins = new Button("Windows");
bWins.setOnAction(e -> builder.windowsTest(globalScene, mainBox, primaryStage));
Button bAnim = new Button("Animation");
bAnim.setOnAction(e -> builder.animationTest(globalScene, mainBox));
Button bEffs = new Button("Effects");
bEffs.setOnAction(e -> builder.effectsTest(globalScene, mainBox));
Button bgestures = new Button("Gesture Actions");
bgestures.setOnAction(e -> builder.GestureTest(globalScene, mainBox));
Button bquit = new Button("Quit");
bquit.setOnAction(e -> primaryStage.close());
mainBox.getChildren().addAll(welcome, bControls, bTabs, bWins,
bAnim, bEffs, bgestures, bquit);
globalScene.setRoot(mainBox);
globalScene.getStylesheets().add("hello/HelloSanityStyles.css");
primaryStage.setScene(globalScene);
primaryStage.show();
}
}
