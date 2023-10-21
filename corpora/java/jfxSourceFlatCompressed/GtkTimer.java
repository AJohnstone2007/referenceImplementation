package com.sun.glass.ui.gtk;
import com.sun.glass.ui.Timer;
final class GtkTimer extends Timer{
public GtkTimer(Runnable runnable) {
super(runnable);
}
@Override protected long _start(Runnable runnable) {
throw new RuntimeException("vsync timer not supported");
}
@Override
protected native long _start(Runnable runnable, int period);
@Override
protected native void _stop(long timer);
@Override protected void _pause(long timer) {}
@Override protected void _resume(long timer) {}
}
