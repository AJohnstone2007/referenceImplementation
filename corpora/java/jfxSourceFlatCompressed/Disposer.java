package com.sun.webkit;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
public final class Disposer implements Runnable {
private static final ReferenceQueue queue = new ReferenceQueue();
private static final Disposer disposerInstance = new Disposer();
private static final Set<WeakDisposerRecord> records =
new HashSet<WeakDisposerRecord>();
static {
@SuppressWarnings("removal")
var dummy = java.security.AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
ThreadGroup tg = Thread.currentThread().getThreadGroup();
for (ThreadGroup tgn = tg;
tgn != null;
tg = tgn, tgn = tg.getParent());
Thread t = new Thread(tg, disposerInstance, "Disposer");
t.setDaemon(true);
t.setPriority(Thread.MAX_PRIORITY);
t.start();
return null;
});
}
public static void addRecord(Object target, DisposerRecord rec) {
disposerInstance.add(target, rec);
}
private synchronized void add(Object target, DisposerRecord rec) {
records.add(new WeakDisposerRecord(target, rec));
}
public static void addRecord(WeakDisposerRecord rec) {
disposerInstance.add(rec);
}
private synchronized void add(WeakDisposerRecord rec) {
records.add(rec);
}
public void run() {
while (true) {
try {
WeakDisposerRecord obj = (WeakDisposerRecord) queue.remove();
obj.clear();
DisposerRunnable.getInstance().enqueue(obj);
} catch (Exception e) {
System.out.println("Exception while removing reference: " + e);
e.printStackTrace();
}
}
}
private static final class DisposerRunnable implements Runnable {
private static final DisposerRunnable theInstance = new DisposerRunnable();
private static DisposerRunnable getInstance() {
return theInstance;
}
private boolean isRunning = false;
private final Object disposerLock = new Object();
private final LinkedBlockingQueue<WeakDisposerRecord> disposerQueue
= new LinkedBlockingQueue<WeakDisposerRecord>();
private void enqueueAll(Collection<WeakDisposerRecord> objs) {
synchronized (disposerLock) {
disposerQueue.addAll(objs);
if (!isRunning) {
Invoker.getInvoker().invokeOnEventThread(this);
isRunning = true;
}
}
}
private void enqueue(WeakDisposerRecord obj) {
enqueueAll(Arrays.asList(obj));
}
@Override public void run() {
while (true) {
WeakDisposerRecord obj;
synchronized (disposerLock) {
obj = disposerQueue.poll();
if (obj == null) {
isRunning = false;
break;
}
}
if (records.contains(obj)) {
records.remove(obj);
obj.dispose();
}
}
}
}
public static class WeakDisposerRecord
extends WeakReference
implements DisposerRecord
{
protected WeakDisposerRecord(Object referent) {
super(referent, Disposer.queue);
this.record = null;
}
private WeakDisposerRecord(Object referent, DisposerRecord record) {
super(referent, Disposer.queue);
this.record = record;
}
private final DisposerRecord record;
@Override
public void dispose() {
record.dispose();
}
}
}
