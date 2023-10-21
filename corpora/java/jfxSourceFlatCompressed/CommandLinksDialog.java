package hello.dialog.dialogs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
public class CommandLinksDialog extends Dialog<ButtonType> {
private final static int gapSize = 10;
private final List<Button> buttons = new ArrayList<>();
private GridPane grid = new GridPane() {
@Override protected double computePrefWidth(double height) {
double pw = 0;
for (int i = 0; i < buttons.size(); i++) {
Button btn = buttons.get(i);
pw = Math.min(pw, btn.prefWidth(-1));
}
return pw + gapSize;
}
@Override protected double computePrefHeight(double width) {
double ph = getDialogPane().getHeader() == null ? 0 : 10;
for (int i = 0; i < buttons.size(); i++) {
Button btn = buttons.get(i);
ph += btn.prefHeight(width) + gapSize;
}
return ph * 1.5;
}
};
public CommandLinksDialog(ButtonType... links) {
this(Arrays.asList(links));
}
public CommandLinksDialog(List<ButtonType> links) {
this.grid.setHgap(gapSize);
this.grid.setVgap(gapSize);
final DialogPane dialogPane = new DialogPane() {
@Override protected Node createButtonBar() {
return null;
}
};
setDialogPane(dialogPane);
dialogPane.getStylesheets().add(getClass().getResource("commandlink.css").toExternalForm());
dialogPane.setGraphic(new ImageView(new Image(getClass().getResource("/hello/dialog/dialog-information.png").toExternalForm())));
dialogPane.getButtonTypes().addAll(links);
dialogPane.contentProperty().addListener(o -> updateGrid());
updateGrid();
dialogPane.getButtonTypes().addListener((ListChangeListener<? super ButtonType>)c -> {
updateGrid();
});
}
private void updateGrid() {
final Node content = getDialogPane().getContent();
final boolean dialogContentIsGrid = grid == content;
if (! dialogContentIsGrid) {
if (content != null) {
content.getStyleClass().add("command-link-message");
grid.add(content, 0, 0);
}
}
grid.getChildren().removeAll(buttons);
int row = 1;
for (final ButtonType action : getDialogPane().getButtonTypes()) {
if (action == null) continue;
final Button button = buildCommandLinkButton(action);
final ButtonData buttonType = action.getButtonData();
button.setDefaultButton(buttonType != null && buttonType.isDefaultButton());
button.setOnAction(new EventHandler<ActionEvent>() {
@Override public void handle(ActionEvent ae) {
setResult(action);
}
});
GridPane.setHgrow(button, Priority.ALWAYS);
GridPane.setVgrow(button, Priority.ALWAYS);
grid.add(button, 0, row++);
buttons.add(button);
}
GridPane.setMargin(buttons.get(buttons.size() - 1), new Insets(0,0,10,0));
if (! dialogContentIsGrid) {
getDialogPane().setContent(grid);
}
}
private Button buildCommandLinkButton(ButtonType commandLink) {
final Button button = new Button();
button.getStyleClass().addAll("command-link-button");
button.setMaxHeight(Double.MAX_VALUE);
button.setMaxWidth(Double.MAX_VALUE);
button.setAlignment(Pos.CENTER_LEFT);
final Label titleLabel = new Label(commandLink.getText() );
titleLabel.minWidthProperty().bind(new DoubleBinding() {
{
bind(titleLabel.prefWidthProperty());
}
@Override protected double computeValue() {
return titleLabel.getPrefWidth() + 400;
}
});
titleLabel.getStyleClass().addAll("line-1");
titleLabel.setWrapText(true);
titleLabel.setAlignment(Pos.TOP_LEFT);
GridPane.setVgrow(titleLabel, Priority.NEVER);
ImageView arrow = new ImageView(getClass().getResource("/hello/about_16.png").toExternalForm());
GridPane.setValignment(arrow, VPos.TOP);
GridPane.setMargin(arrow, new Insets(0,10,0,0));
GridPane grid = new GridPane();
grid.minWidthProperty().bind(titleLabel.prefWidthProperty());
grid.setMaxHeight(Double.MAX_VALUE);
grid.setMaxWidth(Double.MAX_VALUE);
grid.getStyleClass().add("container");
grid.add(arrow, 0, 0, 1, 2);
grid.add(titleLabel, 1, 0);
button.setGraphic(grid);
button.minWidthProperty().bind(titleLabel.prefWidthProperty());
return button;
}
}
