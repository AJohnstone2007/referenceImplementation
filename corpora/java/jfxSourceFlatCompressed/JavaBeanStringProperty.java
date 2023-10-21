package javafx.beans.property.adapter;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
public final class JavaBeanStringProperty extends StringProperty implements JavaBeanProperty<String> {
private final PropertyDescriptor descriptor;
private final PropertyDescriptor.Listener<String> listener;
private ObservableValue<? extends String> observable = null;
private ExpressionHelper<String> helper = null;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
JavaBeanStringProperty(PropertyDescriptor descriptor, Object bean) {
this.descriptor = descriptor;
this.listener = descriptor.new Listener<String>(bean, this);
descriptor.addListener(listener);
Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, listener));
}
@SuppressWarnings("removal")
@Override
public String get() {
return AccessController.doPrivileged((PrivilegedAction<String>) () -> {
try {
return (String)MethodHelper.invoke(descriptor.getGetter(), getBean(), (Object[])null);
} catch (IllegalAccessException e) {
throw new UndeclaredThrowableException(e);
} catch (InvocationTargetException e) {
throw new UndeclaredThrowableException(e);
}
}, acc);
}
@SuppressWarnings("removal")
@Override
public void set(final String value) {
if (isBound()) {
throw new RuntimeException("A bound value cannot be set.");
}
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
try {
MethodHelper.invoke(descriptor.getSetter(), getBean(), new Object[] {value});
ExpressionHelper.fireValueChangedEvent(helper);
} catch (IllegalAccessException e) {
throw new UndeclaredThrowableException(e);
} catch (InvocationTargetException e) {
throw new UndeclaredThrowableException(e);
}
return null;
}, acc);
}
@Override
public void bind(ObservableValue<? extends String> observable) {
if (observable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (!observable.equals(this.observable)) {
unbind();
set(observable.getValue());
this.observable = observable;
this.observable.addListener(listener);
}
}
@Override
public void unbind() {
if (observable != null) {
observable.removeListener(listener);
observable = null;
}
}
@Override
public boolean isBound() {
return observable != null;
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
public void addListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
@Override
public void dispose() {
descriptor.removeListener(listener);
}
}
