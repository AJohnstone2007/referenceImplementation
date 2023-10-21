package test.javafx.scene.bounds;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
public class EffectBoundsTest {
public @Test void testBoundsOnRectangleWithShadow() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
rect.setEffect(ds);
assertEquals(box(-2, -2, 104, 104), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectangleWithShadowRemoved() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
rect.setEffect(ds);
assertEquals(box(-2, -2, 104, 104), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
rect.setEffect(null);
assertEquals(box(0, 0, 100, 100), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectangleWithShadowChanged() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
rect.setEffect(ds);
assertEquals(box(-2, -2, 104, 104), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
ds.setRadius(4);
assertEquals(box(-3, -3, 106, 106), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectangleWithShadowAndGlowChanged() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
Glow g = new Glow();
g.setInput(ds);
rect.setEffect(g);
assertEquals(box(-2, -2, 104, 104), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
ds.setRadius(4);
assertEquals(box(-3, -3, 106, 106), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectangleWithShadowAndClip() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
rect.setEffect(ds);
rect.setClip(new Rectangle(-10, -10, 30, 30));
assertEquals(box(-2, -2, 22, 22), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectangleWithShadowAndReflection() {
Rectangle rect = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
Reflection r = new Reflection();
r.setFraction(0.5f);
ds.setInput(r);
rect.setEffect(ds);
assertEquals(box(-2, -2, 104, 154), rect.getBoundsInLocal());
assertEquals(rect.getBoundsInLocal(), rect.getBoundsInParent());
}
public @Test void testBoundsOnRectanglesWithShadowChanged() {
Rectangle rect1 = new Rectangle(100, 100);
Rectangle rect2 = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
rect1.setEffect(ds);
rect2.setEffect(ds);
assertEquals(box(-2, -2, 104, 104), rect1.getBoundsInLocal());
assertEquals(rect1.getBoundsInLocal(), rect1.getBoundsInParent());
assertEquals(box(-2, -2, 104, 104), rect2.getBoundsInLocal());
assertEquals(rect2.getBoundsInLocal(), rect2.getBoundsInParent());
ds.setRadius(4);
assertEquals(box(-3, -3, 106, 106), rect1.getBoundsInLocal());
assertEquals(rect1.getBoundsInLocal(), rect1.getBoundsInParent());
assertEquals(box(-3, -3, 106, 106), rect2.getBoundsInLocal());
assertEquals(rect2.getBoundsInLocal(), rect2.getBoundsInParent());
}
public @Test void testBoundsOnRectanglesWithShadowAndGlowChanged() {
Rectangle rect1 = new Rectangle(100, 100);
Rectangle rect2 = new Rectangle(100, 100);
DropShadow ds = new DropShadow();
ds.setRadius(2);
Glow g = new Glow();
g.setInput(ds);
rect1.setEffect(g);
rect2.setEffect(g);
assertEquals(box(-2, -2, 104, 104), rect1.getBoundsInLocal());
assertEquals(rect1.getBoundsInLocal(), rect1.getBoundsInParent());
assertEquals(box(-2, -2, 104, 104), rect2.getBoundsInLocal());
assertEquals(rect2.getBoundsInLocal(), rect2.getBoundsInParent());
ds.setRadius(4);
assertEquals(box(-3, -3, 106, 106), rect1.getBoundsInLocal());
assertEquals(rect1.getBoundsInLocal(), rect1.getBoundsInParent());
assertEquals(box(-3, -3, 106, 106), rect2.getBoundsInLocal());
assertEquals(rect2.getBoundsInLocal(), rect2.getBoundsInParent());
}
}
