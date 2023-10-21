package test.javafx.scene.effect;
import test.javafx.scene.effect.EffectsTestBase;
import static org.junit.Assert.assertEquals;
import java.lang.reflect.Method;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.effect.FloatMapShim;
import org.junit.Before;
import org.junit.Test;
public class FloatMapTest extends EffectsTestBase {
private FloatMap floatMap;
private DisplacementMap displacementMap;
@Before
public void setUp() {
floatMap = new FloatMap();
displacementMap = new DisplacementMap();
displacementMap.setMapData(floatMap);
setupTest(displacementMap);
}
@Test
public void testSetWidth() {
floatMap.setWidth(2);
assertEquals(2, floatMap.getWidth());
pulse();
assertEquals(2, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
}
@Test
public void testDefaultWidth() {
assertEquals(1, floatMap.getWidth());
assertEquals(1, floatMap.widthProperty().get());
pulse();
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
}
@Test
public void testMinWidth() {
floatMap.setWidth(1);
floatMap.setWidth(0);
assertEquals(0, floatMap.getWidth());
pulse();
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
}
@Test
public void testMaxWidth() {
floatMap.setWidth(4096);
floatMap.setWidth(4097);
assertEquals(4097, floatMap.getWidth());
pulse();
assertEquals(4096, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
}
@Test
public void testSetHeight() {
floatMap.setHeight(5);
assertEquals(5, floatMap.getHeight());
pulse();
assertEquals(5, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
@Test
public void testDefaultHeight() {
assertEquals(1, floatMap.getHeight());
assertEquals(1, floatMap.heightProperty().get());
pulse();
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
@Test
public void testMinHeight() {
floatMap.setHeight(1);
floatMap.setHeight(0);
assertEquals(0, floatMap.getHeight());
pulse();
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
@Test
public void testMaxHeight() {
floatMap.setHeight(4096);
floatMap.setHeight(4097);
assertEquals(4097, floatMap.getHeight());
pulse();
assertEquals(4096, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
@Test
public void testSetSamples3() {
floatMap.setSamples(0, 0, 0.5f);
pulse();
com.sun.scenario.effect.FloatMap fm = FloatMapShim.getImpl(floatMap);
float[] data = fm.getData();
assertEquals(0.5f, data[0], 1e-100);
assertEquals(0.0f, data[1], 1e-100);
assertEquals(0.0f, data[2], 1e-100);
assertEquals(0.0f, data[3], 1e-100);
}
@Test
public void testSetSamples4() {
floatMap.setSamples(0, 0, 0.5f, 0.4f);
pulse();
com.sun.scenario.effect.FloatMap fm = FloatMapShim.getImpl(floatMap);
float[] data = fm.getData();
assertEquals(0.5f, data[0], 1e-100);
assertEquals(0.4f, data[1], 1e-100);
assertEquals(0.0f, data[2], 1e-100);
assertEquals(0.0f, data[3], 1e-100);
}
@Test
public void testSetSamples5() {
floatMap.setSamples(0, 0, 0.5f, 0.4f, 0.3f);
pulse();
com.sun.scenario.effect.FloatMap fm = FloatMapShim.getImpl(floatMap);
float[] data = fm.getData();
assertEquals(0.5f, data[0], 1e-100);
assertEquals(0.4f, data[1], 1e-100);
assertEquals(0.3f, data[2], 1e-100);
assertEquals(0.0f, data[3], 1e-100);
}
@Test
public void testSetSamples6() {
floatMap.setSamples(0, 0, 0.5f, 0.4f, 0.3f, 0.2f);
pulse();
com.sun.scenario.effect.FloatMap fm = FloatMapShim.getImpl(floatMap);
float[] data = fm.getData();
assertEquals(0.5f, data[0], 1e-100);
assertEquals(0.4f, data[1], 1e-100);
assertEquals(0.3f, data[2], 1e-100);
assertEquals(0.2f, data[3], 1e-100);
}
@Test
public void testSetSample() {
floatMap.setSample(0, 0, 0, 0.5f);
floatMap.setSample(0, 0, 1, 0.4f);
floatMap.setSample(0, 0, 2, 0.3f);
floatMap.setSample(0, 0, 3, 0.2f);
pulse();
com.sun.scenario.effect.FloatMap fm = FloatMapShim.getImpl(floatMap);
float[] data = fm.getData();
assertEquals(0.5f, data[0], 1e-100);
assertEquals(0.4f, data[1], 1e-100);
assertEquals(0.3f, data[2], 1e-100);
assertEquals(0.2f, data[3], 1e-100);
}
@Test
public void testHeightSynced() throws Exception {
checkIntPropertySynced(
"javafx.scene.effect.FloatMap", "height",
"com.sun.scenario.effect.FloatMap", "height", 10);
}
@Test
public void testWidthSynced() throws Exception {
checkIntPropertySynced(
"javafx.scene.effect.FloatMap", "width",
"com.sun.scenario.effect.FloatMap", "width", 10);
}
@Override
protected void checkIntPropertySynced(
String effectName,
String propertyName,
String pgEffectName,
String pgPropertyName,
int expected)
throws Exception {
final Class effectClass = Class.forName(effectName);
final Class pgEffectClass = Class.forName(pgEffectName);
final StringBuilder pgPropertyNameBuilder = new StringBuilder(pgPropertyName);
pgPropertyNameBuilder.setCharAt(0, Character.toUpperCase(pgPropertyName.charAt(0)));
final String pgGetterName = new StringBuilder("get").append(pgPropertyNameBuilder).toString();
final Method pgGetter = pgEffectClass.getMethod(pgGetterName);
IntegerProperty v = new SimpleIntegerProperty();
Method m = effectClass.getMethod(propertyName + "Property", new Class[] {});
((IntegerProperty)m.invoke(floatMap)).bind(v);
pulse();
v.set(expected);
pulse();
assertEquals(expected, ((Number)pgGetter.invoke(
FloatMapShim.getImpl(floatMap))).intValue());
}
@Test
public void testCreateWithParams() {
floatMap = new FloatMap(2, 3);
displacementMap = new DisplacementMap();
displacementMap.setMapData(floatMap);
setupTest(displacementMap);
assertEquals(2, floatMap.getWidth(), 1e-100);
assertEquals(3, floatMap.getHeight(), 1e-100);
pulse();
assertEquals(2, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
assertEquals(3, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
@Test
public void testCreateWithDefaultParams() {
floatMap = new FloatMap(1, 1);
displacementMap = new DisplacementMap();
displacementMap.setMapData(floatMap);
setupTest(displacementMap);
assertEquals(1, floatMap.getWidth(), 1e-100);
assertEquals(1, floatMap.getHeight(), 1e-100);
pulse();
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getWidth());
assertEquals(1, ((com.sun.scenario.effect.FloatMap)
FloatMapShim.getImpl(floatMap)).getHeight());
}
}
