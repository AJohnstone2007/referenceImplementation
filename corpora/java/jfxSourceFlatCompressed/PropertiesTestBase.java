package test.com.sun.javafx.test;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import org.junit.Assert;
import org.junit.Test;
public abstract class PropertiesTestBase {
private final Configuration configuration;
public PropertiesTestBase(final Configuration configuration) {
this.configuration = configuration;
}
@Test
public void testGetBean() {
configuration.getBeanTest();
}
@Test
public void testGetName() {
configuration.getNameTest();
}
@Test
public void testBasicAccess() {
configuration.basicAccessTest();
}
@Test
public void testBinding() {
configuration.bindingTest();
}
public static Object[] config(final Object bean,
final String propertyName,
final Object propertyValue1,
final Object propertyValue2) {
return config(new Configuration(bean,
propertyName,
propertyValue1,
propertyValue2));
}
public static Object[] config(final Object bean,
final String propertyName,
final Object propertyValue1,
final Object propertyValue2,
final ValueComparator comparator) {
return config(new Configuration(bean,
propertyName,
propertyValue1,
propertyValue2,
comparator));
}
public static Object[] config(final Object beanA,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2) {
return config(new Configuration(beanA,
propertyAName,
propertyAValue1,
propertyAValue2,
propertyBName,
propertyBValue1,
propertyBValue2));
}
public static Object[] config(final Object beanA,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final Object beanB,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2) {
return config(new Configuration(beanA,
propertyAName,
propertyAValue1,
propertyAValue2,
beanB,
propertyBName,
propertyBValue1,
propertyBValue2));
}
public static Object[] config(final Object beanA,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final Object beanB,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2,
final ValueComparator propertyBComparator) {
return config(new Configuration(beanA,
propertyAName,
propertyAValue1,
propertyAValue2,
beanB,
propertyBName,
propertyBValue1,
propertyBValue2,
propertyBComparator));
}
public static Object[] config(final Configuration configuration) {
return new Object[] { configuration };
}
public static class Configuration {
private final Object beanA;
private final PropertyReference propertyAReference;
private final Object propertyAValue1;
private final Object propertyAValue2;
private final Object beanB;
private final PropertyReference propertyBReference;
private final Object propertyBValue1;
private final Object propertyBValue2;
private final ValueComparator propertyBComparator;
private boolean allowMultipleNotifications;
public Configuration(final Object bean,
final String propertyName,
final Object propertyValue1,
final Object propertyValue2) {
this(bean, propertyName, propertyValue1, propertyValue2,
bean, propertyName, propertyValue1, propertyValue2,
ValueComparator.DEFAULT);
}
public Configuration(final Object bean,
final String propertyName,
final Object propertyValue1,
final Object propertyValue2,
final ValueComparator valueComparator) {
this(bean, propertyName, propertyValue1, propertyValue2,
bean, propertyName, propertyValue1, propertyValue2,
valueComparator);
}
public Configuration(final Object bean,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2) {
this(bean, propertyAName, propertyAValue1, propertyAValue2,
bean, propertyBName, propertyBValue1, propertyBValue2,
ValueComparator.DEFAULT);
}
public Configuration(final Object beanA,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final Object beanB,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2) {
this(beanA, propertyAName, propertyAValue1, propertyAValue2,
beanB, propertyBName, propertyBValue1, propertyBValue2,
ValueComparator.DEFAULT);
}
public Configuration(final Object beanA,
final String propertyAName,
final Object propertyAValue1,
final Object propertyAValue2,
final Object beanB,
final String propertyBName,
final Object propertyBValue1,
final Object propertyBValue2,
final ValueComparator propertyBComparator) {
this.beanA = beanA;
this.propertyAReference = PropertyReference.createForBean(
beanA.getClass(),
propertyAName);
this.propertyAValue1 = propertyAValue1;
this.propertyAValue2 = propertyAValue2;
this.beanB = beanB;
this.propertyBReference = PropertyReference.createForBean(
beanB.getClass(),
propertyBName);
this.propertyBValue1 = propertyBValue1;
this.propertyBValue2 = propertyBValue2;
this.propertyBComparator = propertyBComparator;
}
public void setAllowMultipleNotifications(
final boolean allowMultipleNotifications) {
this.allowMultipleNotifications = allowMultipleNotifications;
}
public void getBeanTest() {
final ReadOnlyProperty<?> propertyA =
(ReadOnlyProperty<?>) BindingHelper.getPropertyModel(
beanA, propertyAReference);
final ReadOnlyProperty<?> propertyB =
(ReadOnlyProperty<?>) BindingHelper.getPropertyModel(
beanB, propertyBReference);
Assert.assertSame(beanA, propertyA.getBean());
Assert.assertSame(beanB, propertyB.getBean());
}
public void getNameTest() {
final ReadOnlyProperty<?> propertyA =
(ReadOnlyProperty<?>) BindingHelper.getPropertyModel(
beanA, propertyAReference);
final ReadOnlyProperty<?> propertyB =
(ReadOnlyProperty<?>) BindingHelper.getPropertyModel(
beanB, propertyBReference);
Assert.assertEquals(propertyAReference.getPropertyName(),
propertyA.getName());
Assert.assertEquals(propertyBReference.getPropertyName(),
propertyB.getName());
}
public void basicAccessTest() {
propertyAReference.setValue(beanA, propertyAValue1);
propertyBComparator.assertEquals(
propertyBValue1,
propertyBReference.getValue(beanB));
final ValueInvalidationListener valueInvalidationListener =
new ValueInvalidationListener(allowMultipleNotifications);
final ObservableValue observableValueB =
(ObservableValue) BindingHelper.getPropertyModel(
beanB, propertyBReference);
observableValueB.addListener(valueInvalidationListener);
propertyAReference.setValue(beanA, propertyAValue2);
valueInvalidationListener.assertCalled();
valueInvalidationListener.reset();
propertyBComparator.assertEquals(
propertyBValue2,
propertyBReference.getValue(beanB));
propertyAReference.setValue(beanA, propertyAValue2);
valueInvalidationListener.assertNotCalled();
observableValueB.removeListener(valueInvalidationListener);
propertyAReference.setValue(beanA, propertyAValue1);
propertyBComparator.assertEquals(
propertyBValue1,
propertyBReference.getValue(beanB));
valueInvalidationListener.assertNotCalled();
}
public void bindingTest() {
propertyAReference.setValue(beanA, propertyAValue1);
final Object firstVariable =
BindingHelper.createVariable(propertyAValue2);
BindingHelper.bind(beanA, propertyAReference, firstVariable);
propertyBComparator.assertEquals(
propertyBValue2,
propertyBReference.getValue(beanB));
final ValueInvalidationListener valueInvalidationListener =
new ValueInvalidationListener(allowMultipleNotifications);
final ObservableValue observableValue =
(ObservableValue) BindingHelper.getPropertyModel(
beanB, propertyBReference);
observableValue.addListener(valueInvalidationListener);
BindingHelper.setWritableValue(propertyAReference.getValueType(),
firstVariable, propertyAValue1);
valueInvalidationListener.assertCalled();
valueInvalidationListener.reset();
propertyBComparator.assertEquals(
propertyBValue1,
propertyBReference.getValue(beanB));
final Object secondVariable =
BindingHelper.createVariable(propertyAValue2);
BindingHelper.bind(beanA, propertyAReference, secondVariable);
valueInvalidationListener.assertCalled();
valueInvalidationListener.reset();
propertyBComparator.assertEquals(
propertyBValue2,
propertyBReference.getValue(beanB));
BindingHelper.unbind(beanA, propertyAReference);
valueInvalidationListener.assertNotCalled();
BindingHelper.setWritableValue(propertyAReference.getValueType(),
secondVariable, propertyAValue1);
valueInvalidationListener.assertNotCalled();
propertyBComparator.assertEquals(
propertyBValue2,
propertyBReference.getValue(beanB));
propertyAReference.setValue(beanA, propertyAValue1);
valueInvalidationListener.assertCalled();
valueInvalidationListener.reset();
propertyBComparator.assertEquals(
propertyBValue1,
propertyBReference.getValue(beanB));
observableValue.removeListener(valueInvalidationListener);
}
}
private static final class ValueInvalidationListener
implements InvalidationListener {
private final boolean allowMultipleNotifications;
private int counter;
public ValueInvalidationListener(
final boolean allowMultipleNotifications) {
this.allowMultipleNotifications = allowMultipleNotifications;
}
public void reset() {
counter = 0;
}
public void assertCalled() {
if (counter == 0) {
Assert.fail("Listener has not been called!");
return;
}
if (!allowMultipleNotifications && (counter > 1)) {
Assert.fail("Listener called multiple times!");
}
}
public void assertNotCalled() {
if (counter != 0) {
Assert.fail("Listener has been called!");
return;
}
}
@Override
public void invalidated(final Observable valueModel) {
++counter;
}
}
}
