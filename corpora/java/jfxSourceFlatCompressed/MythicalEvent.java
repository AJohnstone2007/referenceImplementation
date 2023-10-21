package test.javafx.concurrent.mocks;
import javafx.event.Event;
import javafx.event.EventType;
public class MythicalEvent extends Event {
public static final EventType<MythicalEvent> ANY = new EventType<>(Event.ANY, "MYTHICAL");
public MythicalEvent() {
super(ANY);
}
}
