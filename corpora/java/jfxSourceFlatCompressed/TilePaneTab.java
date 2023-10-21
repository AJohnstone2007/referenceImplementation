package layout;
import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
public class TilePaneTab extends Tab {
int sizeArr[] = {75, 100, 150, 200};
double hgap = 5;
double vgap = 5;
TilePane tilePane = new TilePane(hgap, vgap);
public TilePaneTab(String text) {
this.setText(text);
init();
}
public void init() {
for (int i = 1; i < 16; i++) {
String imageStr = "resources/images/squares" + i + ".jpg";
int index = i % sizeArr.length;
tilePane.getChildren().add(new ImageView(new Image(imageStr,
sizeArr[index], sizeArr[index], true, true)));
}
tilePane.getStyleClass().add("layout");
BorderPane root = new BorderPane(tilePane);
Label orientationLabel = new Label("Orientation");
ChoiceBox<Orientation> orientationCBox = new ChoiceBox<>();
orientationCBox.getItems().addAll(Orientation.HORIZONTAL, Orientation.VERTICAL);
orientationCBox.getSelectionModel().select(tilePane.getOrientation());
orientationCBox.getSelectionModel().selectedItemProperty().addListener(this::orientationChanged);
Label nodeOrientationLabel = new Label("Node Orientation");
ChoiceBox<NodeOrientation> nodeOrientationCBox = new ChoiceBox<>();
nodeOrientationCBox.getItems().addAll(NodeOrientation.INHERIT,
NodeOrientation.LEFT_TO_RIGHT, NodeOrientation.RIGHT_TO_LEFT);
nodeOrientationCBox.getSelectionModel().select(tilePane.getNodeOrientation());
nodeOrientationCBox.getSelectionModel().selectedItemProperty().addListener(this::nodeOrientationChanged);
Label childAlignmentLabel = new Label("Child Alignment");
ChoiceBox<Pos> childAlignmentCBox = new ChoiceBox<>();
childAlignmentCBox.getItems().addAll(null,
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
childAlignmentCBox.getSelectionModel().select(TilePane.getAlignment(tilePane.getChildren().get(0)));
childAlignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::childAlignmentChanged);
Label alignmentLabel = new Label("Alignment");
ChoiceBox<Pos> alignmentCBox = new ChoiceBox<>();
alignmentCBox.getItems().addAll(
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
alignmentCBox.getSelectionModel().select(tilePane.getAlignment());
alignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::alignmentChanged);
HBox controlGrp = new HBox(alignmentLabel, alignmentCBox,
orientationLabel, orientationCBox,
nodeOrientationLabel, nodeOrientationCBox,
childAlignmentLabel, childAlignmentCBox);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
}
public void alignmentChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
tilePane.setAlignment(newValue);
}
public void orientationChanged(ObservableValue<? extends Orientation> observable,
Orientation oldValue,
Orientation newValue) {
tilePane.setOrientation(newValue);
}
public void nodeOrientationChanged(ObservableValue<? extends NodeOrientation> observable,
NodeOrientation oldValue,
NodeOrientation newValue) {
tilePane.setNodeOrientation(newValue);
}
public void childAlignmentChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
tilePane.setTileAlignment(newValue);
}
}
