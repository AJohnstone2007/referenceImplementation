package com.sun.javafx.tk;
import java.lang.reflect.Constructor;
import java.security.Permission;
import static com.sun.javafx.FXPermissions.ACCESS_CLIPBOARD_PERMISSION;
import java.security.AccessControlContext;
import java.security.AccessControlException;
public class PermissionHelper {
private static boolean awtInitialized = false;
private static Permission awtClipboardPermission;
private static synchronized Permission getAWTClipboardPermission() {
if (!awtInitialized) {
try {
Class clazz = Class.forName("java.awt.AWTPermission",
false, PermissionHelper.class.getClassLoader());
Constructor c = clazz.getConstructor(String.class);
awtClipboardPermission = (Permission) c.newInstance("accessClipboard");
} catch (Exception ex) {
awtClipboardPermission = null;
}
awtInitialized = true;
}
return awtClipboardPermission;
}
public static void checkClipboardPermission() {
@SuppressWarnings("removal")
final SecurityManager securityManager = System.getSecurityManager();
if (securityManager == null) return;
try {
securityManager.checkPermission(ACCESS_CLIPBOARD_PERMISSION);
} catch (SecurityException ex) {
final Permission perm = getAWTClipboardPermission();
if (perm == null) throw ex;
try {
securityManager.checkPermission(perm);
} catch (SecurityException ex2) {
throw ex;
}
}
}
@SuppressWarnings("removal")
public static void checkClipboardPermission(AccessControlContext context) {
final SecurityManager securityManager = System.getSecurityManager();
if (securityManager == null) return;
if (context == null) {
throw new AccessControlException("AccessControlContext is null");
}
try {
securityManager.checkPermission(ACCESS_CLIPBOARD_PERMISSION, context);
} catch (SecurityException ex) {
final Permission perm = getAWTClipboardPermission();
if (perm == null) throw ex;
try {
securityManager.checkPermission(perm, context);
} catch (SecurityException ex2) {
throw ex;
}
}
}
private PermissionHelper() {}
}
