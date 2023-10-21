package javafx.beans.property;
import com.sun.javafx.binding.SetExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
public abstract class SetPropertyBase<E> extends SetProperty<E> {
private final SetChangeListener<E> setChangeListener = change -> {
invalidateProperties();
invalidated();
fireValueChangedEvent(change);
};
private ObservableSet<E> value;
private ObservableValue<? extends ObservableSet<E>> observable = null;
private InvalidationListener listener = null;
private boolean valid = true;
private SetExpressionHelper<E> helper = null;
private SizeProperty size0;
private EmptyProperty empty0;
public SetPropertyBase() {}
public SetPropertyBase(ObservableSet<E> initialValue) {
this.value = initialValue;
if (initialValue != null) {
initialValue.addListener(setChangeListener);
}
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
if (size0 == null) {
size0 = new SizeProperty();
}
return size0;
}
private class SizeProperty extends ReadOnlyIntegerPropertyBase {
@Override
public int get() {
return size();
}
@Override
public Object getBean() {
return SetPropertyBase.this;
}
@Override
public String getName() {
return "size";
}
@Override
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
if (empty0 == null) {
empty0 = new EmptyProperty();
}
return empty0;
}
private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
@Override
public boolean get() {
return isEmpty();
}
@Override
public Object getBean() {
return SetPropertyBase.this;
}
@Override
public String getName() {
return "empty";
}
@Override
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
@Override
public void addListener(InvalidationListener listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
SetExpressionHelper.fireValueChangedEvent(helper);
}
protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> change) {
SetExpressionHelper.fireValueChangedEvent(helper, change);
}
private void invalidateProperties() {
if (size0 != null) {
size0.fireValueChangedEvent();
}
if (empty0 != null) {
empty0.fireValueChangedEvent();
}
}
private void markInvalid(ObservableSet<E> oldValue) {
if (valid) {
if (oldValue != null) {
oldValue.removeListener(setChangeListener);
}
valid = false;
invalidateProperties();
invalidated();
fireValueChangedEvent();
}
}
protected void invalidated() {
}
@Override
public ObservableSet<E> get() {
if (!valid) {
value = observable == null ? value : observable.getValue();
valid = true;
if (value != null) {
value.addListener(setChangeListener);
}
}
return value;
}
@Override
public void set(ObservableSet<E> newValue) {
if (isBound()) {
throw new java.lang.RuntimeException((getBean() != null && getName() != null ?
getBean().getClass().getSimpleName() + "." + getName() + " : ": "") + "A bound value cannot be set.");
}
if (value != newValue) {
final ObservableSet<E> oldValue = value;
value = newValue;
markInvalid(oldValue);
}
}
@Override
public boolean isBound() {
return observable != null;
}
@Override
public void bind(final ObservableValue<? extends ObservableSet<E>> newObservable) {
if (newObservable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (newObservable != this.observable) {
unbind();
observable = newObservable;
if (listener == null) {
listener = new Listener<>(this);
}
observable.addListener(listener);
markInvalid(value);
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
final StringBuilder result = new StringBuilder("SetProperty [");
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
private static class Listener<E> implements InvalidationListener, WeakListener {
private final WeakReference<SetPropertyBase<E>> wref;
public Listener(SetPropertyBase<E> ref) {
this.wref = new WeakReference<SetPropertyBase<E>>(ref);
}
@Override
public void invalidated(Observable observable) {
SetPropertyBase<E> ref = wref.get();
if (ref == null) {
observable.removeListener(this);
} else {
ref.markInvalid(ref.value);
}
}
@Override
public boolean wasGarbageCollected() {
return wref.get() == null;
}
}
}
