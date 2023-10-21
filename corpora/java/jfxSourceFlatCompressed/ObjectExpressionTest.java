package test.javafx.binding.expression;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValueStub;
import test.javafx.binding.DependencyUtils;
import javafx.collections.FXCollections;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ObjectExpressionTest {
private Object data1;
private Object data2;
private ObjectProperty<Object> op1;
private ObjectProperty<Object> op2;
@Before
public void setUp() {
data1 = new Object();
data2 = new Object();
op1 = new SimpleObjectProperty<Object>(data1);
op2 = new SimpleObjectProperty<Object>(data2);
}
@Test
public void testEquals() {
BooleanBinding binding = op1.isEqualTo(op1);
assertEquals(true, binding.get());
binding = op1.isEqualTo(op2);
assertEquals(false, binding.get());
binding = op1.isEqualTo(data1);
assertEquals(true, binding.get());
binding = op1.isEqualTo(data2);
assertEquals(false, binding.get());
}
@Test
public void testNotEquals() {
BooleanBinding binding = op1.isNotEqualTo(op1);
assertEquals(false, binding.get());
binding = op1.isNotEqualTo(op2);
assertEquals(true, binding.get());
binding = op1.isNotEqualTo(data1);
assertEquals(false, binding.get());
binding = op1.isNotEqualTo(data2);
assertEquals(true, binding.get());
}
@Test
public void testIsNull() {
BooleanBinding binding = op1.isNull();
assertEquals(false, binding.get());
ObjectProperty<Object> op3 = new SimpleObjectProperty<Object>(null);
binding = op3.isNull();
assertEquals(true, binding.get());
}
@Test
public void testIsNotNull() {
BooleanBinding binding = op1.isNotNull();
assertEquals(true, binding.get());
ObjectProperty<Object> op3 = new SimpleObjectProperty<Object>(null);
binding = op3.isNotNull();
assertEquals(false, binding.get());
}
@Test
public void testFactory() {
final ObservableObjectValueStub<Object> valueModel = new ObservableObjectValueStub<Object>();
final ObjectExpression<Object> exp = ObjectExpression.objectExpression(valueModel);
assertTrue(exp instanceof ObjectBinding);
assertEquals(FXCollections.singletonObservableList(valueModel), ((ObjectBinding<Object>)exp).getDependencies());
assertEquals(null, exp.get());
valueModel.set(data1);
assertEquals(data1, exp.get());
valueModel.set(data2);
assertEquals(data2, exp.get());
assertEquals(op1, ObjectExpression.objectExpression(op1));
}
@Test(expected=NullPointerException.class)
public void testFactory_Null() {
ObjectExpression.objectExpression(null);
}
@Test
public void testAsString() {
final StringBinding binding = op1.asString();
DependencyUtils.checkDependencies(binding.getDependencies(), op1);
assertEquals(op1.get().toString(), binding.get());
op1.set(new Object() {
@Override
public String toString() {
return "foo";
}
});
assertEquals("foo", binding.get());
}
@Test
public void testAsString_Format() {
final StringBinding binding = op1.asString("%h");
op1.set(new Object() {
@Override
public String toString() {
return "foo";
}
});
DependencyUtils.checkDependencies(binding.getDependencies(), op1);
assertEquals(Integer.toHexString(op1.get().hashCode()), binding.get());
}
}
