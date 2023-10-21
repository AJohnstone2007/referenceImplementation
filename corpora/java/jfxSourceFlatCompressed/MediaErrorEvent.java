package javafx.scene.media;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class MediaErrorEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<MediaErrorEvent> MEDIA_ERROR =
new EventType<MediaErrorEvent>(Event.ANY, "Media Error Event");
private MediaException error;
MediaErrorEvent(Object source, EventTarget target, MediaException error) {
super(source, target, MEDIA_ERROR);
if(error == null) {
throw new IllegalArgumentException("error == null!");
}
this.error = error;
}
public MediaException getMediaError() {
return error;
}
@Override
public String toString() {
return super.toString() + ": source " + getSource() +
"; target " + getTarget() + "; error " + error;
}
@Override
public MediaErrorEvent copyFor(Object newSource, EventTarget newTarget) {
return (MediaErrorEvent) super.copyFor(newSource, newTarget);
}
@Override
public EventType<MediaErrorEvent> getEventType() {
return (EventType<MediaErrorEvent>) super.getEventType();
}
}
