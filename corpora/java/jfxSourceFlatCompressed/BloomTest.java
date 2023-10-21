package test.javafx.scene.effect;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.EffectShim;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import test.javafx.scene.effect.EffectsTestBase;
public class BloomTest extends EffectsTestBase {
private Bloom effect;
@Before
public void setUp() {
effect = new Bloom();
setupTest(effect);
}
@Test
public void testSetThreshold() {
effect.setThreshold(1.0f);
assertEquals(1.0f, (float) effect.getThreshold(), 1e-100);
pulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
@Test
public void testDefaultThreshold() {
assertEquals(0.3f, (float) effect.getThreshold(), 1e-100);
assertEquals(0.3f, (float) effect.thresholdProperty().get(), 1e-100);
pulse();
assertEquals(0.3f, (float) ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
@Test
public void testMinThreshold() {
effect.setThreshold(0);
effect.setThreshold(-0.1f);
assertEquals(-0.1f, (float) effect.getThreshold(), 1e-100);
pulse();
assertEquals(0.0f, (float) ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
@Test
public void testMaxThreshold() {
effect.setThreshold(1);
effect.setThreshold(1.1f);
assertEquals(1.1f, (float) effect.getThreshold(), 1e-100);
pulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
@Test
public void testThresholdSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Bloom", "threshold",
"com.sun.scenario.effect.Bloom", "threshold", 0.3);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Bloom", "input",
"com.sun.scenario.effect.Bloom", "input",
blur,
(com.sun.scenario.effect.BoxBlur) EffectShim.getPeer(blur));
}
@Test
public void testBounds() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
}
@Test
public void testBoundsWidthInput() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
BoxBlur blur = new BoxBlur();
effect.setInput(blur);
assertEquals(box(-2, -2, 104, 104), n.getBoundsInLocal());
}
@Test
public void testCreateWithParams() {
effect = new Bloom(1);
setupTest(effect);
assertEquals(1, effect.getThreshold(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Bloom(0.3);
setupTest(effect);
assertEquals(0.3, effect.getThreshold(), 1e-100);
pulse();
assertEquals(0.3f, ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(effect)).getThreshold(), 1e-100);
}
}
