package javafx.event;
public interface EventTarget {
EventDispatchChain buildEventDispatchChain(EventDispatchChain tail);
}
