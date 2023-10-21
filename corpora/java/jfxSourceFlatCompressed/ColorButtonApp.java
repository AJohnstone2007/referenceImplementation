package ensemble.samples.controls.button.colorbutton;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class ColorButtonApp extends Application {
public Parent createContent() {
HBox hBox = new HBox();
hBox.setSpacing(5);
for (int i = 0; i < 7; i++) {
Button b = new Button("Color");
b.setStyle(String.format("-fx-base: rgb(%d,%d,%d);",
(10 * i), (20 * i), (10 * i)));
hBox.getChildren().add(b);
}
HBox hBox2 = new HBox();
hBox2.setSpacing(5);
hBox2.setTranslateY(30);
hBox2.getChildren().addAll(getButton("Red"), getButton("Orange"),
getButton("Yellow"), getButton("Green"),
getButton("Blue"), getButton("Indigo"),
getButton("Violet"));
VBox vBox = new VBox(20);
vBox.getChildren().addAll(hBox, hBox2);
vBox.setPrefHeight(140);
vBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
vBox.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
return vBox;
}
protected Button getButton(String name) {
Button button = new Button(name);
button.setStyle(String.format("-fx-base: %s;", name.toLowerCase()));
return button;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
