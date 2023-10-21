package javafx.collections;
import com.sun.javafx.collections.ArrayListenerHelper;
import javafx.beans.InvalidationListener;
public abstract class ObservableArrayBase<T extends ObservableArray<T>> implements ObservableArray<T> {
private ArrayListenerHelper<T> listenerHelper;
public ObservableArrayBase() {
}
@Override public final void addListener(InvalidationListener listener) {
listenerHelper = ArrayListenerHelper.<T>addListener(listenerHelper, (T) this, listener);
}
@Override public final void removeListener(InvalidationListener listener) {
listenerHelper = ArrayListenerHelper.removeListener(listenerHelper, listener);
}
@Override public final void addListener(ArrayChangeListener<T> listener) {
listenerHelper = ArrayListenerHelper.<T>addListener(listenerHelper, (T) this, listener);
}
@Override public final void removeListener(ArrayChangeListener<T> listener) {
listenerHelper = ArrayListenerHelper.removeListener(listenerHelper, listener);
}
protected final void fireChange(boolean sizeChanged, int from, int to) {
ArrayListenerHelper.fireValueChangedEvent(listenerHelper, sizeChanged, from, to);
}
}
