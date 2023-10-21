package javafx.scene.layout;
import com.sun.javafx.scene.layout.PaneHelper;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
@DefaultProperty("children")
public class Pane extends Region {
static {
PaneHelper.setPaneAccessor(new PaneHelper.PaneAccessor() {
});
}
static void setConstraint(Node node, Object key, Object value) {
if (value == null) {
node.getProperties().remove(key);
} else {
node.getProperties().put(key, value);
}
if (node.getParent() != null) {
node.getParent().requestLayout();
}
}
static Object getConstraint(Node node, Object key) {
if (node.hasProperties()) {
Object value = node.getProperties().get(key);
if (value != null) {
return value;
}
}
return null;
}
{
PaneHelper.initHelper(this);
}
public Pane() {
super();
}
public Pane(Node... children) {
super();
getChildren().addAll(children);
}
@Override public ObservableList<Node> getChildren() {
return super.getChildren();
}
}
