package javafx.beans.property.adapter;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
public final class ReadOnlyJavaBeanBooleanProperty extends ReadOnlyBooleanPropertyBase implements ReadOnlyJavaBeanProperty<Boolean> {
private final ReadOnlyPropertyDescriptor descriptor;
private final ReadOnlyPropertyDescriptor.ReadOnlyListener<Boolean> listener;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
ReadOnlyJavaBeanBooleanProperty(ReadOnlyPropertyDescriptor descriptor, Object bean) {
this.descriptor = descriptor;
this.listener = descriptor.new ReadOnlyListener<Boolean>(bean, this);
descriptor.addListener(listener);
Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, listener));
}
@SuppressWarnings("removal")
@Override
public boolean get() {
return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
try {
return (Boolean)MethodHelper.invoke(descriptor.getGetter(), getBean(), (Object[])null);
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
