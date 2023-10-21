package com.sun.webkit;
import com.sun.webkit.perf.PerfLogger;
import java.util.concurrent.locks.ReentrantLock;
public abstract class Invoker {
private static Invoker instance;
private static final PerfLogger locksLog = PerfLogger.getLogger("Locks");
public static synchronized void setInvoker(Invoker invoker) {
instance = invoker;
}
public static synchronized Invoker getInvoker() {
return instance;
}
protected boolean lock(ReentrantLock lock) {
if (lock.getHoldCount() == 0) {
lock.lock();
locksLog.resumeCount(isEventThread() ? "EventThread" : "RenderThread");
return true;
}
return false;
}
protected boolean unlock(ReentrantLock lock) {
if (lock.getHoldCount() != 0) {
locksLog.suspendCount(isEventThread() ? "EventThread" : "RenderThread");
lock.unlock();
return true;
}
return false;
}
protected abstract boolean isEventThread();
public void checkEventThread() {
if (!isEventThread()) {
throw new IllegalStateException("Current thread is not event thread"
+ ", current thread: " + Thread.currentThread().getName());
}
}
public abstract void invokeOnEventThread(Runnable r);
public abstract void postOnEventThread(Runnable r);
}
