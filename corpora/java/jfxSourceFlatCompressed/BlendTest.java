package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;
import com.sun.scenario.effect.Blend.Mode;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.effect.EffectShim;
import test.javafx.scene.effect.EffectsTestBase;
public class BlendTest extends EffectsTestBase {
private Blend effect;
@Before
public void setUp() {
effect = new Blend();
setupTest(effect);
}
@Test
public void testSetOpacity() {
effect.setOpacity(0.5f);
assertEquals(0.5f, effect.getOpacity(), 1e-100);
pulse();
assertEquals(0.5f, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getOpacity(), 1e-100);
}
@Test
public void testDefaultOpacity() {
assertEquals(1f, effect.getOpacity(), 1e-100);
assertEquals(1f, effect.opacityProperty().get(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getOpacity(), 1e-100);
}
@Test
public void testMinOpacity() {
effect.setOpacity(0);
effect.setOpacity(-0.1f);
assertEquals(-0.1f, effect.getOpacity(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getOpacity(), 1e-100);
}
@Test
public void testMaxOpacity() {
effect.setOpacity(1);
effect.setOpacity(1.1f);
assertEquals(1.1f, effect.getOpacity(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getOpacity(), 1e-100);
}
@Test
public void testSetMode() {
effect.setMode(BlendMode.ADD);
assertEquals(BlendMode.ADD, effect.getMode());
pulse();
assertEquals(Mode.ADD, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.BLUE);
assertEquals(BlendMode.BLUE, effect.getMode());
pulse();
assertEquals(Mode.BLUE, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.COLOR_BURN);
assertEquals(BlendMode.COLOR_BURN, effect.getMode());
pulse();
assertEquals(Mode.COLOR_BURN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.COLOR_DODGE);
assertEquals(BlendMode.COLOR_DODGE, effect.getMode());
pulse();
assertEquals(Mode.COLOR_DODGE, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.DARKEN);
assertEquals(BlendMode.DARKEN, effect.getMode());
pulse();
assertEquals(Mode.DARKEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.DIFFERENCE);
assertEquals(BlendMode.DIFFERENCE, effect.getMode());
pulse();
assertEquals(Mode.DIFFERENCE, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.EXCLUSION);
assertEquals(BlendMode.EXCLUSION, effect.getMode());
pulse();
assertEquals(Mode.EXCLUSION, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.GREEN);
assertEquals(BlendMode.GREEN, effect.getMode());
pulse();
assertEquals(Mode.GREEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.HARD_LIGHT);
assertEquals(BlendMode.HARD_LIGHT, effect.getMode());
pulse();
assertEquals(Mode.HARD_LIGHT, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.LIGHTEN);
assertEquals(BlendMode.LIGHTEN, effect.getMode());
pulse();
assertEquals(Mode.LIGHTEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.MULTIPLY);
assertEquals(BlendMode.MULTIPLY, effect.getMode());
pulse();
assertEquals(Mode.MULTIPLY, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.OVERLAY);
assertEquals(BlendMode.OVERLAY, effect.getMode());
pulse();
assertEquals(Mode.OVERLAY, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.RED);
assertEquals(BlendMode.RED, effect.getMode());
pulse();
assertEquals(Mode.RED, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.ADD);
assertEquals(BlendMode.ADD, effect.getMode());
pulse();
assertEquals(Mode.ADD, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.SCREEN);
assertEquals(BlendMode.SCREEN, effect.getMode());
pulse();
assertEquals(Mode.SCREEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.SOFT_LIGHT);
assertEquals(BlendMode.SOFT_LIGHT, effect.getMode());
pulse();
assertEquals(Mode.SOFT_LIGHT, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.SRC_ATOP);
assertEquals(BlendMode.SRC_ATOP, effect.getMode());
pulse();
assertEquals(Mode.SRC_ATOP, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(BlendMode.SRC_OVER);
assertEquals(BlendMode.SRC_OVER, effect.getMode());
pulse();
assertEquals(Mode.SRC_OVER, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
effect.setMode(null);
assertNull(effect.getMode());
pulse();
assertEquals(Mode.SRC_OVER, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
}
@Test
public void testDefaultMode() {
assertEquals(BlendMode.SRC_OVER, effect.getMode());
assertEquals(BlendMode.SRC_OVER, effect.modeProperty().get());
pulse();
assertEquals(Mode.SRC_OVER, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
}
@Test
public void testBottomInput() {
assertNull(effect.getBottomInput());
Effect blur = new BoxBlur();
effect.setBottomInput(blur);
assertEquals(blur, effect.getBottomInput());
effect.setBottomInput(null);
assertNull(effect.getBottomInput());
}
@Test
public void testTopInput() {
assertNull(effect.getTopInput());
Effect blur = new BoxBlur();
effect.setTopInput(blur);
assertEquals(blur, effect.getTopInput());
effect.setTopInput(null);
assertNull(effect.getTopInput());
}
@Test
public void testOpacitySynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Blend", "opacity",
"com.sun.scenario.effect.Blend", "opacity",
0.3);
}
@Test
public void testModeSynced() throws Exception {
checkObjectPropertySynced(
"javafx.scene.effect.Blend", "mode",
"com.sun.scenario.effect.Blend", "mode",
BlendMode.OVERLAY, Mode.OVERLAY,
BlendMode.ADD);
}
@Test
public void testTopInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Blend", "topInput",
"com.sun.scenario.effect.Blend", "topInput",
blur, (com.sun.scenario.effect.BoxBlur) EffectShim.getPeer(blur));
}
@Test
public void testBottomInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Blend","bottomInput",
"com.sun.scenario.effect.Blend", "bottomInput",
blur, (com.sun.scenario.effect.BoxBlur) EffectShim.getPeer(blur));
}
@Test
public void testCycles() {
try {
effect.setTopInput(effect);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {
assertEquals(null, effect.getTopInput());
}
try {
effect.setBottomInput(effect);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {
assertEquals(null, effect.getBottomInput());
}
BoxBlur blur = new BoxBlur();
effect.setBottomInput(blur);
effect.setTopInput(blur);
try {
blur.setInput(effect);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {
assertEquals(null, blur.getInput());
}
effect.setBottomInput(null);
effect.setTopInput(null);
blur.setInput(effect);
try {
effect.setBottomInput(blur);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {}
try {
effect.setTopInput(blur);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {}
assertEquals(null, effect.getTopInput());
assertEquals(null, effect.getBottomInput());
ColorInput f = new ColorInput();
effect.setBottomInput(f);
effect.setTopInput(f);
assertEquals(f, effect.getBottomInput());
assertEquals(f, effect.getTopInput());
}
int countIllegalArgumentException = 0;
@Test
public void testCyclesForBoundInput() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof IllegalArgumentException) {
countIllegalArgumentException++;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
ObjectProperty vTop = new SimpleObjectProperty();
effect.topInputProperty().bind(vTop);
vTop.set(effect);
assertEquals(null, effect.getTopInput());
vTop.set(null);
effect.topInputProperty().bind(vTop);
ObjectProperty vBottom = new SimpleObjectProperty();
effect.bottomInputProperty().bind(vBottom);
vBottom.set(effect);
assertEquals(null, effect.getBottomInput());
vBottom.set(null);
effect.bottomInputProperty().bind(vBottom);
BoxBlur blur = new BoxBlur();
ObjectProperty vBlur = new SimpleObjectProperty();
blur.inputProperty().bind(vBlur);
vBottom.set(blur);
vBlur.set(effect);
assertEquals(null, blur.getInput());
vBlur.set(null);
blur.inputProperty().bind(vBlur);
vTop.set(null);
vBottom.set(null);
vBlur.set(effect);
vBottom.set(blur);
assertEquals(null, effect.getBottomInput());
vBottom.set(null);
effect.bottomInputProperty().bind(vBottom);
vTop.set(blur);
assertEquals(null, effect.getTopInput());
vTop.set(null);
effect.topInputProperty().bind(vTop);
assertEquals(null, effect.getTopInput());
assertEquals(null, effect.getBottomInput());
ColorInput f = new ColorInput();
vBottom.set(f);
vTop.set(f);
assertEquals(f, effect.getBottomInput());
assertEquals(f, effect.getTopInput());
assertEquals("Cycle in effect chain detected, exception should occur 5 times.", 5, countIllegalArgumentException);
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Test
public void testBounds() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
}
@Test
public void testBoundsWidthTopInput() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
BoxBlur blur = new BoxBlur();
effect.setTopInput(blur);
assertEquals(box(-2, -2, 104, 104), n.getBoundsInLocal());
}
@Test
public void testBoundsWidthBottomInput() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
BoxBlur blur = new BoxBlur();
effect.setBottomInput(blur);
assertEquals(box(-2, -2, 104, 104), n.getBoundsInLocal());
}
@Test
public void testBoundsWidthTopAndBottomInput() {
assertEquals(box(0, 0, 100, 100), n.getBoundsInLocal());
BoxBlur blur = new BoxBlur();
blur.setIterations(2);
effect.setTopInput(blur);
BoxBlur blur2 = new BoxBlur();
blur2.setHeight(100);
effect.setBottomInput(blur2);
assertEquals(box(-4, -50, 108, 200), n.getBoundsInLocal());
}
@Test
public void testCreateWithParams() {
effect = new Blend(BlendMode.GREEN);
setupTest(effect);
assertEquals(BlendMode.GREEN, effect.getMode());
pulse();
assertEquals(Mode.GREEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
}
@Test
public void testCreateWithParams3() {
Effect blur = new BoxBlur();
Effect bloom = new Bloom(1);
effect = new Blend(BlendMode.GREEN, bloom, blur);
setupTest(effect);
assertEquals(BlendMode.GREEN, effect.getMode());
assertEquals(bloom, effect.getBottomInput());
assertEquals(blur, effect.getTopInput());
pulse();
assertEquals(Mode.GREEN, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
assertEquals(1.0f, ((com.sun.scenario.effect.Bloom)
EffectShim.getPeer(bloom)).getThreshold(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Blend(BlendMode.SRC_OVER);
setupTest(effect);
assertEquals(BlendMode.SRC_OVER, effect.getMode());
pulse();
assertEquals(Mode.SRC_OVER, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
}
@Test
public void testCreateWithDefaultParams3() {
effect = new Blend(BlendMode.SRC_OVER, null, null);
setupTest(effect);
assertEquals(BlendMode.SRC_OVER, effect.getMode());
assertNull(effect.getBottomInput());
assertNull(effect.getTopInput());
pulse();
assertEquals(Mode.SRC_OVER, ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getMode());
assertNull( ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getBottomInput());
assertNull( ((com.sun.scenario.effect.Blend)
EffectShim.getPeer(effect)).getTopInput());
}
}
