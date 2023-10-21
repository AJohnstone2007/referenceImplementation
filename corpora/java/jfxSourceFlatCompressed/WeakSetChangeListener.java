package javafx.collections;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;
import java.lang.ref.WeakReference;
public final class WeakSetChangeListener<E> implements SetChangeListener<E>, WeakListener {
private final WeakReference<SetChangeListener<E>> ref;
public WeakSetChangeListener(@NamedArg("listener") SetChangeListener<E> listener) {
if (listener == null) {
throw new NullPointerException("Listener must be specified.");
}
this.ref = new WeakReference<SetChangeListener<E>>(listener);
}
@Override
public boolean wasGarbageCollected() {
return (ref.get() == null);
}
@Override
public void onChanged(Change<? extends E> change) {
final SetChangeListener<E> listener = ref.get();
if (listener != null) {
listener.onChanged(change);
} else {
change.getSet().removeListener(this);
}
}
}
