package test.com.sun.javafx.test.binding;
public final class BindingProxyRefImpl extends BindingProxy {
public BindingProxyRefImpl(final Class<?> typeClass,
final Class<?> observableValueClass,
final Class<?> writableValueClass,
final Class<?> variableClass,
final Class<?> propertyModelClass) {
super(typeClass,
new ObservableValueProxyRefImpl(observableValueClass),
new WritableValueProxyRefImpl(writableValueClass),
new VariableFactoryRefImpl(variableClass),
new PropertyModelProxyRefImpl(propertyModelClass));
}
public static BindingProxyRefImpl autoLookup(final String typeName) {
return new BindingProxyRefImpl(
ReflectionHelper.classForName("java.lang." + typeName),
ReflectionHelper.classForName("javafx.beans.value.Observable"
+ typeName + "Value"),
ReflectionHelper.classForName("javafx.beans.value.Writable"
+ typeName + "Value"),
ReflectionHelper.classForName("javafx.beans.property.Simple"
+ typeName + "Property"),
ReflectionHelper.classForName("javafx.beans.property."
+ typeName
+ "Property"));
}
}
