package javafx.beans.property;
import com.sun.javafx.binding.ListExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public abstract class ListPropertyBase<E> extends ListProperty<E> {
private final ListChangeListener<E> listChangeListener = change -> {
invalidateProperties();
invalidated();
fireValueChangedEvent(change);
};
private ObservableList<E> value;
private ObservableValue<? extends ObservableList<E>> observable = null;
private InvalidationListener listener = null;
private boolean valid = true;
private ListExpressionHelper<E> helper = null;
private SizeProperty size0;
private EmptyProperty empty0;
public ListPropertyBase() {}
public ListPropertyBase(ObservableList<E> initialValue) {
this.value = initialValue;
if (initialValue != null) {
initialValue.addListener(listChangeListener);
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
return ListPropertyBase.this;
}
@Override
public String getName() {
return "size";
}
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
return ListPropertyBase.this;
}
@Override
public String getName() {
return "empty";
}
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
@Override
public void addListener(InvalidationListener listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ListExpressionHelper.fireValueChangedEvent(helper);
}
protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
ListExpressionHelper.fireValueChangedEvent(helper, change);
}
private void invalidateProperties() {
if (size0 != null) {
size0.fireValueChangedEvent();
}
if (empty0 != null) {
empty0.fireValueChangedEvent();
}
}
private void markInvalid(ObservableList<E> oldValue) {
if (valid) {
if (oldValue != null) {
oldValue.removeListener(listChangeListener);
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
public ObservableList<E> get() {
if (!valid) {
value = observable == null ? value : observable.getValue();
valid = true;
if (value != null) {
value.addListener(listChangeListener);
}
}
return value;
}
@Override
public void set(ObservableList<E> newValue) {
if (isBound()) {
throw new java.lang.RuntimeException((getBean() != null && getName() != null ?
getBean().getClass().getSimpleName() + "." + getName() + " : ": "") + "A bound value cannot be set.");
}
if (value != newValue) {
final ObservableList<E> oldValue = value;
value = newValue;
markInvalid(oldValue);
}
}
@Override
public boolean isBound() {
return observable != null;
}
@Override
public void bind(final ObservableValue<? extends ObservableList<E>> newObservable) {
if (newObservable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (newObservable != observable) {
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
final StringBuilder result = new StringBuilder("ListProperty [");
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
private final WeakReference<ListPropertyBase<E>> wref;
public Listener(ListPropertyBase<E> ref) {
this.wref = new WeakReference<ListPropertyBase<E>>(ref);
}
@Override
public void invalidated(Observable observable) {
ListPropertyBase<E> ref = wref.get();
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
