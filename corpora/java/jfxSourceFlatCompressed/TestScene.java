package test.com.sun.javafx.test.objects;
import com.sun.javafx.scene.SceneHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
public class TestScene extends Scene {
private final String name;
private final ObjectProperty<Window> _windowProperty =
new ObjectPropertyBase<Window>() {
private Window old_window;
@Override
protected void invalidated() {
final Window new_window = get();
if (old_window != new_window) {
if (getWindow() != new_window) {
if (new_window instanceof Stage) {
((Stage)new_window).setScene(TestScene.this);
} else if (old_window instanceof Stage) {
((Stage)old_window).setScene(null);
}
}
old_window = new_window;
}
}
@Override
public Object getBean() {
return TestScene.this;
}
@Override
public String getName() {
return "_window";
}
};
public TestScene(final Parent root) {
this("SCENE", root);
}
public TestScene(final String name, final Parent root) {
super(root);
this.name = name;
SceneHelper.preferredSize(this);
}
public void set_window(final Window window) {
_windowProperty.set(window);
}
public Window get_window() {
return _windowProperty.get();
}
public ObjectProperty<Window> _windowProperty() {
return _windowProperty;
}
@Override
public String toString() {
return name;
}
}
