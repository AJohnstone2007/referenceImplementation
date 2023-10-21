package javafx.fxml;
import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.builder.JavaFXFontBuilder;
import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import com.sun.javafx.fxml.builder.JavaFXSceneBuilder;
import com.sun.javafx.fxml.builder.ProxyBuilder;
import com.sun.javafx.fxml.builder.TriangleMeshBuilder;
import com.sun.javafx.fxml.builder.URLBuilder;
import com.sun.javafx.logging.PlatformLogger;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import com.sun.javafx.reflect.ConstructorUtil;
import com.sun.javafx.reflect.MethodUtil;
public final class JavaFXBuilderFactory implements BuilderFactory {
private final ClassLoader classLoader;
private final boolean webSupported;
private static final String WEBVIEW_NAME = "javafx.scene.web.WebView";
private static final String WEBVIEW_BUILDER_NAME =
"com.sun.javafx.fxml.builder.web.WebViewBuilder";
public JavaFXBuilderFactory() {
this(FXMLLoader.getDefaultClassLoader());
}
public JavaFXBuilderFactory(ClassLoader classLoader) {
if (classLoader == null) {
throw new NullPointerException();
}
this.classLoader = classLoader;
this.webSupported = Platform.isSupported(ConditionalFeature.WEB);
}
@Override
public Builder<?> getBuilder(Class<?> type) {
if (type == null) {
throw new NullPointerException();
}
Builder<?> builder;
if (type == Scene.class) {
builder = new JavaFXSceneBuilder();
} else if (type == Font.class) {
builder = new JavaFXFontBuilder();
} else if (type == Image.class) {
builder = new JavaFXImageBuilder();
} else if (type == URL.class) {
builder = new URLBuilder(classLoader);
} else if (type == TriangleMesh.class) {
builder = new TriangleMeshBuilder();
} else if (webSupported && type.getName().equals(WEBVIEW_NAME)) {
try {
Class<?> builderClass = classLoader.loadClass(WEBVIEW_BUILDER_NAME);
ObjectBuilderWrapper wrapper = new ObjectBuilderWrapper(builderClass);
builder = wrapper.createBuilder();
} catch (Exception ex) {
builder = null;
}
} else if (scanForConstructorAnnotations(type)) {
builder = new ProxyBuilder(type);
} else {
builder = null;
}
return builder;
}
private boolean scanForConstructorAnnotations(Class<?> type) {
Constructor constructors[] = ConstructorUtil.getConstructors(type);
for (Constructor constructor : constructors) {
Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
for (int i = 0; i < constructor.getParameterTypes().length; i++) {
for (Annotation annotation : paramAnnotations[i]) {
if (annotation instanceof NamedArg) {
return true;
}
}
}
}
return false;
}
private static final class ObjectBuilderWrapper {
private static final Object[] NO_ARGS = {};
private static final Class<?>[] NO_SIG = {};
private final Class<?> builderClass;
private final Method createMethod;
private final Method buildMethod;
private final Map<String,Method> methods = new HashMap<String, Method>();
private final Map<String,Method> getters = new HashMap<String,Method>();
private final Map<String,Method> setters = new HashMap<String,Method>();
final class ObjectBuilder extends AbstractMap<String, Object> implements Builder<Object> {
private final Map<String,Object> containers = new HashMap<String,Object>();
private Object builder = null;
private Map<Object,Object> properties;
private ObjectBuilder() {
try {
builder = createMethod.invoke(null, NO_ARGS);
} catch (Exception e) {
throw new RuntimeException("Creation of the builder " + builderClass.getName() + " failed.", e);
}
}
@Override
public Object build() {
for (Iterator<Entry<String,Object>> iter = containers.entrySet().iterator(); iter.hasNext(); ) {
Entry<String, Object> entry = iter.next();
put(entry.getKey(), entry.getValue());
}
Object res;
try {
res = buildMethod.invoke(builder, NO_ARGS);
if (properties != null && res instanceof Node) {
((Map<Object, Object>)((Node)res).getProperties()).putAll(properties);
}
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
} finally {
builder = null;
}
return res;
}
@Override
public int size() {
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
@SuppressWarnings("unchecked")
public Object put(String key, Object value) {
if (Node.class.isAssignableFrom(getTargetClass()) && "properties".equals(key)) {
properties = (Map<Object,Object>) value;
return null;
}
try {
Method m = methods.get(key);
if (m == null) {
m = findMethod(key);
methods.put(key, m);
}
try {
final Class<?> type = m.getParameterTypes()[0];
if (type.isArray()) {
final List<?> list;
if (value instanceof List) {
list = (List<?>)value;
} else {
list = Arrays.asList(value.toString().split(FXMLLoader.ARRAY_COMPONENT_DELIMITER));
}
final Class<?> componentType = type.getComponentType();
Object array = Array.newInstance(componentType, list.size());
for (int i=0; i<list.size(); i++) {
Array.set(array, i, BeanAdapter.coerce(list.get(i), componentType));
}
value = array;
}
m.invoke(builder, new Object[] { BeanAdapter.coerce(value, type) });
} catch (Exception e) {
String msg = "Method " + m.getName() + " failed";
PlatformLogger.getLogger(ObjectBuilderWrapper.class.getName()).warning(msg, e);
}
return null;
} catch (Exception e) {
String msg = "Failed to set " + getTargetClass()+"." + key + " using " + builderClass;
PlatformLogger.getLogger(ObjectBuilderWrapper.class.getName()).warning(msg, e);
return null;
}
}
Object getReadOnlyProperty(String propName) {
if (setters.get(propName) != null) return null;
Method getter = getters.get(propName);
if (getter == null) {
Method setter = null;
Class<?> target = getTargetClass();
String suffix = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
try {
getter = MethodUtil.getMethod(target, "get"+ suffix, NO_SIG);
setter = MethodUtil.getMethod(target, "set"+ suffix, new Class[] { getter.getReturnType() });
} catch (Exception x) {
}
if (getter != null) {
getters.put(propName, getter);
setters.put(propName, setter);
}
if (setter != null) return null;
}
Class<?> type;
if (getter == null) {
final Method m = findMethod(propName);
if (m == null) {
return null;
}
type = m.getParameterTypes()[0];
if (type.isArray()) type = List.class;
} else {
type = getter.getReturnType();
}
if (ObservableMap.class.isAssignableFrom(type)) {
return FXCollections.observableMap(new HashMap<Object, Object>());
} else if (Map.class.isAssignableFrom(type)) {
return new HashMap<Object, Object>();
} else if (ObservableList.class.isAssignableFrom(type)) {
return FXCollections.observableArrayList();
} else if (List.class.isAssignableFrom(type)) {
return new ArrayList<Object>();
} else if (Set.class.isAssignableFrom(type)) {
return new HashSet<Object>();
}
return null;
}
public Object getTemporaryContainer(String propName) {
Object o = containers.get(propName);
if (o == null) {
o = getReadOnlyProperty(propName);
if (o != null) {
containers.put(propName, o);
}
}
return o;
}
@Override
public Object remove(Object key) {
throw new UnsupportedOperationException();
}
@Override
public void putAll(Map<? extends String, ? extends Object> m) {
throw new UnsupportedOperationException();
}
@Override
public void clear() {
throw new UnsupportedOperationException();
}
@Override
public Set<String> keySet() {
throw new UnsupportedOperationException();
}
@Override
public Collection<Object> values() {
throw new UnsupportedOperationException();
}
@Override
public Set<Entry<String, Object>> entrySet() {
throw new UnsupportedOperationException();
}
}
ObjectBuilderWrapper() {
builderClass = null;
createMethod = null;
buildMethod = null;
}
ObjectBuilderWrapper(Class<?> builderClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
this.builderClass = builderClass;
createMethod = MethodUtil.getMethod(builderClass, "create", NO_SIG);
buildMethod = MethodUtil.getMethod(builderClass, "build", NO_SIG);
assert Modifier.isStatic(createMethod.getModifiers());
assert !Modifier.isStatic(buildMethod.getModifiers());
}
Builder<Object> createBuilder() {
return new ObjectBuilder();
}
private Method findMethod(String name) {
if (name.length() > 1
&& Character.isUpperCase(name.charAt(1))) {
name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
}
for (Method m : MethodUtil.getMethods(builderClass)) {
if (m.getName().equals(name)) {
return m;
}
}
throw new IllegalArgumentException("Method " + name + " could not be found at class " + builderClass.getName());
}
public Class<?> getTargetClass() {
return buildMethod.getReturnType();
}
}
}
