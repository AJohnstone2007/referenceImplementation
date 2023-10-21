package javafx.concurrent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
public abstract class ServiceShim<V> extends Service<V> {
public static void fireEvent(Service s, Event event) {
s.fireEvent(event);
}
public static <T extends Event> void setEventHandler(Service s,
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
s.setEventHandler(eventType, eventHandler);
}
@Override
public void runLater(Runnable r) {
super.runLater(r);
}
@Override
public boolean isFxApplicationThread() {
return super.isFxApplicationThread();
}
@Override
public void checkThread() {
super.checkThread();
}
}
