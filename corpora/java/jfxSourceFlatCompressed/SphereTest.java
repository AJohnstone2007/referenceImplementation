package test.javafx.scene.shape;
import com.sun.javafx.scene.NodeHelper;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.Sphere;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class SphereTest {
@Test
public void testImpl_computeGeomBoundsOnNegDimension() {
Sphere sphere = new Sphere(-10);
NodeHelper.updatePeer(sphere);
assertTrue(sphere.getBoundsInLocal().isEmpty());
assertEquals(sphere.getRadius(), -10, 0.00001);
}
@Test
public void testImpl_computeGeomBoundsOnDegeneratedShape() {
Sphere sphere = new Sphere(0);
NodeHelper.updatePeer(sphere);
assertEquals(sphere.getBoundsInLocal(), new BoundingBox(0, 0, 0, 0, 0, 0));
}
}
