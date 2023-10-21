package test.com.sun.javafx.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
public class TestHelper {
public static void assertSimilar(Bounds expected, Bounds actual) {
assertEquals("minX", expected.getMinX(), actual.getMinX(), 1);
assertEquals("minY", expected.getMinY(), actual.getMinY(), 1);
assertEquals("maxX", expected.getMaxX(), actual.getMaxX(), 1);
assertEquals("maxY", expected.getMaxY(), actual.getMaxY(), 1);
}
public static final float EPSILON = 1.0e-4f;
public static void assertAlmostEquals(float a, float b) {
assertEquals(a, b, EPSILON);
}
public static void assertBoundsEqual(Bounds expected, Bounds actual) {
if (expected.isEmpty() && actual.isEmpty()) {
return;
} else {
assertEquals(expected, actual);
}
}
public static void assertGroupBounds(Group g) {
float x1 = 0;
float y1 = 0;
float x2 = -1;
float y2 = -1;
boolean first = true;
for (Node n : g.getChildren()) {
if (n.isVisible() && !n.getBoundsInLocal().isEmpty()) {
if (first) {
x1 = (float) n.getBoundsInParent().getMinX();
y1 = (float) n.getBoundsInParent().getMinY();
x2 = (float) n.getBoundsInParent().getMaxX();
y2 = (float) n.getBoundsInParent().getMaxY();
first = false;
} else {
x1 = Math.min(x1, (float) n.getBoundsInParent().getMinX());
y1 = Math.min(y1, (float) n.getBoundsInParent().getMinY());
x2 = Math.max(x2, (float) n.getBoundsInParent().getMaxX());
y2 = Math.max(y2, (float) n.getBoundsInParent().getMaxY());
}
}
}
Bounds expected = box2(x1, y1, x2, y2);
assertBoundsEqual(expected, g.getBoundsInLocal());
}
public static String formatBounds(Bounds b) {
return "(" + b.getMinX() + ", " + b.getMinY() + ", " + b.getWidth()
+ ", " + b.getHeight() + ")";
}
public static BoundingBox box(int minX, int minY, int width, int height) {
return box((float) minX, (float) minY, (float) width, (float) height);
}
public static BoundingBox box(double minX, double minY, double width, double height) {
return box((float) minX, (float) minY, (float) width, (float) height);
}
public static BoundingBox box(float minX, float minY, float width,
float height) {
return new BoundingBox(minX, minY, width, height);
}
public static BoundingBox box(float minX, float minY, float minZ, float width,
float height, float depth) {
return new BoundingBox(minX, minY, minZ, width, height, depth);
}
public static BoundingBox box2(int minX, int minY, int maxX, int maxY) {
return box2((float) minX, (float) minY, (float) maxX, (float) maxY);
}
public static BoundingBox box2(float minX, float minY, float maxX,
float maxY) {
return box(minX, minY, maxX - minX, maxY - minY);
}
@SuppressWarnings({ "rawtypes", "unchecked" })
public static void assertImmutableList(List list) {
try {
list.add(new Object());
fail("Exception expected while modifying the list.");
} catch (Exception e) {
}
}
}
