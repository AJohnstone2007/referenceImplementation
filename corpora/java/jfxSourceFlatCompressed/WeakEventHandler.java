package javafx.event;
import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
public final class WeakEventHandler<T extends Event>
implements EventHandler<T> {
private final WeakReference<EventHandler<T>> weakRef;
public WeakEventHandler(final @NamedArg("eventHandler") EventHandler<T> eventHandler) {
weakRef = new WeakReference<EventHandler<T>>(eventHandler);
}
public boolean wasGarbageCollected() {
return weakRef.get() == null;
}
@Override
public void handle(final T event) {
final EventHandler<T> eventHandler = weakRef.get();
if (eventHandler != null) {
eventHandler.handle(event);
}
}
void clear() {
weakRef.clear();
}
}
