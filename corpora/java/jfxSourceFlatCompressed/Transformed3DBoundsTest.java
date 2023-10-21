package test.javafx.scene.bounds;
import static test.com.sun.javafx.test.TestHelper.assertSimilar;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import org.junit.Test;
public class Transformed3DBoundsTest {
public @Test
void test3DBoundsForTranslatedRectangle() {
Rectangle rect = new Rectangle(100, 100);
rect.setTranslateX(10);
rect.setTranslateY(10);
rect.setTranslateZ(10);
BoundingBox bb = box(0, 0, 0, 100, 100, 0);
BoundingBox tbb = box(10, 10, 10, 100, 100, 0);
assertEquals(bb, rect.getBoundsInLocal());
assertEquals(tbb, rect.getBoundsInParent());
assertEquals(rect.getBoundsInLocal(), rect.getLayoutBounds());
assertSimilar(tbb, rect.localToParent(rect.getBoundsInLocal()));
assertSimilar(tbb, rect.localToScene(rect.getBoundsInLocal()));
assertSimilar(bb, rect.parentToLocal(
rect.localToParent(rect.getBoundsInLocal())));
assertSimilar(bb, rect.sceneToLocal(
rect.localToScene(rect.getBoundsInLocal())));
}
public @Test
void test3DBoundsForNegativeScaledRectangle() {
Rectangle rect = new Rectangle(100, 100);
rect.setScaleX(2);
rect.setScaleY(2);
rect.setScaleZ(2);
BoundingBox bb = box(0, 0, 0, 100, 100, 0);
BoundingBox tbb = box(-50, -50, 200, 200);
assertEquals(bb, rect.getBoundsInLocal());
assertEquals(tbb, rect.getBoundsInParent());
assertEquals(rect.getBoundsInLocal(), rect.getLayoutBounds());
assertSimilar(tbb, rect.localToParent(rect.getBoundsInLocal()));
assertSimilar(tbb, rect.localToScene(rect.getBoundsInLocal()));
assertSimilar(bb, rect.parentToLocal(
rect.localToParent(rect.getBoundsInLocal())));
assertSimilar(bb, rect.sceneToLocal(
rect.localToScene(rect.getBoundsInLocal())));
}
public @Test
void testBoundsForXRotatedRectangle() {
Rectangle rect = new Rectangle(100, 100);
rect.setRotate(90);
rect.setRotationAxis(Rotate.X_AXIS);
BoundingBox bb = box(0, 0, 0, 100, 100, 0);
BoundingBox tbb = box(0, 50, -50, 100, 0, 100);
assertEquals(bb, rect.getBoundsInLocal());
assertSimilar(tbb, rect.getBoundsInParent());
assertEquals(rect.getBoundsInLocal(), rect.getLayoutBounds());
assertSimilar(tbb, rect.localToParent(rect.getBoundsInLocal()));
assertSimilar(tbb, rect.localToScene(rect.getBoundsInLocal()));
assertSimilar(bb, rect.parentToLocal(
rect.localToParent(rect.getBoundsInLocal())));
assertSimilar(bb, rect.sceneToLocal(
rect.localToScene(rect.getBoundsInLocal())));
}
public @Test
void testBoundsForYRotatedRectangle() {
Rectangle rect = new Rectangle(100, 100);
rect.setRotate(90);
rect.setRotationAxis(Rotate.Y_AXIS);
BoundingBox bb = box(0, 0, 0, 100, 100, 0);
BoundingBox tbb = box(50, 0, -50, 0, 100, 100);
assertEquals(bb, rect.getBoundsInLocal());
assertSimilar(tbb, rect.getBoundsInParent());
assertEquals(rect.getBoundsInLocal(), rect.getLayoutBounds());
assertSimilar(tbb, rect.localToParent(rect.getBoundsInLocal()));
assertSimilar(tbb, rect.localToScene(rect.getBoundsInLocal()));
assertSimilar(bb, rect.parentToLocal(
rect.localToParent(rect.getBoundsInLocal())));
assertSimilar(bb, rect.sceneToLocal(
rect.localToScene(rect.getBoundsInLocal())));
}
public @Test
void testBoundsForZRotatedRectangle() {
Rectangle rect = new Rectangle(100, 100);
rect.setRotate(90);
rect.setRotationAxis(Rotate.Z_AXIS);
BoundingBox bb = box(0, 0, 0, 100, 100, 0);
BoundingBox tbb = box(0, 0, 0, 100, 100, 0);
assertEquals(bb, rect.getBoundsInLocal());
assertSimilar(tbb, rect.getBoundsInParent());
assertEquals(rect.getBoundsInLocal(), rect.getLayoutBounds());
assertSimilar(tbb, rect.localToParent(rect.getBoundsInLocal()));
assertSimilar(tbb, rect.localToScene(rect.getBoundsInLocal()));
assertSimilar(bb, rect.parentToLocal(
rect.localToParent(rect.getBoundsInLocal())));
assertSimilar(bb, rect.sceneToLocal(
rect.localToScene(rect.getBoundsInLocal())));
}
}
