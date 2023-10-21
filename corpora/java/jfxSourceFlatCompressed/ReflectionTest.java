package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Reflection;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ReflectionTest extends EffectsTestBase {
private Reflection effect;
@Before
public void setUp() {
effect = new Reflection();
setupTest(effect);
}
@Test
public void testSetTopOpacity() {
effect.setTopOpacity(1.0f);
assertEquals(1.0f, effect.getTopOpacity(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
}
@Test
public void testDefaultTopOpacity() {
assertEquals(0.5f, effect.getTopOpacity(), 1e-100);
assertEquals(0.5f, effect.topOpacityProperty().get(), 1e-100);
pulse();
assertEquals(0.5f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
}
@Test
public void testMinTopOpacity() {
effect.setTopOpacity(0);
effect.setTopOpacity(-0.1f);
assertEquals(-0.1f, effect.getTopOpacity(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
}
@Test
public void testMaxTopOpacity() {
effect.setTopOpacity(1);
effect.setTopOpacity(1.1f);
assertEquals(1.1f, effect.getTopOpacity(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
}
@Test
public void testSetBottomOpacity() {
effect.setBottomOpacity(1.0f);
assertEquals(1.0f, effect.getBottomOpacity(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
@Test
public void testDefaultBottomOpacity() {
assertEquals(0f, effect.getBottomOpacity(), 1e-100);
assertEquals(0f, effect.bottomOpacityProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
@Test
public void testMinBottomOpacity() {
effect.setBottomOpacity(0);
effect.setBottomOpacity(-0.1f);
assertEquals(-0.1f, effect.getBottomOpacity(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
@Test
public void testMaxBottomOpacity() {
effect.setBottomOpacity(1);
effect.setBottomOpacity(1.1f);
assertEquals(1.1f, effect.getBottomOpacity(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
@Test
public void testSetFraction() {
effect.setFraction(1.0f);
assertEquals(1.0f, effect.getFraction(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
}
@Test
public void testDefaultFraction() {
assertEquals(0.75f, effect.getFraction(), 1e-100);
assertEquals(0.75f, effect.fractionProperty().get(), 1e-100);
pulse();
assertEquals(0.75f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
}
@Test
public void testMinFraction() {
effect.setFraction(0);
effect.setFraction(-0.1f);
assertEquals(-0.1f, effect.getFraction(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
}
@Test
public void testMaxFraction() {
effect.setFraction(1);
effect.setFraction(1.1f);
assertEquals(1.1f, effect.getFraction(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
}
@Test
public void testSetTopOffset() {
effect.setTopOffset(1.0f);
assertEquals(1.0f, effect.getTopOffset(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOffset(), 1e-100);
}
@Test
public void testDefaultTopOffset() {
assertEquals(0f, effect.getTopOffset(), 1e-100);
assertEquals(0f, effect.topOffsetProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOffset(), 1e-100);
}
@Test
public void testBottomOpacitySynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Reflection", "bottomOpacity",
"com.sun.scenario.effect.Reflection", "bottomOpacity", 0.3);
}
@Test
public void testTopOpacitySynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Reflection", "topOpacity",
"com.sun.scenario.effect.Reflection", "topOpacity", 0.3);
}
@Test
public void testTopOffsetSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Reflection", "topOffset",
"com.sun.scenario.effect.Reflection", "topOffset", 15);
}
@Test
public void testFractionSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Reflection", "fraction",
"com.sun.scenario.effect.Reflection", "fraction", 0.5);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Reflection", "input",
"com.sun.scenario.effect.Reflection", "input",
blur, (com.sun.scenario.effect.BoxBlur) EffectHelper.getPeer(blur));
}
@Test
public void testCreateWithParams() {
effect = new Reflection(1, 0.2, 0.4, 0.5);
setupTest(effect);
assertEquals(1, effect.getTopOffset(), 1e-100);
assertEquals(0.2, effect.getFraction(), 1e-100);
assertEquals(0.4, effect.getTopOpacity(), 1e-100);
assertEquals(0.5, effect.getBottomOpacity(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOffset(), 1e-100);
assertEquals(0.2f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
assertEquals(0.4f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
assertEquals(0.5f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Reflection(0, 0.75, 0.5, 1);
setupTest(effect);
assertEquals(0, effect.getTopOffset(), 1e-100);
assertEquals(0.75, effect.getFraction(), 1e-100);
assertEquals(0.5, effect.getTopOpacity(), 1e-100);
assertEquals(1, effect.getBottomOpacity(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOffset(), 1e-100);
assertEquals(0.75f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getFraction(), 1e-100);
assertEquals(0.5f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getTopOpacity(), 1e-100);
assertEquals(1f, ((com.sun.scenario.effect.Reflection) EffectHelper.getPeer(effect)).getBottomOpacity(), 1e-100);
}
}
