package test.javafx.scene.shape;
import com.sun.javafx.scene.NodeHelper;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.Cylinder;
import static org.junit.Assert.*;
import org.junit.Test;
public class CylinderTest {
@Test
public void testGetDivisionsClamp() {
Cylinder cylinder = new Cylinder(10, 10, 1);
assertEquals(cylinder.getDivisions(), 3);
cylinder = new Cylinder(10, 10, -1);
assertEquals(cylinder.getDivisions(), 3);
}
@Test
public void testImpl_computeGeomBoundsOnNegDimension() {
Cylinder cylinder = new Cylinder(10, -10);
NodeHelper.updatePeer(cylinder);
assertTrue(cylinder.getBoundsInLocal().isEmpty());
assertEquals(cylinder.getHeight(), -10, 0.00001);
cylinder = new Cylinder(-10, 10);
NodeHelper.updatePeer(cylinder);
assertTrue(cylinder.getBoundsInLocal().isEmpty());
assertEquals(cylinder.getRadius(), -10, 0.00001);
}
@Test
public void testImpl_computeGeomBoundsOnDegeneratedShape() {
Cylinder cylinder = new Cylinder(0, 0);
NodeHelper.updatePeer(cylinder);
assertEquals(cylinder.getBoundsInLocal(), new BoundingBox(0, 0, 0, 0, 0, 0));
cylinder = new Cylinder(10, 0);
NodeHelper.updatePeer(cylinder);
assertEquals(cylinder.getBoundsInLocal(), new BoundingBox(-10.0, 0, -10.0, 20, 0, 20));
cylinder = new Cylinder(0, 10);
NodeHelper.updatePeer(cylinder);
assertEquals(cylinder.getBoundsInLocal(), new BoundingBox(0, -5.0, 0, 0, 10, 0));
}
}
