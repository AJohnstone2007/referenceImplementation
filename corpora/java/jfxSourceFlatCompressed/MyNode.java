package ensemble.samples.scenegraph.customnode;
import javafx.scene.Group;
import javafx.scene.control.Label;
public class MyNode extends Group {
private Label text;
public MyNode(String name) {
text = new Label(name);
text.setStyle("-fx-border-color:black; -fx-padding:3px;");
text.setLayoutX(4);
text.setLayoutY(2);
getChildren().addAll(text);
}
}