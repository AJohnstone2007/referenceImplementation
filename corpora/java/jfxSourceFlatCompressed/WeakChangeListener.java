package javafx.beans.value;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;
import java.lang.ref.WeakReference;
public final class WeakChangeListener<T> implements ChangeListener<T>, WeakListener {
private final WeakReference<ChangeListener<T>> ref;
public WeakChangeListener(@NamedArg("listener") ChangeListener<T> listener) {
if (listener == null) {
throw new NullPointerException("Listener must be specified.");
}
this.ref = new WeakReference<ChangeListener<T>>(listener);
}
@Override
public boolean wasGarbageCollected() {
return (ref.get() == null);
}
@Override
public void changed(ObservableValue<? extends T> observable, T oldValue,
T newValue) {
ChangeListener<T> listener = ref.get();
if (listener != null) {
listener.changed(observable, oldValue, newValue);
} else {
observable.removeListener(this);
}
}
}
