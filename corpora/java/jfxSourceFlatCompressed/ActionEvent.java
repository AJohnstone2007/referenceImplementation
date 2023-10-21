package javafx.event;
public class ActionEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<ActionEvent> ACTION =
new EventType<ActionEvent>(Event.ANY, "ACTION");
public static final EventType<ActionEvent> ANY = ACTION;
public ActionEvent() {
super(ACTION);
}
public ActionEvent(Object source, EventTarget target) {
super(source, target, ACTION);
}
@Override
public ActionEvent copyFor(Object newSource, EventTarget newTarget) {
return (ActionEvent) super.copyFor(newSource, newTarget);
}
@Override
public EventType<? extends ActionEvent> getEventType() {
return (EventType<? extends ActionEvent>) super.getEventType();
}
}
