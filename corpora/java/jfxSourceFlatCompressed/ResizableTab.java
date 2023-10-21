package layout;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
public class ResizableTab extends Tab {
public ResizableTab(String text) {
this.setText(text);
init();
}
public void init() {
Button button = new Button("Button");
button.setStyle("-fx-font-size: 40px");
Label label = new Label("Label");
label.setStyle("-fx-font-size: 36px");
VBox vbox = new VBox(50, button, label);
vbox.setAlignment(Pos.CENTER_RIGHT);
Rectangle rect = new Rectangle(600, 400, Color.BURLYWOOD);
rect.setStrokeWidth(3);
rect.setStroke(Color.RED);
Text text = new Text("Rectangle");
text.setStyle("-fx-font-size: 40px");
StackPane rectGroup = new StackPane(rect, text);
HBox root = new HBox();
root.setSpacing(20);
root.getChildren().addAll(vbox, rectGroup);
HBox.setHgrow(rectGroup, Priority.ALWAYS);
root.getStyleClass().add("layout");
this.setContent(root);
}
}
