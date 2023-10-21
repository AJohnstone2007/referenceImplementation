package test.javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ReadOnlyListPropertyTest {
private static final Object DEFAULT = null;
@Before
public void setUp() throws Exception {
}
@Test
public void testToString() {
final ReadOnlyListProperty<Object> v1 = new ReadOnlyListPropertyStub(null, "");
assertEquals("ReadOnlyListProperty [value: " + DEFAULT + "]", v1.toString());
final ReadOnlyListProperty<Object> v2 = new ReadOnlyListPropertyStub(null, null);
assertEquals("ReadOnlyListProperty [value: " + DEFAULT + "]", v2.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyListProperty<Object> v3 = new ReadOnlyListPropertyStub(bean, name);
assertEquals("ReadOnlyListProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v3.toString());
final ReadOnlyListProperty<Object> v4 = new ReadOnlyListPropertyStub(bean, "");
assertEquals("ReadOnlyListProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v4.toString());
final ReadOnlyListProperty<Object> v5 = new ReadOnlyListPropertyStub(bean, null);
assertEquals("ReadOnlyListProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v5.toString());
final ReadOnlyListProperty<Object> v6 = new ReadOnlyListPropertyStub(null, name);
assertEquals("ReadOnlyListProperty [name: My name, value: " + DEFAULT + "]", v6.toString());
}
private static class ReadOnlyListPropertyStub extends ReadOnlyListProperty<Object> {
private final Object bean;
private final String name;
private ReadOnlyListPropertyStub(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
@Override public ObservableList<Object> get() { return null; }
@Override
public void addListener(ChangeListener<? super ObservableList<Object>> listener) {
}
@Override
public void removeListener(ChangeListener<? super ObservableList<Object>> listener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
@Override
public void addListener(ListChangeListener<? super Object> listChangeListener) {
}
@Override
public void removeListener(ListChangeListener<? super Object> listChangeListener) {
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
