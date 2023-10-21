package com.sun.javafx.event;
import java.util.Set;
import javafx.event.EventTarget;
public interface CompositeEventTarget extends EventTarget {
Set<EventTarget> getTargets();
boolean containsTarget(EventTarget target);
}
