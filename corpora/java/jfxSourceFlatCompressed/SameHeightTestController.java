package modena;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
public class SameHeightTestController implements Initializable{
private @FXML Button horizFirstButton;
private @FXML TextField vertFirstTextField;
private @FXML Region horizBaseLine;
private @FXML Region vertBaseLine;
private @FXML Region arrowButtonLeftLine;
private @FXML Region arrowButtonRightLine;
private @FXML Region arrowLeftLine;
private @FXML Region arrowRightLine;
private @FXML ComboBox editableCombo;
private @FXML AnchorPane arrowButtonContainer;
private Node arrowButton;
private Node arrow;
@Override public void initialize(URL url, ResourceBundle rb) {
Platform.runLater(() -> {
Text buttonTextNode = (Text)horizFirstButton.lookup(".text");
buttonTextNode.layoutYProperty().addListener((ov, t, t1) -> StackPane.setMargin(horizBaseLine, new Insets(t1.doubleValue(),0,0,0)));
Text textFieldTextNode = (Text)vertFirstTextField.lookup(".text");
textFieldTextNode.layoutXProperty().addListener((ov, t, t1) -> StackPane.setMargin(vertBaseLine, new Insets(0,0,0,t1.doubleValue())));
arrowButton = editableCombo.lookup(".arrow-button");
arrow = editableCombo.lookup(".arrow");
ChangeListener updater = (ov, t, t1) -> updateArrowLinePositions();
arrow.layoutBoundsProperty().addListener(updater);
arrowButton.layoutBoundsProperty().addListener(updater);
editableCombo.layoutBoundsProperty().addListener(updater);
arrowButtonContainer.layoutBoundsProperty().addListener(updater);
updateArrowLinePositions();
});
}
private void updateArrowLinePositions() {
double left = arrowButton.localToScene(0, 0).getX() - arrowButtonContainer.localToScene(0, 0).getX();
arrowButtonLeftLine.setLayoutX(left-1);
arrowButtonLeftLine.setPrefHeight(arrowButtonContainer.getLayoutBounds().getHeight());
arrowButtonRightLine.setLayoutX(left + arrowButton.getLayoutBounds().getWidth());
arrowButtonRightLine.setPrefHeight(arrowButtonContainer.getLayoutBounds().getHeight());
double arrowLeft = arrow.localToScene(0, 0).getX() - arrowButtonContainer.localToScene(0, 0).getX();
arrowLeftLine.setLayoutX(arrowLeft-1);
arrowLeftLine.setPrefHeight(arrowButtonContainer.getLayoutBounds().getHeight());
arrowRightLine.setLayoutX(arrowLeft + arrow.getLayoutBounds().getWidth());
arrowRightLine.setPrefHeight(arrowButtonContainer.getLayoutBounds().getHeight());
}
}
