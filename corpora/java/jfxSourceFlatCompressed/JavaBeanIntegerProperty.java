package javafx.beans.property.adapter;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
public final class JavaBeanIntegerProperty extends IntegerProperty implements JavaBeanProperty<Number> {
private final PropertyDescriptor descriptor;
private final PropertyDescriptor.Listener<Number> listener;
private ObservableValue<? extends Number> observable = null;
private ExpressionHelper<Number> helper = null;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
JavaBeanIntegerProperty(PropertyDescriptor descriptor, Object bean) {
this.descriptor = descriptor;
this.listener = descriptor.new Listener<Number>(bean, this);
descriptor.addListener(listener);
Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, listener));
}
@SuppressWarnings("removal")
@Override
public int get() {
return AccessController.doPrivileged((PrivilegedAction<Integer>) () -> {
try {
return ((Number)MethodHelper.invoke(
descriptor.getGetter(), getBean(), (Object[])null)).intValue();
} catch (IllegalAccessException e) {
throw new UndeclaredThrowableException(e);
} catch (InvocationTargetException e) {
throw new UndeclaredThrowableException(e);
}
}, acc);
}
@SuppressWarnings("removal")
@Override
public void set(final int value) {
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
public void bind(ObservableValue<? extends Number> observable) {
if (observable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (!observable.equals(this.observable)) {
unbind();
set(observable.getValue().intValue());
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
public void addListener(ChangeListener<? super Number> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
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
final StringBuilder result = new StringBuilder("IntegerProperty [");
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
