package test.com.sun.javafx.test.objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
public class TestGroup extends Group {
private final String name;
private final ObjectProperty<Scene> _sceneProperty =
new SimpleObjectProperty<Scene>() {
private Scene old_scene;
@Override
protected void invalidated() {
final Scene new_scene = get();
if (old_scene != new_scene) {
if (getScene() != new_scene) {
if (old_scene != null) {
old_scene.setRoot(null);
}
if (new_scene != null) {
new_scene.setRoot(TestGroup.this);
}
}
old_scene = new_scene;
}
}
};
public TestGroup() {
this("GROUP");
}
public TestGroup(final String name) {
this.name = name;
}
public void set_scene(final Scene scene) {
_sceneProperty.set(scene);
}
public Scene get_scene() {
return _sceneProperty.get();
}
public ObjectProperty<Scene> _sceneProperty() {
return _sceneProperty;
}
@Override
public String toString() {
return name;
}
}
