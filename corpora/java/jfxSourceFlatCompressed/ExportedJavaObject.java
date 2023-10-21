package javafx.scene.web;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import com.sun.javafx.reflect.ReflectUtil;
class ExportedJavaObject {
private final JS2JavaBridge owner;
private final String objId;
private final Object javaObject;
private final Class cls;
private Method[] methods;
private List<String> jsNames = new ArrayList<String>(1);
private JS2JavaBridge getJSBridge() {
return owner;
}
public ExportedJavaObject(JS2JavaBridge owner, String objId, Object javaObject) {
this.owner = owner;
this.objId = objId;
this.javaObject = javaObject;
cls = javaObject.getClass();
ReflectUtil.checkPackageAccess(cls);
methods = getPublicMethods(cls);
}
public String getObjectId() {
return objId;
}
public Object getJavaObject() {
return javaObject;
}
public void addJSName(String jsName) {
jsNames.add(jsName);
}
public List<String> getJSNames() {
return Collections.unmodifiableList(jsNames);
}
public String getJSDecl() {
StringBuilder sb = new StringBuilder();
sb.append("{\n");
for (int i=0; i<methods.length; i++) {
if (i>0) {
sb.append(",\n");
}
Method m = methods[i];
String methodName = m.getName();
Class<?>[] params = m.getParameterTypes();
sb.append("  ").append(methodName).append(" : function(");
if (params.length > 0) {
sb.append("p0");
for (int j=1; j<params.length; j++) {
sb.append(", p").append(j);
}
}
sb.append(") {");
sb.append(" return ").append(owner.getJavaBridge()).append(".call('").append(objId).append(":").append(i).append("', [");
if (params.length > 0) {
sb.append("p0");
for (int j=1; j<params.length; j++) {
sb.append(", p").append(j);
}
}
sb.append("]); }");
}
sb.append("}");
return sb.toString();
}
public class CallException extends Exception {
public CallException(String message) {
super(message);
}
public CallException(String message, Throwable cause) {
super(message, cause);
}
}
private Object cast(Class<?> desiredType, Object value) throws Exception {
log("ExportedJavaObject.cast: desired=" + desiredType.getSimpleName()
+ ", value=" + (value == null ? "null" : value.getClass().getSimpleName()));
if (value == null) {
return null;
}
if (desiredType.isPrimitive()) {
Class[] wrapperClasses = new Class[]{
Byte.class, Short.class, Integer.class, Long.class,
Float.class, Double.class, Boolean.class, Character.class};
for (Class wrap: wrapperClasses) {
if (desiredType.equals(wrap.getField("TYPE").get(null))) {
log("ExportedJavaObject.cast: replace " + desiredType.getName()
+ " with " + wrap.getName());
desiredType = wrap;
break;
}
}
}
if (Number.class.isAssignableFrom(value.getClass())) {
Number n = (Number)value;
if (desiredType.equals(Byte.class)) {
value = Byte.valueOf(n.byteValue());
} else if (desiredType.equals(Short.class)) {
value = Short.valueOf(n.shortValue());
} else if (desiredType.equals(Integer.class)) {
value = Integer.valueOf(n.intValue());
} else if (desiredType.equals(Long.class)) {
value = Long.valueOf(n.longValue());
} else if (desiredType.equals(BigInteger.class)) {
value = BigInteger.valueOf(n.longValue());
} else if (desiredType.equals(Float.class)) {
value = Float.valueOf(n.floatValue());
} else if (desiredType.equals(Double.class)) {
value = Double.valueOf(n.doubleValue());
} else if (desiredType.equals(BigDecimal.class)) {
value = BigDecimal.valueOf(n.doubleValue());
}
}
return desiredType.cast(value);
}
public String call(final String methodName, final String args) throws CallException {
try {
return AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
@Override
public String run() throws Exception {
return callWorker(methodName, args);
}
}, getJSBridge().getAccessControlContext());
} catch (Exception e) {
if (e instanceof CallException) {
throw (CallException) e;
}
throw new UndeclaredThrowableException(e);
}
}
private String callWorker(String methodName, String args) throws CallException {
Method m;
try {
int mIndex = Integer.parseInt(methodName);
m = methods[mIndex];
} catch (Exception ex) {
throw new CallException("Wrong method id", ex);
}
log("call: " + javaObject.getClass().getSimpleName()
+ "." + m.getName() + "(" + args + ")");
Object[] params;
try {
Object argObj = getJSBridge().decode(args);
params = (Object[])argObj;
} catch (Exception ex) {
throw new CallException("Could not parse arguments", ex);
}
Class<?>[] paramTypes = m.getParameterTypes();
if (paramTypes.length != params.length) {
throw new CallException("Incorrect argument number: "
+ params.length + " instead " + paramTypes);
}
Object[] castedParams = new Object[paramTypes.length];
int i = 0;
try {
for (i=0; i<paramTypes.length; i++) {
castedParams[i] = cast(paramTypes[i], params[i]);
}
} catch (Exception ex) {
throw new CallException("Argument " + i + " casting error", ex);
}
Object resObj;
try {
resObj = m.invoke(javaObject, castedParams);
if (Void.class.equals(m.getReturnType())) {
return null;
}
} catch (InvocationTargetException ex) {
Throwable reason = ex.getTargetException();
throw new CallException("Java Exception ("
+ reason.getClass().getSimpleName() + "): "
+ reason.getMessage(), reason);
} catch (Exception ex) {
throw new CallException("Invoke error ("
+ ex.getClass().getSimpleName() + "): "
+ ex.getMessage(), ex);
}
try {
StringBuilder sb = new StringBuilder(1024);
getJSBridge().encode(resObj, sb);
return sb.toString();
} catch (Exception ex) {
throw new CallException("Result encoding error", ex);
}
}
private Method[] getPublicMethods(final Class clz) {
Method[] m = clz.getDeclaredMethods();
ArrayList<Method> am = new ArrayList<Method>();
for (int i = 0; i < m.length; i++) {
if (Modifier.isPublic(m[i].getModifiers())){
am.add(m[i]);
}
}
Method[] publicMethods = new Method[am.size()];
return am.toArray(publicMethods);
}
static void log(String s) {
JS2JavaBridge.log(s);
}
static void log(Exception ex) {
JS2JavaBridge.log(ex);
}
}
