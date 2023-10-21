package com.sun.webkit;
import com.sun.javafx.reflect.MethodUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.reflect.ReflectUtil;
public class MethodHelper {
@SuppressWarnings("removal")
private static final boolean logAccessErrors
= AccessController.doPrivileged((PrivilegedAction<Boolean>) ()
-> Boolean.getBoolean("sun.reflect.debugModuleAccessChecks"));
private static final Module trampolineModule = MethodUtil.getTrampolineModule();
public static Object invoke(Method m, Object obj, Object[] params)
throws InvocationTargetException, IllegalAccessException {
final Class<?> clazz = m.getDeclaringClass();
final String packageName = clazz.getPackage().getName();
final Module module = clazz.getModule();
final Module thisModule = MethodHelper.class.getModule();
try {
if (!module.isExported(packageName)) {
if (!module.isOpen(packageName, thisModule)) {
throw new IllegalAccessException(
"module " + thisModule.getName()
+ " cannot access class " + clazz.getName()
+ " (in module " + module.getName()
+ ") because module " + module.getName()
+ " does not open " + packageName
+ " to " + thisModule.getName());
}
if (!module.isOpen(packageName, trampolineModule)) {
ReflectUtil.checkPackageAccess(packageName);
module.addOpens(packageName, trampolineModule);
}
}
} catch (IllegalAccessException ex) {
if (logAccessErrors) {
ex.printStackTrace(System.err);
}
throw ex;
}
return MethodUtil.invoke(m, obj, params);
}
private MethodHelper() {
}
}
