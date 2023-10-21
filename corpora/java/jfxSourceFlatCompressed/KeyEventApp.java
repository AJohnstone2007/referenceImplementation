package ensemble.samples.scenegraph.events.keyevent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class KeyEventApp extends Application {
public Parent createContent() {
final ListView<String> console =
new ListView<String>(FXCollections.<String>observableArrayList());
ListChangeListener<? super String> listener =
(ListChangeListener.Change<? extends String> change) -> {
while (change.next()) {
if (change.getList().size() > 20.0) {
change.getList().remove(0);
}
}
};
console.getItems().addListener(listener);
console.setPrefHeight(150);
console.setMaxHeight(ListView.USE_PREF_SIZE);
final TextField textBox = new TextField();
textBox.setPromptText("Write here");
textBox.setStyle("-fx-font-size: 34;");
textBox.setOnKeyPressed((KeyEvent ke) -> {
console.getItems().add("Key Pressed: " + ke.getText());
});
textBox.setOnKeyReleased((KeyEvent ke) -> {
console.getItems().add("Key Released: " + ke.getText());
});
textBox.setOnKeyTyped((KeyEvent ke) -> {
String text = "Key Typed: " + ke.getCharacter();
if (ke.isAltDown()) {
text += " , alt down";
}
if (ke.isControlDown()) {
text += " , ctrl down";
}
if (ke.isMetaDown()) {
text += " , meta down";
}
if (ke.isShiftDown()) {
text += " , shift down";
}
console.getItems().add(text);
});
VBox vb = new VBox(10);
vb.getChildren().addAll(textBox, console);
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
