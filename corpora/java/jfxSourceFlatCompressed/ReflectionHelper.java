package test.com.sun.javafx.test.binding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public final class ReflectionHelper {
private ReflectionHelper() {
}
public static Class<?> classForName(final String className) {
try {
return Class.forName(className);
} catch (final ClassNotFoundException e) {
throw convertToRuntimeException(e);
}
}
public static Object newInstance(final Class<?> cls) {
try {
return cls.getDeclaredConstructor().newInstance();
} catch (final RuntimeException e) {
throw e;
} catch (final Exception e) {
throw convertToRuntimeException(e);
}
}
public static Method getMethod(final Class<?> cls,
final String methodName,
final Class<?>... parameterTypes) {
try {
return cls.getMethod(methodName, parameterTypes);
} catch (final NoSuchMethodException e) {
throw convertToRuntimeException(e);
} catch (final SecurityException e) {
throw convertToRuntimeException(e);
}
}
public static Object invokeMethod(final Object object,
final Method method,
final Object... args) {
try {
return method.invoke(object, args);
} catch (final IllegalAccessException e) {
throw convertToRuntimeException(e);
} catch (final IllegalArgumentException e) {
throw convertToRuntimeException(e);
} catch (final InvocationTargetException e) {
throw convertToRuntimeException(e);
}
}
private static RuntimeException convertToRuntimeException(
final Exception e) {
return new RuntimeException(e);
}
}
