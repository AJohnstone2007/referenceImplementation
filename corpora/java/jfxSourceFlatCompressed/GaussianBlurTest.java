package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class GaussianBlurTest extends EffectsTestBase {
private GaussianBlur effect;
@Before
public void setUp() {
effect = new GaussianBlur();
setupTest(effect);
}
@Test
public void testSetRadius() {
effect.setRadius(1.0f);
assertEquals(1.0f, effect.getRadius(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testDefaultRadius() {
assertEquals(10, effect.getRadius(), 1e-10);
assertEquals(10, effect.radiusProperty().get(), 1e-10);
pulse();
assertEquals(10, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMinRadius() {
effect.setRadius(0);
effect.setRadius(-0.1f);
assertEquals(-0.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMaxRadius() {
effect.setRadius(63);
effect.setRadius(63.1f);
assertEquals(63.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(63f, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testRadiusSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.GaussianBlur", "radius",
"com.sun.scenario.effect.GaussianBlur", "radius", 15);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.GaussianBlur", "input",
"com.sun.scenario.effect.GaussianBlur", "input",
blur, (com.sun.scenario.effect.BoxBlur) EffectHelper.getPeer(blur));
}
@Test
public void testCreateWithParams() {
effect = new GaussianBlur(4);
setupTest(effect);
assertEquals(4, effect.getRadius(), 1e-100);
pulse();
assertEquals(4f, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new GaussianBlur(10);
setupTest(effect);
assertEquals(10, effect.getRadius(), 1e-100);
pulse();
assertEquals(10f, ((com.sun.scenario.effect.GaussianBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
}
