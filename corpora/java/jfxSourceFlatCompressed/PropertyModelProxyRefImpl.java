package test.com.sun.javafx.test.binding;
import java.lang.reflect.Method;
import javafx.beans.value.ObservableValue;
public final class PropertyModelProxyRefImpl implements PropertyModelProxy {
private final Method bindMethod;
private final Method unbindMethod;
public PropertyModelProxyRefImpl(final Class<?> propertyModelClass) {
this.bindMethod = ReflectionHelper.getMethod(propertyModelClass,
"bind",
ObservableValue.class);
this.unbindMethod = ReflectionHelper.getMethod(propertyModelClass,
"unbind");
}
@Override
public void bind(final Object propertyModel, final Object observableValue) {
ReflectionHelper.invokeMethod(propertyModel,
bindMethod,
observableValue);
}
@Override
public void unbind(final Object propertyModel) {
ReflectionHelper.invokeMethod(propertyModel,
unbindMethod);
}
}
