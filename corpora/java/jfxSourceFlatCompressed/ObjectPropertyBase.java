package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
public abstract class ObjectPropertyBase<T> extends ObjectProperty<T> {
private T value;
private ObservableValue<? extends T> observable = null;;
private InvalidationListener listener = null;
private boolean valid = true;
private ExpressionHelper<T> helper = null;
public ObjectPropertyBase() {
}
public ObjectPropertyBase(T initialValue) {
this.value = initialValue;
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
public void addListener(ChangeListener<? super T> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super T> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
private void markInvalid() {
if (valid) {
valid = false;
invalidated();
fireValueChangedEvent();
}
}
protected void invalidated() {
}
@Override
public T get() {
valid = true;
return observable == null ? value : observable.getValue();
}
@Override
public void set(T newValue) {
if (isBound()) {
throw new java.lang.RuntimeException((getBean() != null && getName() != null ?
getBean().getClass().getSimpleName() + "." + getName() + " : ": "") + "A bound value cannot be set.");
}
if (value != newValue) {
value = newValue;
markInvalid();
}
}
@Override
public boolean isBound() {
return observable != null;
}
@Override
public void bind(final ObservableValue<? extends T> newObservable) {
if (newObservable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (!newObservable.equals(this.observable)) {
unbind();
observable = newObservable;
if (listener == null) {
listener = new Listener(this);
}
observable.addListener(listener);
markInvalid();
}
}
@Override
public void unbind() {
if (observable != null) {
value = observable.getValue();
observable.removeListener(listener);
observable = null;
}
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder("ObjectProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
if (isBound()) {
result.append("bound, ");
if (valid) {
result.append("value: ").append(get());
} else {
result.append("invalid");
}
} else {
result.append("value: ").append(get());
}
result.append("]");
return result.toString();
}
private static class Listener implements InvalidationListener, WeakListener {
private final WeakReference<ObjectPropertyBase<?>> wref;
public Listener(ObjectPropertyBase<?> ref) {
this.wref = new WeakReference<ObjectPropertyBase<?>>(ref);
}
@Override
public void invalidated(Observable observable) {
ObjectPropertyBase<?> ref = wref.get();
if (ref == null) {
observable.removeListener(this);
} else {
ref.markInvalid();
}
}
@Override
public boolean wasGarbageCollected() {
return wref.get() == null;
}
}
}
