package test.javafx.binding;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
public class When_Object_Test extends WhenTestBase<Object, ObjectProperty<Object>> {
@SuppressWarnings("unchecked")
public When_Object_Test() {
super(
new Object(), new Object(), new Object(), new Object(),
new SimpleObjectProperty<Object>(), new SimpleObjectProperty<Object>()
);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Object>[] generatePropertyPropertyList(ObjectProperty<Object> p0, ObjectProperty<Object>[] probs) {
return new Binding[] {
Bindings.when(cond).then(p0).otherwise(probs[0])
};
}
@Override
public Binding<Object> generatePropertyProperty(ObjectProperty<Object> op0, ObjectProperty<Object> op1) {
return Bindings.when(cond).then(op0).otherwise(op1);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Object>[] generatePropertyPrimitive(ObjectProperty<Object> op0, Object op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Object>[] generatePrimitiveProperty(Object op0, ObjectProperty<Object> op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Object>[] generatePrimitivePrimitive(Object op0, Object op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@Override
public void check(Object expected, Binding<Object> binding) {
org.junit.Assert.assertEquals(expected, binding.getValue());
}
}
