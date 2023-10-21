package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.EffectShim;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.javafx.scene.effect.EffectsTestBase;
public class BoxBlurTest extends EffectsTestBase {
private BoxBlur effect;
@Before
public void setUp() {
effect = new BoxBlur();
setupTest(effect);
}
@After
public void tearDown() {
}
@Test
public void testSetWidth() {
effect.setWidth(1.0f);
assertEquals(1.0f, effect.getWidth(), 1e-100);
pulse();
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
}
@Test
public void testDefaultWidth() {
assertEquals(5.0f, effect.getWidth(), 1e-100);
assertEquals(5.0f, effect.widthProperty().get(), 1e-100);
pulse();
assertEquals(5, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
}
@Test
public void testMinWidth() {
effect.setWidth(0);
effect.setWidth(-0.1f);
assertEquals(-0.1f, effect.getWidth(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
}
@Test
public void testMaxWidth() {
effect.setWidth(255);
effect.setWidth(255.1f);
assertEquals(255.1f, effect.getWidth(), 1e-100);
pulse();
assertEquals(255, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
}
@Test
public void testSetHeight() {
effect.setHeight(1.0f);
assertEquals(1.0f, effect.getHeight(), 1e-100);
pulse();
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
}
@Test
public void testDefaultHeight() {
assertEquals(5.0f, effect.getHeight(), 1e-100);
assertEquals(5.0f, effect.heightProperty().get(), 1e-100);
pulse();
assertEquals(5, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
}
@Test
public void testMinHeight() {
effect.setHeight(0);
effect.setHeight(-0.1f);
assertEquals(-0.1f, effect.getHeight(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
}
@Test
public void testMaxHeight() {
effect.setHeight(255);
effect.setHeight(255.1f);
assertEquals(255.1f, effect.getHeight(), 1e-100);
pulse();
assertEquals(255, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
}
@Test
public void testSetIterations() {
effect.setIterations(2);
assertEquals(2, effect.getIterations());
pulse();
assertEquals(2, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testDefaultIterations() {
assertEquals(1, effect.getIterations());
assertEquals(1, effect.iterationsProperty().get());
pulse();
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testMinIterations() {
effect.setIterations(0);
effect.setIterations(-1);
assertEquals(-1, effect.getIterations());
pulse();
assertEquals(0, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testMaxIterations() {
effect.setIterations(3);
effect.setIterations(4);
assertEquals(4, effect.getIterations());
pulse();
assertEquals(3, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testCreateWithParams() {
effect = new BoxBlur(1, 1, 3);
setupTest(effect);
assertEquals(1, effect.getWidth(), 1e-100);
assertEquals(1, effect.getHeight(), 1e-100);
assertEquals(3, effect.getIterations());
pulse();
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
assertEquals(3, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testCreateWithDefaultParams() {
effect = new BoxBlur(5, 5, 1);
setupTest(effect);
assertEquals(5, effect.getWidth(), 1e-100);
assertEquals(5, effect.getHeight(), 1e-100);
assertEquals(1, effect.getIterations());
pulse();
assertEquals(5, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getHorizontalSize());
assertEquals(5, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getVerticalSize());
assertEquals(1, ((com.sun.scenario.effect.BoxBlur)
EffectShim.getPeer(effect)).getPasses());
}
@Test
public void testHeightSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.BoxBlur", "height",
"com.sun.scenario.effect.BoxBlur", "verticalSize", 10);
}
@Test
public void testWidthSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.BoxBlur", "width",
"com.sun.scenario.effect.BoxBlur", "horizontalSize", 10);
}
@Test
public void testIterationsSynced() throws Exception {
checkIntPropertySynced(
"javafx.scene.effect.BoxBlur", "iterations",
"com.sun.scenario.effect.BoxBlur", "passes", 2);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.BoxBlur", "input",
"com.sun.scenario.effect.BoxBlur", "input",
blur, (com.sun.scenario.effect.BoxBlur) EffectHelper.getPeer(blur));
}
}
