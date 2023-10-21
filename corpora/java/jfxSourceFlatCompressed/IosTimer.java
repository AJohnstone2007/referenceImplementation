package com.sun.glass.ui.ios;
import com.sun.glass.ui.Timer;
final class IosTimer extends Timer implements Runnable {
private Thread timerThread;
private Runnable timerRunnable;
private long timerPeriod;
protected IosTimer(Runnable runnable) {
super(runnable);
}
@Override native protected long _start(Runnable runnable);
@Override protected void _pause(long timer) {}
@Override protected void _resume(long timer) {}
native protected void _stopVsyncTimer(long timer);
@Override
protected long _start(Runnable runnable, int period) {
timerThread = new Thread(this);
timerRunnable = runnable;
timerPeriod = period;
timerThread.start();
return timerThread.hashCode();
}
@Override
protected void _stop(long timer) {
if (timerThread != null ) {
Thread t = timerThread;
timerThread = null;
try {
t.join();
} catch (InterruptedException e) { }
} else {
_stopVsyncTimer(timer);
}
}
static int getMinPeriod_impl() {
return 0;
}
static int getMaxPeriod_impl() {
return 1000000;
}
@Override
public void run() {
Thread t = Thread.currentThread();
long start;
long sleepTime;
while (t == timerThread) {
start = System.currentTimeMillis();
timerRunnable.run();
sleepTime = timerPeriod - (System.currentTimeMillis() - start);
if (sleepTime > 0) {
try {
Thread.sleep(sleepTime);
} catch (InterruptedException e) { }
}
}
}
}
