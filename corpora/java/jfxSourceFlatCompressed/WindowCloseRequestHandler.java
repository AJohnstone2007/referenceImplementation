package com.sun.javafx.stage;
import com.sun.javafx.event.BasicEventDispatcher;
import javafx.event.Event;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
public final class WindowCloseRequestHandler extends BasicEventDispatcher {
private final Window window;
public WindowCloseRequestHandler(final Window window) {
this.window = window;
}
@Override
public Event dispatchBubblingEvent(Event event) {
if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
window.hide();
event.consume();
}
return event;
}
}
