package test.javafx.beans.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueStub;
import javafx.collections.FXCollections;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyBooleanPropertyTest {
private static final boolean DEFAULT = false;
@Before
public void setUp() throws Exception {
}
@Test
public void testToString() {
final ReadOnlyBooleanProperty v1 = new ReadOnlyBooleanPropertyStub(null, "");
assertEquals("ReadOnlyBooleanProperty [value: " + DEFAULT + "]", v1.toString());
final ReadOnlyBooleanProperty v2 = new ReadOnlyBooleanPropertyStub(null, null);
assertEquals("ReadOnlyBooleanProperty [value: " + DEFAULT + "]", v2.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyBooleanProperty v3 = new ReadOnlyBooleanPropertyStub(bean, name);
assertEquals("ReadOnlyBooleanProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v3.toString());
final ReadOnlyBooleanProperty v4 = new ReadOnlyBooleanPropertyStub(bean, "");
assertEquals("ReadOnlyBooleanProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v4.toString());
final ReadOnlyBooleanProperty v5 = new ReadOnlyBooleanPropertyStub(bean, null);
assertEquals("ReadOnlyBooleanProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v5.toString());
final ReadOnlyBooleanProperty v6 = new ReadOnlyBooleanPropertyStub(null, name);
assertEquals("ReadOnlyBooleanProperty [name: My name, value: " + DEFAULT + "]", v6.toString());
}
@Test
public void testAsObject() {
final ReadOnlyBooleanWrapper valueModel = new ReadOnlyBooleanWrapper();
final ReadOnlyObjectProperty<Boolean> exp = valueModel.getReadOnlyProperty().asObject();
assertEquals(Boolean.FALSE, exp.get());
valueModel.set(true);
assertEquals(Boolean.TRUE, exp.get());
valueModel.set(false);
assertEquals(Boolean.FALSE, exp.get());
}
@Test
public void testObjectToBoolean() {
final ReadOnlyObjectWrapper<Boolean> valueModel = new ReadOnlyObjectWrapper<Boolean>();
final ReadOnlyBooleanProperty exp = ReadOnlyBooleanProperty.readOnlyBooleanProperty(valueModel.getReadOnlyProperty());
assertEquals(false, exp.get());
valueModel.set(true);
assertEquals(true, exp.get());
valueModel.set(false);
assertEquals(false, exp.get());
}
private static class ReadOnlyBooleanPropertyStub extends ReadOnlyBooleanProperty {
private final Object bean;
private final String name;
private ReadOnlyBooleanPropertyStub(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
@Override public boolean get() { return false; }
@Override
public void addListener(ChangeListener<? super Boolean> listener) {
}
@Override
public void removeListener(ChangeListener<? super Boolean> listener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
}
}
