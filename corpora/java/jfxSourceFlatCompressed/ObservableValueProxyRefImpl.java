package test.com.sun.javafx.test.binding;
import java.lang.reflect.Method;
public final class ObservableValueProxyRefImpl implements ObservableValueProxy {
private final Method getValueMethod;
public ObservableValueProxyRefImpl(final Class<?> observableValueClass) {
this.getValueMethod = ReflectionHelper.getMethod(
observableValueClass,
"getValue");
}
@Override
public Object getValue(final Object observableValue) {
return ReflectionHelper.invokeMethod(observableValue, getValueMethod);
}
}
