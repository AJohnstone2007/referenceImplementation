package test.javafx.scene.bounds;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
public class ClipBoundsTest {
public @Test void testClippingWithRectangle() {
Rectangle rect = new Rectangle(100, 100);
Rectangle clip = new Rectangle(10, 10, 100, 100);
rect.setClip(clip);
assertEquals(box(10, 10, 90, 90), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testClippingWithGroup() {
Rectangle r1 = new Rectangle(20, 20, 50, 50);
Rectangle r2 = new Rectangle(90, 20, 50, 50);
Group group = new Group(r1, r2);
Rectangle rect = new Rectangle(100, 100);
rect.setClip(group);
assertEquals(box(20, 20, 80, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testClippingWithClippedRectangle() {
Rectangle clip = new Rectangle(10, 10, 100, 100);
Rectangle clipClip = new Rectangle(20, 20, 50, 50);
clip.setClip(clipClip);
Rectangle rect = new Rectangle(40, 40);
rect.setClip(clip);
assertEquals(box(20, 20, 20, 20), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testSwappingSubClipOnClippingRectangle() {
Rectangle clip = new Rectangle(10, 10, 100, 100);
Rectangle clipClip = new Rectangle(20, 20, 50, 50);
clip.setClip(clipClip);
Rectangle rect = new Rectangle(40, 40);
rect.setClip(clip);
assertEquals(box(20, 20, 20, 20), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
Rectangle clipClip2 = new Rectangle(30, 30, 50, 50);
clip.setClip(clipClip2);
assertEquals(box(30, 30, 10, 10), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testClippingWithComplexClippedRectangle() {
Rectangle clip = new Rectangle(10, 10, 100, 100);
Polygon polyClip = new Polygon(new double[] {
30, 25, 35, 30, 30, 35, 25, 30,
});
clip.setClip(polyClip);
Rectangle rect = new Rectangle(40, 40);
rect.setClip(clip);
assertEquals(box(25, 25, 10, 10), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testChangingClipBounds() {
Rectangle clip = new Rectangle(50, 50);
Rectangle rect = new Rectangle(100, 100);
rect.setClip(clip);
assertEquals(box(0, 0, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
clip.setWidth(60);
clip.setHeight(60);
assertEquals(box(0, 0, 60, 60), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testSettingClip() {
Rectangle rect = new Rectangle(100, 100);
assertEquals(box(0, 0, 100, 100), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setClip(new Rectangle(50, 50));
assertEquals(box(0, 0, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testSwappingClip() {
Rectangle clip = new Rectangle(50, 50);
Rectangle rect = new Rectangle(100, 100);
rect.setClip(clip);
assertEquals(box(0, 0, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setClip(new Rectangle(10, 10, 50, 50));
assertEquals(box(10, 10, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testRemovingClip() {
Rectangle clip = new Rectangle(50, 50);
Rectangle rect = new Rectangle(100, 100);
rect.setClip(clip);
assertEquals(box(0, 0, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setClip(null);
assertEquals(box(0, 0, 100, 100), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testClippingAndChangingGeometry() {
Rectangle clip = new Rectangle(50, 50);
Rectangle rect = new Rectangle(100, 100);
rect.setClip(clip);
assertEquals(box(0, 0, 50, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setWidth(20);
assertEquals(box(0, 0, 20, 50), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
}
