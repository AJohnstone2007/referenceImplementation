package com.sun.javafx.webkit;
import com.sun.javafx.tk.Toolkit;
import com.sun.webkit.EventLoop;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
public final class EventLoopImpl extends EventLoop {
private static final long DELAY = 20;
private static final ScheduledExecutorService executor =
Executors.newSingleThreadScheduledExecutor();
@Override
protected void cycle() {
final Object key = new Object();
executor.schedule(() -> {
Platform.runLater(new Runnable() {
@Override
public void run() {
Toolkit.getToolkit().exitNestedEventLoop(key, null);
}
});
}, DELAY, TimeUnit.MILLISECONDS);
Toolkit.getToolkit().enterNestedEventLoop(key);
}
}
