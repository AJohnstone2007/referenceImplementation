package test.javafx.scene.effect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import com.sun.scenario.effect.AbstractShadow.ShadowMode;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Shadow;
public class ShadowTest extends EffectsTestBase {
private Shadow effect;
@Before
public void setUp() {
effect = new Shadow();
setupTest(effect);
}
@Test
public void testSetBlurType() {
effect.setBlurType(BlurType.ONE_PASS_BOX);
assertEquals(BlurType.ONE_PASS_BOX, effect.getBlurType());
pulse();
assertEquals(ShadowMode.ONE_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
effect.setBlurType(BlurType.TWO_PASS_BOX);
assertEquals(BlurType.TWO_PASS_BOX, effect.getBlurType());
pulse();
assertEquals(ShadowMode.TWO_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
effect.setBlurType(BlurType.THREE_PASS_BOX);
assertEquals(BlurType.THREE_PASS_BOX, effect.getBlurType());
pulse();
assertEquals(ShadowMode.THREE_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
effect.setBlurType(BlurType.GAUSSIAN);
assertEquals(BlurType.GAUSSIAN, effect.getBlurType());
pulse();
assertEquals(ShadowMode.GAUSSIAN, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
}
@Test
public void testDefaultBlurType() {
assertEquals(BlurType.THREE_PASS_BOX, effect.getBlurType());
assertEquals(BlurType.THREE_PASS_BOX, effect.blurTypeProperty().get());
pulse();
assertEquals(ShadowMode.THREE_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
}
@Test
public void testNullBlurType() {
effect.setBlurType(null);
assertEquals(null, effect.getBlurType());
assertEquals(null, effect.blurTypeProperty().get());
pulse();
assertEquals(ShadowMode.THREE_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
}
@Test
public void testSetColor() {
effect.setColor(Color.RED);
assertEquals(Color.RED, effect.getColor());
pulse();
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(red, actual);
}
@Test
public void testDefaultColor() {
assertEquals(Color.BLACK, effect.getColor());
assertEquals(Color.BLACK, effect.colorProperty().get());
pulse();
Color color = Color.BLACK;
Color4f black = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(black, actual);
}
@Test
public void testNullColor() {
effect.setColor(null);
assertNull(effect.getColor());
assertNull(effect.colorProperty().get());
pulse();
Color color = Color.BLACK;
Color4f black = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(black, actual);
}
@Test
public void testSetWidth() {
effect.setWidth(9.0f);
assertEquals(9.0f, effect.getWidth(), 1e-100);
pulse();
assertEquals(9.0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(7.0f, effect.getRadius(), 1e-100);
}
@Test
public void testDefaultWidth() {
assertEquals(21f, effect.getWidth(), 1e-100);
assertEquals(21f, effect.widthProperty().get(), 1e-100);
pulse();
assertEquals(21f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
}
@Test
public void testMinWidth() {
effect.setWidth(0);
effect.setWidth(-0.1f);
assertEquals(-0.1f, effect.getWidth(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
}
@Test
public void testMaxWidth() {
effect.setWidth(255);
effect.setWidth(255.1f);
assertEquals(255.1f, effect.getWidth(), 1e-100);
pulse();
assertEquals(255f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
}
@Test
public void testSetHeight() {
effect.setHeight(9.0f);
assertEquals(9.0f, effect.getHeight(), 1e-100);
pulse();
assertEquals(9.0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(7.0f, effect.getRadius(), 1e-100);
}
@Test
public void testDefaultHeight() {
assertEquals(21f, effect.getHeight(), 1e-100);
assertEquals(21f, effect.heightProperty().get(), 1e-100);
pulse();
assertEquals(21f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
}
@Test
public void testMinHeight() {
effect.setHeight(0);
effect.setHeight(-0.1f);
assertEquals(-0.1f, effect.getHeight(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
}
@Test
public void testMaxHeight() {
effect.setHeight(1);
effect.setHeight(255.1f);
assertEquals(255.1f, effect.getHeight(), 1e-100);
pulse();
assertEquals(255f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
}
@Test
public void testSetRadius() {
effect.setRadius(4.0f);
assertEquals(4.0f, effect.getRadius(), 1e-100);
pulse();
assertEquals(4.0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
assertEquals(9.0f, effect.getHeight(), 1e-100);
assertEquals(9.0f, effect.getWidth(), 1e-100);
}
@Test
public void testDefaultRadius() {
assertEquals(10f, effect.getRadius(), 1e-100);
assertEquals(10f, effect.radiusProperty().get(), 1e-100);
pulse();
assertEquals(10f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMinRadius() {
effect.setRadius(0);
effect.setRadius(-0.1f);
assertEquals(-0.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testMaxRadius() {
effect.setRadius(127);
effect.setRadius(127.1f);
assertEquals(127.1f, effect.getRadius(), 1e-100);
pulse();
assertEquals(127f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testRadiusNotNegative() {
effect.setHeight(0.1f);
effect.setWidth(0.1f);
assertEquals(0f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
effect.setWidth(0.2f);
effect.setHeight(0.2f);
assertEquals(0f, effect.getRadius(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getRadius(), 1e-100);
}
@Test
public void testHeightSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Shadow", "height",
"com.sun.scenario.effect.GeneralShadow", "gaussianHeight", 9);
assertEquals(7, effect.getRadius(), 1e-100);
}
@Test
public void testWidthSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Shadow", "width",
"com.sun.scenario.effect.GeneralShadow", "gaussianWidth", 9);
assertEquals(7, effect.getRadius(), 1e-100);
}
@Test
public void testBlurTypeSynced() throws Exception {
checkObjectPropertySynced(
"javafx.scene.effect.Shadow", "blurType",
"com.sun.scenario.effect.GeneralShadow", "shadowMode",
BlurType.TWO_PASS_BOX, ShadowMode.TWO_PASS_BOX,
BlurType.GAUSSIAN);
}
@Test
public void testRadiusSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.Shadow", "radius",
"com.sun.scenario.effect.GeneralShadow", "radius", 4);
assertEquals(9, effect.getHeight(), 1e-100);
assertEquals(9, effect.getWidth(), 1e-100);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.Shadow", "input",
"com.sun.scenario.effect.GeneralShadow", "input",
blur, (com.sun.scenario.effect.BoxBlur)EffectHelper.getPeer(blur));
}
@Test
public void testColorSynced() throws Exception {
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f result = (Color4f) getObjectPropertySynced(
"javafx.scene.effect.Shadow", "color",
"com.sun.scenario.effect.GeneralShadow", "color",
Color.RED);
assertColor4fEquals(red, result);
}
@Test
public void testRadiusBound() throws Exception {
DoubleProperty boundRadius = new SimpleDoubleProperty();
effect.radiusProperty().bind(boundRadius);
boundRadius.set(4);
effect.setWidth(9);
assertEquals(9, effect.getHeight(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
effect.setHeight(3);
assertEquals(15, effect.getWidth(), 1e-100);
pulse();
assertEquals(15, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
}
@Test
public void testHeightBound() throws Exception {
DoubleProperty boundHeight = new SimpleDoubleProperty();
effect.heightProperty().bind(boundHeight);
boundHeight.set(9);
effect.setRadius(4);
assertEquals(9, effect.getWidth(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
effect.setWidth(3);
assertEquals(2.5, effect.getRadius(), 1e-100);
}
@Test
public void testWidthBound() throws Exception {
DoubleProperty boundWidth = new SimpleDoubleProperty();
effect.widthProperty().bind(boundWidth);
boundWidth.set(9);
effect.setRadius(4);
assertEquals(9, effect.getHeight(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
effect.setHeight(3);
assertEquals(2.5, effect.getRadius(), 1e-100);
}
@Test
public void testRadiusWidthBound() throws Exception {
DoubleProperty boundRadius = new SimpleDoubleProperty();
DoubleProperty boundWidth = new SimpleDoubleProperty();
effect.radiusProperty().bind(boundRadius);
effect.widthProperty().bind(boundWidth);
boundRadius.set(4);
boundWidth.set(9);
assertEquals(9, effect.getHeight(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
boundRadius.set(7);
assertEquals(21, effect.getHeight(), 1e-100);
pulse();
assertEquals(21, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
}
@Test
public void testRadiusHeightBound() throws Exception {
DoubleProperty boundRadius = new SimpleDoubleProperty();
DoubleProperty boundHeight = new SimpleDoubleProperty();
effect.radiusProperty().bind(boundRadius);
effect.heightProperty().bind(boundHeight);
boundRadius.set(4);
boundHeight.set(9);
assertEquals(9, effect.getWidth(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
boundRadius.set(7);
assertEquals(21, effect.getWidth(), 1e-100);
pulse();
assertEquals(21, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
}
@Test
public void testWidthHeightBound() throws Exception {
DoubleProperty boundWidth = new SimpleDoubleProperty();
DoubleProperty boundHeight = new SimpleDoubleProperty();
effect.widthProperty().bind(boundWidth);
effect.heightProperty().bind(boundHeight);
boundHeight.set(9);
boundWidth.set(9);
assertEquals(4, effect.getRadius(), 1e-100);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
boundHeight.set(21);
assertEquals(7, effect.getRadius(), 1e-100);
pulse();
assertEquals(7, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
}
@Test
public void testWidthHeightRadiusBound() {
DoubleProperty boundWidth = new SimpleDoubleProperty();
DoubleProperty boundHeight = new SimpleDoubleProperty();
DoubleProperty boundRadius = new SimpleDoubleProperty();
effect.widthProperty().bind(boundWidth);
effect.heightProperty().bind(boundHeight);
effect.radiusProperty().bind(boundRadius);
boundHeight.set(9);
boundWidth.set(9);
boundRadius.set(5);
pulse();
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianHeight(), 1e-100);
assertEquals(9, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianWidth(), 1e-100);
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
}
@Test
public void testCreateWithParams() {
effect = new Shadow(4, Color.RED);
setupTest(effect);
assertEquals(4, effect.getRadius(), 1e-100);
assertEquals(Color.RED, effect.getColor());
assertEquals(9, effect.getHeight(), 1e-100);
assertEquals(9, effect.getWidth(), 1e-100);
pulse();
assertEquals(4, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(red, actual);
}
@Test
public void testCreateWithParams3() {
effect = new Shadow(BlurType.GAUSSIAN, Color.RED, 4);
setupTest(effect);
assertEquals(4, effect.getRadius(), 1e-100);
assertEquals(Color.RED, effect.getColor());
assertEquals(BlurType.GAUSSIAN, effect.getBlurType());
assertEquals(9, effect.getHeight(), 1e-100);
assertEquals(9, effect.getWidth(), 1e-100);
pulse();
assertEquals(4f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
assertEquals(ShadowMode.GAUSSIAN, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
Color color = Color.RED;
Color4f red = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(red, actual);
}
@Test
public void testCreateWithDefaultParams() {
effect = new Shadow(10, Color.BLACK);
setupTest(effect);
assertEquals(10, effect.getRadius(), 1e-100);
assertEquals(Color.BLACK, effect.getColor());
assertEquals(21, effect.getHeight(), 1e-100);
assertEquals(21, effect.getWidth(), 1e-100);
pulse();
assertEquals(10, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
Color color = Color.BLACK;
Color4f black = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(black, actual);
}
@Test
public void testCreateWithDefaultParams3() {
effect = new Shadow(BlurType.THREE_PASS_BOX, Color.BLACK, 10);
setupTest(effect);
assertEquals(10, effect.getRadius(), 1e-100);
assertEquals(Color.BLACK, effect.getColor());
assertEquals(BlurType.THREE_PASS_BOX, effect.getBlurType());
assertEquals(21, effect.getHeight(), 1e-100);
assertEquals(21, effect.getWidth(), 1e-100);
pulse();
assertEquals(10f, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getGaussianRadius(), 1e-100);
assertEquals(ShadowMode.THREE_PASS_BOX, ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getShadowMode());
Color color = Color.BLACK;
Color4f black = new Color4f((float) color.getRed(), (float) color.getGreen(),
(float) color.getBlue(), (float) color.getOpacity());
Color4f actual = ((com.sun.scenario.effect.GeneralShadow) EffectHelper.getPeer(effect)).getColor();
assertColor4fEquals(black, actual);
}
}
