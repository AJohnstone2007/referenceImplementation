package test.javafx.beans.property;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleDoublePropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final double DEFAULT_VALUE = 0.0;
private static final double VALUE_1 = -Math.E;
private static final double EPSILON = 1e-12;
@Test
public void testConstructor_NoArguments() {
final DoubleProperty v = new SimpleDoubleProperty();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get(), EPSILON);
}
@Test
public void testConstructor_InitialValue() {
final DoubleProperty v1 = new SimpleDoubleProperty(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get(), EPSILON);
final DoubleProperty v2 = new SimpleDoubleProperty(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final DoubleProperty v = new SimpleDoubleProperty(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get(), EPSILON);
final DoubleProperty v2 = new SimpleDoubleProperty(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final DoubleProperty v1 = new SimpleDoubleProperty(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get(), EPSILON);
final DoubleProperty v2 = new SimpleDoubleProperty(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get(), EPSILON);
final DoubleProperty v3 = new SimpleDoubleProperty(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get(), EPSILON);
final DoubleProperty v4 = new SimpleDoubleProperty(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get(), EPSILON);
}
}
