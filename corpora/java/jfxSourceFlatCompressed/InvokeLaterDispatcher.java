package com.sun.glass.ui;
import java.util.*;
import java.util.concurrent.*;
public final class InvokeLaterDispatcher extends Thread {
private final BlockingDeque<Runnable> deque = new LinkedBlockingDeque<Runnable>();
private final Object LOCK = new StringBuilder("InvokeLaterLock");
private boolean nestedEventLoopEntered = false;
private volatile boolean leavingNestedEventLoop = false;
public static interface InvokeLaterSubmitter {
public void submitForLaterInvocation(Runnable r);
}
private final InvokeLaterSubmitter invokeLaterSubmitter;
public InvokeLaterDispatcher(InvokeLaterSubmitter invokeLaterSubmitter) {
super("InvokeLaterDispatcher");
setDaemon(true);
this.invokeLaterSubmitter = invokeLaterSubmitter;
}
private class Future implements Runnable {
private boolean done = false;
private final Runnable runnable;
public Future(Runnable r) {
this.runnable = r;
}
public boolean isDone() {
return done;
}
@Override public void run() {
try {
this.runnable.run();
} finally {
synchronized (LOCK) {
this.done = true;
LOCK.notifyAll();
}
}
}
}
@Override public void run() {
try {
while (true) {
Runnable r = deque.takeFirst();
if (leavingNestedEventLoop) {
deque.addFirst(r);
synchronized (LOCK) {
while (leavingNestedEventLoop) {
LOCK.wait();
}
}
} else {
final Future future = new Future(r);
invokeLaterSubmitter.submitForLaterInvocation(future);
synchronized (LOCK) {
try {
while (!future.isDone() && !nestedEventLoopEntered) {
LOCK.wait();
}
} finally {
nestedEventLoopEntered = false;
}
}
}
}
} catch (InterruptedException ex) {
}
}
public void invokeAndWait(Runnable runnable) {
final Future future = new Future(runnable);
invokeLaterSubmitter.submitForLaterInvocation(future);
synchronized (LOCK) {
try {
while (!future.isDone()) {
LOCK.wait();
}
} catch (InterruptedException ex) {
}
}
}
public void invokeLater(Runnable command) {
deque.addLast(command);
}
public void notifyEnteringNestedEventLoop() {
synchronized (LOCK) {
nestedEventLoopEntered = true;
LOCK.notifyAll();
}
}
public void notifyLeavingNestedEventLoop() {
synchronized (LOCK) {
leavingNestedEventLoop = true;
LOCK.notifyAll();
}
}
public void notifyLeftNestedEventLoop() {
synchronized (LOCK) {
leavingNestedEventLoop = false;
LOCK.notifyAll();
}
}
}
