package test.javafx.scene;
import static org.junit.Assert.fail;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import org.junit.Test;
public class HashCodeTest {
private static final boolean VERBOSE = false;
void checkEqualsAndHashCode(Object o1, Object o2, boolean isEqual, boolean isNaN) {
int o1Hash = o1.hashCode();
int o2Hash = o2.hashCode();
boolean o1EqualsO2 = o1.equals(o2);
StringBuffer errMsg = new StringBuffer();
if (o1EqualsO2 == isEqual) {
if (o1EqualsO2 && (o1Hash != o2Hash)) {
errMsg.append("ERROR: Equal objects have different hashCode");
errMsg.append("\n    o1 = ").append(o1);
errMsg.append("\n    o2 = ").append(o2);
errMsg.append("\n    o1.hashCode() = ").append(Integer.toHexString(o1Hash)).append(", o2.hashCode() = ").append(Integer.toHexString(o2Hash));
fail(errMsg.toString());
}
else if ((!o1EqualsO2) && (o1Hash == o2Hash)) {
if (isNaN) {
if (VERBOSE) {
System.out.println("Non-equal objects with NaN have same hashCode (as expected)");
System.out.println("o1 = " + o1);
System.out.println("o2 = " + o2);
System.out.println("o1.hashCode() = " + Integer.toHexString(o1Hash) +
", o2.hashCode() = " +
Integer.toHexString(o2Hash));
System.out.println("");
}
}
else {
errMsg.append("Warning: Non-equal objects have same hashCode");
errMsg.append("\n    o1 = ").append(o1);
errMsg.append("\n    o2 = ").append(o2);
errMsg.append("\n    o1.hashCode() = ").append(Integer.toHexString(o1Hash)).append(", o2.hashCode() = ").append(Integer.toHexString(o2Hash));
fail(errMsg.toString());
}
}
else if (VERBOSE) {
System.out.println("SUCCESS:");
System.out.println("o1 = " + o1);
System.out.println("o2 = " + o2);
System.out.println("o1.equals(o2) = " + o1EqualsO2 + ", expected: " + isEqual);
System.out.println("o1.hashCode() = " + Integer.toHexString(o1Hash) +
", o2.hashCode() = " + Integer.toHexString(o2Hash));
System.out.println("");
}
}
else {
errMsg.append("ERROR: o1.equals(o2) incorrect");
errMsg.append("\n    o1 = ").append(o1);
errMsg.append("\n    o2 = ").append(o2);
errMsg.append("\n    o1.equals(o2) = ").append(o1EqualsO2).append(", expected: ").append(isEqual);
errMsg.append("\n    o1.hashCode() = ").append(Integer.toHexString(o1Hash)).append(", o2.hashCode() = ").append(Integer.toHexString(o2Hash));
fail(errMsg.toString());
}
}
@Test
public void testRectangleHash() {
Rectangle2D r1 = new Rectangle2D(0,0,0,0);
Rectangle2D r2 = new Rectangle2D(0,0,0,0);
checkEqualsAndHashCode(r1, r2, true, false);
r1 = new Rectangle2D(1,2,3,4);
r2 = new Rectangle2D(1,2,3,4);
checkEqualsAndHashCode(r1, r2, true, false);
r1 = new Rectangle2D(0,1,0,0);
r2 = new Rectangle2D(1,0,0,0);
checkEqualsAndHashCode(r1, r2, false, false);
r1 = new Rectangle2D(0.0f,1,0,0);
r2 = new Rectangle2D(0.0f,1,0,0);
checkEqualsAndHashCode(r1, r2, true, false);
r1 = new Rectangle2D(1.0f,Float.POSITIVE_INFINITY, 0.0f, 0.0f);
r2 = new Rectangle2D(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(r1, r2, false, false);
r1 = new Rectangle2D(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
r2 = new Rectangle2D(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(r1, r2, true, false);
r1 = new Rectangle2D(1.0f, Float.POSITIVE_INFINITY, 0.0f, 0.0f);
r2 = new Rectangle2D(1.0f, Float.POSITIVE_INFINITY, 0.0f, 0.0f);
checkEqualsAndHashCode(r1, r2, true, false);
r1 = new Rectangle2D(Float.NaN, 1.0f, 0.0f, 0.0f);
r2 = new Rectangle2D(Float.NaN, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(r1, r2, false, true);
r1 = new Rectangle2D(1.0f, Float.NaN, 0.0f, 0.0f);
r2 = new Rectangle2D(1.0f, Float.NaN, 0.0f, 0.0f);
checkEqualsAndHashCode(r1, r2, false, true);
}
@Test
public void testPointHash() {
Point2D p1 = new Point2D(0,0);
Point2D p2 = new Point2D(0,0);
checkEqualsAndHashCode(p1, p2, true, false);
p1 = new Point2D(1, 2);
p2 = new Point2D(1, 2);
checkEqualsAndHashCode(p1, p2, true, false);
p1 = new Point2D(0, 1);
p2 = new Point2D(1, 0);
checkEqualsAndHashCode(p1, p2, false, false);
p1 = new Point2D(1, 0.0f);
p2 = new Point2D(1, 0.0f);
checkEqualsAndHashCode(p1, p2, true, false);
}
@Test
public void testBoundingBoxHash() {
Bounds b1 = new BoundingBox(0,0,0,0);
Bounds b2 = new BoundingBox(0,0,0,0);
checkEqualsAndHashCode(b1, b2, true, false);
b1 = new BoundingBox(1,2,3,4);
b2 = new BoundingBox(1,2,3,4);
checkEqualsAndHashCode(b1, b2, true, false);
b1 = new BoundingBox(0,1,0,0);
b2 = new BoundingBox(1,0,0,0);
checkEqualsAndHashCode(b1, b2, false, false);
b1 = new BoundingBox(0.0f,1,0,0);
b2 = new BoundingBox(0.0f,1,0,0);
checkEqualsAndHashCode(b1, b2, true, false);
b1 = new BoundingBox(1.0f, Float.POSITIVE_INFINITY,0.0f,0.0f);
b2 = new BoundingBox(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(b1, b2, false, false);
b1 = new BoundingBox(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
b2 = new BoundingBox(Float.POSITIVE_INFINITY, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(b1, b2, true, false);
b1 = new BoundingBox(1.0f, Float.POSITIVE_INFINITY, 0.0f, 0.0f);
b2 = new BoundingBox(1.0f, Float.POSITIVE_INFINITY, 0.0f, 0.0f);
checkEqualsAndHashCode(b1, b2, true, false);
b1 = new BoundingBox(Float.NaN, 1.0f, 0.0f, 0.0f);
b2 = new BoundingBox(Float.NaN, 1.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(b1, b2, false, true);
b1 = new BoundingBox(1.0f, Float.NaN, 0.0f, 0.0f);
b2 = new BoundingBox(1.0f, Float.NaN, 0.0f, 0.0f);
checkEqualsAndHashCode(b1, b2, false, true);
}
@Test
public void testColorHash() {
Color c1 = new Color(0,0,0,1);
Color c2 = new Color(0,0,0,1);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.RED;
c2 = new Color(1.0f,0.0f,0.0f,1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.LIME;
c2 = new Color(0.0f,1.0f,0.0f,1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.BLUE;
c2 = new Color(0.0f,0.0f,1.0f,1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.WHITE;
c2 = new Color(1.0f,1.0f,1.0f,1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.RED;
c2 = Color.color(1.0f, 0.0f, 0.0f, 1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.LIME;
c2 = Color.color(0.0f, 1.0f, 0.0f, 1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.BLUE;
c2 = Color.color(0.0f, 0.0f, 1.0f, 1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.WHITE;
c2 = Color.color(1.0f, 1.0f, 1.0f, 1.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.color(0.5f, 0.0f, 0.0f, 0.0f);
c2 = Color.color(0.5f, 0.0f, 0.0f, 0.0f);
checkEqualsAndHashCode(c1, c2, true, false);
c1 = Color.color(0.5f, 0.0f, 0.0f, 0.0f);
c2 = Color.color(0.0f, 0.5f, 0.0f, 0.0f);
checkEqualsAndHashCode(c1, c2, false, false);
c1 = Color.color(0.5f, 0.0f, 0.0f, 0.0f);
c2 = Color.color(0.0f, 0.0f, 0.5f, 0.0f);
checkEqualsAndHashCode(c1, c2, false, false);
c1 = Color.color(0.5f, 0.0f, 0.0f, 0.0f);
c2 = Color.color(0.0f, 0.0f, 0.0f, 0.5f);
checkEqualsAndHashCode(c1, c2, false, false);
c1 = Color.color(0.0f, 0.5f, 0.0f, 0.0f);
c2 = Color.color(0.0f, 0.0f, 0.5f, 0.0f);
checkEqualsAndHashCode(c1, c2, false, false);
c1 = Color.color(0.0f, 0.5f, 0.0f, 0.0f);
c2 = Color.color(0.0f, 0.0f, 0.0f, 0.5f);
checkEqualsAndHashCode(c1, c2, false, false);
c1 = Color.color(0.0f, 0.0f, 0.5f, 0.0f);
c2 = Color.color(0.0f, 0.0f, 0.0f, 0.5f);
checkEqualsAndHashCode(c1, c2, false, false);
}
}
