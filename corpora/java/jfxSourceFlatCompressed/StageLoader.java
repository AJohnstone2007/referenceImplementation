package test.com.sun.javafx.scene.control.infrastructure;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class StageLoader {
private Group group;
private Scene scene;
private Stage stage;
public StageLoader(Node... content) {
if (content == null || content.length == 0) {
throw new IllegalArgumentException("Null / empty content not allowed");
}
group = new Group();
group.getChildren().setAll(content);
scene = new Scene(group);
stage = new Stage();
stage.setScene(scene);
stage.show();
}
public StageLoader(Scene scene) {
stage = new Stage();
stage.setScene(scene);
stage.show();
}
public Stage getStage() {
return stage;
}
public void dispose() {
if (group != null) {
group.getChildren().clear();
group = null;
}
if (stage != null) {
stage.hide();
scene = null;
stage = null;
}
}
}
