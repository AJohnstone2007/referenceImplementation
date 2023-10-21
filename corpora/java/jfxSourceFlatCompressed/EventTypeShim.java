package javafx.event;
import java.util.List;
public class EventTypeShim<T extends Event> {
public static Object getEventTypeSerialization(List<String> path) {
return new EventType.EventTypeSerialization(path);
}
public static class EventTypeSerializationShim extends EventType.EventTypeSerialization {
public EventTypeSerializationShim(List<String> path) {
super(path);
}
}
}
