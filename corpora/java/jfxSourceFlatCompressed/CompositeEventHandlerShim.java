package com.sun.javafx.event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
public class CompositeEventHandlerShim {
public static boolean containsHandler(CompositeEventHandler handler, final EventHandler eventHandler) {
return handler.containsHandler(eventHandler);
}
public static boolean containsFilter(CompositeEventHandler handler, final WeakEventHandler eventHandler) {
return handler.containsFilter(eventHandler);
}
}
