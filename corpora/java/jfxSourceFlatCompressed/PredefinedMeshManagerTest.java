package test.javafx.scene.shape;
import static org.junit.Assert.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.PredefinedMeshManagerShim;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.scene.NodeHelper;
public class PredefinedMeshManagerTest {
@Before
public void clearCaches() {
PredefinedMeshManagerShim.clearCaches();
}
private void testShapeAddition(Shape3D shape, int correctSize) {
NodeHelper.updatePeer(shape);
int size = -1;
String name = null;
if (shape instanceof Box) {
size = PredefinedMeshManagerShim.getBoxCacheSize();
name = "box";
}
else if (shape instanceof Sphere) {
size = PredefinedMeshManagerShim.getSphereCacheSize();
name = "sphere";
}
else if (shape instanceof Cylinder) {
size = PredefinedMeshManagerShim.getCylinderCacheSize();
name = "cylinder";
}
assertEquals("Added a " + name + " - cache should contain " + correctSize + " mesh.", correctSize, size);
}
@Test
public void boxCacheTest() {
Box box1 = new Box(9, 1, 12);
testShapeAddition(box1 ,1);
Box box2 = new Box(4.5, 4, 6);
testShapeAddition(box2 ,2);
Box box1again = new Box(9, 1, 12);
testShapeAddition(box1again, 2);
}
@Test
public void sphereCacheTest() {
Sphere sphere1 = new Sphere(10, 50);
testShapeAddition(sphere1, 1);
Sphere sphere2 = new Sphere(9.999998, 96);
testShapeAddition(sphere2, 2);
Sphere sphere1again = new Sphere(10, 50);
testShapeAddition(sphere1again, 2);
}
@Test
public void cylinderCacheTest() {
Cylinder cylinder1 = new Cylinder(10, 20, 100);
testShapeAddition(cylinder1, 1);
Cylinder cylinder2 = new Cylinder(30560, 31072, 100);
testShapeAddition(cylinder2, 2);
Cylinder cylinder1again = new Cylinder(10, 20, 100);
testShapeAddition(cylinder1again, 2);
}
}
