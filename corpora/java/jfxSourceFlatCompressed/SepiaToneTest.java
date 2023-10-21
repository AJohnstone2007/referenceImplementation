package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.SepiaTone;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class SepiaToneTest extends EffectsTestBase {
private SepiaTone effect;
@Before
public void setUp() {
effect = new SepiaTone();
setupTest(effect);
}
@Test
public void testSetLevel() {
effect.setLevel(0.5f);
assertEquals(0.5f, effect.getLevel(), 1e-100);
pulse();
assertEquals(0.5f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testDefaultLevel() {
assertEquals(1f, effect.getLevel(), 1e-100);
assertEquals(1f, effect.levelProperty().get(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testMinLevel() {
effect.setLevel(0);
effect.setLevel(-0.1f);
assertEquals(-0.1f, effect.getLevel(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testMaxLevel() {
effect.setLevel(1);
effect.setLevel(1.1f);
assertEquals(1.1f, effect.getLevel(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testLevelSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.SepiaTone", "level",
"com.sun.scenario.effect.SepiaTone", "level", 0.5);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.SepiaTone", "input",
"com.sun.scenario.effect.SepiaTone", "input",
blur, (com.sun.scenario.effect.BoxBlur) EffectHelper.getPeer(blur));
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
effect = new SepiaTone(0.1);
setupTest(effect);
assertEquals(0.1, effect.getLevel(), 1e-100);
pulse();
assertEquals(0.1f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new SepiaTone(1);
setupTest(effect);
assertEquals(1, effect.getLevel(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.SepiaTone) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
}
