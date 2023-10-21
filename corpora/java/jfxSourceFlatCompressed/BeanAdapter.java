package com.sun.javafx.fxml;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.reflect.FieldUtil;
import com.sun.javafx.reflect.MethodUtil;
import com.sun.javafx.reflect.ReflectUtil;
public class BeanAdapter extends AbstractMap<String, Object> {
private final Object bean;
private static class MethodCache {
private final Map<String, List<Method>> methods;
private final MethodCache nextClassCache;
private MethodCache(Map<String, List<Method>> methods, MethodCache nextClassCache) {
this.methods = methods;
this.nextClassCache = nextClassCache;
}
private Method getMethod(String name, Class<?>... parameterTypes) {
List<Method> namedMethods = methods.get(name);
if (namedMethods != null) {
for (int i = 0; i < namedMethods.size(); i++) {
Method namedMethod = namedMethods.get(i);
if (namedMethod.getName().equals(name)
&& Arrays.equals(namedMethod.getParameterTypes(), parameterTypes)) {
return namedMethod;
}
}
}
return nextClassCache != null ? nextClassCache.getMethod(name, parameterTypes) : null;
}
}
private static final HashMap<Class<?>, MethodCache> globalMethodCache =
new HashMap<>();
private final MethodCache localCache;
public static final String GET_PREFIX = "get";
public static final String IS_PREFIX = "is";
public static final String SET_PREFIX = "set";
public static final String PROPERTY_SUFFIX = "Property";
public static final String VALUE_OF_METHOD_NAME = "valueOf";
public BeanAdapter(Object bean) {
this.bean = bean;
localCache = getClassMethodCache(bean.getClass());
}
private static MethodCache getClassMethodCache(final Class<?> type) {
if (type == Object.class) {
return null;
}
MethodCache classMethodCache;
synchronized (globalMethodCache) {
if ((classMethodCache = globalMethodCache.get(type)) != null) {
return classMethodCache;
}
Map<String, List<Method>> classMethods = new HashMap<>();
ReflectUtil.checkPackageAccess(type);
if (Modifier.isPublic(type.getModifiers())) {
@SuppressWarnings("removal")
final Method[] declaredMethods =
AccessController.doPrivileged(
new PrivilegedAction<Method[]>() {
@Override
public Method[] run() {
return type.getDeclaredMethods();
}
});
for (int i = 0; i < declaredMethods.length; i++) {
Method method = declaredMethods[i];
int modifiers = method.getModifiers();
if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
String name = method.getName();
List<Method> namedMethods = classMethods.get(name);
if (namedMethods == null) {
namedMethods = new ArrayList<>();
classMethods.put(name, namedMethods);
}
namedMethods.add(method);
}
}
}
MethodCache cache = new MethodCache(classMethods, getClassMethodCache(type.getSuperclass()));
globalMethodCache.put(type, cache);
return cache;
}
}
public Object getBean() {
return bean;
}
private Method getGetterMethod(String key) {
Method getterMethod = localCache.getMethod(getMethodName(GET_PREFIX, key));
if (getterMethod == null) {
getterMethod = localCache.getMethod(getMethodName(IS_PREFIX, key));
}
return getterMethod;
}
private Method getSetterMethod(String key) {
Class<?> type = getType(key);
if (type == null) {
throw new UnsupportedOperationException("Cannot determine type for property.");
}
return localCache.getMethod(getMethodName(SET_PREFIX, key), type);
}
private static String getMethodName(String prefix, String key) {
return prefix + Character.toUpperCase(key.charAt(0)) + key.substring(1);
}
@Override
public Object get(Object key) {
if (key == null) {
throw new NullPointerException();
}
return get(key.toString());
}
private Object get(String key) {
Method getterMethod = key.endsWith(PROPERTY_SUFFIX) ? localCache.getMethod(key) : getGetterMethod(key);
Object value;
if (getterMethod != null) {
try {
value = ModuleHelper.invoke(getterMethod, bean, (Object[]) null);
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
}
} else {
value = null;
}
return value;
}
@Override
public Object put(String key, Object value) {
if (key == null) {
throw new NullPointerException();
}
Method setterMethod = getSetterMethod(key);
if (setterMethod == null) {
throw new PropertyNotFoundException("Property \"" + key + "\" does not exist"
+ " or is read-only.");
}
try {
ModuleHelper.invoke(setterMethod, bean, new Object[] { coerce(value, getType(key)) });
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
}
return null;
}
@Override
public boolean containsKey(Object key) {
if (key == null) {
throw new NullPointerException();
}
return getType(key.toString()) != null;
}
@Override
public Set<Entry<String, Object>> entrySet() {
throw new UnsupportedOperationException();
}
public boolean isReadOnly(String key) {
if (key == null) {
throw new NullPointerException();
}
return getSetterMethod(key) == null;
}
@SuppressWarnings("unchecked")
public <T> ObservableValue<T> getPropertyModel(String key) {
if (key == null) {
throw new NullPointerException();
}
return (ObservableValue<T>)get(key + BeanAdapter.PROPERTY_SUFFIX);
}
public Class<?> getType(String key) {
if (key == null) {
throw new NullPointerException();
}
Method getterMethod = getGetterMethod(key);
return (getterMethod == null) ? null : getterMethod.getReturnType();
}
public Type getGenericType(String key) {
if (key == null) {
throw new NullPointerException();
}
Method getterMethod = getGetterMethod(key);
return (getterMethod == null) ? null : getterMethod.getGenericReturnType();
}
@Override
public boolean equals(Object object) {
boolean equals = false;
if (object instanceof BeanAdapter) {
BeanAdapter beanAdapter = (BeanAdapter)object;
equals = (bean == beanAdapter.bean);
}
return equals;
}
@Override
public int hashCode() {
return (bean == null) ? -1 : bean.hashCode();
}
@SuppressWarnings("unchecked")
public static <T> T coerce(Object value, Class<? extends T> type) {
if (type == null) {
throw new NullPointerException();
}
Object coercedValue = null;
if (value == null) {
coercedValue = null;
} else if (type.isAssignableFrom(value.getClass())) {
coercedValue = value;
} else if (type == Boolean.class
|| type == Boolean.TYPE) {
coercedValue = Boolean.valueOf(value.toString());
} else if (type == Character.class
|| type == Character.TYPE) {
coercedValue = value.toString().charAt(0);
} else if (type == Byte.class
|| type == Byte.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).byteValue();
} else {
coercedValue = Byte.valueOf(value.toString());
}
} else if (type == Short.class
|| type == Short.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).shortValue();
} else {
coercedValue = Short.valueOf(value.toString());
}
} else if (type == Integer.class
|| type == Integer.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).intValue();
} else {
coercedValue = Integer.valueOf(value.toString());
}
} else if (type == Long.class
|| type == Long.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).longValue();
} else {
coercedValue = Long.valueOf(value.toString());
}
} else if (type == BigInteger.class) {
if (value instanceof Number) {
coercedValue = BigInteger.valueOf(((Number)value).longValue());
} else {
coercedValue = new BigInteger(value.toString());
}
} else if (type == Float.class
|| type == Float.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).floatValue();
} else {
coercedValue = Float.valueOf(value.toString());
}
} else if (type == Double.class
|| type == Double.TYPE) {
if (value instanceof Number) {
coercedValue = ((Number)value).doubleValue();
} else {
coercedValue = Double.valueOf(value.toString());
}
} else if (type == Number.class) {
String number = value.toString();
if (number.contains(".")) {
coercedValue = Double.valueOf(number);
} else {
coercedValue = Long.valueOf(number);
}
} else if (type == BigDecimal.class) {
if (value instanceof Number) {
coercedValue = BigDecimal.valueOf(((Number)value).doubleValue());
} else {
coercedValue = new BigDecimal(value.toString());
}
} else if (type == Class.class) {
try {
final String className = value.toString();
ReflectUtil.checkPackageAccess(className);
final ClassLoader cl = Thread.currentThread().getContextClassLoader();
coercedValue = Class.forName(
className,
false,
cl);
} catch (ClassNotFoundException exception) {
throw new IllegalArgumentException(exception);
}
} else {
Class<?> valueType = value.getClass();
Method valueOfMethod = null;
while (valueOfMethod == null
&& valueType != null) {
try {
ReflectUtil.checkPackageAccess(type);
valueOfMethod = type.getDeclaredMethod(VALUE_OF_METHOD_NAME, valueType);
} catch (NoSuchMethodException exception) {
}
if (valueOfMethod == null) {
valueType = valueType.getSuperclass();
}
}
if (valueOfMethod == null) {
throw new IllegalArgumentException("Unable to coerce " + value + " to " + type + ".");
}
if (type.isEnum()
&& value instanceof String
&& Character.isLowerCase(((String)value).charAt(0))) {
value = toAllCaps((String)value);
}
try {
coercedValue = ModuleHelper.invoke(valueOfMethod, null, new Object[] { value });
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
} catch (SecurityException exception) {
throw new RuntimeException(exception);
}
}
return (T)coercedValue;
}
@SuppressWarnings("unchecked")
public static <T> T get(Object target, Class<?> sourceType, String key) {
T value = null;
Class<?> targetType = target.getClass();
Method getterMethod = getStaticGetterMethod(sourceType, key, targetType);
if (getterMethod != null) {
try {
value = (T) ModuleHelper.invoke(getterMethod, null, new Object[] { target } );
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
}
}
return value;
}
public static void put(Object target, Class<?> sourceType, String key, Object value) {
Class<?> targetType = target.getClass();
Method setterMethod = null;
if (value != null) {
setterMethod = getStaticSetterMethod(sourceType, key, value.getClass(), targetType);
}
if (setterMethod == null) {
Class<?> propertyType = getType(sourceType, key, targetType);
if (propertyType != null) {
setterMethod = getStaticSetterMethod(sourceType, key, propertyType, targetType);
value = coerce(value, propertyType);
}
}
if (setterMethod == null) {
throw new PropertyNotFoundException("Static property \"" + key + "\" does not exist"
+ " or is read-only.");
}
try {
ModuleHelper.invoke(setterMethod, null, new Object[] { target, value });
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
}
}
public static boolean isDefined(Class<?> sourceType, String key, Class<?> targetType) {
return (getStaticGetterMethod(sourceType, key, targetType) != null);
}
public static Class<?> getType(Class<?> sourceType, String key, Class<?> targetType) {
Method getterMethod = getStaticGetterMethod(sourceType, key, targetType);
return (getterMethod == null) ? null : getterMethod.getReturnType();
}
public static Type getGenericType(Class<?> sourceType, String key, Class<?> targetType) {
Method getterMethod = getStaticGetterMethod(sourceType, key, targetType);
return (getterMethod == null) ? null : getterMethod.getGenericReturnType();
}
public static Class<?> getListItemType(Type listType) {
Type itemType = getGenericListItemType(listType);
if (itemType instanceof ParameterizedType) {
itemType = ((ParameterizedType)itemType).getRawType();
}
return (Class<?>)itemType;
}
public static Class<?> getMapValueType(Type mapType) {
Type valueType = getGenericMapValueType(mapType);
if (valueType instanceof ParameterizedType) {
valueType = ((ParameterizedType)valueType).getRawType();
}
return (Class<?>)valueType;
}
public static Type getGenericListItemType(Type listType) {
Type itemType = null;
Type parentType = listType;
while (parentType != null) {
if (parentType instanceof ParameterizedType) {
ParameterizedType parameterizedType = (ParameterizedType)parentType;
Class<?> rawType = (Class<?>)parameterizedType.getRawType();
if (List.class.isAssignableFrom(rawType)) {
itemType = parameterizedType.getActualTypeArguments()[0];
}
break;
}
Class<?> classType = (Class<?>)parentType;
Type[] genericInterfaces = classType.getGenericInterfaces();
for (int i = 0; i < genericInterfaces.length; i++) {
Type genericInterface = genericInterfaces[i];
if (genericInterface instanceof ParameterizedType) {
ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
Class<?> interfaceType = (Class<?>)parameterizedType.getRawType();
if (List.class.isAssignableFrom(interfaceType)) {
itemType = parameterizedType.getActualTypeArguments()[0];
break;
}
}
}
if (itemType != null) {
break;
}
parentType = classType.getGenericSuperclass();
}
if (itemType != null && itemType instanceof TypeVariable<?>) {
itemType = Object.class;
}
return itemType;
}
public static Type getGenericMapValueType(Type mapType) {
Type valueType = null;
Type parentType = mapType;
while (parentType != null) {
if (parentType instanceof ParameterizedType) {
ParameterizedType parameterizedType = (ParameterizedType)parentType;
Class<?> rawType = (Class<?>)parameterizedType.getRawType();
if (Map.class.isAssignableFrom(rawType)) {
valueType = parameterizedType.getActualTypeArguments()[1];
}
break;
}
Class<?> classType = (Class<?>)parentType;
Type[] genericInterfaces = classType.getGenericInterfaces();
for (int i = 0; i < genericInterfaces.length; i++) {
Type genericInterface = genericInterfaces[i];
if (genericInterface instanceof ParameterizedType) {
ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
Class<?> interfaceType = (Class<?>)parameterizedType.getRawType();
if (Map.class.isAssignableFrom(interfaceType)) {
valueType = parameterizedType.getActualTypeArguments()[1];
break;
}
}
}
if (valueType != null) {
break;
}
parentType = classType.getGenericSuperclass();
}
if (valueType != null && valueType instanceof TypeVariable<?>) {
valueType = Object.class;
}
return valueType;
}
public static Object getConstantValue(Class<?> type, String name) {
if (type == null) {
throw new IllegalArgumentException();
}
if (name == null) {
throw new IllegalArgumentException();
}
Field field;
try {
field = FieldUtil.getField(type, name);
} catch (NoSuchFieldException exception) {
throw new IllegalArgumentException(exception);
}
int fieldModifiers = field.getModifiers();
if ((fieldModifiers & Modifier.STATIC) == 0
|| (fieldModifiers & Modifier.FINAL) == 0) {
throw new IllegalArgumentException("Field is not a constant.");
}
Object value;
try {
value = field.get(null);
} catch (IllegalAccessException exception) {
throw new IllegalArgumentException(exception);
}
return value;
}
private static Method getStaticGetterMethod(Class<?> sourceType, String key,
Class<?> targetType) {
if (sourceType == null) {
throw new NullPointerException();
}
if (key == null) {
throw new NullPointerException();
}
Method method = null;
if (targetType != null) {
key = Character.toUpperCase(key.charAt(0)) + key.substring(1);
String getMethodName = GET_PREFIX + key;
String isMethodName = IS_PREFIX + key;
try {
method = MethodUtil.getMethod(sourceType, getMethodName, new Class[] { targetType });
} catch (NoSuchMethodException exception) {
}
if (method == null) {
try {
method = MethodUtil.getMethod(sourceType, isMethodName, new Class[] { targetType });
} catch (NoSuchMethodException exception) {
}
}
if (method == null) {
Class<?>[] interfaces = targetType.getInterfaces();
for (int i = 0; i < interfaces.length; i++) {
try {
method = MethodUtil.getMethod(sourceType, getMethodName, new Class[] { interfaces[i] });
} catch (NoSuchMethodException exception) {
}
if (method == null) {
try {
method = MethodUtil.getMethod(sourceType, isMethodName, new Class[] { interfaces[i] });
} catch (NoSuchMethodException exception) {
}
}
if (method != null) {
break;
}
}
}
if (method == null) {
method = getStaticGetterMethod(sourceType, key, targetType.getSuperclass());
}
}
return method;
}
private static Method getStaticSetterMethod(Class<?> sourceType, String key,
Class<?> valueType, Class<?> targetType) {
if (sourceType == null) {
throw new NullPointerException();
}
if (key == null) {
throw new NullPointerException();
}
if (valueType == null) {
throw new NullPointerException();
}
Method method = null;
if (targetType != null) {
key = Character.toUpperCase(key.charAt(0)) + key.substring(1);
String setMethodName = SET_PREFIX + key;
try {
method = MethodUtil.getMethod(sourceType, setMethodName, new Class[] { targetType, valueType });
} catch (NoSuchMethodException exception) {
}
if (method == null) {
Class<?>[] interfaces = targetType.getInterfaces();
for (int i = 0; i < interfaces.length; i++) {
try {
method = MethodUtil.getMethod(sourceType, setMethodName, new Class[] { interfaces[i], valueType });
} catch (NoSuchMethodException exception) {
}
if (method != null) {
break;
}
}
}
if (method == null) {
method = getStaticSetterMethod(sourceType, key, valueType, targetType.getSuperclass());
}
}
return method;
}
private static String toAllCaps(String value) {
if (value == null) {
throw new NullPointerException();
}
StringBuilder allCapsBuilder = new StringBuilder();
for (int i = 0, n = value.length(); i < n; i++) {
char c = value.charAt(i);
if (Character.isUpperCase(c)) {
allCapsBuilder.append('_');
}
allCapsBuilder.append(Character.toUpperCase(c));
}
return allCapsBuilder.toString();
}
}
