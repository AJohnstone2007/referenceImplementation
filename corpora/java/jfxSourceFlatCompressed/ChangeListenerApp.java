package ensemble.samples.language.changelistener;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
public class ChangeListenerApp extends Application {
Rectangle rect1 = new Rectangle(25, 25, 40, 40);
Rectangle rect2 = new Rectangle(135, 25, 40, 40);
public Parent createContent() {
HBox outerHbox = new HBox();
outerHbox.setAlignment(Pos.CENTER);
VBox vbox = new VBox(10);
vbox.setPrefWidth(200);
final Rectangle rect = new Rectangle(150, 0, 60, 60);
rect.setFill(Color.DODGERBLUE);
rect.setEffect(new Lighting());
final Text text = new Text(0, 0, "Add a hover listener");
text.setStyle("-fx-font-size: 22;");
text.setTextOrigin(VPos.TOP);
text.setTextAlignment(TextAlignment.CENTER);
final InvalidationListener hoverListener = (Observable ov) -> {
if (rect.isHover()) {
text.setText("hovered");
} else {
text.setText("not hovered");
}
};
Button buttonAdd = new Button("Add listener");
buttonAdd.setPrefSize(140, 18);
buttonAdd.setOnAction((ActionEvent t) -> {
rect.hoverProperty().addListener(hoverListener);
text.setText("listener added");
});
Button buttonRemove = new Button("Remove listener");
buttonRemove.setPrefSize(140, 18);
buttonRemove.setOnAction((ActionEvent t) -> {
rect.hoverProperty().removeListener(hoverListener);
text.setText("listener removed");
});
vbox.getChildren().addAll(text, buttonAdd, buttonRemove);
outerHbox.getChildren().addAll(vbox, rect);
outerHbox.setPadding(new Insets(5,5,5,5));
return outerHbox;
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
