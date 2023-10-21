package test.javafx.scene.effect;
import test.javafx.scene.effect.LightTestBase;
import static org.junit.Assert.assertEquals;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.sun.scenario.effect.Color4f;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightShim;
public class SpotLightTest extends LightTestBase {
@Rule
public ExpectedException thrown = ExpectedException.none();
private Light.Spot effect;
@Before
public void setUp() {
effect = new Light.Spot();
setupTest(effect);
}
@Test
public void testSetPointsAtX() {
effect.setPointsAtX(1.0f);
assertEquals(1.0f, effect.getPointsAtX(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtX(), 1e-100);
}
@Test
public void testDefaultPointsAtX() {
assertEquals(0f, effect.getPointsAtX(), 1e-100);
assertEquals(0f, effect.pointsAtXProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtX(), 1e-100);
}
@Test
public void testSetPointsAtY() {
effect.setPointsAtY(1.0f);
assertEquals(1.0f, effect.getPointsAtY(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtY(), 1e-100);
}
@Test
public void testDefaultPointsAtY() {
assertEquals(0f, effect.getPointsAtY(), 1e-100);
assertEquals(0f, effect.pointsAtYProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtY(), 1e-100);
}
@Test
public void testSetPointsAtZ() {
effect.setPointsAtZ(1.0f);
assertEquals(1.0f, effect.getPointsAtZ(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtZ(), 1e-100);
}
@Test
public void testDefaultPointsAtZ() {
assertEquals(0f, effect.getPointsAtZ(), 1e-100);
assertEquals(0f, effect.pointsAtZProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getPointsAtZ(), 1e-100);
}
@Test
public void testSetSpecularExponent() {
effect.setSpecularExponent(1.1f);
assertEquals(1.1f, effect.getSpecularExponent(), 1e-100);
pulse();
assertEquals(1.1f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
}
@Test
public void testDefaultSpecularExponent() {
assertEquals(1f, effect.getSpecularExponent(), 1e-100);
assertEquals(1f, effect.specularExponentProperty().get(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
}
@Test
public void testMinSpecularExponent() {
effect.setSpecularExponent(0);
effect.setSpecularExponent(-0.1f);
assertEquals(-0.1f, effect.getSpecularExponent(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
}
@Test
public void testMaxSpecularExponent() {
effect.setSpecularExponent(4);
effect.setSpecularExponent(4.1f);
assertEquals(4.1f, effect.getSpecularExponent(), 1e-100);
pulse();
assertEquals(4f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
}
@Test
public void testPointsAtXSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Spot", "pointsAtX",
"com.sun.scenario.effect.light.SpotLight", "pointsAtX", 0.3);
}
@Test
public void testPointsAtYSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Spot", "pointsAtY",
"com.sun.scenario.effect.light.SpotLight", "pointsAtY", 0.3);
}
@Test
public void testSpecularExponentSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Spot", "specularExponent",
"com.sun.scenario.effect.light.SpotLight", "specularExponent", 0.3);
}
@Test
public void testPointsAtZSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Spot", "pointsAtZ",
"com.sun.scenario.effect.light.SpotLight", "pointsAtZ", 0.3);
}
@Test
public void testColorSynced() throws Exception {
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f result = (Color4f) getObjectPropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Spot", "color",
"com.sun.scenario.effect.light.SpotLight", "color",
Color.RED);
assertColor4fEquals(red, result);
}
@Test
public void testCreateWithParams() {
effect = new Light.Spot(1, 2, 3, 4, Color.RED);
setupTest(effect);
assertEquals(1, effect.getX(), 1e-100);
assertEquals(2, effect.getY(), 1e-100);
assertEquals(3, effect.getZ(), 1e-100);
assertEquals(4, effect.getSpecularExponent(), 1e-100);
assertEquals(Color.RED, effect.getColor());
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getX(), 1e-100);
assertEquals(2.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getY(), 1e-100);
assertEquals(3.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
assertEquals(4.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(0f, c.getGreen(), 1e-5);
assertEquals(0f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Light.Spot(0, 0, 0, 1, Color.WHITE);
setupTest(effect);
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.getY(), 1e-100);
assertEquals(0, effect.getZ(), 1e-100);
assertEquals(1, effect.getSpecularExponent(), 1e-100);
assertEquals(Color.WHITE, effect.getColor());
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getX(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getY(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
assertEquals(1.0f, ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getSpecularExponent(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.SpotLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(1f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
}
