package test.com.sun.javafx.test.binding;
import java.lang.reflect.Method;
public final class WritableValueProxyRefImpl implements WritableValueProxy {
private final Method setValueMethod;
public WritableValueProxyRefImpl(final Class<?> writableValueClass) {
final Method getValueMethod = ReflectionHelper.getMethod(
writableValueClass,
"getValue");
this.setValueMethod = ReflectionHelper.getMethod(
writableValueClass,
"setValue",
getValueMethod.getReturnType());
}
@Override
public void setValue(final Object writableValue,
final Object value) {
ReflectionHelper.invokeMethod(writableValue, setValueMethod, value);
}
}
