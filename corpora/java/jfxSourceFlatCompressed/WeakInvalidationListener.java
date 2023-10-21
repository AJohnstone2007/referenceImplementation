package javafx.beans;
import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
public final class WeakInvalidationListener implements InvalidationListener, WeakListener {
private final WeakReference<InvalidationListener> ref;
public WeakInvalidationListener(@NamedArg("listener") InvalidationListener listener) {
if (listener == null) {
throw new NullPointerException("Listener must be specified.");
}
this.ref = new WeakReference<InvalidationListener>(listener);
}
@Override
public boolean wasGarbageCollected() {
return (ref.get() == null);
}
@Override
public void invalidated(Observable observable) {
InvalidationListener listener = ref.get();
if (listener != null) {
listener.invalidated(observable);
} else {
observable.removeListener(this);
}
}
}
