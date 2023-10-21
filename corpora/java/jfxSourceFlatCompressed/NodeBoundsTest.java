package test.javafx.scene.bounds;
import static test.com.sun.javafx.test.TestHelper.assertBoundsEqual;
import static test.com.sun.javafx.test.TestHelper.assertSimilar;
import static test.com.sun.javafx.test.TestHelper.box;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
public class NodeBoundsTest {
public @Test
void testBoundsForLeafNode() {
Rectangle rect = new Rectangle(10, 10, 50, 50);
assertSimilar(box(10, 10, 50, 50), rect.getBoundsInLocal());
}
public @Test
void testBounds3DForLeafNode() {
Rectangle rect = new Rectangle(10, 10, 50, 50);
rect.setTranslateZ(30);
assertSimilar(box(10, 10, 30, 50, 50, 30), rect.getBoundsInLocal());
}
public @Test
void testBoundsForInvisibleLeafNode() {
Rectangle rect = new Rectangle(10, 10, 50, 50);
rect.setVisible(false);
assertBoundsEqual(box(10, 10, 50, 50), rect.getBoundsInLocal());
assertBoundsEqual(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setVisible(true);
assertBoundsEqual(box(10, 10, 50, 50), rect.getBoundsInLocal());
assertBoundsEqual(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setVisible(false);
assertBoundsEqual(box(10, 10, 50, 50), rect.getBoundsInLocal());
assertBoundsEqual(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test
void testBoundsForLeafNodeUpdatesWhenGeomChanges() {
Rectangle rect = new Rectangle();
assertBoundsEqual(box(0, 0, 0, 0), rect.getBoundsInLocal());
rect.setX(50);
assertBoundsEqual(box(50, 0, 0, 0), rect.getBoundsInLocal());
rect.setY(50);
rect.setWidth(100);
rect.setHeight(30);
assertBoundsEqual(box(50, 50, 100, 30), rect.getBoundsInLocal());
}
}
