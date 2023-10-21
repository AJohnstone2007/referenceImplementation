package test.javafx.binding;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class When_String_Test extends WhenTestBase<String, StringProperty> {
public When_String_Test() {
super(
null, "Hello", "Hello World", "",
new SimpleStringProperty(), new SimpleStringProperty()
);
}
@SuppressWarnings("unchecked")
@Override
public Binding<String>[] generatePropertyPropertyList(StringProperty p0, StringProperty[] probs) {
return new Binding[] {
Bindings.when(cond).then(p0).otherwise(probs[0])
};
}
@Override
public Binding<String> generatePropertyProperty(StringProperty op0, StringProperty op1) {
return Bindings.when(cond).then(op0).otherwise(op1);
}
@SuppressWarnings("unchecked")
@Override
public Binding<String>[] generatePropertyPrimitive(StringProperty op0, String op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<String>[] generatePrimitiveProperty(String op0, StringProperty op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@SuppressWarnings("unchecked")
@Override
public Binding<String>[] generatePrimitivePrimitive(String op0, String op1) {
return new Binding[] {
Bindings.when(cond).then(op0).otherwise(op1)
};
}
@Override
public void check(String expected, Binding<String> binding) {
org.junit.Assert.assertEquals(expected, binding.getValue());
}
}
