package com.sun.glass.ui.win;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Timer;
import java.security.AccessController;
import java.security.PrivilegedAction;
final class WinTimer extends Timer {
static {
minPeriod = _getMinPeriod();
maxPeriod = _getMaxPeriod();
}
private static final int minPeriod, maxPeriod;
protected WinTimer(Runnable runnable) {
super(runnable);
}
native private static int _getMinPeriod();
native private static int _getMaxPeriod();
static int getMinPeriod_impl() {
return minPeriod;
}
static int getMaxPeriod_impl() {
return maxPeriod;
}
@Override protected long _start(Runnable runnable) {
throw new RuntimeException("vsync timer not supported");
}
@Override native protected long _start(Runnable runnable, int period);
@Override native protected void _stop(long timer);
@Override protected void _pause(long timer) {}
@Override protected void _resume(long timer) {}
}
