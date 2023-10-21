package com.sun.javafx.reflect;
import java.lang.reflect.Proxy;
public final class ReflectUtil {
private ReflectUtil() {
}
public static void checkPackageAccess(Class<?> clazz) {
@SuppressWarnings("removal")
SecurityManager s = System.getSecurityManager();
if (s != null) {
privateCheckPackageAccess(s, clazz);
}
}
private static void privateCheckPackageAccess(@SuppressWarnings("removal") SecurityManager s, Class<?> clazz) {
while (clazz.isArray()) {
clazz = clazz.getComponentType();
}
String pkg = clazz.getPackageName();
if (pkg != null && !pkg.isEmpty()) {
s.checkPackageAccess(pkg);
}
if (isNonPublicProxyClass(clazz)) {
privateCheckProxyPackageAccess(s, clazz);
}
}
public static void checkPackageAccess(String name) {
@SuppressWarnings("removal")
SecurityManager s = System.getSecurityManager();
if (s != null) {
String cname = name.replace('/', '.');
if (cname.startsWith("[")) {
int b = cname.lastIndexOf('[') + 2;
if (b > 1 && b < cname.length()) {
cname = cname.substring(b);
}
}
int i = cname.lastIndexOf('.');
if (i != -1) {
s.checkPackageAccess(cname.substring(0, i));
}
}
}
public static boolean isPackageAccessible(Class<?> clazz) {
try {
checkPackageAccess(clazz);
} catch (SecurityException e) {
return false;
}
return true;
}
private static void privateCheckProxyPackageAccess(@SuppressWarnings("removal") SecurityManager s, Class<?> clazz) {
if (Proxy.isProxyClass(clazz)) {
for (Class<?> intf : clazz.getInterfaces()) {
privateCheckPackageAccess(s, intf);
}
}
}
public static final String PROXY_PACKAGE = "com.sun.proxy";
public static boolean isNonPublicProxyClass(Class<?> cls) {
if (!Proxy.isProxyClass(cls)) {
return false;
}
String pkg = cls.getPackageName();
return pkg == null || !pkg.startsWith(PROXY_PACKAGE);
}
}
