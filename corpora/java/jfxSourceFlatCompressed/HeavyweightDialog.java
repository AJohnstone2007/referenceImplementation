package javafx.scene.control;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
class HeavyweightDialog extends FXDialog {
final Stage stage = new Stage() {
@Override public void centerOnScreen() {
Window owner = HeavyweightDialog.this.getOwner();
if (owner != null) {
positionStage();
} else {
if (getWidth() > 0 && getHeight() > 0) {
super.centerOnScreen();
}
}
}
};
private Scene scene;
private final Parent DUMMY_ROOT = new Region();
private final Dialog<?> dialog;
private DialogPane dialogPane;
private double prefX = Double.NaN;
private double prefY = Double.NaN;
HeavyweightDialog(Dialog<?> dialog) {
this.dialog = dialog;
stage.setResizable(false);
stage.setOnCloseRequest(windowEvent -> {
if (requestPermissionToClose(dialog)) {
dialog.close();
} else {
windowEvent.consume();
}
});
stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
if (keyEvent.getCode() == KeyCode.ESCAPE) {
if (!keyEvent.isConsumed() && requestPermissionToClose(dialog)) {
dialog.close();
keyEvent.consume();
}
}
});
}
@Override void initStyle(StageStyle style) {
stage.initStyle(style);
}
@Override StageStyle getStyle() {
return stage.getStyle();
}
@Override public void initOwner(Window newOwner) {
updateStageBindings(stage.getOwner(), newOwner);
stage.initOwner(newOwner);
}
@Override public Window getOwner() {
return stage.getOwner();
}
@Override public void initModality(Modality modality) {
stage.initModality(modality == null? Modality.APPLICATION_MODAL : modality);
}
@Override public Modality getModality() {
return stage.getModality();
}
@Override public void setDialogPane(DialogPane dialogPane) {
this.dialogPane = dialogPane;
if (scene == null) {
scene = new Scene(dialogPane);
stage.setScene(scene);
} else {
scene.setRoot(dialogPane);
}
dialogPane.autosize();
stage.sizeToScene();
}
@Override public void show() {
scene.setRoot(dialogPane);
stage.centerOnScreen();
stage.show();
}
@Override public void showAndWait() {
scene.setRoot(dialogPane);
stage.centerOnScreen();
stage.showAndWait();
}
@Override public void close() {
if (stage.isShowing()) {
stage.hide();
}
if (scene != null) {
scene.setRoot(DUMMY_ROOT);
}
}
@Override public ReadOnlyBooleanProperty showingProperty() {
return stage.showingProperty();
}
@Override public Window getWindow() {
return stage;
}
@Override public Node getRoot() {
return stage.getScene().getRoot();
}
@Override public double getX() {
return stage.getX();
}
@Override public void setX(double x) {
stage.setX(x);
}
@Override public ReadOnlyDoubleProperty xProperty() {
return stage.xProperty();
}
@Override public double getY() {
return stage.getY();
}
@Override public void setY(double y) {
stage.setY(y);
}
@Override public ReadOnlyDoubleProperty yProperty() {
return stage.yProperty();
}
@Override ReadOnlyDoubleProperty heightProperty() {
return stage.heightProperty();
}
@Override void setHeight(double height) {
stage.setHeight(height);
}
@Override double getSceneHeight() {
return scene == null ? 0 : scene.getHeight();
}
@Override ReadOnlyDoubleProperty widthProperty() {
return stage.widthProperty();
}
@Override void setWidth(double width) {
stage.setWidth(width);
}
@Override BooleanProperty resizableProperty() {
return stage.resizableProperty();
}
@Override StringProperty titleProperty() {
return stage.titleProperty();
}
@Override ReadOnlyBooleanProperty focusedProperty() {
return stage.focusedProperty();
}
@Override public void sizeToScene() {
stage.sizeToScene();
}
private void positionStage() {
double x = getX();
double y = getY();
if (!Double.isNaN(x) && !Double.isNaN(y) &&
Double.compare(x, prefX) != 0 && Double.compare(y, prefY) != 0) {
setX(x);
setY(y);
return;
}
dialogPane.applyCss();
dialogPane.layout();
final Window owner = getOwner();
final Scene ownerScene = owner.getScene();
final double titleBarHeight = ownerScene.getY();
final double dialogWidth = dialogPane.prefWidth(-1);
final double dialogHeight = dialogPane.prefHeight(dialogWidth);
x = owner.getX() + (ownerScene.getWidth() / 2.0) - (dialogWidth / 2.0);
y = owner.getY() + titleBarHeight / 2.0 + (ownerScene.getHeight() / 2.0) - (dialogHeight / 2.0);
prefX = x;
prefY = y;
setX(x);
setY(y);
}
private void updateStageBindings(Window oldOwner, Window newOwner) {
final Scene dialogScene = stage.getScene();
if (oldOwner != null && oldOwner instanceof Stage) {
Stage oldStage = (Stage) oldOwner;
Bindings.unbindContent(stage.getIcons(), oldStage.getIcons());
stage.renderScaleXProperty().unbind();
stage.renderScaleYProperty().unbind();
Scene oldScene = oldStage.getScene();
if (scene != null && dialogScene != null) {
Bindings.unbindContent(dialogScene.getStylesheets(), oldScene.getStylesheets());
}
}
if (newOwner instanceof Stage) {
Stage newStage = (Stage) newOwner;
Bindings.bindContent(stage.getIcons(), newStage.getIcons());
stage.renderScaleXProperty().bind(newStage.renderScaleXProperty());
stage.renderScaleYProperty().bind(newStage.renderScaleYProperty());
Scene newScene = newStage.getScene();
if (scene != null && dialogScene != null) {
Bindings.bindContent(dialogScene.getStylesheets(), newScene.getStylesheets());
}
}
}
}
