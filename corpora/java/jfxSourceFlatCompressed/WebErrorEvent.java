package javafx.scene.web;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
public final class WebErrorEvent extends Event {
public static final EventType<WebErrorEvent> ANY =
new EventType<WebErrorEvent>(Event.ANY, "WEB_ERROR");
public static final EventType<WebErrorEvent>
USER_DATA_DIRECTORY_ALREADY_IN_USE = new EventType<WebErrorEvent>(
WebErrorEvent.ANY, "USER_DATA_DIRECTORY_ALREADY_IN_USE");
public static final EventType<WebErrorEvent>
USER_DATA_DIRECTORY_IO_ERROR = new EventType<WebErrorEvent>(
WebErrorEvent.ANY, "USER_DATA_DIRECTORY_IO_ERROR");
public static final EventType<WebErrorEvent>
USER_DATA_DIRECTORY_SECURITY_ERROR = new EventType<WebErrorEvent>(
WebErrorEvent.ANY, "USER_DATA_DIRECTORY_SECURITY_ERROR");
private final String message;
private final Throwable exception;
public WebErrorEvent(@NamedArg("source") Object source, @NamedArg("type") EventType<WebErrorEvent> type,
@NamedArg("message") String message, @NamedArg("exception") Throwable exception)
{
super(source, null, type);
this.message = message;
this.exception = exception;
}
public String getMessage() {
return message;
}
public Throwable getException() {
return exception;
}
@Override public String toString() {
return String.format("WebErrorEvent [source = %s, eventType = %s, "
+ "message = \"%s\", exception = %s]",
getSource(), getEventType(), getMessage(), getException());
}
}
