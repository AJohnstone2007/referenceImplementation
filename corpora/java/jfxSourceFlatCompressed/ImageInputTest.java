package test.javafx.scene.effect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import test.javafx.scene.image.TestImages;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.ImageInput;
public class ImageInputTest extends EffectsTestBase {
private ImageInput effect;
@Before
public void setUp() {
effect = new ImageInput();
setupTest(effect);
}
@Test
public void testSetX() {
effect.setX(1.0f);
assertEquals(1.0f, effect.getX(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().x, 1e-100);
}
@Test
public void testDefaultX() {
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.xProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().x, 1e-100);
}
@Test
public void testSetY() {
effect.setY(1.0f);
assertEquals(1.0f, effect.getY(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().y, 1e-100);
}
@Test
public void testDefaultY() {
assertEquals(0, effect.getY(), 1e-100);
assertEquals(0, effect.yProperty().get(), 1e-100);
pulse();
assertEquals(0, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().y, 1e-100);
}
@Test
public void testSetSource() {
Image i = new Image("test/javafx/scene/image/test.png");
effect.setSource(i);
assertEquals(i, effect.getSource());
pulse();
assertEquals(null, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource());
effect.setSource(TestImages.TEST_IMAGE_32x32);
pulse();
assertEquals(TestImages.TEST_IMAGE_32x32, effect.getSource());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalHeight(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalHeight());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalWidth(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalWidth());
}
@Test
public void testDefaultSource() {
assertNull(effect.getSource());
assertNull(effect.sourceProperty().get());
pulse();
assertNull(((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource());
}
@Test
public void testXSynced() throws Exception {
Point2D pt = (Point2D) getDoublePropertySynced(
"javafx.scene.effect.ImageInput", "x",
"com.sun.scenario.effect.Identity", "location", 10);
assertEquals(10, pt.x, 1e-100);
}
@Test
public void testYSynced() throws Exception {
Point2D pt = (Point2D) getDoublePropertySynced(
"javafx.scene.effect.ImageInput", "y",
"com.sun.scenario.effect.Identity", "location", 10);
assertEquals(10, pt.y, 1e-100);
}
@Test
public void testSourceSynced() throws Exception {
ObjectProperty source = new SimpleObjectProperty();
effect.sourceProperty().bind(source);
source.set(TestImages.TEST_IMAGE_32x32);
pulse();
assertEquals(TestImages.TEST_IMAGE_32x32, effect.getSource());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalHeight(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalHeight());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalWidth(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalWidth());
}
@Test
public void testCreateWithParams() {
effect = new ImageInput(TestImages.TEST_IMAGE_32x32);
setupTest(effect);
assertEquals(TestImages.TEST_IMAGE_32x32, effect.getSource());
pulse();
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalHeight(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalHeight());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalWidth(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalWidth());
}
@Test
public void testCreateWithParams3() {
effect = new ImageInput(TestImages.TEST_IMAGE_32x32, 1, 2);
setupTest(effect);
assertEquals(TestImages.TEST_IMAGE_32x32, effect.getSource());
assertEquals(1, effect.getX(), 1e-100);
assertEquals(2, effect.getY(), 1e-100);
pulse();
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalHeight(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalHeight());
assertEquals(Toolkit.getToolkit().toFilterable(TestImages.TEST_IMAGE_32x32).getPhysicalWidth(),
((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource().getPhysicalWidth());
assertEquals(1.0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().x, 1e-100);
assertEquals(2.0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().y, 1e-100);
}
@Test
public void testCreateWithDefaultParams() {
effect = new ImageInput(null);
setupTest(effect);
assertNull(effect.getSource());
pulse();
assertNull(((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource());
}
@Test
public void testCreateWithDefaultParams3() {
effect = new ImageInput(null, 0, 0);
setupTest(effect);
assertNull(effect.getSource());
assertEquals(0, effect.getX(), 1e-100);
assertEquals(0, effect.getY(), 1e-100);
pulse();
assertNull(((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getSource());
assertEquals(0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().x, 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.Identity) EffectHelper.getPeer(effect)).getLocation().y, 1e-100);
}
}
