package javafx.scene;
import java.util.List;
import javafx.collections.ObservableList;
public class ParentShim extends Parent {
public static final int DIRTY_CHILDREN_THRESHOLD = Parent.DIRTY_CHILDREN_THRESHOLD;
public ObservableList<Node> getChildren() {
return super.getChildren();
}
public static ObservableList<Node> getChildren(Parent p) {
return p.getChildren();
}
public static <E extends Node> List<E> getManagedChildren(Parent p) {
return p.getManagedChildren();
}
public static List<Node> test_getRemoved(Parent p) {
return p.test_getRemoved();
}
public static List<Node> test_getViewOrderChildren(Parent p) {
return p.test_getViewOrderChildren();
}
}
