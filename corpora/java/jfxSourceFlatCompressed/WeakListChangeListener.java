package javafx.collections;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;
import java.lang.ref.WeakReference;
public final class WeakListChangeListener<E> implements ListChangeListener<E>, WeakListener {
private final WeakReference<ListChangeListener<E>> ref;
public WeakListChangeListener(@NamedArg("listener") ListChangeListener<E> listener) {
if (listener == null) {
throw new NullPointerException("Listener must be specified.");
}
this.ref = new WeakReference<ListChangeListener<E>>(listener);
}
@Override
public boolean wasGarbageCollected() {
return (ref.get() == null);
}
@Override
public void onChanged(Change<? extends E> change) {
final ListChangeListener<E> listener = ref.get();
if (listener != null) {
listener.onChanged(change);
} else {
change.getList().removeListener(this);
}
}
}
