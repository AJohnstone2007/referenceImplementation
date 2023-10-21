package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.MotionBlur;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class MotionBlurTest extends EffectsTestBase {
private MotionBlur effect;
@Before
public void setUp() {
effect = new MotionBlur();
setupTest(effect);
}
@Test
public void testSetAngle() {
effect.setAngle(1.5f);
assertEquals(1.5f, effect.getAngle(), 1e-100);
pulse();
assertEquals(1.5f, Math.toDegrees(((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getAngle()), 1e-5);
}
@Test
public void testDefaultAngle() {
assertEquals(0f, effect.getAngle(), 1e-100);
assertEquals(0f, effect.angleProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getAngle(), 1e-100);
}
@Test
public void testSetRadius() {
effect.setRadius(0.5f);
assertEquals(0.5f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0.5f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testDefaultRadius() {
assertEquals(10f, effect.getRadius(), 1e-100);
assertEquals(10f, effect.radiusProperty().get(), 1e-100);
pulse();
assertEquals(10f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMinRadius() {
effect.setRadius(0);
effect.setRadius(-0.1f);
assertEquals(-0.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMaxRadius() {
effect.setRadius(63);
effect.setRadius(63.1f);
assertEquals(63.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(63f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testAngleSynced() throws Exception {
float result = (Float)getDoublePropertySynced(
"javafx.scene.effect.MotionBlur", "angle",
"com.sun.scenario.effect.MotionBlur", "angle", 10);
assertEquals(10, Math.toDegrees(result), 1e-5);
}
@Test
public void testRadiusSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.MotionBlur", "radius",
"com.sun.scenario.effect.MotionBlur", "radius", 15);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.MotionBlur", "input",
"com.sun.scenario.effect.MotionBlur", "input",
blur, (com.sun.scenario.effect.BoxBlur) EffectHelper.getPeer(blur));
}
@Test
public void testCreateWithParams() {
effect = new MotionBlur(1, 2);
setupTest(effect);
assertEquals(1, effect.getAngle(), 1e-100);
assertEquals(2, effect.getRadius(), 1e-100);
pulse();
assertEquals(1.0f, Math.toDegrees(((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getAngle()), 1e-5);
assertEquals(2.0f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new MotionBlur(0, 10);
setupTest(effect);
assertEquals(0, effect.getAngle(), 1e-100);
assertEquals(10, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, Math.toDegrees(((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getAngle()), 1e-5);
assertEquals(10f, ((com.sun.scenario.effect.MotionBlur) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
}
