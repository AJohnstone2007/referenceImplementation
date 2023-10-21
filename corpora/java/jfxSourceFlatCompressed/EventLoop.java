package com.sun.webkit;
import com.sun.javafx.logging.PlatformLogger;
public abstract class EventLoop {
private static final PlatformLogger logger =
PlatformLogger.getLogger(EventLoop.class.getName());
private static EventLoop instance;
public static void setEventLoop(EventLoop eventLoop) {
instance = eventLoop;
}
private static void fwkCycle() {
logger.fine("Executing event loop cycle");
instance.cycle();
}
protected abstract void cycle();
}
