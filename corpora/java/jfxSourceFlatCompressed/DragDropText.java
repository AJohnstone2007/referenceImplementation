package dragdrop;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
public class DragDropText extends Application {
private Point2D pressedCoords;
private Timeline timeout;
boolean timeoutPassed;
@Override public void start(final Stage stage) {
final DndTextEdit textEdit = new DndTextEdit();
textEdit.setTranslateX(50);
textEdit.setTranslateY(50);
textEdit.setText("This one features default DnD");
final DndTextEdit macLike = new DndTextEdit();
macLike.setTranslateX(50);
macLike.setTranslateY(130);
macLike.setText("This is forced to behave like Mac");
macLike.setOnMousePressed(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
pressedCoords = new Point2D(event.getSceneX(), event.getSceneY());
timeout = new Timeline();
timeout.getKeyFrames().add(new KeyFrame(new Duration(500),
new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent t) {
timeoutPassed = true;
timeout = null;
}
}));
timeout.play();
timeoutPassed = false;
}
});
macLike.setOnMouseDragged(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
event.setDragDetect(timeoutPassed);
timeoutPassed = false;
if (timeout != null &&
(event.getSceneX() != pressedCoords.getX() ||
event.getSceneY() != pressedCoords.getY())) {
timeout.stop();
macLike.clearSelection();
timeout = null;
}
}
});
final Group root = new Group();
root.getChildren().add(textEdit);
root.getChildren().add(macLike);
final Scene scene = new Scene(root);
stage.setTitle("Drag and Drop Text");
stage.setWidth(500);
stage.setHeight(250);
stage.setResizable(false);
stage.setScene(scene);
stage.show();
textEdit.requestFocus();
}
public static String info() {
return
"This application contains two text drag/drop" +
"boxes. Drag and drop text to/from them";
}
public static void main(String[] args) {
Application.launch(DragDropText.class, args);
}
}
