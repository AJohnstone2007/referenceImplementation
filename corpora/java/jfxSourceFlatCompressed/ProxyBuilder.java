package com.sun.javafx.fxml.builder;
import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.ModuleHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javafx.beans.NamedArg;
import javafx.util.Builder;
import com.sun.javafx.reflect.ConstructorUtil;
import com.sun.javafx.reflect.ReflectUtil;
public class ProxyBuilder<T> extends AbstractMap<String, Object> implements Builder<T> {
private Class<?> type;
private final Map<Constructor, Map<String, AnnotationValue>> constructorsMap;
private final Map<String, Property> propertiesMap;
private final Set<Constructor> constructors;
private Set<String> propertyNames;
private boolean hasDefaultConstructor = false;
private Constructor defaultConstructor;
private static final String SETTER_PREFIX = "set";
private static final String GETTER_PREFIX = "get";
public ProxyBuilder(Class<?> tp) {
this.type = tp;
constructorsMap = new HashMap<>();
Constructor ctors[] = ConstructorUtil.getConstructors(type);
for (Constructor c : ctors) {
Map<String, AnnotationValue> args;
Class<?> paramTypes[] = c.getParameterTypes();
Annotation[][] paramAnnotations = c.getParameterAnnotations();
if (paramTypes.length == 0) {
hasDefaultConstructor = true;
defaultConstructor = c;
} else {
int i = 0;
boolean properlyAnnotated = true;
args = new LinkedHashMap<>();
for (Class<?> clazz : paramTypes) {
NamedArg argAnnotation = null;
for (Annotation annotation : paramAnnotations[i]) {
if (annotation instanceof NamedArg) {
argAnnotation = (NamedArg) annotation;
break;
}
}
if (argAnnotation != null) {
AnnotationValue av = new AnnotationValue(
argAnnotation.value(),
argAnnotation.defaultValue(),
clazz);
args.put(argAnnotation.value(), av);
} else {
properlyAnnotated = false;
break;
}
i++;
}
if (properlyAnnotated) {
constructorsMap.put(c, args);
}
}
}
if (!hasDefaultConstructor && constructorsMap.isEmpty()) {
throw new RuntimeException("Cannot create instance of "
+ type.getCanonicalName()
+ " the constructor is not properly annotated.");
}
constructors = new TreeSet<>(constructorComparator);
constructors.addAll(constructorsMap.keySet());
propertiesMap = scanForSetters();
}
private final Comparator<Constructor> constructorComparator
= (Constructor o1, Constructor o2) -> {
int len1 = o1.getParameterCount();
int len2 = o2.getParameterCount();
int lim = Math.min(len1, len2);
for (int i = 0; i < lim; i++) {
Class c1 = o1.getParameterTypes()[i];
Class c2 = o2.getParameterTypes()[i];
if (c1.equals(c2)) {
continue;
}
if (c1.equals(Integer.TYPE) && c2.equals(Double.TYPE)) {
return -1;
}
if (c1.equals(Double.TYPE) && c2.equals(Integer.TYPE)) {
return 1;
}
return c1.getCanonicalName().compareTo(c2.getCanonicalName());
}
return len1 - len2;
};
private final Map<String, Object> userValues = new HashMap<>();
@Override
public Object put(String key, Object value) {
userValues.put(key, value);
return null;
}
private final Map<String, Object> containers = new HashMap<>();
private Object getTemporaryContainer(String propName) {
Object o = containers.get(propName);
if (o == null) {
o = getReadOnlyProperty(propName);
if (o != null) {
containers.put(propName, o);
}
}
return o;
}
private static class ArrayListWrapper<T> extends ArrayList<T> {
}
private Object getReadOnlyProperty(String propName) {
return new ArrayListWrapper<>();
}
@Override
public int size() {
throw new UnsupportedOperationException();
}
@Override
public Set<Entry<String, Object>> entrySet() {
throw new UnsupportedOperationException();
}
@Override
public boolean isEmpty() {
throw new UnsupportedOperationException();
}
@Override
public boolean containsKey(Object key) {
return (getTemporaryContainer(key.toString()) != null);
}
@Override
public boolean containsValue(Object value) {
throw new UnsupportedOperationException();
}
@Override
public Object get(Object key) {
return getTemporaryContainer(key.toString());
}
@Override
public T build() {
Object retObj = null;
for (Entry<String, Object> entry : containers.entrySet()) {
put(entry.getKey(), entry.getValue());
}
propertyNames = userValues.keySet();
for (Constructor c : constructors) {
Set<String> argumentNames = getArgumentNames(c);
if (propertyNames.equals(argumentNames)) {
retObj = createObjectWithExactArguments(c, argumentNames);
if (retObj != null) {
return (T) retObj;
}
}
}
Set<String> settersArgs = propertiesMap.keySet();
if (settersArgs.containsAll(propertyNames) && hasDefaultConstructor) {
retObj = createObjectFromDefaultConstructor();
if (retObj != null) {
return (T) retObj;
}
}
Set<String> propertiesToSet = new HashSet<>(propertyNames);
propertiesToSet.retainAll(settersArgs);
Set<Constructor> chosenConstructors = chooseBestConstructors(settersArgs);
for (Constructor constructor : chosenConstructors) {
retObj = createObjectFromConstructor(constructor, propertiesToSet);
if (retObj != null) {
return (T) retObj;
}
}
if (retObj == null) {
throw new RuntimeException("Cannot create instance of "
+ type.getCanonicalName() + " with given set of properties: "
+ userValues.keySet().toString());
}
return (T) retObj;
}
private Set<Constructor> chooseBestConstructors(Set<String> settersArgs) {
Set<String> immutablesToSet = new HashSet<>(propertyNames);
immutablesToSet.removeAll(settersArgs);
Set<String> propertiesToSet = new HashSet<>(propertyNames);
propertiesToSet.retainAll(settersArgs);
int propertiesToSetCount = Integer.MAX_VALUE;
int mutablesToSetCount = Integer.MAX_VALUE;
Set<Constructor> chosenConstructors = new TreeSet<>(constructorComparator);
Set<String> argsNotSet = null;
for (Constructor c : constructors) {
Set<String> argumentNames = getArgumentNames(c);
if (!argumentNames.containsAll(immutablesToSet)) {
continue;
}
Set<String> propertiesToSetInConstructor = new HashSet<>(argumentNames);
propertiesToSetInConstructor.removeAll(propertyNames);
Set<String> mutablesNotSet = new HashSet<>(propertiesToSet);
mutablesNotSet.removeAll(argumentNames);
int currentPropSize = propertiesToSetInConstructor.size();
if (propertiesToSetCount == currentPropSize
&& mutablesToSetCount == mutablesNotSet.size()) {
chosenConstructors.add(c);
}
if (propertiesToSetCount > currentPropSize
|| (propertiesToSetCount == currentPropSize && mutablesToSetCount > mutablesNotSet.size())) {
propertiesToSetCount = currentPropSize;
mutablesToSetCount = mutablesNotSet.size();
chosenConstructors.clear();
chosenConstructors.add(c);
}
}
if (argsNotSet != null && !argsNotSet.isEmpty()) {
throw new RuntimeException("Cannot create instance of "
+ type.getCanonicalName()
+ " no constructor contains all properties specified in FXML.");
}
return chosenConstructors;
}
private Set<String> getArgumentNames(Constructor c) {
Map<String, AnnotationValue> constructorArgsMap = constructorsMap.get(c);
Set<String> argumentNames = null;
if (constructorArgsMap != null) {
argumentNames = constructorArgsMap.keySet();
}
return argumentNames;
}
private Object createObjectFromDefaultConstructor() throws RuntimeException {
Object retObj = null;
try {
retObj = createInstance(defaultConstructor, new Object[]{});
} catch (Exception ex) {
throw new RuntimeException(ex);
}
for (String propName : propertyNames) {
try {
Property property = propertiesMap.get(propName);
property.invoke(retObj, getUserValue(propName, property.getType()));
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
return retObj;
}
private Object createObjectFromConstructor(Constructor constructor, Set<String> propertiesToSet) {
Object retObj = null;
Map<String, AnnotationValue> constructorArgsMap = constructorsMap.get(constructor);
Object argsForConstruction[] = new Object[constructorArgsMap.size()];
int i = 0;
Set<String> currentPropertiesToSet = new HashSet<>(propertiesToSet);
for (AnnotationValue value : constructorArgsMap.values()) {
Object userValue = getUserValue(value.getName(), value.getType());
if (userValue != null) {
try {
argsForConstruction[i] = BeanAdapter.coerce(userValue, value.getType());
} catch (Exception ex) {
return null;
}
} else {
if (!value.getDefaultValue().isEmpty()) {
try {
argsForConstruction[i] = BeanAdapter.coerce(value.getDefaultValue(), value.getType());
} catch (Exception ex) {
return null;
}
} else {
argsForConstruction[i] = getDefaultValue(value.getType());
}
}
currentPropertiesToSet.remove(value.getName());
i++;
}
try {
retObj = createInstance(constructor, argsForConstruction);
} catch (Exception ex) {
}
if (retObj != null) {
for (String propName : currentPropertiesToSet) {
try {
Property property = propertiesMap.get(propName);
property.invoke(retObj, getUserValue(propName, property.getType()));
} catch (Exception ex) {
return null;
}
}
}
return retObj;
}
private Object getUserValue(String key, Class<?> type) {
Object val = userValues.get(key);
if (val == null) {
return null;
}
if (type.isAssignableFrom(val.getClass())) {
return val;
}
if (type.isArray()) {
try {
return convertListToArray(val, type);
} catch (RuntimeException ex) {
}
}
if (ArrayListWrapper.class.equals(val.getClass())) {
List l = (List) val;
return l.get(0);
}
return val;
}
private Object createObjectWithExactArguments(Constructor c, Set<String> argumentNames) {
Object retObj = null;
Object argsForConstruction[] = new Object[argumentNames.size()];
Map<String, AnnotationValue> constructorArgsMap = constructorsMap.get(c);
int i = 0;
for (String arg : argumentNames) {
Class<?> tp = constructorArgsMap.get(arg).getType();
Object value = getUserValue(arg, tp);
try {
argsForConstruction[i++] = BeanAdapter.coerce(value, tp);
} catch (Exception ex) {
return null;
}
}
try {
retObj = createInstance(c, argsForConstruction);
} catch (Exception ex) {
}
return retObj;
}
private Object createInstance(Constructor c, Object args[]) throws Exception {
Object retObj = null;
ReflectUtil.checkPackageAccess(type);
retObj = c.newInstance(args);
return retObj;
}
private Map<String, Property> scanForSetters() {
Map<String, Property> strsMap = new HashMap<>();
Map<String, LinkedList<Method>> methods = getClassMethodCache(type);
for (String methodName : methods.keySet()) {
if (methodName.startsWith(SETTER_PREFIX) && methodName.length() > SETTER_PREFIX.length()) {
String propName = methodName.substring(SETTER_PREFIX.length());
propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
List<Method> methodsList = methods.get(methodName);
for (Method m : methodsList) {
Class<?> retType = m.getReturnType();
Class<?> argType[] = m.getParameterTypes();
if (retType.equals(Void.TYPE) && argType.length == 1) {
strsMap.put(propName, new Setter(m, argType[0]));
}
}
}
if (methodName.startsWith(GETTER_PREFIX) && methodName.length() > GETTER_PREFIX.length()) {
String propName = methodName.substring(GETTER_PREFIX.length());
propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
List<Method> methodsList = methods.get(methodName);
for (Method m : methodsList) {
Class<?> retType = m.getReturnType();
Class<?> argType[] = m.getParameterTypes();
if (Collection.class.isAssignableFrom(retType) && argType.length == 0) {
strsMap.put(propName, new Getter(m, retType));
}
}
}
}
return strsMap;
}
private static abstract class Property {
protected final Method method;
protected final Class<?> type;
public Property(Method m, Class<?> t) {
method = m;
type = t;
}
public Class<?> getType() {
return type;
}
public abstract void invoke(Object obj, Object argStr) throws Exception;
}
private static class Setter extends Property {
public Setter(Method m, Class<?> t) {
super(m, t);
}
public void invoke(Object obj, Object argStr) throws Exception {
Object arg[] = new Object[]{BeanAdapter.coerce(argStr, type)};
ModuleHelper.invoke(method, obj, arg);
}
}
private static class Getter extends Property {
public Getter(Method m, Class<?> t) {
super(m, t);
}
@Override
public void invoke(Object obj, Object argStr) throws Exception {
Collection to = (Collection) ModuleHelper.invoke(method, obj, new Object[]{});
if (argStr instanceof Collection) {
Collection from = (Collection) argStr;
to.addAll(from);
} else {
to.add(argStr);
}
}
}
private static class AnnotationValue {
private final String name;
private final String defaultValue;
private final Class<?> type;
public AnnotationValue(String name, String defaultValue, Class<?> type) {
this.name = name;
this.defaultValue = defaultValue;
this.type = type;
}
public String getName() {
return name;
}
public String getDefaultValue() {
return defaultValue;
}
public Class<?> getType() {
return type;
}
}
private static HashMap<String, LinkedList<Method>> getClassMethodCache(Class<?> type) {
HashMap<String, LinkedList<Method>> classMethodCache = new HashMap<>();
ReflectUtil.checkPackageAccess(type);
Method[] declaredMethods = type.getMethods();
for (Method method : declaredMethods) {
int modifiers = method.getModifiers();
if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
String name = method.getName();
LinkedList<Method> namedMethods = classMethodCache.get(name);
if (namedMethods == null) {
namedMethods = new LinkedList<>();
classMethodCache.put(name, namedMethods);
}
namedMethods.add(method);
}
}
return classMethodCache;
}
private static Object[] convertListToArray(Object userValue, Class<?> localType) {
Class<?> arrayType = localType.getComponentType();
List l = (List) BeanAdapter.coerce(userValue, List.class);
return l.toArray((Object[]) Array.newInstance(arrayType, 0));
}
private static Object getDefaultValue(Class clazz) {
return DEFAULTS_MAP.get(clazz);
}
private static final Map<Class<?>, Object> DEFAULTS_MAP = new HashMap<>(9);
static {
DEFAULTS_MAP.put(byte.class, (byte) 0);
DEFAULTS_MAP.put(short.class, (short) 0);
DEFAULTS_MAP.put(int.class, 0);
DEFAULTS_MAP.put(long.class, 0L);
DEFAULTS_MAP.put(float.class, 0.0f);
DEFAULTS_MAP.put(double.class, 0.0d);
DEFAULTS_MAP.put(char.class, '\u0000');
DEFAULTS_MAP.put(boolean.class, false);
DEFAULTS_MAP.put(Object.class, null);
}
}
