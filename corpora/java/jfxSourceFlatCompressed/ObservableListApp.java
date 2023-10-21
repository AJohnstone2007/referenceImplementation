package ensemble.samples.language.observablelist;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
public class ObservableListApp extends Application {
public Parent createContent() {
final List<Integer> listData = new ArrayList<>();
for (int i = 1; i < 10; i++) {
listData.add(i);
}
final ObservableList<Integer> list =
FXCollections.<Integer>observableList(listData);
final Text textList = new Text(0, 0, list.toString());
textList.setStyle("-fx-font-size: 16;");
textList.setTextOrigin(VPos.TOP);
textList.setTextAlignment(TextAlignment.CENTER);
final Text textMessage = new Text(0, 0, "Add a listener");
textMessage.setStyle("-fx-font-size: 16;");
textMessage.setTextOrigin(VPos.TOP);
textMessage.setTextAlignment(TextAlignment.CENTER);
Button buttonAddNumber = new Button("Replace random integer");
buttonAddNumber.setPrefSize(190, 45);
buttonAddNumber.setOnAction((ActionEvent t) -> {
int randomIndex = (int) (Math.round(Math.random() *
(list.size() - 1)));
int randomNumber = (int) (Math.round(Math.random() * 10));
list.set(randomIndex, randomNumber);
textList.setText(list.toString());
});
Button buttonAdd = new Button("Add list listener");
buttonAdd.setPrefSize(190, 45);
final ListChangeListener<Integer> listener =
(ListChangeListener.Change<? extends Integer> c) -> {
while (c.next()) {
textMessage.setText("replacement on index " + c.getFrom());
}
};
buttonAdd.setOnAction((ActionEvent t) -> {
list.addListener(listener);
textMessage.setText("listener added");
});
Button buttonRemove = new Button("Remove list listener");
buttonRemove.setPrefSize(190, 45);
buttonRemove.setOnAction((ActionEvent t) -> {
list.removeListener(listener);
textMessage.setText("listener removed");
});
VBox vBoxTop = new VBox(10);
vBoxTop.setAlignment(Pos.CENTER);
VBox vBoxBottom = new VBox();
vBoxBottom.setAlignment(Pos.CENTER);
vBoxBottom.setSpacing(10);
VBox outerVBox = new VBox(10);
outerVBox.setAlignment(Pos.CENTER);
vBoxTop.getChildren().addAll(textMessage, buttonAdd, buttonRemove);
vBoxBottom.getChildren().addAll(buttonAddNumber, textList);
outerVBox.getChildren().addAll(vBoxTop, vBoxBottom);
return outerVBox;
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
