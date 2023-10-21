package javafx.beans.property.adapter;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
public final class ReadOnlyJavaBeanDoubleProperty extends ReadOnlyDoublePropertyBase implements ReadOnlyJavaBeanProperty<Number> {
private final ReadOnlyPropertyDescriptor descriptor;
private final ReadOnlyPropertyDescriptor.ReadOnlyListener<Number> listener;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
ReadOnlyJavaBeanDoubleProperty(ReadOnlyPropertyDescriptor descriptor, Object bean) {
this.descriptor = descriptor;
this.listener = descriptor.new ReadOnlyListener<Number>(bean, this);
descriptor.addListener(listener);
Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, listener));
}
@SuppressWarnings("removal")
@Override
public double get() {
return AccessController.doPrivileged((PrivilegedAction<Double>) () -> {
try {
return ((Number)MethodHelper.invoke(
descriptor.getGetter(), getBean(), (Object[])null)).doubleValue();
} catch (IllegalAccessException e) {
throw new UndeclaredThrowableException(e);
} catch (InvocationTargetException e) {
throw new UndeclaredThrowableException(e);
}
}, acc);
}
@Override
public Object getBean() {
return listener.getBean();
}
@Override
public String getName() {
return descriptor.getName();
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
@Override
public void dispose() {
descriptor.removeListener(listener);
}
}
