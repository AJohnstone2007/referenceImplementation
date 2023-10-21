package javafx.event;
public interface EventDispatcher {
Event dispatchEvent(Event event, EventDispatchChain tail);
}
