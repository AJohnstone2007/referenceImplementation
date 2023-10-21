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
public class HBoxTab extends Tab {
final HBox hbox = new HBox(10);
final Button okBtn = new Button("OK");
final Button cancelBtn = new Button("Cancel");
public HBoxTab(String text) {
this.setText(text);
init();
}
public void init() {
final Label descLbl = new Label("Description:");
final TextArea desc = new TextArea();
desc.setPrefColumnCount(10);
desc.setPrefRowCount(3);
desc.setWrapText(true);
hbox.getChildren().addAll(descLbl, desc, okBtn, cancelBtn);
hbox.getStyleClass().add("layout");
BorderPane root = new BorderPane(hbox);
final CheckBox hGrowCbx = new CheckBox("TextArea Hgrow");
hGrowCbx.setSelected(false);
hGrowCbx.setOnAction(e
-> growHorizontal(desc, hGrowCbx.isSelected()));
CheckBox maxSizeButtonCbx = new CheckBox("Button Max Size");
maxSizeButtonCbx.setOnAction(e -> maxSizeButton(maxSizeButtonCbx.isSelected()));
CheckBox fillHeightCbx = new CheckBox("Fill Height");
fillHeightCbx.setSelected(true);
fillHeightCbx.setOnAction(e
-> hbox.setFillHeight(fillHeightCbx.isSelected()));
ChoiceBox<Pos> alignmentCBox = new ChoiceBox<>();
alignmentCBox.getItems().addAll(
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
alignmentCBox.getSelectionModel().select(hbox.getAlignment());
alignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::itemChanged);
alignmentCBox.getSelectionModel().selectedIndexProperty().addListener(this::indexChanged);
Label alignmentLabel = new Label("Alignment");
HBox controlGrp = new HBox(alignmentLabel, alignmentCBox,
fillHeightCbx, hGrowCbx, maxSizeButtonCbx);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
}
void maxSizeButton(boolean stretch) {
if (stretch) {
okBtn.setMaxHeight(Double.MAX_VALUE);
cancelBtn.setMaxHeight(Double.MAX_VALUE);
} else {
okBtn.setMaxHeight(okBtn.getPrefHeight());
cancelBtn.setMaxHeight(cancelBtn.getPrefHeight());
}
}
void growHorizontal(TextArea desc, boolean grow) {
if (grow) {
HBox.setHgrow(desc, Priority.ALWAYS);
} else {
HBox.setHgrow(desc, Priority.NEVER);
}
}
public void itemChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
hbox.setAlignment(newValue);
}
public void indexChanged(ObservableValue<? extends Number> observable,
Number oldValue,
Number newValue) {
}
}
