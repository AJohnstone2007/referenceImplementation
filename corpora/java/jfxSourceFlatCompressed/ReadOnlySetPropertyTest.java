package test.javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ReadOnlySetPropertyTest {
private static final Object DEFAULT = null;
@Before
public void setUp() throws Exception {
}
@Test
public void testToString() {
final ReadOnlySetProperty<Object> v1 = new ReadOnlySetPropertyStub(null, "");
assertEquals("ReadOnlySetProperty [value: " + DEFAULT + "]", v1.toString());
final ReadOnlySetProperty<Object> v2 = new ReadOnlySetPropertyStub(null, null);
assertEquals("ReadOnlySetProperty [value: " + DEFAULT + "]", v2.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlySetProperty<Object> v3 = new ReadOnlySetPropertyStub(bean, name);
assertEquals("ReadOnlySetProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v3.toString());
final ReadOnlySetProperty<Object> v4 = new ReadOnlySetPropertyStub(bean, "");
assertEquals("ReadOnlySetProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v4.toString());
final ReadOnlySetProperty<Object> v5 = new ReadOnlySetPropertyStub(bean, null);
assertEquals("ReadOnlySetProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v5.toString());
final ReadOnlySetProperty<Object> v6 = new ReadOnlySetPropertyStub(null, name);
assertEquals("ReadOnlySetProperty [name: My name, value: " + DEFAULT + "]", v6.toString());
}
private static class ReadOnlySetPropertyStub extends ReadOnlySetProperty<Object> {
private final Object bean;
private final String name;
private ReadOnlySetPropertyStub(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
@Override public ObservableSet<Object> get() { return null; }
@Override
public void addListener(ChangeListener<? super ObservableSet<Object>> listener) {
}
@Override
public void removeListener(ChangeListener<? super ObservableSet<Object>> listener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
@Override
public void addListener(SetChangeListener<? super Object> listChangeListener) {
}
@Override
public void removeListener(SetChangeListener<? super Object> listChangeListener) {
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
fail("Not in use");
return null;
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
fail("Not in use");
return null;
}
}
}
