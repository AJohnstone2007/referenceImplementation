package test.javafx.beans.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyDoublePropertyTest {
private static final double DEFAULT = 0.0;
private static final float EPSILON = 1e-6f;
@Before
public void setUp() throws Exception {
}
@Test
public void testToString() {
final ReadOnlyDoubleProperty v1 = new ReadOnlyDoublePropertyStub(null, "");
assertEquals("ReadOnlyDoubleProperty [value: " + DEFAULT + "]", v1.toString());
final ReadOnlyDoubleProperty v2 = new ReadOnlyDoublePropertyStub(null, null);
assertEquals("ReadOnlyDoubleProperty [value: " + DEFAULT + "]", v2.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyDoubleProperty v3 = new ReadOnlyDoublePropertyStub(bean, name);
assertEquals("ReadOnlyDoubleProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v3.toString());
final ReadOnlyDoubleProperty v4 = new ReadOnlyDoublePropertyStub(bean, "");
assertEquals("ReadOnlyDoubleProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v4.toString());
final ReadOnlyDoubleProperty v5 = new ReadOnlyDoublePropertyStub(bean, null);
assertEquals("ReadOnlyDoubleProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v5.toString());
final ReadOnlyDoubleProperty v6 = new ReadOnlyDoublePropertyStub(null, name);
assertEquals("ReadOnlyDoubleProperty [name: My name, value: " + DEFAULT + "]", v6.toString());
}
@Test
public void testAsObject() {
final ReadOnlyDoubleWrapper valueModel = new ReadOnlyDoubleWrapper();
final ReadOnlyObjectProperty<Double> exp = valueModel.getReadOnlyProperty().asObject();
assertEquals(0.0, exp.getValue(), EPSILON);
valueModel.set(-4354.3);
assertEquals(-4354.3, exp.getValue(), EPSILON);
valueModel.set(5e11);
assertEquals(5e11, exp.getValue(), EPSILON);
}
@Test
public void testObjectToDouble() {
final ReadOnlyObjectWrapper<Double> valueModel = new ReadOnlyObjectWrapper<Double>();
final ReadOnlyDoubleProperty exp = ReadOnlyDoubleProperty.readOnlyDoubleProperty(valueModel.getReadOnlyProperty());
assertEquals(0.0, exp.doubleValue(), EPSILON);
valueModel.set(-4354.3);
assertEquals(-4354.3, exp.doubleValue(), EPSILON);
valueModel.set(5e11);
assertEquals(5e11, exp.doubleValue(), EPSILON);
}
private static class ReadOnlyDoublePropertyStub extends ReadOnlyDoubleProperty {
private final Object bean;
private final String name;
private ReadOnlyDoublePropertyStub(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
@Override public double get() { return 0.0; }
@Override
public void addListener(ChangeListener<? super Number> listener) {
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
}
}
