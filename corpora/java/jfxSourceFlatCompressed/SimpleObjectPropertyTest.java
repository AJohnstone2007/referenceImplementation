package test.javafx.beans.property;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleObjectPropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final Object DEFAULT_VALUE = null;
private static final Object VALUE_1 = new Object();
@Test
public void testConstructor_NoArguments() {
final ObjectProperty<Object> v = new SimpleObjectProperty<Object>();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
}
@Test
public void testConstructor_InitialValue() {
final ObjectProperty<Object> v1 = new SimpleObjectProperty<Object>(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get());
final ObjectProperty<Object> v2 = new SimpleObjectProperty<Object>(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final ObjectProperty<Object> v = new SimpleObjectProperty<Object>(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
final ObjectProperty<Object> v2 = new SimpleObjectProperty<Object>(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final ObjectProperty<Object> v1 = new SimpleObjectProperty<Object>(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get());
final ObjectProperty<Object> v2 = new SimpleObjectProperty<Object>(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
final ObjectProperty<Object> v3 = new SimpleObjectProperty<Object>(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get());
final ObjectProperty<Object> v4 = new SimpleObjectProperty<Object>(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get());
}
}
