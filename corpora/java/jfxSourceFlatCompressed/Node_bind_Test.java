package test.javafx.scene;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Shadow;
import javafx.scene.shape.Rectangle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
public class Node_bind_Test {
@Rule
public ExpectedException thrown = ExpectedException.none();
@Test public void testClip() {
Rectangle rectA = new Rectangle(300, 300);
Rectangle clip1 = new Rectangle(10, 10);
Rectangle clip2 = new Rectangle(100, 100);
ObjectProperty<Node> v = new SimpleObjectProperty<Node>(clip1);
rectA.clipProperty().bind(v);
assertEquals(rectA.getClip(), clip1);
v.set(clip2);
assertEquals(rectA.getClip(), clip2);
}
int countIllegalArgumentException = 0;
@Test public void testIllegalClip() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof IllegalArgumentException) {
countIllegalArgumentException++;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
Rectangle rectA = new Rectangle(300, 300);
Rectangle clip1 = new Rectangle(10, 10);
Rectangle clip2 = new Rectangle(100, 100);
clip2.setClip(rectA);
ObjectProperty<Node> v = new SimpleObjectProperty<Node>(clip1);
rectA.clipProperty().bind(v);
assertEquals(rectA.getClip(), clip1);
v.set(clip2);
assertNotSame(rectA.getClip(), clip2);
assertEquals("Cycle in effect chain detected, exception should occur once.", 1, countIllegalArgumentException);
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Test public void testBackToLegalClip() {
countIllegalArgumentException = 0;
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof IllegalArgumentException) {
countIllegalArgumentException++;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
Rectangle rectA = new Rectangle(300, 300);
Rectangle clip1 = new Rectangle(10, 10);
Rectangle clip2 = new Rectangle(100, 100);
clip2.setClip(rectA);
ObjectProperty<Node> v = new SimpleObjectProperty<Node>(clip1);
rectA.clipProperty().bind(v);
assertEquals(rectA.getClip(), clip1);
v.set(clip2);
assertEquals(rectA.getClip(), clip1);
assertEquals("Cycle in effect chain detected, exception should occur once.", 1, countIllegalArgumentException);
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Test public void testEffect() {
Shadow effect1 = new Shadow();
Blend effect2 = new Blend();
Rectangle rectA = new Rectangle(100, 100);
ObjectProperty<Effect> v = new SimpleObjectProperty<Effect>(effect1);
rectA.effectProperty().bind(v);
assertEquals(rectA.getEffect(), effect1);
v.set(effect2);
assertEquals(rectA.getEffect(), effect2);
}
}
