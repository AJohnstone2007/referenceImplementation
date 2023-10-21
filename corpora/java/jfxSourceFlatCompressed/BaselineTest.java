package test.javafx.scene.layout;
import static org.junit.Assert.assertEquals;
import javafx.scene.Parent;
import javafx.scene.ParentShim;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Test;
public class BaselineTest {
@Test public void testShapeBaselineAtBottom() {
Rectangle rect = new Rectangle(100,200);
assertEquals(200, rect.getBaselineOffset(),1e-100);
}
@Test public void testTextBaseline() {
Text text = new Text("Graphically");
float size = (float) text.getFont().getSize();
assertEquals(size, text.getBaselineOffset(),1e-100);
}
@Test public void testParentBaselineMatchesFirstChild() {
Parent p = new MockParent();
p.layout();
assertEquals(180, p.getBaselineOffset(),1e-100);
}
@Test public void testParentBaselineIgnoresUnmanagedChild() {
MockParent p = new MockParent();
Rectangle r = new Rectangle(20,30);
r.setManaged(false);
ParentShim.getChildren(p).add(0, r);
p.layout();
assertEquals(180, p.getBaselineOffset(),1e-100);
}
}
