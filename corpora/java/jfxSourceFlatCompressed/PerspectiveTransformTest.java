package test.javafx.scene.effect;
import javafx.scene.effect.PerspectiveTransform;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class PerspectiveTransformTest extends EffectsTestBase {
private PerspectiveTransform effect;
@Before
public void setUp() {
effect = new PerspectiveTransform();
setupTest(effect);
}
@Test
public void testLlx() {
effect.setLlx(1.0f);
assertEquals(1.0f, effect.getLlx(), 1e-100);
}
@Test
public void testLly() {
effect.setLly(1.0f);
assertEquals(1.0f, effect.getLly(), 1e-100);
}
@Test
public void testLrx() {
effect.setLrx(1.0f);
assertEquals(1.0f, effect.getLrx(), 1e-100);
}
@Test
public void testLry() {
effect.setLry(1.0f);
assertEquals(1.0f, effect.getLry(), 1e-100);
}
@Test
public void testULx() {
effect.setUlx(1.0f);
assertEquals(1.0f, effect.getUlx(), 1e-100);
}
@Test
public void testUly() {
effect.setUly(1.0f);
assertEquals(1.0f, effect.getUly(), 1e-100);
}
@Test
public void testUrx() {
effect.setUrx(1.0f);
assertEquals(1.0f, effect.getUrx(), 1e-100);
}
@Test
public void testUry() {
effect.setUry(1.0f);
assertEquals(1.0f, effect.getUry(), 1e-100);
}
@Test
public void testCreateWithParams() {
effect = new PerspectiveTransform(1, 2, 3, 4, 5, 6, 7, 8);
setupTest(effect);
assertEquals(1, effect.getUlx(), 1e-100);
assertEquals(2, effect.getUly(), 1e-100);
assertEquals(3, effect.getUrx(), 1e-100);
assertEquals(4, effect.getUry(), 1e-100);
assertEquals(5, effect.getLrx(), 1e-100);
assertEquals(6, effect.getLry(), 1e-100);
assertEquals(7, effect.getLlx(), 1e-100);
assertEquals(8, effect.getLly(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new PerspectiveTransform(0, 0, 0, 0, 0, 0, 0, 0);
setupTest(effect);
assertEquals(0, effect.getUlx(), 1e-100);
assertEquals(0, effect.getUly(), 1e-100);
assertEquals(0, effect.getUrx(), 1e-100);
assertEquals(0, effect.getUry(), 1e-100);
assertEquals(0, effect.getLrx(), 1e-100);
assertEquals(0, effect.getLry(), 1e-100);
assertEquals(0, effect.getLlx(), 1e-100);
assertEquals(0, effect.getLly(), 1e-100);
}
}
