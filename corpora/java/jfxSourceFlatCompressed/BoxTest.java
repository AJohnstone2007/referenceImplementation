package test.javafx.scene.shape;
import com.sun.javafx.scene.NodeHelper;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.Box;
import static org.junit.Assert.*;
import org.junit.Test;
public class BoxTest {
@Test
public void testImpl_computeGeomBoundsOnNegDimension() {
Box box = new Box(10, 10, -10);
NodeHelper.updatePeer(box);
assertTrue(box.getBoundsInLocal().isEmpty());
assertEquals(box.getDepth(), -10, 0.00001);
box = new Box(10, -10, 10);
NodeHelper.updatePeer(box);
assertTrue(box.getBoundsInLocal().isEmpty());
assertEquals(box.getHeight(), -10, 0.00001);
box = new Box(-10, 10, 10);
NodeHelper.updatePeer(box);
assertTrue(box.getBoundsInLocal().isEmpty());
assertEquals(box.getWidth(), -10, 0.00001);
}
@Test
public void testImpl_computeGeomBoundsOnDegeneratedShape() {
Box box = new Box(10, 0, 0);
NodeHelper.updatePeer(box);
assertEquals(box.getBoundsInLocal(), new BoundingBox(-5.0, 0, 0, 10, 0, 0));
box = new Box(10, 20, 0);
NodeHelper.updatePeer(box);
assertEquals(box.getBoundsInLocal(), new BoundingBox(-5.0, -10.0, 0, 10, 20, 0));
box = new Box(0, 0, 0);
NodeHelper.updatePeer(box);
assertEquals(box.getBoundsInLocal(), new BoundingBox(0, 0, 0, 0, 0, 0));
}
}
