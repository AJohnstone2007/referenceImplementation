package test.com.sun.javafx.test;
import java.lang.reflect.Method;
import test.com.sun.javafx.test.binding.ReflectionHelper;
public final class PropertyReference {
private final String propertyName;
private final Class<?> valueType;
private final Method getterMethod;
private final Method setterMethod;
public PropertyReference(final String propertyName,
final Class<?> valueType,
final Method getterMethod,
final Method setterMethod) {
this.propertyName = propertyName;
this.valueType = valueType;
this.getterMethod = getterMethod;
this.setterMethod = setterMethod;
}
public String getPropertyName() {
return propertyName;
}
public Class<?> getValueType() {
return valueType;
}
public Object getValue(final Object object) {
if (getterMethod == null) {
throw new RuntimeException("No getter associated with "
+ propertyName + "!");
}
return ReflectionHelper.invokeMethod(object, getterMethod);
}
public void setValue(final Object object, final Object value) {
if (setterMethod == null) {
throw new RuntimeException("No setter associated with "
+ propertyName + "!");
}
ReflectionHelper.invokeMethod(object, setterMethod, value);
}
public static PropertyReference createForBean(final Class<?> beanClass,
final String propertyName) {
final String capitalizedPropertyName = capitalizeName(propertyName);
Method propertyGetterMethod;
try {
propertyGetterMethod = ReflectionHelper.getMethod(
beanClass,
"get" + capitalizedPropertyName);
} catch (final RuntimeException eget) {
try {
propertyGetterMethod = ReflectionHelper.getMethod(
beanClass,
"is" + capitalizedPropertyName);
} catch (final RuntimeException eis) {
throw new RuntimeException("Failed to obtain getter for "
+ propertyName + "!");
}
}
final Class<?> propertyValueType = propertyGetterMethod.getReturnType();
Method propertySetterMethod;
try {
propertySetterMethod = ReflectionHelper.getMethod(
beanClass,
"set" + capitalizedPropertyName,
propertyValueType);
} catch (final RuntimeException e) {
propertySetterMethod = null;
}
return new PropertyReference(
propertyName,
propertyValueType,
propertyGetterMethod,
propertySetterMethod);
}
private static String capitalizeName(final String input) {
return !input.isEmpty()
? Character.toUpperCase(input.charAt(0)) + input.substring(1)
: input;
}
}
