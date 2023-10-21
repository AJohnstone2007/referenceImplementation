package javafx.scene.control;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
public class TextInputDialog extends Dialog<String> {
private final GridPane grid;
private final Label label;
private final TextField textField;
private final String defaultValue;
public TextInputDialog() {
this("");
}
public TextInputDialog(@NamedArg("defaultValue") String defaultValue) {
final DialogPane dialogPane = getDialogPane();
this.textField = new TextField(defaultValue);
this.textField.setMaxWidth(Double.MAX_VALUE);
GridPane.setHgrow(textField, Priority.ALWAYS);
GridPane.setFillWidth(textField, true);
label = DialogPane.createContentLabel(dialogPane.getContentText());
label.setPrefWidth(Region.USE_COMPUTED_SIZE);
label.textProperty().bind(dialogPane.contentTextProperty());
this.defaultValue = defaultValue;
this.grid = new GridPane();
this.grid.setHgap(10);
this.grid.setMaxWidth(Double.MAX_VALUE);
this.grid.setAlignment(Pos.CENTER_LEFT);
dialogPane.contentTextProperty().addListener(o -> updateGrid());
setTitle(ControlResources.getString("Dialog.confirm.title"));
dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
dialogPane.getStyleClass().add("text-input-dialog");
dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
updateGrid();
setResultConverter((dialogButton) -> {
ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
return data == ButtonData.OK_DONE ? textField.getText() : null;
});
}
public final TextField getEditor() {
return textField;
}
public final String getDefaultValue() {
return defaultValue;
}
private void updateGrid() {
grid.getChildren().clear();
grid.add(label, 0, 0);
grid.add(textField, 1, 0);
getDialogPane().setContent(grid);
Platform.runLater(() -> textField.requestFocus());
}
}
