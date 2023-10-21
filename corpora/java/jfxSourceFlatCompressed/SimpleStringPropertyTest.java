package test.javafx.beans.property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleStringPropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final String DEFAULT_VALUE = null;
private static final String VALUE_1 = "Hello World";
@Test
public void testConstructor_NoArguments() {
final StringProperty v = new SimpleStringProperty();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
}
@Test
public void testConstructor_InitialValue() {
final StringProperty v1 = new SimpleStringProperty(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get());
final StringProperty v2 = new SimpleStringProperty(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final StringProperty v = new SimpleStringProperty(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
final StringProperty v2 = new SimpleStringProperty(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final StringProperty v1 = new SimpleStringProperty(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get());
final StringProperty v2 = new SimpleStringProperty(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
final StringProperty v3 = new SimpleStringProperty(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get());
final StringProperty v4 = new SimpleStringProperty(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get());
}
}
