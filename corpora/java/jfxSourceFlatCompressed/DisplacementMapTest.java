package test.javafx.scene.effect;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
public class DisplacementMapTest extends EffectsTestBase {
private DisplacementMap effect;
@Before
public void setUp() {
effect = new DisplacementMap();
setupTest(effect);
}
@Test
public void testSetOffsetX() {
effect.setOffsetX(1.0f);
assertEquals(1.0f, effect.getOffsetX(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetX(), 1e-100);
}
@Test
public void testDefaultOffsetX() {
assertEquals(0f, effect.getOffsetX(), 1e-100);
assertEquals(0f, effect.offsetXProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetX(), 1e-100);
}
@Test
public void testSetOffsetY() {
effect.setOffsetY(1.0f);
assertEquals(1.0f, effect.getOffsetY(), 1e-100);
pulse();
assertEquals(1.0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetY(), 1e-100);
}
@Test
public void testDefaultOffsetY() {
assertEquals(0f, effect.getOffsetY(), 1e-100);
assertEquals(0f, effect.offsetYProperty().get(), 1e-100);
pulse();
assertEquals(0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetY(), 1e-100);
}
@Test
public void testSetScaleX() {
effect.setScaleX(1.1f);
assertEquals(1.1f, effect.getScaleX(), 1e-100);
pulse();
assertEquals(1.1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleX(), 1e-100);
}
@Test
public void testDefaultScaleX() {
assertEquals(1f, effect.getScaleX(), 1e-100);
assertEquals(1f, effect.scaleXProperty().get(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleX(), 1e-100);
}
@Test
public void testSetScaleY() {
effect.setScaleY(1.1f);
assertEquals(1.1f, effect.getScaleY(), 1e-100);
pulse();
assertEquals(1.1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleY(), 1e-100);
}
@Test
public void testDefaultScaleY() {
assertEquals(1f, effect.getScaleY(), 1e-100);
assertEquals(1f, effect.scaleYProperty().get(), 1e-100);
pulse();
assertEquals(1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleY(), 1e-100);
}
@Test
public void testSetWrap() {
effect.setWrap(true);
assertTrue(effect.isWrap());
pulse();
assertTrue(((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getWrap());
}
@Test
public void testDefaultWrap() {
assertFalse(effect.isWrap());
assertFalse(effect.wrapProperty().get());
pulse();
assertFalse(((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getWrap());
}
private FloatMap createFloatMap(int width, int height) {
FloatMap map = new FloatMap();
map.setWidth(width);
map.setHeight(height);
for (int i = 0; i < width; i++) {
double v = 1.0;
for (int j = 0; j < height; j++) {
map.setSamples(i, j, 0.0f, (float) v);
}
}
return map;
}
@Test
public void testSetMap() {
int w = 100;
int h = 50;
FloatMap map = createFloatMap(w, h);
map.setWidth(w);
map.setHeight(h);
effect.setMapData(map);
assertEquals(w, map.getWidth());
assertEquals(h, map.getHeight());
pulse();
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(w, mapImpl.getWidth());
assertEquals(h, mapImpl.getHeight());
}
@Test
public void testDefaultMap() {
FloatMap map = effect.getMapData();
assertNotNull(map);
assertEquals(1, map.getWidth());
assertEquals(1, map.getHeight());
pulse();
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(1, mapImpl.getWidth());
assertEquals(1, mapImpl.getHeight());
assertEquals(map, effect.mapDataProperty().get());
}
@Test
public void testDefaultMapNotChangedByThisEffect() {
FloatMap map = effect.getMapData();
map.setHeight(100);
map.setWidth(200);
effect.setMapData(null);
assertNull(effect.getMapData());
pulse();
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(1, mapImpl.getWidth());
assertEquals(1, mapImpl.getHeight());
}
@Test
public void testOffsetXSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.DisplacementMap", "offsetX",
"com.sun.scenario.effect.DisplacementMap", "offsetX", 10);
}
@Test
public void testOffsetYSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.DisplacementMap", "offsetY",
"com.sun.scenario.effect.DisplacementMap", "offsetY", 10);
}
@Test
public void testScaleXSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.DisplacementMap", "scaleX",
"com.sun.scenario.effect.DisplacementMap", "scaleX", 10);
}
@Test
public void testScaleYSynced() throws Exception {
checkDoublePropertySynced(
"javafx.scene.effect.DisplacementMap", "scaleY",
"com.sun.scenario.effect.DisplacementMap", "scaleY", 10);
}
@Test
public void testWrapSynced() throws Exception {
checkBooleanPropertySynced(
"javafx.scene.effect.DisplacementMap", "wrap",
"com.sun.scenario.effect.DisplacementMap", "wrap", true);
}
@Test
public void testInputSynced() throws Exception {
BoxBlur blur = new BoxBlur();
checkEffectPropertySynced(
"javafx.scene.effect.DisplacementMap", "input",
"com.sun.scenario.effect.DisplacementMap", "contentInput",
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
int w = 100;
int h = 50;
FloatMap map = createFloatMap(w, h);
effect = new DisplacementMap(map);
setupTest(effect);
assertEquals(map, effect.getMapData());
pulse();
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(w, mapImpl.getWidth());
assertEquals(h, mapImpl.getHeight());
}
@Test
public void testCreateWithParams5() {
int w = 100;
int h = 50;
FloatMap map = createFloatMap(w, h);
effect = new DisplacementMap(map, 1, 2, 3, 4);
setupTest(effect);
assertEquals(1, effect.getOffsetX(), 1e-100);
assertEquals(2, effect.getOffsetY(), 1e-100);
assertEquals(3, effect.getScaleX(), 1e-100);
assertEquals(4, effect.getScaleY(), 1e-100);
assertEquals(map, effect.getMapData());
pulse();
assertEquals(1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetX(), 1e-100);
assertEquals(2f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetY(), 1e-100);
assertEquals(3f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleX(), 1e-100);
assertEquals(4f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleY(), 1e-100);
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(w, mapImpl.getWidth());
assertEquals(h, mapImpl.getHeight());
}
@Test
public void testCreateWithDefaultParams() {
int w = 1;
int h = 1;
FloatMap map = createFloatMap(w, h);
effect = new DisplacementMap(map);
setupTest(effect);
assertEquals(map, effect.getMapData());
pulse();
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(w, mapImpl.getWidth());
assertEquals(h, mapImpl.getHeight());
}
@Test
public void testCreateWithDefaultParams5() {
int w = 1;
int h = 1;
FloatMap map = createFloatMap(w, h);
effect = new DisplacementMap(map, 0, 0, 1, 1);
setupTest(effect);
assertEquals(0, effect.getOffsetX(), 1e-100);
assertEquals(0, effect.getOffsetY(), 1e-100);
assertEquals(1, effect.getScaleX(), 1e-100);
assertEquals(1, effect.getScaleY(), 1e-100);
assertEquals(map, effect.getMapData());
pulse();
assertEquals(0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetX(), 1e-100);
assertEquals(0f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getOffsetY(), 1e-100);
assertEquals(1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleX(), 1e-100);
assertEquals(1f, ((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getScaleY(), 1e-100);
com.sun.scenario.effect.FloatMap mapImpl =
((com.sun.scenario.effect.DisplacementMap) EffectHelper.getPeer(effect)).getMapData();
assertNotNull(mapImpl);
assertEquals(w, mapImpl.getWidth());
assertEquals(h, mapImpl.getHeight());
}
}
