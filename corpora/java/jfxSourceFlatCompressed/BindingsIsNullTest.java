package test.javafx.binding;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.Before;
import org.junit.Test;
public class BindingsIsNullTest {
private ObjectProperty<Object> oo;
private StringProperty os;
private InvalidationListenerMock observer;
@Before
public void setUp() {
oo = new SimpleObjectProperty<Object>();
os = new SimpleStringProperty();
observer = new InvalidationListenerMock();
}
@Test
public void test_Object_IsNull() {
final BooleanBinding binding = Bindings.isNull(oo);
binding.addListener(observer);
assertTrue(binding.get());
DependencyUtils.checkDependencies(binding.getDependencies(), oo);
observer.reset();
oo.set(new Object());
assertFalse(binding.get());
observer.check(binding, 1);
oo.set(null);
assertTrue(binding.get());
observer.check(binding, 1);
}
@Test
public void test_Object_IsNotNull() {
final BooleanBinding binding = Bindings.isNotNull(oo);
binding.addListener(observer);
assertFalse(binding.get());
DependencyUtils.checkDependencies(binding.getDependencies(), oo);
observer.reset();
oo.set(new Object());
assertTrue(binding.get());
observer.check(binding, 1);
oo.set(null);
assertFalse(binding.get());
observer.check(binding, 1);
}
@Test
public void test_String_IsNull() {
final BooleanBinding binding = Bindings.isNull(os);
binding.addListener(observer);
assertTrue(binding.get());
DependencyUtils.checkDependencies(binding.getDependencies(), os);
observer.reset();
os.set("Hello World");
assertFalse(binding.get());
observer.check(binding, 1);
os.set(null);
assertTrue(binding.get());
observer.check(binding, 1);
}
@Test
public void test_String_IsNotNull() {
final BooleanBinding binding = Bindings.isNotNull(os);
binding.addListener(observer);
assertFalse(binding.get());
DependencyUtils.checkDependencies(binding.getDependencies(), os);
observer.reset();
os.set("Hello World");
assertTrue(binding.get());
observer.check(binding, 1);
os.set(null);
assertFalse(binding.get());
observer.check(binding, 1);
}
@Test(expected=NullPointerException.class)
public void test_IsNull_NPE() {
Bindings.isNull(null);
}
@Test(expected=NullPointerException.class)
public void test_IsNotNull_NPE() {
Bindings.isNotNull(null);
}
}
