package test.javafx.binding;
import static org.junit.Assert.assertEquals;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableNumberValue;
public class When_Long_Test extends WhenTestBase<Number, Property<Number>> {
@SuppressWarnings("unchecked")
public When_Long_Test() {
super(
-1L, 0L, Long.MIN_VALUE, Long.MAX_VALUE,
new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleFloatProperty(), new SimpleLongProperty(), new SimpleIntegerProperty()
);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Number>[] generatePropertyPropertyList(Property<Number> p0, Property<Number>[] props) {
final int n = props.length;
final Binding<Number>[] result = new Binding[n];
for (int i=0; i<n; i++) {
result[i] = Bindings.when(cond).then((ObservableNumberValue)p0).otherwise((ObservableNumberValue)props[i]);
}
return result;
}
@Override
public Binding<Number> generatePropertyProperty(Property<Number> op0, Property<Number> op1) {
return Bindings.when(cond).then((ObservableNumberValue)op0).otherwise((ObservableNumberValue)op1);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Number>[] generatePropertyPrimitive(Property<Number> op0, Number op1) {
final ObservableNumberValue p0 = (ObservableNumberValue)op0;
return new Binding[] {
Bindings.when(cond).then(p0).otherwise(op1.doubleValue()),
Bindings.when(cond).then(p0).otherwise(op1.floatValue()),
Bindings.when(cond).then(p0).otherwise(op1.longValue()),
Bindings.when(cond).then(p0).otherwise(op1.intValue())
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Number>[] generatePrimitiveProperty(Number op0, Property<Number> op1) {
final ObservableNumberValue p1 = (ObservableNumberValue)op1;
return new Binding[] {
Bindings.when(cond).then(op0.doubleValue()).otherwise(p1),
Bindings.when(cond).then(op0.floatValue()).otherwise(p1),
Bindings.when(cond).then(op0.longValue()).otherwise(p1),
Bindings.when(cond).then(op0.intValue()).otherwise(p1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Number>[] generatePrimitivePrimitive(Number op0, Number op1) {
return new Binding[] {
Bindings.when(cond).then(op0.longValue()).otherwise(op1.doubleValue()),
Bindings.when(cond).then(op0.longValue()).otherwise(op1.floatValue()),
Bindings.when(cond).then(op0.longValue()).otherwise(op1.longValue()),
Bindings.when(cond).then(op0.longValue()).otherwise(op1.intValue())
};
}
@Override
public void check(Number expected, Binding<Number> binding) {
assertEquals(expected.longValue(), binding.getValue().longValue());
}
}
