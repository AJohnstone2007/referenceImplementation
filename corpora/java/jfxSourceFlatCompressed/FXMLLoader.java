package javafx.fxml;
import com.sun.javafx.util.Logging;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.ParseTraceElement;
import com.sun.javafx.fxml.PropertyNotFoundException;
import com.sun.javafx.fxml.expression.Expression;
import com.sun.javafx.fxml.expression.ExpressionValue;
import com.sun.javafx.fxml.expression.KeyPath;
import static com.sun.javafx.FXPermissions.MODIFY_FXML_CLASS_LOADER_PERMISSION;
import com.sun.javafx.fxml.FXMLLoaderHelper;
import com.sun.javafx.fxml.MethodHelper;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumMap;
import java.util.Locale;
import java.util.StringTokenizer;
import com.sun.javafx.reflect.ConstructorUtil;
import com.sun.javafx.reflect.MethodUtil;
import com.sun.javafx.reflect.ReflectUtil;
public class FXMLLoader {
private static final RuntimePermission GET_CLASSLOADER_PERMISSION =
new RuntimePermission("getClassLoader");
@SuppressWarnings("removal")
private static final StackWalker walker =
AccessController.doPrivileged((PrivilegedAction<StackWalker>) () ->
StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE));
private abstract class Element {
public final Element parent;
public Object value = null;
private BeanAdapter valueAdapter = null;
public final LinkedList<Attribute> eventHandlerAttributes = new LinkedList<Attribute>();
public final LinkedList<Attribute> instancePropertyAttributes = new LinkedList<Attribute>();
public final LinkedList<Attribute> staticPropertyAttributes = new LinkedList<Attribute>();
public final LinkedList<PropertyElement> staticPropertyElements = new LinkedList<PropertyElement>();
public Element() {
parent = current;
}
public boolean isCollection() {
boolean collection;
if (value instanceof List<?>) {
collection = true;
} else {
Class<?> type = value.getClass();
DefaultProperty defaultProperty = type.getAnnotation(DefaultProperty.class);
if (defaultProperty != null) {
collection = getProperties().get(defaultProperty.value()) instanceof List<?>;
} else {
collection = false;
}
}
return collection;
}
@SuppressWarnings("unchecked")
public void add(Object element) throws LoadException {
List<Object> list;
if (value instanceof List<?>) {
list = (List<Object>)value;
} else {
Class<?> type = value.getClass();
DefaultProperty defaultProperty = type.getAnnotation(DefaultProperty.class);
String defaultPropertyName = defaultProperty.value();
list = (List<Object>)getProperties().get(defaultPropertyName);
if (!Map.class.isAssignableFrom(type)) {
Type listType = getValueAdapter().getGenericType(defaultPropertyName);
element = BeanAdapter.coerce(element, BeanAdapter.getListItemType(listType));
}
}
list.add(element);
}
public void set(Object value) throws LoadException {
if (this.value == null) {
throw constructLoadException("Cannot set value on this element.");
}
Class<?> type = this.value.getClass();
DefaultProperty defaultProperty = type.getAnnotation(DefaultProperty.class);
if (defaultProperty == null) {
throw constructLoadException("Element does not define a default property.");
}
getProperties().put(defaultProperty.value(), value);
}
public void updateValue(Object value) {
this.value = value;
valueAdapter = null;
}
public boolean isTyped() {
return !(value instanceof Map<?, ?>);
}
public BeanAdapter getValueAdapter() {
if (valueAdapter == null) {
valueAdapter = new BeanAdapter(value);
}
return valueAdapter;
}
@SuppressWarnings("unchecked")
public Map<String, Object> getProperties() {
return (isTyped()) ? getValueAdapter() : (Map<String, Object>)value;
}
public void processStartElement() throws IOException {
for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
String prefix = xmlStreamReader.getAttributePrefix(i);
String localName = xmlStreamReader.getAttributeLocalName(i);
String value = xmlStreamReader.getAttributeValue(i);
if (loadListener != null
&& prefix != null
&& prefix.equals(FX_NAMESPACE_PREFIX)) {
loadListener.readInternalAttribute(prefix + ":" + localName, value);
}
processAttribute(prefix, localName, value);
}
}
public void processEndElement() throws IOException {
}
public void processCharacters() throws IOException {
throw constructLoadException("Unexpected characters in input stream.");
}
public void processInstancePropertyAttributes() throws IOException {
if (instancePropertyAttributes.size() > 0) {
for (Attribute attribute : instancePropertyAttributes) {
processPropertyAttribute(attribute);
}
}
}
public void processAttribute(String prefix, String localName, String value)
throws IOException{
if (prefix == null) {
if (localName.startsWith(EVENT_HANDLER_PREFIX)) {
if (loadListener != null) {
loadListener.readEventHandlerAttribute(localName, value);
}
eventHandlerAttributes.add(new Attribute(localName, null, value));
} else {
int i = localName.lastIndexOf('.');
if (i == -1) {
if (loadListener != null) {
loadListener.readPropertyAttribute(localName, null, value);
}
instancePropertyAttributes.add(new Attribute(localName, null, value));
} else {
String name = localName.substring(i + 1);
Class<?> sourceType = getType(localName.substring(0, i));
if (sourceType != null) {
if (loadListener != null) {
loadListener.readPropertyAttribute(name, sourceType, value);
}
staticPropertyAttributes.add(new Attribute(name, sourceType, value));
} else if (staticLoad) {
if (loadListener != null) {
loadListener.readUnknownStaticPropertyAttribute(localName, value);
}
} else {
throw constructLoadException(localName + " is not a valid attribute.");
}
}
}
} else {
throw constructLoadException(prefix + ":" + localName
+ " is not a valid attribute.");
}
}
@SuppressWarnings("unchecked")
public void processPropertyAttribute(Attribute attribute) throws IOException {
String value = attribute.value;
if (isBindingExpression(value)) {
Expression expression;
if (attribute.sourceType != null) {
throw constructLoadException("Cannot bind to static property.");
}
if (!isTyped()) {
throw constructLoadException("Cannot bind to untyped object.");
}
if (this.value instanceof Builder) {
throw constructLoadException("Cannot bind to builder property.");
}
if (!isStaticLoad()) {
value = value.substring(BINDING_EXPRESSION_PREFIX.length(),
value.length() - 1);
expression = Expression.valueOf(value);
BeanAdapter targetAdapter = new BeanAdapter(this.value);
ObservableValue<Object> propertyModel = targetAdapter.getPropertyModel(attribute.name);
Class<?> type = targetAdapter.getType(attribute.name);
if (propertyModel instanceof Property<?>) {
((Property<Object>) propertyModel).bind(new ExpressionValue(namespace, expression, type));
}
}
} else if (isBidirectionalBindingExpression(value)) {
throw constructLoadException(new UnsupportedOperationException("This feature is not currently enabled."));
} else {
processValue(attribute.sourceType, attribute.name, value);
}
}
private boolean isBindingExpression(String aValue) {
return aValue.startsWith(BINDING_EXPRESSION_PREFIX)
&& aValue.endsWith(BINDING_EXPRESSION_SUFFIX);
}
private boolean isBidirectionalBindingExpression(String aValue) {
return aValue.startsWith(BI_DIRECTIONAL_BINDING_PREFIX);
}
private boolean processValue(Class sourceType, String propertyName, String aValue)
throws LoadException {
boolean processed = false;
if (sourceType == null && isTyped()) {
BeanAdapter valueAdapter = getValueAdapter();
Class<?> type = valueAdapter.getType(propertyName);
if (type == null) {
throw new PropertyNotFoundException("Property \"" + propertyName
+ "\" does not exist" + " or is read-only.");
}
if (List.class.isAssignableFrom(type)
&& valueAdapter.isReadOnly(propertyName)) {
populateListFromString(valueAdapter, propertyName, aValue);
processed = true;
} else if (type.isArray()) {
applyProperty(propertyName, sourceType,
populateArrayFromString(type, aValue));
processed = true;
}
}
if (!processed) {
applyProperty(propertyName, sourceType, resolvePrefixedValue(aValue));
processed = true;
}
return processed;
}
private Object resolvePrefixedValue(String aValue) throws LoadException {
if (aValue.startsWith(ESCAPE_PREFIX)) {
aValue = aValue.substring(ESCAPE_PREFIX.length());
if (aValue.length() == 0
|| !(aValue.startsWith(ESCAPE_PREFIX)
|| aValue.startsWith(RELATIVE_PATH_PREFIX)
|| aValue.startsWith(RESOURCE_KEY_PREFIX)
|| aValue.startsWith(EXPRESSION_PREFIX)
|| aValue.startsWith(BI_DIRECTIONAL_BINDING_PREFIX))) {
throw constructLoadException("Invalid escape sequence.");
}
return aValue;
} else if (aValue.startsWith(RELATIVE_PATH_PREFIX)) {
aValue = aValue.substring(RELATIVE_PATH_PREFIX.length());
if (aValue.length() == 0) {
throw constructLoadException("Missing relative path.");
}
if (aValue.startsWith(RELATIVE_PATH_PREFIX)) {
warnDeprecatedEscapeSequence(RELATIVE_PATH_PREFIX);
return aValue;
} else {
if (aValue.charAt(0) == '/') {
final URL res = getClassLoader().getResource(aValue.substring(1));
if (res == null) {
throw constructLoadException("Invalid resource: " + aValue + " not found on the classpath");
}
return res.toString();
} else {
try {
return new URL(FXMLLoader.this.location, aValue).toString();
} catch (MalformedURLException e) {
System.err.println(FXMLLoader.this.location + "/" + aValue);
}
}
}
} else if (aValue.startsWith(RESOURCE_KEY_PREFIX)) {
aValue = aValue.substring(RESOURCE_KEY_PREFIX.length());
if (aValue.length() == 0) {
throw constructLoadException("Missing resource key.");
}
if (aValue.startsWith(RESOURCE_KEY_PREFIX)) {
warnDeprecatedEscapeSequence(RESOURCE_KEY_PREFIX);
return aValue;
} else {
if (resources == null) {
throw constructLoadException("No resources specified.");
}
if (!resources.containsKey(aValue)) {
throw constructLoadException("Resource \"" + aValue + "\" not found.");
}
return resources.getString(aValue);
}
} else if (aValue.startsWith(EXPRESSION_PREFIX)) {
aValue = aValue.substring(EXPRESSION_PREFIX.length());
if (aValue.length() == 0) {
throw constructLoadException("Missing expression.");
}
if (aValue.startsWith(EXPRESSION_PREFIX)) {
warnDeprecatedEscapeSequence(EXPRESSION_PREFIX);
return aValue;
} else if (aValue.equals(NULL_KEYWORD)) {
return null;
}
return Expression.get(namespace, KeyPath.parse(aValue));
}
return aValue;
}
private Object populateArrayFromString(
Class<?>type,
String stringValue) throws LoadException {
Object propertyValue = null;
Class<?> componentType = type.getComponentType();
if (stringValue.length() > 0) {
String[] values = stringValue.split(ARRAY_COMPONENT_DELIMITER);
propertyValue = Array.newInstance(componentType, values.length);
for (int i = 0; i < values.length; i++) {
Array.set(propertyValue, i,
BeanAdapter.coerce(resolvePrefixedValue(values[i].trim()),
type.getComponentType()));
}
} else {
propertyValue = Array.newInstance(componentType, 0);
}
return propertyValue;
}
private void populateListFromString(
BeanAdapter valueAdapter,
String listPropertyName,
String stringValue) throws LoadException {
List<Object> list = (List<Object>)valueAdapter.get(listPropertyName);
Type listType = valueAdapter.getGenericType(listPropertyName);
Type itemType = (Class<?>)BeanAdapter.getGenericListItemType(listType);
if (itemType instanceof ParameterizedType) {
itemType = ((ParameterizedType)itemType).getRawType();
}
if (stringValue.length() > 0) {
String[] values = stringValue.split(ARRAY_COMPONENT_DELIMITER);
for (String aValue: values) {
aValue = aValue.trim();
list.add(
BeanAdapter.coerce(resolvePrefixedValue(aValue),
(Class<?>)itemType));
}
}
}
public void warnDeprecatedEscapeSequence(String prefix) {
System.err.println(prefix + prefix + " is a deprecated escape sequence. "
+ "Please use \\" + prefix + " instead.");
}
public void applyProperty(String name, Class<?> sourceType, Object value) {
if (sourceType == null) {
getProperties().put(name, value);
} else {
BeanAdapter.put(this.value, sourceType, name, value);
}
}
private Object getExpressionObject(String handlerValue) throws LoadException{
if (handlerValue.startsWith(EXPRESSION_PREFIX)) {
handlerValue = handlerValue.substring(EXPRESSION_PREFIX.length());
if (handlerValue.length() == 0) {
throw constructLoadException("Missing expression reference.");
}
Object expression = Expression.get(namespace, KeyPath.parse(handlerValue));
if (expression == null) {
throw constructLoadException("Unable to resolve expression : $" + handlerValue);
}
return expression;
}
return null;
}
private <T> T getExpressionObjectOfType(String handlerValue, Class<T> type) throws LoadException{
Object expression = getExpressionObject(handlerValue);
if (expression != null) {
if (type.isInstance(expression)) {
return (T) expression;
}
throw constructLoadException("Error resolving \"" + handlerValue +"\" expression."
+ "Does not point to a " + type.getName());
}
return null;
}
private MethodHandler getControllerMethodHandle(String handlerName, SupportedType... types) throws LoadException {
if (handlerName.startsWith(CONTROLLER_METHOD_PREFIX)) {
handlerName = handlerName.substring(CONTROLLER_METHOD_PREFIX.length());
if (!handlerName.startsWith(CONTROLLER_METHOD_PREFIX)) {
if (handlerName.length() == 0) {
throw constructLoadException("Missing controller method.");
}
if (controller == null) {
throw constructLoadException("No controller specified.");
}
for (SupportedType t : types) {
Method method = controllerAccessor
.getControllerMethods()
.get(t)
.get(handlerName);
if (method != null) {
return new MethodHandler(controller, method, t);
}
}
Method method = controllerAccessor
.getControllerMethods()
.get(SupportedType.PARAMETERLESS)
.get(handlerName);
if (method != null) {
return new MethodHandler(controller, method, SupportedType.PARAMETERLESS);
}
return null;
}
}
return null;
}
public void processEventHandlerAttributes() throws LoadException {
if (eventHandlerAttributes.size() > 0 && !staticLoad) {
for (Attribute attribute : eventHandlerAttributes) {
String handlerName = attribute.value;
if (value instanceof ObservableList && attribute.name.equals(COLLECTION_HANDLER_NAME)) {
processObservableListHandler(handlerName);
} else if (value instanceof ObservableMap && attribute.name.equals(COLLECTION_HANDLER_NAME)) {
processObservableMapHandler(handlerName);
} else if (value instanceof ObservableSet && attribute.name.equals(COLLECTION_HANDLER_NAME)) {
processObservableSetHandler(handlerName);
} else if (attribute.name.endsWith(CHANGE_EVENT_HANDLER_SUFFIX)) {
processPropertyHandler(attribute.name, handlerName);
} else {
EventHandler<? extends Event> eventHandler = null;
MethodHandler handler = getControllerMethodHandle(handlerName, SupportedType.EVENT);
if (handler != null) {
eventHandler = new ControllerMethodEventHandler<>(handler);
}
if (eventHandler == null) {
eventHandler = getExpressionObjectOfType(handlerName, EventHandler.class);
}
if (eventHandler == null) {
if (handlerName.length() == 0 || scriptEngine == null) {
throw constructLoadException("Error resolving " + attribute.name + "='" + attribute.value
+ "', either the event handler is not in the Namespace or there is an error in the script.");
}
eventHandler = new ScriptEventHandler(handlerName, scriptEngine, location.getPath()
+ "-" + attribute.name + "_attribute_in_element_ending_at_line_" + getLineNumber());
}
getValueAdapter().put(attribute.name, eventHandler);
}
}
}
}
private void processObservableListHandler(String handlerValue) throws LoadException {
ObservableList list = (ObservableList)value;
if (handlerValue.startsWith(CONTROLLER_METHOD_PREFIX)) {
MethodHandler handler = getControllerMethodHandle(handlerValue, SupportedType.LIST_CHANGE_LISTENER);
if (handler != null) {
list.addListener(new ObservableListChangeAdapter(handler));
} else {
throw constructLoadException("Controller method \"" + handlerValue + "\" not found.");
}
} else if (handlerValue.startsWith(EXPRESSION_PREFIX)) {
Object listener = getExpressionObject(handlerValue);
if (listener instanceof ListChangeListener) {
list.addListener((ListChangeListener) listener);
} else if (listener instanceof InvalidationListener) {
list.addListener((InvalidationListener) listener);
} else {
throw constructLoadException("Error resolving \"" + handlerValue + "\" expression."
+ "Must be either ListChangeListener or InvalidationListener");
}
}
}
private void processObservableMapHandler(String handlerValue) throws LoadException {
ObservableMap map = (ObservableMap)value;
if (handlerValue.startsWith(CONTROLLER_METHOD_PREFIX)) {
MethodHandler handler = getControllerMethodHandle(handlerValue, SupportedType.MAP_CHANGE_LISTENER);
if (handler != null) {
map.addListener(new ObservableMapChangeAdapter(handler));
} else {
throw constructLoadException("Controller method \"" + handlerValue + "\" not found.");
}
} else if (handlerValue.startsWith(EXPRESSION_PREFIX)) {
Object listener = getExpressionObject(handlerValue);
if (listener instanceof MapChangeListener) {
map.addListener((MapChangeListener) listener);
} else if (listener instanceof InvalidationListener) {
map.addListener((InvalidationListener) listener);
} else {
throw constructLoadException("Error resolving \"" + handlerValue + "\" expression."
+ "Must be either MapChangeListener or InvalidationListener");
}
}
}
private void processObservableSetHandler(String handlerValue) throws LoadException {
ObservableSet set = (ObservableSet)value;
if (handlerValue.startsWith(CONTROLLER_METHOD_PREFIX)) {
MethodHandler handler = getControllerMethodHandle(handlerValue, SupportedType.SET_CHANGE_LISTENER);
if (handler != null) {
set.addListener(new ObservableSetChangeAdapter(handler));
} else {
throw constructLoadException("Controller method \"" + handlerValue + "\" not found.");
}
} else if (handlerValue.startsWith(EXPRESSION_PREFIX)) {
Object listener = getExpressionObject(handlerValue);
if (listener instanceof SetChangeListener) {
set.addListener((SetChangeListener) listener);
} else if (listener instanceof InvalidationListener) {
set.addListener((InvalidationListener) listener);
} else {
throw constructLoadException("Error resolving \"" + handlerValue + "\" expression."
+ "Must be either SetChangeListener or InvalidationListener");
}
}
}
private void processPropertyHandler(String attributeName, String handlerValue) throws LoadException {
int i = EVENT_HANDLER_PREFIX.length();
int j = attributeName.length() - CHANGE_EVENT_HANDLER_SUFFIX.length();
if (i != j) {
String key = Character.toLowerCase(attributeName.charAt(i))
+ attributeName.substring(i + 1, j);
ObservableValue<Object> propertyModel = getValueAdapter().getPropertyModel(key);
if (propertyModel == null) {
throw constructLoadException(value.getClass().getName() + " does not define"
+ " a property model for \"" + key + "\".");
}
if (handlerValue.startsWith(CONTROLLER_METHOD_PREFIX)) {
final MethodHandler handler = getControllerMethodHandle(handlerValue, SupportedType.PROPERTY_CHANGE_LISTENER, SupportedType.EVENT);
if (handler != null) {
if (handler.type == SupportedType.EVENT) {
propertyModel.addListener(new ChangeListener<Object>() {
@Override
public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
handler.invoke(new Event(value, null, Event.ANY));
}
});
} else {
propertyModel.addListener(new PropertyChangeAdapter(handler));
}
} else {
throw constructLoadException("Controller method \"" + handlerValue + "\" not found.");
}
} else if (handlerValue.startsWith(EXPRESSION_PREFIX)) {
Object listener = getExpressionObject(handlerValue);
if (listener instanceof ChangeListener) {
propertyModel.addListener((ChangeListener) listener);
} else if (listener instanceof InvalidationListener) {
propertyModel.addListener((InvalidationListener) listener);
} else {
throw constructLoadException("Error resolving \"" + handlerValue + "\" expression."
+ "Must be either ChangeListener or InvalidationListener");
}
}
}
}
}
private abstract class ValueElement extends Element {
public String fx_id = null;
@Override
public void processStartElement() throws IOException {
super.processStartElement();
updateValue(constructValue());
if (value instanceof Builder<?>) {
processInstancePropertyAttributes();
} else {
processValue();
}
}
@Override
@SuppressWarnings("unchecked")
public void processEndElement() throws IOException {
super.processEndElement();
if (value instanceof Builder<?>) {
Builder<Object> builder = (Builder<Object>)value;
updateValue(builder.build());
processValue();
} else {
processInstancePropertyAttributes();
}
processEventHandlerAttributes();
if (staticPropertyAttributes.size() > 0) {
for (Attribute attribute : staticPropertyAttributes) {
processPropertyAttribute(attribute);
}
}
if (staticPropertyElements.size() > 0) {
for (PropertyElement element : staticPropertyElements) {
BeanAdapter.put(value, element.sourceType, element.name, element.value);
}
}
if (parent != null) {
if (parent.isCollection()) {
parent.add(value);
} else {
parent.set(value);
}
}
}
private Object getListValue(Element parent, String listPropertyName, Object value) {
if (parent.isTyped()) {
Type listType = parent.getValueAdapter().getGenericType(listPropertyName);
if (listType != null) {
Type itemType = BeanAdapter.getGenericListItemType(listType);
if (itemType instanceof ParameterizedType) {
itemType = ((ParameterizedType)itemType).getRawType();
}
value = BeanAdapter.coerce(value, (Class<?>)itemType);
}
}
return value;
}
private void processValue() throws LoadException {
if (parent == null) {
root = value;
String fxNSURI = xmlStreamReader.getNamespaceContext().getNamespaceURI("fx");
if (fxNSURI != null) {
String fxVersion = fxNSURI.substring(fxNSURI.lastIndexOf("/") + 1);
if (compareJFXVersions(FX_NAMESPACE_VERSION, fxVersion) < 0) {
throw constructLoadException("Loading FXML document of version " +
fxVersion + " by JavaFX runtime supporting version " + FX_NAMESPACE_VERSION);
}
}
String defaultNSURI = xmlStreamReader.getNamespaceContext().getNamespaceURI("");
if (defaultNSURI != null) {
String nsVersion = defaultNSURI.substring(defaultNSURI.lastIndexOf("/") + 1);
if (compareJFXVersions(JAVAFX_VERSION, nsVersion) < 0) {
Logging.getJavaFXLogger().warning("Loading FXML document with JavaFX API of version " +
nsVersion + " by JavaFX runtime of version " + JAVAFX_VERSION);
}
}
}
if (fx_id != null) {
namespace.put(fx_id, value);
IDProperty idProperty = value.getClass().getAnnotation(IDProperty.class);
if (idProperty != null) {
Map<String, Object> properties = getProperties();
if (properties.get(idProperty.value()) == null) {
properties.put(idProperty.value(), fx_id);
}
}
injectFields(fx_id, value);
}
}
@Override
@SuppressWarnings("unchecked")
public void processCharacters() throws LoadException {
Class<?> type = value.getClass();
DefaultProperty defaultProperty = type.getAnnotation(DefaultProperty.class);
if (defaultProperty != null) {
String text = xmlStreamReader.getText();
text = extraneousWhitespacePattern.matcher(text).replaceAll(" ");
String defaultPropertyName = defaultProperty.value();
BeanAdapter valueAdapter = getValueAdapter();
if (valueAdapter.isReadOnly(defaultPropertyName)
&& List.class.isAssignableFrom(valueAdapter.getType(defaultPropertyName))) {
List<Object> list = (List<Object>)valueAdapter.get(defaultPropertyName);
list.add(getListValue(this, defaultPropertyName, text));
} else {
valueAdapter.put(defaultPropertyName, text.trim());
}
} else {
throw constructLoadException(type.getName() + " does not have a default property.");
}
}
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException{
if (prefix != null
&& prefix.equals(FX_NAMESPACE_PREFIX)) {
if (localName.equals(FX_ID_ATTRIBUTE)) {
if (value.equals(NULL_KEYWORD)) {
throw constructLoadException("Invalid identifier.");
}
for (int i = 0, n = value.length(); i < n; i++) {
if (!Character.isJavaIdentifierPart(value.charAt(i))) {
throw constructLoadException("Invalid identifier.");
}
}
fx_id = value;
} else if (localName.equals(FX_CONTROLLER_ATTRIBUTE)) {
if (current.parent != null) {
throw constructLoadException(FX_NAMESPACE_PREFIX + ":" + FX_CONTROLLER_ATTRIBUTE
+ " can only be applied to root element.");
}
if (controller != null) {
throw constructLoadException("Controller value already specified.");
}
if (!staticLoad) {
Class<?> type;
try {
type = getClassLoader().loadClass(value);
} catch (ClassNotFoundException exception) {
throw constructLoadException(exception);
}
try {
if (controllerFactory == null) {
ReflectUtil.checkPackageAccess(type);
setController(type.getDeclaredConstructor().newInstance());
} else {
setController(controllerFactory.call(type));
}
} catch (Exception e) {
throw constructLoadException(e);
}
}
} else {
throw constructLoadException("Invalid attribute.");
}
} else {
super.processAttribute(prefix, localName, value);
}
}
public abstract Object constructValue() throws IOException;
}
private class InstanceDeclarationElement extends ValueElement {
public Class<?> type;
public String constant = null;
public String factory = null;
public InstanceDeclarationElement(Class<?> type) throws LoadException {
this.type = type;
}
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix != null
&& prefix.equals(FX_NAMESPACE_PREFIX)) {
if (localName.equals(FX_VALUE_ATTRIBUTE)) {
this.value = value;
} else if (localName.equals(FX_CONSTANT_ATTRIBUTE)) {
constant = value;
} else if (localName.equals(FX_FACTORY_ATTRIBUTE)) {
factory = value;
} else {
super.processAttribute(prefix, localName, value);
}
} else {
super.processAttribute(prefix, localName, value);
}
}
@Override
public Object constructValue() throws IOException {
Object value;
if (this.value != null) {
value = BeanAdapter.coerce(this.value, type);
} else if (constant != null) {
value = BeanAdapter.getConstantValue(type, constant);
} else if (factory != null) {
Method factoryMethod;
try {
factoryMethod = MethodUtil.getMethod(type, factory, new Class[] {});
} catch (NoSuchMethodException exception) {
throw constructLoadException(exception);
}
try {
value = MethodHelper.invoke(factoryMethod, null, new Object [] {});
} catch (IllegalAccessException exception) {
throw constructLoadException(exception);
} catch (InvocationTargetException exception) {
throw constructLoadException(exception);
}
} else {
value = (builderFactory == null) ? null : builderFactory.getBuilder(type);
if (value == null) {
value = DEFAULT_BUILDER_FACTORY.getBuilder(type);
}
if (value == null) {
try {
ReflectUtil.checkPackageAccess(type);
value = type.getDeclaredConstructor().newInstance();
} catch (Exception e) {
throw constructLoadException(e);
}
}
}
return value;
}
}
private class UnknownTypeElement extends ValueElement {
@DefaultProperty("items")
public class UnknownValueMap extends AbstractMap<String, Object> {
private ArrayList<?> items = new ArrayList<Object>();
private HashMap<String, Object> values = new HashMap<String, Object>();
@Override
public Object get(Object key) {
if (key == null) {
throw new NullPointerException();
}
return (key.equals(getClass().getAnnotation(DefaultProperty.class).value())) ?
items : values.get(key);
}
@Override
public Object put(String key, Object value) {
if (key == null) {
throw new NullPointerException();
}
if (key.equals(getClass().getAnnotation(DefaultProperty.class).value())) {
throw new IllegalArgumentException();
}
return values.put(key, value);
}
@Override
public Set<Entry<String, Object>> entrySet() {
return Collections.emptySet();
}
}
@Override
public void processEndElement() throws IOException {
}
@Override
public Object constructValue() throws LoadException {
return new UnknownValueMap();
}
}
private class IncludeElement extends ValueElement {
public String source = null;
public ResourceBundle resources = FXMLLoader.this.resources;
public Charset charset = FXMLLoader.this.charset;
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix == null) {
if (localName.equals(INCLUDE_SOURCE_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
source = value;
} else if (localName.equals(INCLUDE_RESOURCES_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
resources = ResourceBundle.getBundle(value, Locale.getDefault(),
FXMLLoader.this.resources.getClass().getClassLoader());
} else if (localName.equals(INCLUDE_CHARSET_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
charset = Charset.forName(value);
} else {
super.processAttribute(prefix, localName, value);
}
} else {
super.processAttribute(prefix, localName, value);
}
}
@Override
public Object constructValue() throws IOException {
if (source == null) {
throw constructLoadException(INCLUDE_SOURCE_ATTRIBUTE + " is required.");
}
URL location;
final ClassLoader cl = getClassLoader();
if (source.charAt(0) == '/') {
location = cl.getResource(source.substring(1));
if (location == null) {
throw constructLoadException("Cannot resolve path: " + source);
}
} else {
if (FXMLLoader.this.location == null) {
throw constructLoadException("Base location is undefined.");
}
location = new URL(FXMLLoader.this.location, source);
}
FXMLLoader fxmlLoader = new FXMLLoader(location, resources,
builderFactory, controllerFactory, charset,
loaders);
fxmlLoader.parentLoader = FXMLLoader.this;
if (isCyclic(FXMLLoader.this, fxmlLoader)) {
throw new IOException(
String.format(
"Including \"%s\" in \"%s\" created cyclic reference.",
fxmlLoader.location.toExternalForm(),
FXMLLoader.this.location.toExternalForm()));
}
fxmlLoader.setClassLoader(cl);
fxmlLoader.setStaticLoad(staticLoad);
Object value = fxmlLoader.loadImpl(callerClass);
if (fx_id != null) {
String id = this.fx_id + CONTROLLER_SUFFIX;
Object controller = fxmlLoader.getController();
namespace.put(id, controller);
injectFields(id, controller);
}
return value;
}
}
private void injectFields(String fieldName, Object value) throws LoadException {
if (controller != null && fieldName != null) {
List<Field> fields = controllerAccessor.getControllerFields().get(fieldName);
if (fields != null) {
try {
for (Field f : fields) {
f.set(controller, value);
}
} catch (IllegalAccessException exception) {
throw constructLoadException(exception);
}
}
}
}
private class ReferenceElement extends ValueElement {
public String source = null;
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix == null) {
if (localName.equals(REFERENCE_SOURCE_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
source = value;
} else {
super.processAttribute(prefix, localName, value);
}
} else {
super.processAttribute(prefix, localName, value);
}
}
@Override
public Object constructValue() throws LoadException {
if (source == null) {
throw constructLoadException(REFERENCE_SOURCE_ATTRIBUTE + " is required.");
}
KeyPath path = KeyPath.parse(source);
if (!Expression.isDefined(namespace, path)) {
throw constructLoadException("Value \"" + source + "\" does not exist.");
}
return Expression.get(namespace, path);
}
}
private class CopyElement extends ValueElement {
public String source = null;
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix == null) {
if (localName.equals(COPY_SOURCE_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
source = value;
} else {
super.processAttribute(prefix, localName, value);
}
} else {
super.processAttribute(prefix, localName, value);
}
}
@Override
public Object constructValue() throws LoadException {
if (source == null) {
throw constructLoadException(COPY_SOURCE_ATTRIBUTE + " is required.");
}
KeyPath path = KeyPath.parse(source);
if (!Expression.isDefined(namespace, path)) {
throw constructLoadException("Value \"" + source + "\" does not exist.");
}
Object sourceValue = Expression.get(namespace, path);
Class<?> sourceValueType = sourceValue.getClass();
Constructor<?> constructor = null;
try {
constructor = ConstructorUtil.getConstructor(sourceValueType, new Class[] { sourceValueType });
} catch (NoSuchMethodException exception) {
}
Object value;
if (constructor != null) {
try {
ReflectUtil.checkPackageAccess(sourceValueType);
value = constructor.newInstance(sourceValue);
} catch (InstantiationException exception) {
throw constructLoadException(exception);
} catch (IllegalAccessException exception) {
throw constructLoadException(exception);
} catch (InvocationTargetException exception) {
throw constructLoadException(exception);
}
} else {
throw constructLoadException("Can't copy value " + sourceValue + ".");
}
return value;
}
}
private class RootElement extends ValueElement {
public String type = null;
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix == null) {
if (localName.equals(ROOT_TYPE_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
type = value;
} else {
super.processAttribute(prefix, localName, value);
}
} else {
super.processAttribute(prefix, localName, value);
}
}
@Override
public Object constructValue() throws LoadException {
if (type == null) {
throw constructLoadException(ROOT_TYPE_ATTRIBUTE + " is required.");
}
Class<?> type = getType(this.type);
if (type == null) {
throw constructLoadException(this.type + " is not a valid type.");
}
Object value;
if (root == null) {
if (staticLoad) {
value = (builderFactory == null) ? null : builderFactory.getBuilder(type);
if (value == null) {
value = DEFAULT_BUILDER_FACTORY.getBuilder(type);
}
if (value == null) {
try {
ReflectUtil.checkPackageAccess(type);
value = type.getDeclaredConstructor().newInstance();
} catch (Exception e) {
throw constructLoadException(e);
}
}
root = value;
} else {
throw constructLoadException("Root hasn't been set. Use method setRoot() before load.");
}
} else {
if (!type.isAssignableFrom(root.getClass())) {
throw constructLoadException("Root is not an instance of "
+ type.getName() + ".");
}
value = root;
}
return value;
}
}
private class PropertyElement extends Element {
public final String name;
public final Class<?> sourceType;
public final boolean readOnly;
public PropertyElement(String name, Class<?> sourceType) throws LoadException {
if (parent == null) {
throw constructLoadException("Invalid root element.");
}
if (parent.value == null) {
throw constructLoadException("Parent element does not support property elements.");
}
this.name = name;
this.sourceType = sourceType;
if (sourceType == null) {
if (name.startsWith(EVENT_HANDLER_PREFIX)) {
throw constructLoadException("\"" + name + "\" is not a valid element name.");
}
Map<String, Object> parentProperties = parent.getProperties();
if (parent.isTyped()) {
readOnly = parent.getValueAdapter().isReadOnly(name);
} else {
readOnly = parentProperties.containsKey(name);
}
if (readOnly) {
Object value = parentProperties.get(name);
if (value == null) {
throw constructLoadException("Invalid property.");
}
updateValue(value);
}
} else {
readOnly = false;
}
}
@Override
public boolean isCollection() {
return (readOnly) ? super.isCollection() : false;
}
@Override
public void add(Object element) throws LoadException {
if (parent.isTyped()) {
Type listType = parent.getValueAdapter().getGenericType(name);
element = BeanAdapter.coerce(element, BeanAdapter.getListItemType(listType));
}
super.add(element);
}
@Override
public void set(Object value) throws LoadException {
updateValue(value);
if (sourceType == null) {
parent.getProperties().put(name, value);
} else {
if (parent.value instanceof Builder) {
parent.staticPropertyElements.add(this);
} else {
BeanAdapter.put(parent.value, sourceType, name, value);
}
}
}
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (!readOnly) {
throw constructLoadException("Attributes are not supported for writable property elements.");
}
super.processAttribute(prefix, localName, value);
}
@Override
public void processEndElement() throws IOException {
super.processEndElement();
if (readOnly) {
processInstancePropertyAttributes();
processEventHandlerAttributes();
}
}
@Override
public void processCharacters() throws IOException {
String text = xmlStreamReader.getText();
text = extraneousWhitespacePattern.matcher(text).replaceAll(" ").trim();
if (readOnly) {
if (isCollection()) {
add(text);
} else {
super.processCharacters();
}
} else {
set(text);
}
}
}
private class UnknownStaticPropertyElement extends Element {
public UnknownStaticPropertyElement() throws LoadException {
if (parent == null) {
throw constructLoadException("Invalid root element.");
}
if (parent.value == null) {
throw constructLoadException("Parent element does not support property elements.");
}
}
@Override
public boolean isCollection() {
return false;
}
@Override
public void set(Object value) {
updateValue(value);
}
@Override
public void processCharacters() throws IOException {
String text = xmlStreamReader.getText();
text = extraneousWhitespacePattern.matcher(text).replaceAll(" ");
updateValue(text.trim());
}
}
private class ScriptElement extends Element {
public String source = null;
public Charset charset = FXMLLoader.this.charset;
@Override
public boolean isCollection() {
return false;
}
@Override
public void processStartElement() throws IOException {
super.processStartElement();
if (source != null && !staticLoad) {
int i = source.lastIndexOf(".");
if (i == -1) {
throw constructLoadException("Cannot determine type of script \""
+ source + "\".");
}
String extension = source.substring(i + 1);
ScriptEngine engine;
final ClassLoader cl = getClassLoader();
if (scriptEngine != null && scriptEngine.getFactory().getExtensions().contains(extension)) {
engine = scriptEngine;
} else {
ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
try {
Thread.currentThread().setContextClassLoader(cl);
ScriptEngineManager scriptEngineManager = getScriptEngineManager();
engine = scriptEngineManager.getEngineByExtension(extension);
} finally {
Thread.currentThread().setContextClassLoader(oldLoader);
}
}
if (engine == null) {
throw constructLoadException("Unable to locate scripting engine for"
+ " extension " + extension + ".");
}
try {
URL location;
if (source.charAt(0) == '/') {
location = cl.getResource(source.substring(1));
} else {
if (FXMLLoader.this.location == null) {
throw constructLoadException("Base location is undefined.");
}
location = new URL(FXMLLoader.this.location, source);
}
Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
String filename = location.getPath();
engineBindings.put(engine.FILENAME, filename);
InputStreamReader scriptReader = null;
String script = null;
try {
scriptReader = new InputStreamReader(location.openStream(), charset);
StringBuilder sb = new StringBuilder();
final int bufSize = 4096;
char[] charBuffer = new char[bufSize];
int n;
do {
n = scriptReader.read(charBuffer,0,bufSize);
if (n > 0) {
sb.append(new String(charBuffer,0,n));
}
} while (n > -1);
script = sb.toString();
} catch (IOException exception) {
throw constructLoadException(exception);
} finally {
if (scriptReader != null) {
scriptReader.close();
}
}
try {
if (engine instanceof Compilable && compileScript) {
CompiledScript compiledScript = null;
try {
compiledScript = ((Compilable) engine).compile(script);
} catch (ScriptException compileExc) {
Logging.getJavaFXLogger().warning(filename + ": compiling caused \"" + compileExc +
"\", falling back to evaluating script in uncompiled mode");
}
if (compiledScript != null) {
compiledScript.eval();
} else {
engine.eval(script);
}
} else {
engine.eval(script);
}
} catch (ScriptException exception) {
System.err.println(filename + ": caused ScriptException");
exception.printStackTrace();
}
}
catch (IOException exception) {
throw constructLoadException(exception);
}
}
}
@Override
public void processEndElement() throws IOException {
super.processEndElement();
if (value != null && !staticLoad) {
String filename = null;
try {
Bindings engineBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
String script = (String) value;
filename = location.getPath() + "-script_starting_at_line_"
+ (getLineNumber() - (int) script.codePoints().filter(c -> c == '\n').count());
engineBindings.put(scriptEngine.FILENAME, filename);
if (scriptEngine instanceof Compilable && compileScript) {
CompiledScript compiledScript = null;
try {
compiledScript = ((Compilable) scriptEngine).compile(script);
} catch (ScriptException compileExc) {
Logging.getJavaFXLogger().warning(filename + ": compiling caused \"" + compileExc +
"\", falling back to evaluating script in uncompiled mode");
}
if (compiledScript != null) {
compiledScript.eval();
} else {
scriptEngine.eval(script);
}
} else {
scriptEngine.eval(script);
}
} catch (ScriptException exception) {
System.err.println(filename + ": caused ScriptException\n" + exception.getMessage());
}
}
}
@Override
public void processCharacters() throws LoadException {
if (source != null) {
throw constructLoadException("Script source already specified.");
}
if (scriptEngine == null && !staticLoad) {
throw constructLoadException("Page language not specified.");
}
updateValue(xmlStreamReader.getText());
}
@Override
public void processAttribute(String prefix, String localName, String value)
throws IOException {
if (prefix == null
&& localName.equals(SCRIPT_SOURCE_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
source = value;
} else if (localName.equals(SCRIPT_CHARSET_ATTRIBUTE)) {
if (loadListener != null) {
loadListener.readInternalAttribute(localName, value);
}
charset = Charset.forName(value);
} else {
throw constructLoadException(prefix == null ? localName : prefix + ":" + localName
+ " is not a valid attribute.");
}
}
}
private class DefineElement extends Element {
@Override
public boolean isCollection() {
return true;
}
@Override
public void add(Object element) {
}
@Override
public void processAttribute(String prefix, String localName, String value)
throws LoadException{
throw constructLoadException("Element does not support attributes.");
}
}
private static class Attribute {
public final String name;
public final Class<?> sourceType;
public final String value;
public Attribute(String name, Class<?> sourceType, String value) {
this.name = name;
this.sourceType = sourceType;
this.value = value;
}
}
private static class ControllerMethodEventHandler<T extends Event> implements EventHandler<T> {
private final MethodHandler handler;
public ControllerMethodEventHandler(MethodHandler handler) {
this.handler = handler;
}
@Override
public void handle(T event) {
handler.invoke(event);
}
}
private static class ScriptEventHandler implements EventHandler<Event> {
public final String script;
public final ScriptEngine scriptEngine;
public final String filename;
public CompiledScript compiledScript;
public boolean isCompiled = false;
public ScriptEventHandler(String script, ScriptEngine scriptEngine, String filename) {
this.script = script;
this.scriptEngine = scriptEngine;
this.filename = filename;
if (scriptEngine instanceof Compilable && compileScript) {
try {
scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(scriptEngine.FILENAME, filename);
this.compiledScript = ((Compilable) scriptEngine).compile(script);
this.isCompiled = true;
} catch (ScriptException compileExc) {
Logging.getJavaFXLogger().warning(filename + ": compiling caused \"" + compileExc +
"\", falling back to evaluating script in uncompiled mode");
}
}
}
@Override
public void handle(Event event) {
Bindings engineBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
Bindings localBindings = scriptEngine.createBindings();
localBindings.putAll(engineBindings);
localBindings.put(EVENT_KEY, event);
localBindings.put(scriptEngine.ARGV, new Object[]{event});
localBindings.put(scriptEngine.FILENAME, filename);
try {
if (isCompiled) {
compiledScript.eval(localBindings);
} else {
scriptEngine.eval(script, localBindings);
}
} catch (ScriptException exception) {
throw new RuntimeException(filename + ": caused ScriptException", exception);
}
}
}
private static class ObservableListChangeAdapter implements ListChangeListener {
private final MethodHandler handler;
public ObservableListChangeAdapter(MethodHandler handler) {
this.handler = handler;
}
@Override
@SuppressWarnings("unchecked")
public void onChanged(Change change) {
if (handler != null) {
handler.invoke(change);
}
}
}
private static class ObservableMapChangeAdapter implements MapChangeListener {
public final MethodHandler handler;
public ObservableMapChangeAdapter(MethodHandler handler) {
this.handler = handler;
}
@Override
public void onChanged(Change change) {
if (handler != null) {
handler.invoke(change);
}
}
}
private static class ObservableSetChangeAdapter implements SetChangeListener {
public final MethodHandler handler;
public ObservableSetChangeAdapter(MethodHandler handler) {
this.handler = handler;
}
@Override
public void onChanged(Change change) {
if (handler != null) {
handler.invoke(change);
}
}
}
private static class PropertyChangeAdapter implements ChangeListener<Object> {
public final MethodHandler handler;
public PropertyChangeAdapter(MethodHandler handler) {
this.handler = handler;
}
@Override
public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
handler.invoke(observable, oldValue, newValue);
}
}
private static class MethodHandler {
private final Object controller;
private final Method method;
private final SupportedType type;
private MethodHandler(Object controller, Method method, SupportedType type) {
this.method = method;
this.controller = controller;
this.type = type;
}
public void invoke(Object... params) {
try {
if (type != SupportedType.PARAMETERLESS) {
MethodHelper.invoke(method, controller, params);
} else {
MethodHelper.invoke(method, controller, new Object[] {});
}
} catch (InvocationTargetException exception) {
throw new RuntimeException(exception);
} catch (IllegalAccessException exception) {
throw new RuntimeException(exception);
}
}
}
private URL location;
private ResourceBundle resources;
private ObservableMap<String, Object> namespace = FXCollections.observableHashMap();
private Object root = null;
private Object controller = null;
private BuilderFactory builderFactory;
private Callback<Class<?>, Object> controllerFactory;
private Charset charset;
private final LinkedList<FXMLLoader> loaders;
private ClassLoader classLoader = null;
private boolean staticLoad = false;
private LoadListener loadListener = null;
private FXMLLoader parentLoader;
private XMLStreamReader xmlStreamReader = null;
private Element current = null;
private ScriptEngine scriptEngine = null;
private static boolean compileScript = true;
private List<String> packages = new LinkedList<String>();
private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
private ScriptEngineManager scriptEngineManager = null;
private static ClassLoader defaultClassLoader = null;
private static final Pattern extraneousWhitespacePattern = Pattern.compile("\\s+");
private static BuilderFactory DEFAULT_BUILDER_FACTORY = new JavaFXBuilderFactory();
public static final String DEFAULT_CHARSET_NAME = "UTF-8";
public static final String LANGUAGE_PROCESSING_INSTRUCTION = "language";
public static final String IMPORT_PROCESSING_INSTRUCTION = "import";
public static final String COMPILE_PROCESSING_INSTRUCTION = "compile";
public static final String FX_NAMESPACE_PREFIX = "fx";
public static final String FX_CONTROLLER_ATTRIBUTE = "controller";
public static final String FX_ID_ATTRIBUTE = "id";
public static final String FX_VALUE_ATTRIBUTE = "value";
public static final String FX_CONSTANT_ATTRIBUTE = "constant";
public static final String FX_FACTORY_ATTRIBUTE = "factory";
public static final String INCLUDE_TAG = "include";
public static final String INCLUDE_SOURCE_ATTRIBUTE = "source";
public static final String INCLUDE_RESOURCES_ATTRIBUTE = "resources";
public static final String INCLUDE_CHARSET_ATTRIBUTE = "charset";
public static final String SCRIPT_TAG = "script";
public static final String SCRIPT_SOURCE_ATTRIBUTE = "source";
public static final String SCRIPT_CHARSET_ATTRIBUTE = "charset";
public static final String DEFINE_TAG = "define";
public static final String REFERENCE_TAG = "reference";
public static final String REFERENCE_SOURCE_ATTRIBUTE = "source";
public static final String ROOT_TAG = "root";
public static final String ROOT_TYPE_ATTRIBUTE = "type";
public static final String COPY_TAG = "copy";
public static final String COPY_SOURCE_ATTRIBUTE = "source";
public static final String EVENT_HANDLER_PREFIX = "on";
public static final String EVENT_KEY = "event";
public static final String CHANGE_EVENT_HANDLER_SUFFIX = "Change";
private static final String COLLECTION_HANDLER_NAME = EVENT_HANDLER_PREFIX + CHANGE_EVENT_HANDLER_SUFFIX;
public static final String NULL_KEYWORD = "null";
public static final String ESCAPE_PREFIX = "\\";
public static final String RELATIVE_PATH_PREFIX = "@";
public static final String RESOURCE_KEY_PREFIX = "%";
public static final String EXPRESSION_PREFIX = "$";
public static final String BINDING_EXPRESSION_PREFIX = "${";
public static final String BINDING_EXPRESSION_SUFFIX = "}";
public static final String BI_DIRECTIONAL_BINDING_PREFIX = "#{";
public static final String BI_DIRECTIONAL_BINDING_SUFFIX = "}";
public static final String ARRAY_COMPONENT_DELIMITER = ",";
public static final String LOCATION_KEY = "location";
public static final String RESOURCES_KEY = "resources";
public static final String CONTROLLER_METHOD_PREFIX = "#";
public static final String CONTROLLER_KEYWORD = "controller";
public static final String CONTROLLER_SUFFIX = "Controller";
public static final String INITIALIZE_METHOD_NAME = "initialize";
public static final String JAVAFX_VERSION;
public static final String FX_NAMESPACE_VERSION = "1";
static {
@SuppressWarnings("removal")
String tmp = AccessController.doPrivileged(new PrivilegedAction<String>() {
@Override
public String run() {
return System.getProperty("javafx.version");
}
});
JAVAFX_VERSION = tmp;
FXMLLoaderHelper.setFXMLLoaderAccessor(new FXMLLoaderHelper.FXMLLoaderAccessor() {
@Override
public void setStaticLoad(FXMLLoader fxmlLoader, boolean staticLoad) {
fxmlLoader.setStaticLoad(staticLoad);
}
});
}
public FXMLLoader() {
this((URL)null);
}
public FXMLLoader(URL location) {
this(location, null);
}
public FXMLLoader(URL location, ResourceBundle resources) {
this(location, resources, null);
}
public FXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory) {
this(location, resources, builderFactory, null);
}
public FXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory) {
this(location, resources, builderFactory, controllerFactory, Charset.forName(DEFAULT_CHARSET_NAME));
}
public FXMLLoader(Charset charset) {
this(null, null, null, null, charset);
}
public FXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory, Charset charset) {
this(location, resources, builderFactory, controllerFactory, charset,
new LinkedList<FXMLLoader>());
}
public FXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory, Charset charset,
LinkedList<FXMLLoader> loaders) {
setLocation(location);
setResources(resources);
setBuilderFactory(builderFactory);
setControllerFactory(controllerFactory);
setCharset(charset);
this.loaders = new LinkedList(loaders);
}
public URL getLocation() {
return location;
}
public void setLocation(URL location) {
this.location = location;
}
public ResourceBundle getResources() {
return resources;
}
public void setResources(ResourceBundle resources) {
this.resources = resources;
}
public ObservableMap<String, Object> getNamespace() {
return namespace;
}
@SuppressWarnings("unchecked")
public <T> T getRoot() {
return (T)root;
}
public void setRoot(Object root) {
this.root = root;
}
@Override
public boolean equals(Object obj) {
if (obj instanceof FXMLLoader) {
FXMLLoader loader = (FXMLLoader)obj;
if (location == null || loader.location == null) {
return loader.location == location;
}
return location.toExternalForm().equals(
loader.location.toExternalForm());
}
return false;
}
private boolean isCyclic(
FXMLLoader currentLoader,
FXMLLoader node) {
if (currentLoader == null) {
return false;
}
if (currentLoader.equals(node)) {
return true;
}
return isCyclic(currentLoader.parentLoader, node);
}
@SuppressWarnings("unchecked")
public <T> T getController() {
return (T)controller;
}
public void setController(Object controller) {
this.controller = controller;
if (controller == null) {
namespace.remove(CONTROLLER_KEYWORD);
} else {
namespace.put(CONTROLLER_KEYWORD, controller);
}
controllerAccessor.setController(controller);
}
public BuilderFactory getBuilderFactory() {
return builderFactory;
}
public void setBuilderFactory(BuilderFactory builderFactory) {
this.builderFactory = builderFactory;
}
public Callback<Class<?>, Object> getControllerFactory() {
return controllerFactory;
}
public void setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
this.controllerFactory = controllerFactory;
}
public Charset getCharset() {
return charset;
}
public void setCharset(Charset charset) {
if (charset == null) {
throw new NullPointerException("charset is null.");
}
this.charset = charset;
}
public ClassLoader getClassLoader() {
if (classLoader == null) {
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
final Class caller = (sm != null) ?
walker.getCallerClass() :
null;
return getDefaultClassLoader(caller);
}
return classLoader;
}
public void setClassLoader(ClassLoader classLoader) {
if (classLoader == null) {
throw new IllegalArgumentException();
}
this.classLoader = classLoader;
clearImports();
}
boolean isStaticLoad() {
return staticLoad;
}
void setStaticLoad(boolean staticLoad) {
this.staticLoad = staticLoad;
}
public LoadListener getLoadListener() {
return loadListener;
}
public final void setLoadListener(LoadListener loadListener) {
this.loadListener = loadListener;
}
@SuppressWarnings("removal")
public <T> T load() throws IOException {
return loadImpl((System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
@SuppressWarnings("removal")
public <T> T load(InputStream inputStream) throws IOException {
return loadImpl(inputStream, (System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private Class<?> callerClass;
private <T> T loadImpl(final Class<?> callerClass) throws IOException {
if (location == null) {
throw new IllegalStateException("Location is not set.");
}
InputStream inputStream = null;
T value;
try {
inputStream = location.openStream();
value = loadImpl(inputStream, callerClass);
} finally {
if (inputStream != null) {
inputStream.close();
}
}
return value;
}
@SuppressWarnings({ "dep-ann", "unchecked" })
private <T> T loadImpl(InputStream inputStream,
Class<?> callerClass) throws IOException {
if (inputStream == null) {
throw new NullPointerException("inputStream is null.");
}
this.callerClass = callerClass;
controllerAccessor.setCallerClass(callerClass);
try {
clearImports();
namespace.put(LOCATION_KEY, location);
namespace.put(RESOURCES_KEY, resources);
scriptEngine = null;
try {
XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
xmlStreamReader = new StreamReaderDelegate(xmlInputFactory.createXMLStreamReader(inputStreamReader)) {
@Override
public String getPrefix() {
String prefix = super.getPrefix();
if (prefix != null
&& prefix.length() == 0) {
prefix = null;
}
return prefix;
}
@Override
public String getAttributePrefix(int index) {
String attributePrefix = super.getAttributePrefix(index);
if (attributePrefix != null
&& attributePrefix.length() == 0) {
attributePrefix = null;
}
return attributePrefix;
}
};
} catch (XMLStreamException exception) {
throw constructLoadException(exception);
}
loaders.push(this);
try {
while (xmlStreamReader.hasNext()) {
int event = xmlStreamReader.next();
switch (event) {
case XMLStreamConstants.PROCESSING_INSTRUCTION: {
processProcessingInstruction();
break;
}
case XMLStreamConstants.COMMENT: {
processComment();
break;
}
case XMLStreamConstants.START_ELEMENT: {
processStartElement();
break;
}
case XMLStreamConstants.END_ELEMENT: {
processEndElement();
break;
}
case XMLStreamConstants.CHARACTERS: {
processCharacters();
break;
}
}
}
} catch (XMLStreamException exception) {
throw constructLoadException(exception);
}
if (controller != null) {
if (controller instanceof Initializable) {
((Initializable)controller).initialize(location, resources);
} else {
Map<String, List<Field>> controllerFields =
controllerAccessor.getControllerFields();
injectFields(LOCATION_KEY, location);
injectFields(RESOURCES_KEY, resources);
Method initializeMethod = controllerAccessor
.getControllerMethods()
.get(SupportedType.PARAMETERLESS)
.get(INITIALIZE_METHOD_NAME);
if (initializeMethod != null) {
try {
MethodHelper.invoke(initializeMethod, controller, new Object [] {});
} catch (IllegalAccessException exception) {
throw constructLoadException(exception);
} catch (InvocationTargetException exception) {
throw constructLoadException(exception);
}
}
}
}
} catch (final LoadException exception) {
throw exception;
} catch (final Exception exception) {
throw constructLoadException(exception);
} finally {
controllerAccessor.setCallerClass(null);
controllerAccessor.reset();
xmlStreamReader = null;
}
return (T)root;
}
private void clearImports() {
packages.clear();
classes.clear();
}
private LoadException constructLoadException(String message){
return new LoadException(message + constructFXMLTrace());
}
private LoadException constructLoadException(Throwable cause) {
return new LoadException(constructFXMLTrace(), cause);
}
private LoadException constructLoadException(String message, Throwable cause){
return new LoadException(message + constructFXMLTrace(), cause);
}
private String constructFXMLTrace() {
StringBuilder messageBuilder = new StringBuilder("\n");
for (FXMLLoader loader : loaders) {
messageBuilder.append(loader.location != null ? loader.location.getPath() : "unknown path");
if (loader.current != null) {
messageBuilder.append(":");
messageBuilder.append(loader.getLineNumber());
}
messageBuilder.append("\n");
}
return messageBuilder.toString();
}
int getLineNumber() {
return xmlStreamReader.getLocation().getLineNumber();
}
ParseTraceElement[] getParseTrace() {
ParseTraceElement[] parseTrace = new ParseTraceElement[loaders.size()];
int i = 0;
for (FXMLLoader loader : loaders) {
parseTrace[i++] = new ParseTraceElement(loader.location, (loader.current != null) ?
loader.getLineNumber() : -1);
}
return parseTrace;
}
private void processProcessingInstruction() throws LoadException {
String piTarget = xmlStreamReader.getPITarget().trim();
if (piTarget.equals(LANGUAGE_PROCESSING_INSTRUCTION)) {
processLanguage();
} else if (piTarget.equals(IMPORT_PROCESSING_INSTRUCTION)) {
processImport();
} else if (piTarget.equals(COMPILE_PROCESSING_INSTRUCTION)) {
String strCompile = xmlStreamReader.getPIData().trim();
compileScript = (strCompile.length() == 0 ? true : Boolean.parseBoolean(strCompile));
}
}
private void processLanguage() throws LoadException {
if (scriptEngine != null) {
throw constructLoadException("Page language already set.");
}
String language = xmlStreamReader.getPIData();
if (loadListener != null) {
loadListener.readLanguageProcessingInstruction(language);
}
if (!staticLoad) {
ScriptEngineManager scriptEngineManager = getScriptEngineManager();
scriptEngine = scriptEngineManager.getEngineByName(language);
}
}
private void processImport() throws LoadException {
String target = xmlStreamReader.getPIData().trim();
if (loadListener != null) {
loadListener.readImportProcessingInstruction(target);
}
if (target.endsWith(".*")) {
importPackage(target.substring(0, target.length() - 2));
} else {
importClass(target);
}
}
private void processComment() throws LoadException {
if (loadListener != null) {
loadListener.readComment(xmlStreamReader.getText());
}
}
private void processStartElement() throws IOException {
createElement();
current.processStartElement();
if (root == null) {
root = current.value;
}
}
private void createElement() throws IOException {
String prefix = xmlStreamReader.getPrefix();
String localName = xmlStreamReader.getLocalName();
if (prefix == null) {
int i = localName.lastIndexOf('.');
if (Character.isLowerCase(localName.charAt(i + 1))) {
String name = localName.substring(i + 1);
if (i == -1) {
if (loadListener != null) {
loadListener.beginPropertyElement(name, null);
}
current = new PropertyElement(name, null);
} else {
Class<?> sourceType = getType(localName.substring(0, i));
if (sourceType != null) {
if (loadListener != null) {
loadListener.beginPropertyElement(name, sourceType);
}
current = new PropertyElement(name, sourceType);
} else if (staticLoad) {
if (loadListener != null) {
loadListener.beginUnknownStaticPropertyElement(localName);
}
current = new UnknownStaticPropertyElement();
} else {
throw constructLoadException(localName + " is not a valid property.");
}
}
} else {
if (current == null && root != null) {
throw constructLoadException("Root value already specified.");
}
Class<?> type = getType(localName);
if (type != null) {
if (loadListener != null) {
loadListener.beginInstanceDeclarationElement(type);
}
current = new InstanceDeclarationElement(type);
} else if (staticLoad) {
if (loadListener != null) {
loadListener.beginUnknownTypeElement(localName);
}
current = new UnknownTypeElement();
} else {
throw constructLoadException(localName + " is not a valid type.");
}
}
} else if (prefix.equals(FX_NAMESPACE_PREFIX)) {
if (localName.equals(INCLUDE_TAG)) {
if (loadListener != null) {
loadListener.beginIncludeElement();
}
current = new IncludeElement();
} else if (localName.equals(REFERENCE_TAG)) {
if (loadListener != null) {
loadListener.beginReferenceElement();
}
current = new ReferenceElement();
} else if (localName.equals(COPY_TAG)) {
if (loadListener != null) {
loadListener.beginCopyElement();
}
current = new CopyElement();
} else if (localName.equals(ROOT_TAG)) {
if (loadListener != null) {
loadListener.beginRootElement();
}
current = new RootElement();
} else if (localName.equals(SCRIPT_TAG)) {
if (loadListener != null) {
loadListener.beginScriptElement();
}
current = new ScriptElement();
} else if (localName.equals(DEFINE_TAG)) {
if (loadListener != null) {
loadListener.beginDefineElement();
}
current = new DefineElement();
} else {
throw constructLoadException(prefix + ":" + localName + " is not a valid element.");
}
} else {
throw constructLoadException("Unexpected namespace prefix: " + prefix + ".");
}
}
private void processEndElement() throws IOException {
current.processEndElement();
if (loadListener != null) {
loadListener.endElement(current.value);
}
current = current.parent;
}
private void processCharacters() throws IOException {
if (!xmlStreamReader.isWhiteSpace()) {
current.processCharacters();
}
}
private void importPackage(String name) throws LoadException {
packages.add(name);
}
private void importClass(String name) throws LoadException {
try {
loadType(name, true);
} catch (ClassNotFoundException exception) {
throw constructLoadException(exception);
}
}
private Class<?> getType(String name) throws LoadException {
Class<?> type = null;
if (Character.isLowerCase(name.charAt(0))) {
try {
type = loadType(name, false);
} catch (ClassNotFoundException exception) {
}
} else {
type = classes.get(name);
if (type == null) {
for (String packageName : packages) {
try {
type = loadTypeForPackage(packageName, name);
} catch (ClassNotFoundException exception) {
}
if (type != null) {
break;
}
}
if (type != null) {
classes.put(name, type);
}
}
}
return type;
}
private Class<?> loadType(String name, boolean cache) throws ClassNotFoundException {
int i = name.indexOf('.');
int n = name.length();
while (i != -1
&& i < n
&& Character.isLowerCase(name.charAt(i + 1))) {
i = name.indexOf('.', i + 1);
}
if (i == -1 || i == n) {
throw new ClassNotFoundException();
}
String packageName = name.substring(0, i);
String className = name.substring(i + 1);
Class<?> type = loadTypeForPackage(packageName, className);
if (cache) {
classes.put(className, type);
}
return type;
}
private Class<?> loadTypeForPackage(String packageName, String className) throws ClassNotFoundException {
return getClassLoader().loadClass(packageName + "." + className.replace('.', '$'));
}
private static enum SupportedType {
PARAMETERLESS {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 0;
}
},
EVENT {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 1 &&
Event.class.isAssignableFrom(m.getParameterTypes()[0]);
}
},
LIST_CHANGE_LISTENER {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 1 &&
m.getParameterTypes()[0].equals(ListChangeListener.Change.class);
}
},
MAP_CHANGE_LISTENER {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 1 &&
m.getParameterTypes()[0].equals(MapChangeListener.Change.class);
}
},
SET_CHANGE_LISTENER {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 1 &&
m.getParameterTypes()[0].equals(SetChangeListener.Change.class);
}
},
PROPERTY_CHANGE_LISTENER {
@Override
protected boolean methodIsOfType(Method m) {
return m.getParameterTypes().length == 3 &&
ObservableValue.class.isAssignableFrom(m.getParameterTypes()[0])
&& m.getParameterTypes()[1].equals(m.getParameterTypes()[2]);
}
};
protected abstract boolean methodIsOfType(Method m);
}
private static SupportedType toSupportedType(Method m) {
for (SupportedType t : SupportedType.values()) {
if (t.methodIsOfType(m)) {
return t;
}
}
return null;
}
private ScriptEngineManager getScriptEngineManager() {
if (scriptEngineManager == null) {
scriptEngineManager = new javax.script.ScriptEngineManager();
scriptEngineManager.setBindings(new SimpleBindings(namespace));
}
return scriptEngineManager;
}
@Deprecated
public static Class<?> loadType(String packageName, String className) throws ClassNotFoundException {
return loadType(packageName + "." + className.replace('.', '$'));
}
@Deprecated
public static Class<?> loadType(String className) throws ClassNotFoundException {
ReflectUtil.checkPackageAccess(className);
return Class.forName(className, true, getDefaultClassLoader());
}
private static boolean needsClassLoaderPermissionCheck(Class caller) {
if (caller == null) {
return false;
}
return !FXMLLoader.class.getModule().equals(caller.getModule());
}
private static ClassLoader getDefaultClassLoader(Class caller) {
if (defaultClassLoader == null) {
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm != null) {
if (needsClassLoaderPermissionCheck(caller)) {
sm.checkPermission(GET_CLASSLOADER_PERMISSION);
}
}
return Thread.currentThread().getContextClassLoader();
}
return defaultClassLoader;
}
public static ClassLoader getDefaultClassLoader() {
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
final Class caller = (sm != null) ?
walker.getCallerClass() :
null;
return getDefaultClassLoader(caller);
}
public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
if (defaultClassLoader == null) {
throw new NullPointerException();
}
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(MODIFY_FXML_CLASS_LOADER_PERMISSION);
}
FXMLLoader.defaultClassLoader = defaultClassLoader;
}
@SuppressWarnings("removal")
public static <T> T load(URL location) throws IOException {
return loadImpl(location, (System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private static <T> T loadImpl(URL location, Class<?> callerClass)
throws IOException {
return loadImpl(location, null, callerClass);
}
@SuppressWarnings("removal")
public static <T> T load(URL location, ResourceBundle resources)
throws IOException {
return loadImpl(location, resources,
(System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private static <T> T loadImpl(URL location, ResourceBundle resources,
Class<?> callerClass) throws IOException {
return loadImpl(location, resources, null,
callerClass);
}
@SuppressWarnings("removal")
public static <T> T load(URL location, ResourceBundle resources,
BuilderFactory builderFactory)
throws IOException {
return loadImpl(location, resources, builderFactory,
(System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private static <T> T loadImpl(URL location, ResourceBundle resources,
BuilderFactory builderFactory,
Class<?> callerClass) throws IOException {
return loadImpl(location, resources, builderFactory, null, callerClass);
}
@SuppressWarnings("removal")
public static <T> T load(URL location, ResourceBundle resources,
BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory)
throws IOException {
return loadImpl(location, resources, builderFactory, controllerFactory,
(System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private static <T> T loadImpl(URL location, ResourceBundle resources,
BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory,
Class<?> callerClass) throws IOException {
return loadImpl(location, resources, builderFactory, controllerFactory,
Charset.forName(DEFAULT_CHARSET_NAME), callerClass);
}
@SuppressWarnings("removal")
public static <T> T load(URL location, ResourceBundle resources,
BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory,
Charset charset) throws IOException {
return loadImpl(location, resources, builderFactory, controllerFactory,
charset,
(System.getSecurityManager() != null)
? walker.getCallerClass()
: null);
}
private static <T> T loadImpl(URL location, ResourceBundle resources,
BuilderFactory builderFactory,
Callback<Class<?>, Object> controllerFactory,
Charset charset, Class<?> callerClass)
throws IOException {
if (location == null) {
throw new NullPointerException("Location is required.");
}
FXMLLoader fxmlLoader =
new FXMLLoader(location, resources, builderFactory,
controllerFactory, charset);
return fxmlLoader.<T>loadImpl(callerClass);
}
static int compareJFXVersions(String rtVer, String nsVer) {
int retVal = 0;
if (rtVer == null || "".equals(rtVer) ||
nsVer == null || "".equals(nsVer)) {
return retVal;
}
if (rtVer.equals(nsVer)) {
return retVal;
}
int dashIndex = rtVer.indexOf("-");
if (dashIndex > 0) {
rtVer = rtVer.substring(0, dashIndex);
}
int underIndex = rtVer.indexOf("_");
if (underIndex > 0) {
rtVer = rtVer.substring(0, underIndex);
}
if (!Pattern.matches("^(\\d+)(\\.\\d+)*$", rtVer) ||
!Pattern.matches("^(\\d+)(\\.\\d+)*$", nsVer)) {
return retVal;
}
StringTokenizer nsVerTokenizer = new StringTokenizer(nsVer, ".");
StringTokenizer rtVerTokenizer = new StringTokenizer(rtVer, ".");
int nsDigit = 0, rtDigit = 0;
boolean rtVerEnd = false;
while (nsVerTokenizer.hasMoreTokens() && retVal == 0) {
nsDigit = Integer.parseInt(nsVerTokenizer.nextToken());
if (rtVerTokenizer.hasMoreTokens()) {
rtDigit = Integer.parseInt(rtVerTokenizer.nextToken());
retVal = rtDigit - nsDigit;
} else {
rtVerEnd = true;
break;
}
}
if (rtVerTokenizer.hasMoreTokens() && retVal == 0) {
rtDigit = Integer.parseInt(rtVerTokenizer.nextToken());
if (rtDigit > 0) {
retVal = 1;
}
}
if (rtVerEnd) {
if (nsDigit > 0) {
retVal = -1;
} else {
while (nsVerTokenizer.hasMoreTokens()) {
nsDigit = Integer.parseInt(nsVerTokenizer.nextToken());
if (nsDigit > 0) {
retVal = -1;
break;
}
}
}
}
return retVal;
}
private static void checkClassLoaderPermission() {
@SuppressWarnings("removal")
final SecurityManager securityManager = System.getSecurityManager();
if (securityManager != null) {
securityManager.checkPermission(MODIFY_FXML_CLASS_LOADER_PERMISSION);
}
}
private final ControllerAccessor controllerAccessor =
new ControllerAccessor();
private static final class ControllerAccessor {
private static final int PUBLIC = 1;
private static final int PROTECTED = 2;
private static final int PACKAGE = 4;
private static final int PRIVATE = 8;
private static final int INITIAL_CLASS_ACCESS =
PUBLIC | PROTECTED | PACKAGE | PRIVATE;
private static final int INITIAL_MEMBER_ACCESS =
PUBLIC | PROTECTED | PACKAGE | PRIVATE;
private static final int METHODS = 0;
private static final int FIELDS = 1;
private Object controller;
private ClassLoader callerClassLoader;
private Map<String, List<Field>> controllerFields;
private Map<SupportedType, Map<String, Method>> controllerMethods;
void setController(final Object controller) {
if (this.controller != controller) {
this.controller = controller;
reset();
}
}
void setCallerClass(final Class<?> callerClass) {
final ClassLoader newCallerClassLoader =
(callerClass != null) ? callerClass.getClassLoader()
: null;
if (callerClassLoader != newCallerClassLoader) {
callerClassLoader = newCallerClassLoader;
reset();
}
}
void reset() {
controllerFields = null;
controllerMethods = null;
}
Map<String, List<Field>> getControllerFields() {
if (controllerFields == null) {
controllerFields = new HashMap<>();
if (callerClassLoader == null) {
checkClassLoaderPermission();
}
addAccessibleMembers(controller.getClass(),
INITIAL_CLASS_ACCESS,
INITIAL_MEMBER_ACCESS,
FIELDS);
}
return controllerFields;
}
Map<SupportedType, Map<String, Method>> getControllerMethods() {
if (controllerMethods == null) {
controllerMethods = new EnumMap<>(SupportedType.class);
for (SupportedType t: SupportedType.values()) {
controllerMethods.put(t, new HashMap<String, Method>());
}
if (callerClassLoader == null) {
checkClassLoaderPermission();
}
addAccessibleMembers(controller.getClass(),
INITIAL_CLASS_ACCESS,
INITIAL_MEMBER_ACCESS,
METHODS);
}
return controllerMethods;
}
private void addAccessibleMembers(final Class<?> type,
final int prevAllowedClassAccess,
final int prevAllowedMemberAccess,
final int membersType) {
if (type == Object.class) {
return;
}
int allowedClassAccess = prevAllowedClassAccess;
int allowedMemberAccess = prevAllowedMemberAccess;
if ((callerClassLoader != null)
&& (type.getClassLoader() != callerClassLoader)) {
allowedClassAccess &= PUBLIC;
allowedMemberAccess &= PUBLIC;
}
final int classAccess = getAccess(type.getModifiers());
if ((classAccess & allowedClassAccess) == 0) {
return;
}
ReflectUtil.checkPackageAccess(type);
addAccessibleMembers(type.getSuperclass(),
allowedClassAccess,
allowedMemberAccess,
membersType);
final int finalAllowedMemberAccess = allowedMemberAccess;
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
new PrivilegedAction<Void>() {
@Override
public Void run() {
if (membersType == FIELDS) {
addAccessibleFields(type,
finalAllowedMemberAccess);
} else {
addAccessibleMethods(type,
finalAllowedMemberAccess);
}
return null;
}
});
}
private void addAccessibleFields(final Class<?> type,
final int allowedMemberAccess) {
final boolean isPublicType = Modifier.isPublic(type.getModifiers());
final Field[] fields = type.getDeclaredFields();
for (int i = 0; i < fields.length; ++i) {
final Field field = fields[i];
final int memberModifiers = field.getModifiers();
if (((memberModifiers & (Modifier.STATIC
| Modifier.FINAL)) != 0)
|| ((getAccess(memberModifiers) & allowedMemberAccess)
== 0)) {
continue;
}
if (!isPublicType || !Modifier.isPublic(memberModifiers)) {
if (field.getAnnotation(FXML.class) == null) {
continue;
}
field.setAccessible(true);
}
List<Field> list = controllerFields.get(field.getName());
if (list == null) {
list = new ArrayList<>(1);
controllerFields.put(field.getName(), list);
}
list.add(field);
}
}
private void addAccessibleMethods(final Class<?> type,
final int allowedMemberAccess) {
final boolean isPublicType = Modifier.isPublic(type.getModifiers());
final Method[] methods = type.getDeclaredMethods();
for (int i = 0; i < methods.length; ++i) {
final Method method = methods[i];
final int memberModifiers = method.getModifiers();
if (((memberModifiers & (Modifier.STATIC
| Modifier.NATIVE)) != 0)
|| ((getAccess(memberModifiers) & allowedMemberAccess)
== 0)) {
continue;
}
if (!isPublicType || !Modifier.isPublic(memberModifiers)) {
if (method.getAnnotation(FXML.class) == null) {
continue;
}
method.setAccessible(true);
}
final String methodName = method.getName();
final SupportedType convertedType;
if ((convertedType = toSupportedType(method)) != null) {
controllerMethods.get(convertedType)
.put(methodName, method);
}
}
}
private static int getAccess(final int fullModifiers) {
final int untransformedAccess =
fullModifiers & (Modifier.PRIVATE | Modifier.PROTECTED
| Modifier.PUBLIC);
switch (untransformedAccess) {
case Modifier.PUBLIC:
return PUBLIC;
case Modifier.PROTECTED:
return PROTECTED;
case Modifier.PRIVATE:
return PRIVATE;
default:
return PACKAGE;
}
}
}
}
