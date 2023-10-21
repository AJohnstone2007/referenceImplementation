package com.sun.javafx.reflect;
import java.io.EOFException;
import java.security.AllPermission;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.security.PrivilegedExceptionAction;
import java.security.CodeSource;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.sun.javafx.reflect.ReflectUtil;
class Trampoline {
static {
if (Trampoline.class.getClassLoader() == null) {
throw new Error(
"Trampoline must not be defined by the bootstrap classloader");
}
if (Trampoline.class.getClassLoader() == ClassLoader.getPlatformClassLoader()) {
throw new Error(
"Trampoline must not be defined by the platform classloader");
}
}
@SuppressWarnings("removal")
private static void ensureInvocableMethod(Method m)
throws InvocationTargetException
{
Class<?> clazz = m.getDeclaringClass();
if (clazz.equals(AccessController.class) ||
clazz.equals(Method.class) ||
clazz.getName().startsWith("java.lang.invoke."))
throw new InvocationTargetException(
new UnsupportedOperationException("invocation not supported"));
}
private static Object invoke(Method m, Object obj, Object[] params)
throws InvocationTargetException, IllegalAccessException
{
ensureInvocableMethod(m);
return m.invoke(obj, params);
}
}
public final class MethodUtil extends SecureClassLoader {
private static final String MISC_PKG = "com.sun.javafx.reflect.";
private static final String TRAMPOLINE = MISC_PKG + "Trampoline";
private static final Method bounce = getTrampoline();
private MethodUtil() {
super();
}
public static Method getMethod(Class<?> cls, String name, Class<?>[] args)
throws NoSuchMethodException {
ReflectUtil.checkPackageAccess(cls);
return cls.getMethod(name, args);
}
public static Method[] getMethods(Class<?> cls) {
ReflectUtil.checkPackageAccess(cls);
return cls.getMethods();
}
@SuppressWarnings("removal")
static Method[] getPublicMethods(Class<?> cls) {
if (System.getSecurityManager() == null) {
return cls.getMethods();
}
Map<Signature, Method> sigs = new HashMap<Signature, Method>();
while (cls != null) {
boolean done = getInternalPublicMethods(cls, sigs);
if (done) {
break;
}
getInterfaceMethods(cls, sigs);
cls = cls.getSuperclass();
}
return sigs.values().toArray(new Method[sigs.size()]);
}
private static void getInterfaceMethods(Class<?> cls,
Map<Signature, Method> sigs) {
Class<?>[] intfs = cls.getInterfaces();
for (int i=0; i < intfs.length; i++) {
Class<?> intf = intfs[i];
boolean done = getInternalPublicMethods(intf, sigs);
if (!done) {
getInterfaceMethods(intf, sigs);
}
}
}
private static boolean getInternalPublicMethods(Class<?> cls,
Map<Signature, Method> sigs) {
Method[] methods = null;
try {
if (!Modifier.isPublic(cls.getModifiers())) {
return false;
}
if (!ReflectUtil.isPackageAccessible(cls)) {
return false;
}
methods = cls.getMethods();
} catch (SecurityException se) {
return false;
}
boolean done = true;
for (int i=0; i < methods.length; i++) {
Class<?> dc = methods[i].getDeclaringClass();
if (!Modifier.isPublic(dc.getModifiers())) {
done = false;
break;
}
}
if (done) {
for (int i=0; i < methods.length; i++) {
addMethod(sigs, methods[i]);
}
} else {
for (int i=0; i < methods.length; i++) {
Class<?> dc = methods[i].getDeclaringClass();
if (cls.equals(dc)) {
addMethod(sigs, methods[i]);
}
}
}
return done;
}
private static void addMethod(Map<Signature, Method> sigs, Method method) {
Signature signature = new Signature(method);
if (!sigs.containsKey(signature)) {
sigs.put(signature, method);
} else if (!method.getDeclaringClass().isInterface()){
Method old = sigs.get(signature);
if (old.getDeclaringClass().isInterface()) {
sigs.put(signature, method);
}
}
}
private static class Signature {
private final String methodName;
private final Class<?>[] argClasses;
private final int hashCode;
Signature(Method m) {
this.methodName = m.getName();
this.argClasses = m.getParameterTypes();
this.hashCode = methodName.hashCode() + Arrays.hashCode(argClasses);
}
@Override public int hashCode() {
return hashCode;
}
@Override public boolean equals(Object o2) {
if (this == o2) {
return true;
}
Signature that = (Signature)o2;
if (!(methodName.equals(that.methodName))) {
return false;
}
if (argClasses.length != that.argClasses.length) {
return false;
}
for (int i = 0; i < argClasses.length; i++) {
if (!(argClasses[i] == that.argClasses[i])) {
return false;
}
}
return true;
}
}
public static Module getTrampolineModule() {
return bounce.getDeclaringClass().getModule();
}
public static Object invoke(Method m, Object obj, Object[] params)
throws InvocationTargetException, IllegalAccessException {
try {
return bounce.invoke(null, new Object[] {m, obj, params});
} catch (InvocationTargetException ie) {
Throwable t = ie.getCause();
if (t instanceof InvocationTargetException) {
throw (InvocationTargetException)t;
} else if (t instanceof IllegalAccessException) {
throw (IllegalAccessException)t;
} else if (t instanceof RuntimeException) {
throw (RuntimeException)t;
} else if (t instanceof Error) {
throw (Error)t;
} else {
throw new Error("Unexpected invocation error", t);
}
} catch (IllegalAccessException iae) {
throw new Error("Unexpected invocation error", iae);
}
}
@SuppressWarnings("removal")
private static Method getTrampoline() {
try {
return AccessController.doPrivileged(
new PrivilegedExceptionAction<Method>() {
public Method run() throws Exception {
Class<?> t = getTrampolineClass();
Class<?>[] types = {
Method.class, Object.class, Object[].class
};
Method b = t.getDeclaredMethod("invoke", types);
b.setAccessible(true);
return b;
}
});
} catch (Exception e) {
throw new InternalError("bouncer cannot be found", e);
}
}
protected synchronized Class<?> loadClass(String name, boolean resolve)
throws ClassNotFoundException
{
ReflectUtil.checkPackageAccess(name);
Class<?> c = findLoadedClass(name);
if (c == null) {
try {
c = findClass(name);
} catch (ClassNotFoundException e) {
}
if (c == null) {
c = getParent().loadClass(name);
}
}
if (resolve) {
resolveClass(c);
}
return c;
}
protected Class<?> findClass(final String name)
throws ClassNotFoundException
{
if (!name.startsWith(MISC_PKG)) {
throw new ClassNotFoundException(name);
}
String path = name.replace('.', '/').concat(".class");
try {
InputStream in = MethodUtil.class.getModule().getResourceAsStream(path);
if (in != null) {
try (in) {
byte[] b = in.readAllBytes();
return defineClass(name, b);
}
}
} catch (IOException e) {
throw new ClassNotFoundException(name, e);
}
throw new ClassNotFoundException(name);
}
private Class<?> defineClass(String name, byte[] b) throws IOException {
CodeSource cs = new CodeSource(null, (java.security.cert.Certificate[])null);
if (!name.equals(TRAMPOLINE)) {
throw new IOException("MethodUtil: bad name " + name);
}
return defineClass(name, b, 0, b.length, cs);
}
protected PermissionCollection getPermissions(CodeSource codesource)
{
PermissionCollection perms = super.getPermissions(codesource);
perms.add(new AllPermission());
return perms;
}
private static Class<?> getTrampolineClass() {
try {
return Class.forName(TRAMPOLINE, true, new MethodUtil());
} catch (ClassNotFoundException e) {
}
return null;
}
}
