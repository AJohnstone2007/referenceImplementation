package javafx.beans.property.adapter;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
public final class JavaBeanBooleanProperty extends BooleanProperty implements JavaBeanProperty<Boolean> {
private final PropertyDescriptor descriptor;
private final PropertyDescriptor.Listener<Boolean> listener;
private ObservableValue<? extends Boolean> observable = null;
private ExpressionHelper<Boolean> helper = null;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
JavaBeanBooleanProperty(PropertyDescriptor descriptor, Object bean) {
this.descriptor = descriptor;
this.listener = descriptor.new Listener<Boolean>(bean, this);
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
@SuppressWarnings("removal")
@Override
public void set(final boolean value) {
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
public void bind(ObservableValue<? extends Boolean> observable) {
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
public void addListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Boolean> listener) {
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
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder("BooleanProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
if (isBound()) {
result.append("bound, ");
}
result.append("value: ").append(get());
result.append("]");
return result.toString();
}
}
