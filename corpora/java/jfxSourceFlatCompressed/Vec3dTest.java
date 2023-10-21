package test.com.sun.javafx.geom;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3f;
import org.junit.Test;
import static org.junit.Assert.*;
public class Vec3dTest {
private static double EPSILON = 1e-10;
@Test
public void testDefaultContructor() {
Vec3d v3d = new Vec3d();
assertEquals(0, v3d.x, 0);
assertEquals(0, v3d.y, 0);
assertEquals(0, v3d.z, 0);
}
@Test
public void testContructor1() {
Vec3d v3d = new Vec3d(1.0, 2.0, 3.0);
assertEquals(1, v3d.x, 0);
assertEquals(2, v3d.y, 0);
assertEquals(3, v3d.z, 0);
}
@Test
public void testContructor2() {
Vec3f v3f = new Vec3f(1f, 2f, 3f);
Vec3d v3d = new Vec3d(v3f);
assertEquals(1, v3d.x, 0);
assertEquals(2, v3d.y, 0);
assertEquals(3, v3d.z, 0);
}
@Test
public void testLength() {
Vec3d v3d = new Vec3d();
double len = v3d.length();
assertEquals(0, len, 0);
v3d = new Vec3d(1, 2, 3);
len = v3d.length();
assertEquals(Math.sqrt(14.0), len, EPSILON);
v3d = new Vec3d(-1, 2, 3);
len = v3d.length();
assertEquals(Math.sqrt(14.0), len, EPSILON);
v3d = new Vec3d(1, -0.2, -0.03);
len = v3d.length();
assertEquals(Math.sqrt(1.0409), len, EPSILON);
v3d = new Vec3d(-0.1, -0.2, -0.3);
len = v3d.length();
assertEquals(Math.sqrt(0.14), len, EPSILON);
}
}
