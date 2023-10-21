package layout;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
public class AnchorPaneTab extends Tab {
final AnchorPane anchorPane = new AnchorPane();
final Button button = new Button("Button");
boolean top, left, right, bottom, resetPosition;
final Label topRight = new Label("Top Right");
final Label bottomLeft = new Label("Bottom Left");
public AnchorPaneTab(String text) {
this.setText(text);
init();
}
public void init() {
button.setStyle("-fx-font-size:20pt; -fx-font-family: \"Courier New\";");
topRight.setStyle("-fx-font-size:20pt; -fx-font-family: \"Courier New\";");
bottomLeft.setStyle("-fx-font-size:20pt; -fx-font-family: \"Courier New\";");
AnchorPane.setTopAnchor(topRight, 10.0);
AnchorPane.setRightAnchor(topRight, 10.0);
AnchorPane.setBottomAnchor(bottomLeft, 10.0);
AnchorPane.setLeftAnchor(bottomLeft, 10.0);
anchorPane.getChildren().addAll(button, topRight, bottomLeft);
anchorPane.getStyleClass().add("layout");
BorderPane root = new BorderPane(anchorPane);
CheckBox topCbx = new CheckBox("Button Top");
topCbx.setOnAction(e -> setTopAnchor(topCbx.isSelected()));
CheckBox leftCbx = new CheckBox("Button Left");
leftCbx.setOnAction(e -> setLeftAnchor(leftCbx.isSelected()));
CheckBox rightCbx = new CheckBox("Button Right");
rightCbx.setOnAction(e -> setRightAnchor(rightCbx.isSelected()));
CheckBox bottomCbx = new CheckBox("Button Bottom");
bottomCbx.setOnAction(e -> setBottomAnchor(bottomCbx.isSelected()));
CheckBox resetPositionCbx = new CheckBox("Button Reset Position");
resetPositionCbx.setOnAction(e -> setResetPosition(resetPositionCbx.isSelected()));
resetPosition = true;
resetPositionCbx.setSelected(resetPosition);
HBox controlGrp = new HBox(topCbx, leftCbx, rightCbx, bottomCbx, resetPositionCbx);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
}
void setTopAnchor(boolean top) {
this.top = top;
setButton();
}
void setLeftAnchor(boolean left) {
this.left = left;
setButton();
}
void setRightAnchor(boolean right) {
this.right = right;
setButton();
}
void setBottomAnchor(boolean bottom) {
this.bottom = bottom;
setButton();
}
void setResetPosition(boolean resetPosition) {
this.resetPosition = resetPosition;
setButton();
}
void setButton() {
AnchorPane.clearConstraints(button);
if (resetPosition) button.relocate(0, 0);
if (top) AnchorPane.setTopAnchor(button, 200.0);
if (left) AnchorPane.setLeftAnchor(button, 300.0);
if (right) AnchorPane.setRightAnchor(button, 300.0);
if (bottom) AnchorPane.setBottomAnchor(button, 200.0);
}
}
