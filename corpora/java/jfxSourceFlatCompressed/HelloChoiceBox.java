package hello;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloChoiceBox extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
final ChoiceBox<String> choiceBox = new ChoiceBox<String>();
choiceBox.getItems().addAll("Leia Organa", "Luke Skywalker", "Han Solo");
VBox vbox = new VBox(5);
Button slt = new Button("Select item 2");
slt.setOnAction(t -> choiceBox.getSelectionModel().select(2));
vbox.getChildren().add(slt);
Button clearItems = new Button("Clear items sequence");
clearItems.setOnAction(t -> choiceBox.getItems().clear());
vbox.getChildren().add(clearItems);
HBox hbox = new HBox(10);
hbox.getChildren().addAll(choiceBox, vbox);
stage.setScene(new Scene(hbox));
stage.show();
}
}
