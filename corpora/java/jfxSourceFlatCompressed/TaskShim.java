package javafx.concurrent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
public abstract class TaskShim<V> extends Task<V> {
public static <T extends Event> void setEventHandler(
Task t,
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
t.setEventHandler(eventType, eventHandler);
}
@Override
public boolean isFxApplicationThread() {
return super.isFxApplicationThread();
}
@Override
public void runLater(Runnable r) {
super.runLater(r);
}
public void shim_setState(State value) {
super.setState(value);
}
@Override
public void updateTitle(String title) {
super.updateTitle(title);
}
@Override
public void updateMessage(String message) {
super.updateMessage(message);
}
public static void updateProgress(Task t, double workDone, double max) {
t.updateProgress(workDone, max);
}
public static void updateProgress(Task t, long workDone, long max) {
t.updateProgress(workDone, max);
}
@Override
public void updateProgress(double workDone, double max) {
super.updateProgress(workDone, max);
}
}
