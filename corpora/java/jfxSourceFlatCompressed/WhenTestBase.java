package test.javafx.binding;
import static test.javafx.binding.DependencyUtils.checkDependencies;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import org.junit.Before;
import org.junit.Test;
public abstract class WhenTestBase<T, P extends WritableValue<T> & ObservableValue<T>> {
static final float EPSILON_FLOAT = 1e-6f;
static final double EPSILON_DOUBLE = 1e-10;
public abstract Binding<T>[] generatePropertyPropertyList(P p0, P[] properties);
public abstract Binding<T> generatePropertyProperty(P p0, P p1);
public abstract Binding<T>[] generatePropertyPrimitive(P op0, T op1);
public abstract Binding<T>[] generatePrimitiveProperty(T op0, P op1);
public abstract Binding<T>[] generatePrimitivePrimitive(T op0, T op1);
public abstract void check(T expected, Binding<T> binding);
final BooleanProperty cond = new SimpleBooleanProperty();
final P p0;
final P[] properties;
private final T v0;
private final T v1;
private final T v2;
private final T v3;
public WhenTestBase(T v0, T v1, T v2, T v3, P p0, P... properties) {
this.v0 = v0;
this.v1 = v1;
this.v2 = v2;
this.v3 = v3;
this.p0 = p0;
this.properties = properties;
}
@Before
public void setUp() {
cond.set(false);
p0.setValue(v0);
for (final P p : properties) {
p.setValue(v1);
}
}
@Test
public void test_expression_expression() {
final Binding<T>[] bindings = generatePropertyPropertyList(p0, properties);
final int n = bindings.length;
for (int i=0; i<n; i++) {
final Binding<T> binding = bindings[i];
final P p1 = properties[i];
checkDependencies(binding.getDependencies(), cond, p0, p1);
check(p1.getValue(), binding);
p0.setValue(v2);
check(p1.getValue(), binding);
p1.setValue(v3);
check(p1.getValue(), binding);
cond.set(true);
check(v2, binding);
p0.setValue(v0);
check(v0, binding);
p1.setValue(v1);
check(v0, binding);
cond.set(false);
check(p1.getValue(), binding);
}
}
@Test
public void test_expression_primitive() {
final Binding<T>[] bindings = generatePropertyPrimitive(p0, v1);
for (final Binding<T> binding : bindings) {
checkDependencies(binding.getDependencies(), cond, p0);
check(v1, binding);
p0.setValue(v2);
check(v1, binding);
cond.set(true);
check(v2, binding);
p0.setValue(v0);
check(v0, binding);
cond.set(false);
check(v1, binding);
}
}
@Test
public void test_primitive_expression() {
final Binding<T>[] bindings = generatePrimitiveProperty(v1, p0);
for (final Binding<T> binding : bindings) {
checkDependencies(binding.getDependencies(), cond, p0);
check(v0, binding);
p0.setValue(v3);
check(v3, binding);
cond.set(true);
check(v1, binding);
p0.setValue(v0);
check(v1, binding);
cond.set(false);
check(v0, binding);
}
}
@Test
public void test_primitive_primitive() {
final Binding<T>[] bindings = generatePrimitivePrimitive(v0, v1);
for (final Binding<T> binding : bindings) {
checkDependencies(binding.getDependencies(), cond);
check(v1, binding);
cond.set(true);
check(v0, binding);
cond.set(false);
check(v1, binding);
}
}
@Test(expected=NullPointerException.class)
public void test_Null() {
Bindings.when(null);
}
@Test(expected=NullPointerException.class)
public void test_Null_expression() {
generatePropertyProperty(null, p0);
}
@Test(expected=NullPointerException.class)
public void test_expression_Null() {
generatePropertyProperty(p0, null);
}
@Test(expected=NullPointerException.class)
public void test_Null_primitive() {
generatePropertyPrimitive(null, v1);
}
@Test(expected=NullPointerException.class)
public void test_primitive_Null() {
generatePrimitiveProperty(v0, null);
}
}
