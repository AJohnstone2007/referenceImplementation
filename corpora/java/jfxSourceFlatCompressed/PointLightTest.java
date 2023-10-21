package test.javafx.scene.effect;
import test.javafx.scene.effect.LightTestBase;
import static org.junit.Assert.assertEquals;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import com.sun.scenario.effect.Color4f;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightShim;
public class PointLightTest extends LightTestBase {
private Light.Point effect;
@Before
public void setUp() {
effect = new Light.Point();
setupTest(effect);
}
@Test
public void testSetX() {
effect.setX(1.0f);
assertEquals(1.0f, effect.getX(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getX(), 1e-100);
}
@Test
public void testDefaultX() {
assertEquals(0f, effect.getX(), 1e-100);
assertEquals(0f, effect.xProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getX(), 1e-100);
}
@Test
public void testSetY() {
effect.setY(1.0f);
assertEquals(1.0f, effect.getY(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getY(), 1e-100);
}
@Test
public void testDefaultY() {
assertEquals(0f, effect.getY(), 1e-100);
assertEquals(0f, effect.yProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getY(), 1e-100);
}
@Test
public void testSetZ() {
effect.setZ(1.0f);
assertEquals(1.0f, effect.getZ(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
}
@Test
public void testDefaultZ() {
assertEquals(0f, effect.getZ(), 1e-100);
assertEquals(0f, effect.zProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
}
@Test
public void testXSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Point", "x",
"com.sun.scenario.effect.light.PointLight", "x", 0.3);
}
@Test
public void testYSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Point", "y",
"com.sun.scenario.effect.light.PointLight", "y", 0.3);
}
@Test
public void testZSynced() throws Exception {
checkDoublePropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Point", "z",
"com.sun.scenario.effect.light.PointLight", "z", 0.3);
}
@Test
public void testColorSynced() throws Exception {
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f result = (Color4f) getObjectPropertySynced(
effect, LightShim.getPeer(effect),
"javafx.scene.effect.Light$Point", "color",
"com.sun.scenario.effect.light.PointLight", "color",
Color.RED);
assertColor4fEquals(red, result);
}
@Test
public void testCreateWithParams() {
effect = new Light.Point(1, 2, 3, Color.RED);
setupTest(effect);
assertEquals(1, effect.getX(), 1e-100);
assertEquals(2, effect.getY(), 1e-100);
assertEquals(3, effect.getZ(), 1e-100);
assertEquals(Color.RED, effect.getColor());
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getX(), 1e-100);
assertEquals(2.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getY(), 1e-100);
assertEquals(3.0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(0f, c.getGreen(), 1e-5);
assertEquals(0f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Light.Point(0, 0, 0, Color.WHITE);
setupTest(effect);
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.getY(), 1e-100);
assertEquals(0, effect.getZ(), 1e-100);
assertEquals(Color.WHITE, effect.getColor());
pulse();
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getX(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getY(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getZ(), 1e-100);
Color4f c = ((com.sun.scenario.effect.light.PointLight)
LightShim.getPeer(effect)).getColor();
assertEquals(1f, c.getRed(), 1e-5);
assertEquals(1f, c.getGreen(), 1e-5);
assertEquals(1f, c.getBlue(), 1e-5);
assertEquals(1f, c.getAlpha(), 1e-5);
}
}
