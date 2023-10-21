package test.javafx.scene;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParentShim;
import org.junit.Before;
import org.junit.Test;
public class Node_lookup_Test {
private Group root, a, bc, d, e, d2;
@Before public void setup() {
root = new Group();
root.setId("root");
a = new Group();
a.setId("a");
d = new Group();
d.getStyleClass().add("d");
e = new Group();
e.setId("e");
bc = new Group();
bc.getStyleClass().addAll("b", "c");
d2 = new Group();
d2.getStyleClass().add("d");
ParentShim.getChildren(root).addAll(a, bc);
ParentShim.getChildren(a).addAll(d, e);
ParentShim.getChildren(bc).addAll(d2);
}
@Test public void quickTest() {
Node found = root.lookup("Group");
assertSame(root, found);
found = root.lookup("#a");
assertSame(a, found);
found = root.lookup("#a > .d");
assertSame(d, found);
found = root.lookup("#e");
assertSame(e, found);
found = root.lookup(".b .d");
assertSame(d2, found);
found = root.lookup(".c .d");
assertSame(d2, found);
found = root.lookup(".b");
assertSame(bc, found);
}
@Test public void lookupAllTest() {
Set<Node> nodes = root.lookupAll("#a");
assertEquals(1, nodes.size());
assertTrue(nodes.contains(a));
nodes = root.lookupAll(".d");
assertEquals(2, nodes.size());
assertTrue(nodes.contains(d));
assertTrue(nodes.contains(d2));
}
}
