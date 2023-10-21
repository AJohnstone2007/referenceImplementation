package test.javafx.beans.property;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleLongPropertyTest {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private static final long DEFAULT_VALUE = 0L;
private static final long VALUE_1 = -123456789012345L;
@Test
public void testConstructor_NoArguments() {
final LongProperty v = new SimpleLongProperty();
assertEquals(DEFAULT_BEAN, v.getBean());
assertEquals(DEFAULT_NAME, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
}
@Test
public void testConstructor_InitialValue() {
final LongProperty v1 = new SimpleLongProperty(VALUE_1);
assertEquals(DEFAULT_BEAN, v1.getBean());
assertEquals(DEFAULT_NAME, v1.getName());
assertEquals(VALUE_1, v1.get());
final LongProperty v2 = new SimpleLongProperty(DEFAULT_VALUE);
assertEquals(DEFAULT_BEAN, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final LongProperty v = new SimpleLongProperty(bean, name);
assertEquals(bean, v.getBean());
assertEquals(name, v.getName());
assertEquals(DEFAULT_VALUE, v.get());
final LongProperty v2 = new SimpleLongProperty(bean, null);
assertEquals(bean, v2.getBean());
assertEquals(DEFAULT_NAME, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final LongProperty v1 = new SimpleLongProperty(bean, name, VALUE_1);
assertEquals(bean, v1.getBean());
assertEquals(name, v1.getName());
assertEquals(VALUE_1, v1.get());
final LongProperty v2 = new SimpleLongProperty(bean, name, DEFAULT_VALUE);
assertEquals(bean, v2.getBean());
assertEquals(name, v2.getName());
assertEquals(DEFAULT_VALUE, v2.get());
final LongProperty v3 = new SimpleLongProperty(bean, null, VALUE_1);
assertEquals(bean, v3.getBean());
assertEquals(DEFAULT_NAME, v3.getName());
assertEquals(VALUE_1, v3.get());
final LongProperty v4 = new SimpleLongProperty(bean, null, DEFAULT_VALUE);
assertEquals(bean, v4.getBean());
assertEquals(DEFAULT_NAME, v4.getName());
assertEquals(DEFAULT_VALUE, v4.get());
}
}
