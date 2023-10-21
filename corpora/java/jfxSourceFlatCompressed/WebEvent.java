package javafx.scene.web;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
final public class WebEvent<T> extends Event {
public static final EventType<WebEvent> ANY =
new EventType<WebEvent>(Event.ANY, "WEB");
public static final EventType<WebEvent> RESIZED =
new EventType<WebEvent>(WebEvent.ANY, "WEB_RESIZED");
public static final EventType<WebEvent> STATUS_CHANGED =
new EventType<WebEvent>(WebEvent.ANY, "WEB_STATUS_CHANGED");
public static final EventType<WebEvent> VISIBILITY_CHANGED =
new EventType<WebEvent>(WebEvent.ANY, "WEB_VISIBILITY_CHANGED");
public static final EventType<WebEvent> ALERT =
new EventType<WebEvent>(WebEvent.ANY, "WEB_ALERT");
private final T data;
public WebEvent(@NamedArg("source") Object source, @NamedArg("type") EventType<WebEvent> type, @NamedArg("data") T data) {
super(source, null, type);
this.data = data;
}
public T getData() {
return data;
}
@Override public String toString() {
return String.format(
"WebEvent [source = %s, eventType = %s, data = %s]",
getSource(), getEventType(), getData());
}
}
