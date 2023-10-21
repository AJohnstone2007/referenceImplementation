package hello.dialog.wizard;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
public class ImplUtils {
private ImplUtils() {
}
public static List<Node> getChildren(Node n) {
return n instanceof Parent ? getChildren((Parent)n) : Collections.emptyList();
}
@SuppressWarnings("unchecked")
public static ObservableList<Node> getChildren(Parent p) {
ObservableList<Node> children = null;
if (p instanceof Pane) {
children = ((Pane)p).getChildren();
} else if (p instanceof Group) {
children = ((Group)p).getChildren();
} else if (p instanceof Control) {
Control c = (Control) p;
Skin<?> s = c.getSkin();
children = s instanceof SkinBase ? ((SkinBase<?>)s).getChildren() : null;
}
return children == null ? FXCollections.emptyObservableList() : children;
}
}
