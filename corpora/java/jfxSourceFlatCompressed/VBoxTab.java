package layout;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
public class VBoxTab extends Tab {
final VBox vbox = new VBox(10);
final Button okBtn = new Button("OK");
final Button cancelBtn = new Button("Cancel");
public VBoxTab(String text) {
this.setText(text);
init();
}
public void init() {
final Label descLbl = new Label("Description:");
final TextArea desc = new TextArea();
desc.setPrefColumnCount(10);
desc.setPrefRowCount(3);
desc.setWrapText(true);
vbox.getChildren().addAll(descLbl, desc, okBtn, cancelBtn);
vbox.getStyleClass().add("layout");
final BorderPane root = new BorderPane(vbox);
final CheckBox vGrowCbx = new CheckBox("TextArea Vgrow");
vGrowCbx.setSelected(false);
vGrowCbx.setOnAction(e-> growVertical(desc, vGrowCbx.isSelected()));
CheckBox maxSizeButtonCbx = new CheckBox("Button Max Size");
maxSizeButtonCbx.setOnAction(e -> maxSizeButton(maxSizeButtonCbx.isSelected()));
CheckBox fillWidthCbx = new CheckBox("Fill Width");
fillWidthCbx.setSelected(true);
fillWidthCbx.setOnAction(e
-> vbox.setFillWidth(fillWidthCbx.isSelected()));
ChoiceBox<Pos> alignmentCBox = new ChoiceBox<>();
alignmentCBox.getItems().addAll(
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
alignmentCBox.getSelectionModel().select(vbox.getAlignment());
alignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::itemChanged);
alignmentCBox.getSelectionModel().selectedIndexProperty().addListener(this::indexChanged);
Label alignmentLabel = new Label("Alignment");
HBox controlGrp = new HBox(alignmentLabel, alignmentCBox,
fillWidthCbx, vGrowCbx, maxSizeButtonCbx);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
}
void maxSizeButton(boolean stretch) {
if (stretch) {
okBtn.setMaxWidth(Double.MAX_VALUE);
cancelBtn.setMaxWidth(Double.MAX_VALUE);
} else {
okBtn.setMaxWidth(okBtn.getPrefWidth());
cancelBtn.setMaxWidth(cancelBtn.getPrefWidth());
}
}
void growVertical(TextArea desc, boolean grow) {
if (grow) {
VBox.setVgrow(desc, Priority.ALWAYS);
} else {
VBox.setVgrow(desc, Priority.NEVER);
}
}
public void itemChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
vbox.setAlignment(newValue);
}
public void indexChanged(ObservableValue<? extends Number> observable,
Number oldValue,
Number newValue) {
}
}
