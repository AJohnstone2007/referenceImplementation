package layout;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
public class BorderPaneTab extends Tab {
final String imageStr = "resources/images/squares3.jpg";
final Image image = new Image(imageStr, 300, 300, true, true);
final ImageView imageView = new ImageView(image);
final HTMLEditor htmlEditor = new HTMLEditor();
Node center = htmlEditor;
final BorderPane borderPane = new BorderPane(center);
Label top, left, right;
HBox bottom;
public BorderPaneTab(String text) {
this.setText(text);
init();
}
public void init() {
htmlEditor.setMinSize(400, 300);
htmlEditor.setMaxSize(800, 600);
imageView.setEffect(new DropShadow());
center = htmlEditor;
borderPane.getCenter().getStyleClass().add("layout");
BorderPane root = new BorderPane(borderPane);
Label childAlignmentLabel = new Label("Center Node Alignment");
ChoiceBox<Pos> childAlignmentCBox = new ChoiceBox<>();
childAlignmentCBox.getItems().addAll(null,
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
childAlignmentCBox.getSelectionModel().select(BorderPane.getAlignment(center));
childAlignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::childAlignmentChanged);
CheckBox switchCenterNodeCbx = new CheckBox("Switch Center Node");
switchCenterNodeCbx.setOnAction(e
-> this.switchCenterNode(switchCenterNodeCbx.isSelected()));
CheckBox topCbx = new CheckBox("Top");
topCbx.setOnAction(e ->
borderPane.setTop(topCbx.isSelected() ? top : null));
CheckBox rightCbx = new CheckBox("Right");
rightCbx.setOnAction(e ->
borderPane.setRight(rightCbx.isSelected() ? right : null));
CheckBox leftCbx = new CheckBox("Left");
leftCbx.setOnAction(e
-> borderPane.setLeft(leftCbx.isSelected() ? left : null));
CheckBox bottomCbx = new CheckBox("Bottom");
bottomCbx.setOnAction(e
-> {
borderPane.setBottom(bottomCbx.isSelected() ? bottom : null);
});
HBox controlGrp = new HBox(childAlignmentLabel, childAlignmentCBox,
switchCenterNodeCbx, topCbx, rightCbx, leftCbx, bottomCbx);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
top = new Label("Top");
BorderPane.setAlignment(top, Pos.CENTER);
top.setStyle("-fx-font-size:30pt; -fx-font-family: \"Courier New\";");
top.getStyleClass().add("layout");
borderPane.setTop(null);
right = new Label("Right");
right.setStyle("-fx-font-size:30pt; -fx-font-family: \"Courier New\";");
right.getStyleClass().add("layout");
BorderPane.setAlignment(right, Pos.CENTER);
borderPane.setRight(null);
left = new Label("Left");
left.setStyle("-fx-font-size:30pt; -fx-font-family: \"Courier New\";");
left.getStyleClass().add("layout");
BorderPane.setAlignment(left, Pos.CENTER);
borderPane.setLeft(null);
bottom = new HBox();
for (int i = 1; i < 12; i++) {
String imageStr = "resources/images/squares" + i + ".jpg";
bottom.getChildren().add(new ImageView(new Image(imageStr,
100, 100, true, true)));
}
bottom.getStyleClass().add("control");
bottom.setMinWidth(100);
borderPane.setBottom(bottom);
bottomCbx.setSelected(true);
this.setContent(root);
}
public void childAlignmentChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
BorderPane.setAlignment(center, newValue);
}
void switchCenterNode(boolean grow) {
Pos pos = BorderPane.getAlignment(center);
center = grow ? imageView : htmlEditor;
borderPane.setCenter(center);
BorderPane.setAlignment(center, pos);
}
}
