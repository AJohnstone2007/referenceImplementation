package layout;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
public class StackPaneTab extends Tab {
final StackPane stackPane = new StackPane();
final Button btn = new Button("Button");
public StackPaneTab(String text) {
this.setText(text);
init();
}
public void init() {
Pane pane = new Pane();
pane.setPrefSize(400, 300);
pane.setStyle("-fx-background-color: green, white;"
+ "-fx-background-insets: 0, 4;"
+ "-fx-background-radius: 4, 2;");
pane.setMaxSize(pane.getPrefWidth(), pane.getPrefHeight());
stackPane.getChildren().addAll(pane, btn);
stackPane.getStyleClass().add("layout");
BorderPane root = new BorderPane(stackPane);
Label alignmentLabel = new Label("Alignment");
ChoiceBox<Pos> alignmentCBox = new ChoiceBox<>();
alignmentCBox.getItems().addAll(
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
alignmentCBox.getSelectionModel().select(stackPane.getAlignment());
alignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::alignmentChanged);
Label childAlignmentLabel = new Label("Button Alignment");
ChoiceBox<Pos> childAlignmentCBox = new ChoiceBox<>();
childAlignmentCBox.getItems().addAll(null,
Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT,
Pos.BOTTOM_CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT,
Pos.CENTER, Pos.CENTER_LEFT, Pos.CENTER_RIGHT,
Pos.TOP_CENTER, Pos.TOP_LEFT, Pos.TOP_RIGHT);
childAlignmentCBox.getSelectionModel().select(StackPane.getAlignment(btn));
childAlignmentCBox.getSelectionModel().selectedItemProperty().addListener(this::childAlignmentChanged);
HBox controlGrp = new HBox(alignmentLabel, alignmentCBox, childAlignmentLabel, childAlignmentCBox);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
}
public void alignmentChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
stackPane.setAlignment(newValue);
}
public void childAlignmentChanged(ObservableValue<? extends Pos> observable,
Pos oldValue,
Pos newValue) {
StackPane.setAlignment(btn, newValue);
}
}
