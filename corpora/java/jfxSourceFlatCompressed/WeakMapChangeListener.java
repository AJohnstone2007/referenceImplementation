package javafx.collections;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;
import java.lang.ref.WeakReference;
public final class WeakMapChangeListener<K, V> implements MapChangeListener<K, V>, WeakListener {
private final WeakReference<MapChangeListener<K, V>> ref;
public WeakMapChangeListener(@NamedArg("listener") MapChangeListener<K, V> listener) {
if (listener == null) {
throw new NullPointerException("Listener must be specified.");
}
this.ref = new WeakReference<MapChangeListener<K, V>>(listener);
}
@Override
public boolean wasGarbageCollected() {
return (ref.get() == null);
}
@Override
public void onChanged(Change<? extends K,? extends V> change) {
final MapChangeListener<K, V> listener = ref.get();
if (listener != null) {
listener.onChanged(change);
} else {
change.getMap().removeListener(this);
}
}
}
