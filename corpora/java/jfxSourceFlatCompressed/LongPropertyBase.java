package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.LongBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;
public abstract class LongPropertyBase extends LongProperty {
private long value;
private ObservableLongValue observable = null;;
private InvalidationListener listener = null;
private boolean valid = true;
private ExpressionHelper<Number> helper = null;
public LongPropertyBase() {
}
public LongPropertyBase(long initialValue) {
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
public void addListener(ChangeListener<? super Number> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
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
public long get() {
valid = true;
return observable == null ? value : observable.get();
}
@Override
public void set(long newValue) {
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
public void bind(final ObservableValue<? extends Number> rawObservable) {
if (rawObservable == null) {
throw new NullPointerException("Cannot bind to null");
}
ObservableLongValue newObservable;
if (rawObservable instanceof ObservableLongValue) {
newObservable = (ObservableLongValue)rawObservable;
} else if (rawObservable instanceof ObservableNumberValue) {
final ObservableNumberValue numberValue = (ObservableNumberValue)rawObservable;
newObservable = new ValueWrapper(rawObservable) {
@Override
protected long computeValue() {
return numberValue.longValue();
}
};
} else {
newObservable = new ValueWrapper(rawObservable) {
@Override
protected long computeValue() {
final Number value = rawObservable.getValue();
return (value == null)? 0L : value.longValue();
}
};
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
value = observable.get();
observable.removeListener(listener);
if (observable instanceof ValueWrapper) {
((ValueWrapper)observable).dispose();
}
observable = null;
}
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder("LongProperty [");
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
private final WeakReference<LongPropertyBase> wref;
public Listener(LongPropertyBase ref) {
this.wref = new WeakReference<>(ref);
}
@Override
public void invalidated(Observable observable) {
LongPropertyBase ref = wref.get();
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
private abstract class ValueWrapper extends LongBinding {
private ObservableValue<? extends Number> observable;
public ValueWrapper(ObservableValue<? extends Number> observable) {
this.observable = observable;
bind(observable);
}
@Override
public void dispose() {
unbind(observable);
}
}
}
