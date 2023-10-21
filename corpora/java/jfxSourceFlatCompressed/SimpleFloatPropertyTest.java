package test.javafx.beans.property;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleFloatPropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final float DEFAULT_VALUE = 0.0f;
private static final float VALUE_1 = (float) -Math.E;
private static final float EPSILON = 1e-6f;
@Test
public void testConstructor_NoArguments() {
final FloatProperty v = new SimpleFloatProperty();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get(), EPSILON);
}
@Test
public void testConstructor_InitialValue() {
final FloatProperty v1 = new SimpleFloatProperty(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get(), EPSILON);
final FloatProperty v2 = new SimpleFloatProperty(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final FloatProperty v = new SimpleFloatProperty(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get(), EPSILON);
final FloatProperty v2 = new SimpleFloatProperty(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final FloatProperty v1 = new SimpleFloatProperty(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get(), EPSILON);
final FloatProperty v2 = new SimpleFloatProperty(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
final FloatProperty v3 = new SimpleFloatProperty(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get(), EPSILON);
final FloatProperty v4 = new SimpleFloatProperty(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get(), EPSILON);
}
}
