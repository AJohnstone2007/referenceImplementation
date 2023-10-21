package test.javafx.scene.effect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.EffectShim;
public class ColorInputTest extends EffectsTestBase {
private ColorInput effect;
@Before
public void setUp() {
effect = new ColorInput();
setupTest(effect);
}
@Test
public void testSetX() {
effect.setX(1.0f);
assertEquals(1.0f, effect.getX(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Flood) EffectShim.getPeer(effect)).getFloodBounds().getMinX(), 1e-100);
}
@Test
public void testDefaultX() {
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.xProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinX(), 1e-100);
}
@Test
public void testSetY() {
effect.setY(1.0f);
assertEquals(1.0f, effect.getY(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinY(), 1e-100);
}
@Test
public void testDefaultY() {
assertEquals(0, effect.getY(), 1e-100);
assertEquals(0, effect.yProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinY(), 1e-100);
}
@Test
public void testSetWidth() {
effect.setWidth(1.0f);
assertEquals(1.0f, effect.getWidth(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getWidth(), 1e-100);
}
@Test
public void testDefaultWidth() {
assertEquals(0, effect.getWidth(), 1e-100);
assertEquals(0, effect.widthProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getWidth(), 1e-100);
}
@Test
public void testSetHeight() {
effect.setHeight(1.0f);
assertEquals(1.0f, effect.getHeight(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getHeight(), 1e-100);
}
@Test
public void testDefaultHeight() {
assertEquals(0, effect.getHeight(), 1e-100);
assertEquals(0, effect.heightProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getHeight(), 1e-100);
}
@Test
public void testSetPaint() {
effect.setPaint(Color.BLUE);
assertEquals(Color.BLUE, effect.getPaint());
pulse();
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.BLUE),
((com.sun.scenario.effect.Flood) EffectShim.getPeer(effect)).getPaint());
}
@Test
public void testDefaultPaint() {
assertEquals(Color.RED, effect.getPaint());
assertEquals(Color.RED, effect.paintProperty().get());
pulse();
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.RED),
((com.sun.scenario.effect.Flood) EffectShim.getPeer(effect)).getPaint());
}
@Test
public void testNullPaint() {
effect.setPaint(null);
assertNull(effect.getPaint());
assertNull(effect.paintProperty().get());
pulse();
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.RED),
((com.sun.scenario.effect.Flood) EffectShim.getPeer(effect)).getPaint());
}
@Test
public void testHeightSynced() throws Exception {
RectBounds floodBounds = (RectBounds) getDoublePropertySynced(
"javafx.scene.effect.ColorInput", "height",
"com.sun.scenario.effect.Flood", "floodBounds", 10);
assertEquals(10, floodBounds.getHeight(), 1e-100);
}
@Test
public void testWidthSynced() throws Exception {
RectBounds floodBounds = (RectBounds) getDoublePropertySynced(
"javafx.scene.effect.ColorInput", "width",
"com.sun.scenario.effect.Flood", "floodBounds", 10);
assertEquals(10, floodBounds.getWidth(), 1e-100);
}
@Test
public void testXSynced() throws Exception {
RectBounds floodBounds = (RectBounds) getDoublePropertySynced(
"javafx.scene.effect.ColorInput", "x",
"com.sun.scenario.effect.Flood", "floodBounds", 10);
assertEquals(10, floodBounds.getMinX(), 1e-100);
}
@Test
public void testYSynced() throws Exception {
RectBounds floodBounds = (RectBounds) getDoublePropertySynced(
"javafx.scene.effect.ColorInput", "y",
"com.sun.scenario.effect.Flood", "floodBounds", 10);
assertEquals(10, floodBounds.getMinY(), 1e-100);
}
@Test
public void testPaintSynced() throws Exception {
Object paint = getObjectPropertySynced(
"javafx.scene.effect.ColorInput", "paint",
"com.sun.scenario.effect.Flood", "paint",
Color.RED);
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.RED), paint);
}
@Test
public void testCreateWithParams() {
effect = new ColorInput(1, 2, 3, 4, Color.BLUE);
setupTest(effect);
assertEquals(1, effect.getX(), 1e-100);
assertEquals(2, effect.getY(), 1e-100);
assertEquals(3, effect.getWidth(), 1e-100);
assertEquals(4, effect.getHeight(), 1e-100);
assertEquals(Color.BLUE, effect.getPaint());
pulse();
assertEquals(1f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinX(), 1e-100);
assertEquals(2f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinY(), 1e-100);
assertEquals(3f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getWidth(), 1e-100);
assertEquals(4f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getHeight(), 1e-100);
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.BLUE),
((com.sun.scenario.effect.Flood) EffectShim.getPeer(effect)).getPaint());
}
@Test
public void testCreateWithDefaultParams() {
effect = new ColorInput(0, 0, 0, 0, Color.RED);
setupTest(effect);
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.getY(), 1e-100);
assertEquals(0, effect.getWidth(), 1e-100);
assertEquals(0, effect.getHeight(), 1e-100);
assertEquals(Color.RED, effect.getPaint());
pulse();
assertEquals(0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinX(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getMinY(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getWidth(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getFloodBounds().getHeight(), 1e-100);
assertEquals(Toolkit.getPaintAccessor().getPlatformPaint(Color.RED), ((com.sun.scenario.effect.Flood)
EffectShim.getPeer(effect)).getPaint());
}
}
