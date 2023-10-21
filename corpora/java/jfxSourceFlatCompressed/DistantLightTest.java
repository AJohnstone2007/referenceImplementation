package test.javafx.scene.effect;
import test.javafx.scene.effect.LightTestBase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import com.sun.scenario.effect.Color4f;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightShim;
public class DistantLightTest extends LightTestBase {
private Light.Distant effect;
@Before
public void setUp() {
effect = new Light.Distant();
setupTest(effect);
}
@Test
public void testSetAzimuth() {
effect.setAzimuth(1.0f);
assertEquals(1.0f, effect.getAzimuth(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getAzimuth(), 1e-100);
}
@Test
public void testDefaultAzimuth() {
assertEquals(45f, effect.getAzimuth(), 1e-100);
assertEquals(45f, effect.azimuthProperty().get(), 1e-100);
pulse();
assertEquals(45f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getAzimuth(), 1e-100);
}
@Test
public void testSetElevation() {
effect.setElevation(1.0f);
assertEquals(1.0f, effect.getElevation(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getElevation(), 1e-100);
}
@Test
public void testDefaultElevation() {
assertEquals(45f, effect.getElevation(), 1e-100);
assertEquals(45f, effect.elevationProperty().get(), 1e-100);
pulse();
assertEquals(45f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getElevation(), 1e-100);
}
@Test
public void testSetColor() {
effect.setColor(Color.BLUE);
assertEquals(Color.BLUE, effect.getColor());
pulse();
Color4f c = ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getColor();
assertEquals(0f, c.getRed(), 1e-5);
assertEquals(0f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testDefaultColor() {
assertEquals(Color.WHITE, effect.getColor());
assertEquals(Color.WHITE, effect.colorProperty().get());
pulse();
Color4f c = ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(1f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testNullColor() {
effect.setColor(null);
assertNull(effect.getColor());
assertNull(effect.colorProperty().get());
pulse();
Color4f c = ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(1f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testAzimuthSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Distant", "azimuth",
"com.sun.scenario.effect.light.DistantLight", "azimuth", 0.3);
}
@Test
public void testColorSynced() throws Exception {
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f result = (Color4f) getObjectPropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Distant", "color",
"com.sun.scenario.effect.light.DistantLight", "color",
Color.RED);
assertColor4fEquals(red, result);
}
@Test
public void testCreateWithParams() {
effect = new Light.Distant(1, 2, Color.BLUE);
setupTest(effect);
assertEquals(1, effect.getAzimuth(), 1e-100);
assertEquals(2, effect.getElevation(), 1e-100);
assertEquals(Color.BLUE, effect.getColor());
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getAzimuth(), 1e-100);
assertEquals(2.0f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getElevation(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getColor();
assertEquals(0f, c.getRed(), 1e-5);
assertEquals(0f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Light.Distant(45, 45, Color.RED);
setupTest(effect);
assertEquals(45, effect.getAzimuth(), 1e-100);
assertEquals(45, effect.getElevation(), 1e-100);
assertEquals(Color.RED, effect.getColor());
pulse();
assertEquals(45f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getAzimuth(), 1e-100);
assertEquals(45f, ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getElevation(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.DistantLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(0f, c.getGreen(), 1e-5);
assertEquals(0f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
}
