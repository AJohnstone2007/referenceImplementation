package javafx.scene.control;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
public class ChoiceDialog<T> extends Dialog<T> {
private final GridPane grid;
private final Label label;
private final ComboBox<T> comboBox;
private final T defaultChoice;
public ChoiceDialog() {
this((T)null, (T[])null);
}
public ChoiceDialog(T defaultChoice, @SuppressWarnings("unchecked") T... choices) {
this(defaultChoice,
choices == null ? Collections.emptyList() : Arrays.asList(choices));
}
public ChoiceDialog(T defaultChoice, Collection<T> choices) {
final DialogPane dialogPane = getDialogPane();
this.grid = new GridPane();
this.grid.setHgap(10);
this.grid.setMaxWidth(Double.MAX_VALUE);
this.grid.setAlignment(Pos.CENTER_LEFT);
label = DialogPane.createContentLabel(dialogPane.getContentText());
label.setPrefWidth(Region.USE_COMPUTED_SIZE);
label.textProperty().bind(dialogPane.contentTextProperty());
dialogPane.contentTextProperty().addListener(o -> updateGrid());
setTitle(ControlResources.getString("Dialog.confirm.title"));
dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
dialogPane.getStyleClass().add("choice-dialog");
dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
final double MIN_WIDTH = 150;
comboBox = new ComboBox<T>();
comboBox.setMinWidth(MIN_WIDTH);
if (choices != null) {
comboBox.getItems().addAll(choices);
}
comboBox.setMaxWidth(Double.MAX_VALUE);
GridPane.setHgrow(comboBox, Priority.ALWAYS);
GridPane.setFillWidth(comboBox, true);
this.defaultChoice = comboBox.getItems().contains(defaultChoice) ? defaultChoice : null;
if (defaultChoice == null) {
comboBox.getSelectionModel().selectFirst();
} else {
comboBox.getSelectionModel().select(defaultChoice);
}
updateGrid();
setResultConverter((dialogButton) -> {
ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
return data == ButtonData.OK_DONE ? getSelectedItem() : null;
});
}
public final T getSelectedItem() {
return comboBox.getSelectionModel().getSelectedItem();
}
public final ReadOnlyObjectProperty<T> selectedItemProperty() {
return comboBox.getSelectionModel().selectedItemProperty();
}
public final void setSelectedItem(T item) {
comboBox.getSelectionModel().select(item);
}
public final ObservableList<T> getItems() {
return comboBox.getItems();
}
public final T getDefaultChoice() {
return defaultChoice;
}
private void updateGrid() {
grid.getChildren().clear();
grid.add(label, 0, 0);
grid.add(comboBox, 1, 0);
getDialogPane().setContent(grid);
Platform.runLater(() -> comboBox.requestFocus());
}
}
