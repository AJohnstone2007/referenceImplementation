package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class GlowTest extends EffectsTestBase {
private Glow effect;
@Before
public void setUp() {
effect = new Glow();
setupTest(effect);
}
@Test
public void testSetLevel() {
effect.setLevel(1);
assertEquals(1.0f, (float) effect.getLevel(), 1e-100);
pulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testDefaultLevel() {
assertEquals(0.3f, (float) effect.getLevel(), 1e-100);
assertEquals(0.3f, (float) effect.levelProperty().get(), 1e-100);
pulse();
assertEquals(0.3f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testMinLevel() {
effect.setLevel(0);
effect.setLevel(-0.1f);
assertEquals(-0.1f, (float) effect.getLevel(), 1e-100);
pulse();
assertEquals(0.0f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testMaxLevel() {
effect.setLevel(1);
effect.setLevel(1.1f);
assertEquals(1.1f, (float) effect.getLevel(), 1e-100);
pulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testCreateWithParams() {
effect = new Glow(1);
setupTest(effect);
assertEquals(1, effect.getLevel(), 1e-100);
pulse();
assertEquals(1, ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(effect)).getLevel(), 1e-100);
}
@Test
public void testLevelSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Glow", "level",
"com.sun.scenario.effect.Glow", "level", 0.5);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Glow", "input",
"com.sun.scenario.effect.Glow", "input",
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
}
