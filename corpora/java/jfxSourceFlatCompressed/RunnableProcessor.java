package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Application;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
class RunnableProcessor implements Runnable {
private RunnableQueue queue = new RunnableQueue();
private static class RunLoopControl {
boolean active;
Object release;
}
private LinkedList<RunLoopControl> activeRunLoops = new LinkedList<RunLoopControl>();
@Override
public void run() {
runLoop();
}
void invokeLater(Runnable r) {
queue.postRunnable(r);
}
void invokeAndWait(final Runnable r) {
final CountDownLatch latch = new CountDownLatch(1);
queue.postRunnable(() -> {
try {
r.run();
} finally {
latch.countDown();
}
});
try {
latch.await();
} catch (InterruptedException e) { }
}
private Object runLoop() {
final RunLoopControl control = new RunLoopControl();
activeRunLoops.push(control);
control.active = true;
while (control.active) {
try {
queue.getNextRunnable().run();
} catch (Throwable e) {
Application.reportException(e);
}
}
return control.release;
}
Object enterNestedEventLoop() {
Object ret = runLoop();
return ret;
}
void leaveNestedEventLoop(Object retValue) {
RunLoopControl current = activeRunLoops.pop();
assert current != null;
current.active = false;
current.release = retValue;
}
void shutdown() {
synchronized (queue) {
queue.clear();
while (!activeRunLoops.isEmpty()) {
RunLoopControl control = activeRunLoops.pop();
control.active = false;
}
queue.notifyAll();
}
}
static void runLater(Runnable r) {
NativePlatformFactory.getNativePlatform()
.getRunnableProcessor()
.invokeLater(r);
}
}
