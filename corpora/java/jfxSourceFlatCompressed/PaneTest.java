package test.javafx.scene;
import javafx.scene.Node;
import javafx.scene.ParentShim;
import test.javafx.scene.layout.MockRegion;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class PaneTest {
@Test
public void testPrefWidthWithResizableChild() {
Pane pane = new Pane();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(pane).add(region);
pane.layout();
assertEquals(100, pane.prefWidth(-1), 0);
}
@Test
public void testPrefHeightWithResizableChild() {
Pane pane = new Pane();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(pane).add(region);
pane.layout();
assertEquals(150, pane.prefHeight(-1), 0);
}
@Test
public void testMinAndPreferredSizes() {
Pane pane = new Pane();
Rectangle rect = new Rectangle(50,50);
ParentShim.getChildren(pane).add(rect);
rect.relocate(0, 0);
pane.layout();
assertEquals(0, pane.minWidth(-1), 1e-100);
assertEquals(0, pane.minHeight(-1), 1e-100);
assertEquals(50, pane.prefWidth(-1), 1e-100);
assertEquals(50, pane.prefHeight(-1), 1e-100);
rect.setWidth(100);
assertEquals(0, pane.minWidth(-1), 1e-100);
assertEquals(0, pane.minHeight(-1), 1e-100);
assertEquals(100, pane.prefWidth(-1), 1e-100);
assertEquals(50, pane.prefHeight(-1), 1e-100);
rect.setHeight(200);
assertEquals(0, pane.minWidth(-1), 1e-100);
assertEquals(0, pane.minHeight(-1), 1e-100);
assertEquals(100, pane.prefWidth(-1), 1e-100);
assertEquals(200, pane.prefHeight(-1), 1e-100);
}
@Test
public void testPrefSizeRespectsBounds() {
Pane pane = new Pane();
Node n1 = new MockRegion(100, 100, 10, 10, 1000, 1000);
n1.relocate(10, 0);
Node n2 = new MockRegion(0, 0, 200, 200, 100, 100);
n2.relocate(0, 20);
ParentShim.getChildren(pane).addAll(n1, n2);
pane.layout();
assertEquals(110, pane.prefWidth(-1), 1e-100);
assertEquals(120, pane.prefHeight(-1), 1e-100);
}
}
