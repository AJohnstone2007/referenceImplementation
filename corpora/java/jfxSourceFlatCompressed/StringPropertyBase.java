package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
public abstract class StringPropertyBase extends StringProperty {
private String value;
private ObservableValue<? extends String> observable = null;
private InvalidationListener listener = null;
private boolean valid = true;
private ExpressionHelper<String> helper = null;
public StringPropertyBase() {
}
public StringPropertyBase(String initialValue) {
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
public void addListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super String> listener) {
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
public String get() {
valid = true;
return observable == null ? value : observable.getValue();
}
@Override
public void set(String newValue) {
if (isBound()) {
throw new java.lang.RuntimeException((getBean() != null && getName() != null ?
getBean().getClass().getSimpleName() + "." + getName() + " : ": "") + "A bound value cannot be set.");
}
if ((value == null)? newValue != null : !value.equals(newValue)) {
value = newValue;
markInvalid();
}
}
@Override
public boolean isBound() {
return observable != null;
}
@Override
public void bind(ObservableValue<? extends String> newObservable) {
if (newObservable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (!newObservable.equals(observable)) {
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
final StringBuilder result = new StringBuilder("StringProperty [");
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
private final WeakReference<StringPropertyBase> wref;
public Listener(StringPropertyBase ref) {
this.wref = new WeakReference<>(ref);
}
@Override
public void invalidated(Observable observable) {
StringPropertyBase ref = wref.get();
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
