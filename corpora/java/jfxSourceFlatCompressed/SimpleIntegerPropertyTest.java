package test.javafx.beans.property;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleIntegerPropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final int DEFAULT_VALUE = 0;
private static final int VALUE_1 = 13;
@Test
public void testConstructor_NoArguments() {
final IntegerProperty v = new SimpleIntegerProperty();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
}
@Test
public void testConstructor_InitialValue() {
final IntegerProperty v1 = new SimpleIntegerProperty(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get());
final IntegerProperty v2 = new SimpleIntegerProperty(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final IntegerProperty v = new SimpleIntegerProperty(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
final IntegerProperty v2 = new SimpleIntegerProperty(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final IntegerProperty v1 = new SimpleIntegerProperty(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get());
final IntegerProperty v2 = new SimpleIntegerProperty(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
final IntegerProperty v3 = new SimpleIntegerProperty(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get());
final IntegerProperty v4 = new SimpleIntegerProperty(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get());
}
}
