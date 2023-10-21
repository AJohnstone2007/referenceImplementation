package test.javafx.beans.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyObjectPropertyTest {
private static final Object DEFAULT = null;
@Before
public void setUp() throws Exception {
}
@Test
public void testToString() {
final ReadOnlyObjectProperty<Object> v1 = new ReadOnlyObjectPropertyStub(null, "");
assertEquals("ReadOnlyObjectProperty [value: " + DEFAULT + "]", v1.toString());
final ReadOnlyObjectProperty<Object> v2 = new ReadOnlyObjectPropertyStub(null, null);
assertEquals("ReadOnlyObjectProperty [value: " + DEFAULT + "]", v2.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyObjectProperty<Object> v3 = new ReadOnlyObjectPropertyStub(bean, name);
assertEquals("ReadOnlyObjectProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v3.toString());
final ReadOnlyObjectProperty<Object> v4 = new ReadOnlyObjectPropertyStub(bean, "");
assertEquals("ReadOnlyObjectProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v4.toString());
final ReadOnlyObjectProperty<Object> v5 = new ReadOnlyObjectPropertyStub(bean, null);
assertEquals("ReadOnlyObjectProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v5.toString());
final ReadOnlyObjectProperty<Object> v6 = new ReadOnlyObjectPropertyStub(null, name);
assertEquals("ReadOnlyObjectProperty [name: My name, value: " + DEFAULT + "]", v6.toString());
}
private static class ReadOnlyObjectPropertyStub extends ReadOnlyObjectProperty<Object> {
private final Object bean;
private final String name;
private ReadOnlyObjectPropertyStub(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
@Override public Object get() { return null; }
@Override
public void addListener(ChangeListener<? super Object> listener) {
}
@Override
public void removeListener(ChangeListener<? super Object> listener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
}
}
