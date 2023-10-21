package com.sun.javafx.event;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.event.Event;
public final class EventQueue {
private Queue<Event> queue = new ArrayDeque<Event>();
private boolean inLoop;
public void postEvent(Event event) {
queue.add(event);
}
public void fire() {
if (inLoop) {
return;
}
inLoop = true;
try {
while (!queue.isEmpty()) {
Event top = queue.remove();
Event.fireEvent(top.getTarget(), top);
}
} finally {
inLoop = false;
}
}
}
