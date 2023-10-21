package com.sun.glass.ui.mac;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Timer;
import java.security.AccessController;
import java.security.PrivilegedAction;
final class MacTimer extends Timer {
static {
minPeriod = _getMinPeriod();
maxPeriod = _getMaxPeriod();
_initIDs();
}
private static final int minPeriod, maxPeriod;
protected MacTimer(Runnable runnable) {
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
@Override native protected long _start(Runnable runnable);
@Override native protected long _start(Runnable runnable, int period);
@Override native protected void _stop(long timer);
@Override native protected void _pause(long timer);
@Override native protected void _resume(long timer);
native private static void _initIDs();
}
