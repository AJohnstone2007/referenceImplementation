package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ColorAdjustTest extends EffectsTestBase {
private ColorAdjust effect;
@Before
public void setUp() {
effect = new ColorAdjust();
setupTest(effect);
}
@Test
public void testSetBrightness() {
effect.setBrightness(1.0f);
assertEquals(1.0f, effect.getBrightness(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
}
@Test
public void testDefaultBrightness() {
assertEquals(0f, effect.getBrightness(), 1e-100);
assertEquals(0f, effect.brightnessProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
}
@Test
public void testMinBrightness() {
effect.setBrightness(-1);
effect.setBrightness(-1.1f);
assertEquals(-1.1f, effect.getBrightness(), 1e-100);
pulse();
assertEquals(-1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
}
@Test
public void testMaxBrightness() {
effect.setBrightness(1);
effect.setBrightness(1.1f);
assertEquals(1.1f, effect.getBrightness(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
}
@Test
public void testSetContrast() {
effect.setContrast(0.5);
assertEquals(0.5, effect.getContrast(), 1e-100);
pulse();
assertEquals(0.5, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
@Test
public void testDefaultContrast() {
assertEquals(0.0, effect.getContrast(), 1e-100);
assertEquals(0.0, effect.contrastProperty().get(), 1e-100);
pulse();
assertEquals(0.0, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
@Test
public void testMinContrast() {
effect.setContrast(-1.0);
effect.setContrast(-1.1);
assertEquals(-1.1, effect.getContrast(), 1e-100);
pulse();
assertEquals(-1.0, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
@Test
public void testMaxContrast() {
effect.setContrast(1.0);
effect.setContrast(1.1);
assertEquals(1.1, effect.getContrast(), 1e-100);
pulse();
assertEquals(1.0, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
@Test
public void testSetHue() {
effect.setHue(1.0f);
assertEquals(1.0f, effect.getHue(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
}
@Test
public void testDefaultHue() {
assertEquals(0f, effect.getHue(), 1e-100);
assertEquals(0f, effect.hueProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
}
@Test
public void testMinHue() {
effect.setHue(-1);
effect.setHue(-1.1f);
assertEquals(-1.1f, effect.getHue(), 1e-100);
pulse();
assertEquals(-1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
}
@Test
public void testMaxHue() {
effect.setHue(1);
effect.setHue(1.1f);
assertEquals(1.1f, effect.getHue(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
}
@Test
public void testSetSaturation() {
effect.setSaturation(1.0f);
assertEquals(1.0f, effect.getSaturation(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
}
@Test
public void testDefaultSaturation() {
assertEquals(0f, effect.getSaturation(), 1e-100);
assertEquals(0f, effect.saturationProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
}
@Test
public void testMinSaturation() {
effect.setSaturation(-1);
effect.setSaturation(-1.1f);
assertEquals(-1.1f, effect.getSaturation(), 1e-100);
pulse();
assertEquals(-1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
}
@Test
public void testMaxSaturation() {
effect.setSaturation(1);
effect.setSaturation(1.1f);
assertEquals(1.1f, effect.getSaturation(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
}
@Test
public void testBrightnessSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.ColorAdjust", "brightness",
"com.sun.scenario.effect.ColorAdjust", "brightness", 0.3);
}
@Test
public void testContrastSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.ColorAdjust", "contrast",
"com.sun.scenario.effect.ColorAdjust", "contrast", 0.3);
}
@Test
public void testHueSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.ColorAdjust", "hue",
"com.sun.scenario.effect.ColorAdjust", "hue", 0.3);
}
@Test
public void testSaturationSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.ColorAdjust", "saturation",
"com.sun.scenario.effect.ColorAdjust", "saturation", 0.3);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.ColorAdjust", "input",
"com.sun.scenario.effect.ColorAdjust", "input",
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
effect = new ColorAdjust(-.5, 0.5, -0.1, 0.1);
setupTest(effect);
assertEquals(-0.5, effect.getHue(), 1e-100);
assertEquals(0.5, effect.getSaturation(), 1e-100);
assertEquals(-0.1, effect.getBrightness(), 1e-100);
assertEquals(0.1, effect.getContrast(), 1e-100);
pulse();
assertEquals(-0.5f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
assertEquals(0.5f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
assertEquals(-0.1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
assertEquals(0.1f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new ColorAdjust(0, 0, 0, 0);
setupTest(effect);
assertEquals(0, effect.getHue(), 1e-100);
assertEquals(0, effect.getSaturation(), 1e-100);
assertEquals(0, effect.getBrightness(), 1e-100);
assertEquals(0, effect.getContrast(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getHue(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getSaturation(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getBrightness(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.ColorAdjust) EffectHelper.getPeer(effect)).getContrast(), 1e-100);
}
}
