package test.javafx.binding;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
public class When_Boolean_Test extends WhenTestBase<Boolean, BooleanProperty> {
public When_Boolean_Test() {
super(
false, true, true, false,
new SimpleBooleanProperty(), new SimpleBooleanProperty()
);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Boolean>[] generatePropertyPropertyList(BooleanProperty p0, BooleanProperty[] probs) {
return new Binding[] {
Bindings.when(cond).then(p0).otherwise(probs[0])
};
}
@Override
public Binding<Boolean> generatePropertyProperty(BooleanProperty op0, BooleanProperty op1) {
return Bindings.when(cond).then(op0).otherwise(op1);
}
@SuppressWarnings("unchecked")
@Override
public Binding<Boolean>[] generatePropertyPrimitive(BooleanProperty op0, Boolean op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Boolean>[] generatePrimitiveProperty(Boolean op0, BooleanProperty op1) {
return new Binding[] {
Bindings.when(cond).then(op0.booleanValue()).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<Boolean>[] generatePrimitivePrimitive(Boolean op0, Boolean op1) {
return new Binding[] {
Bindings.when(cond).then(op0.booleanValue()).otherwise(op1)
};
}
@Override
public void check(Boolean expected, Binding<Boolean> binding) {
org.junit.Assert.assertEquals(expected, binding.getValue());
}
}
