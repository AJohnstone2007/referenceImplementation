package com.sun.javafx.embed.swing.newimpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.Toolkit;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import jdk.swing.interop.DispatcherWrapper;
public class SwingFXUtilsImplInteropN {
private static class FwSecondaryLoop implements SecondaryLoop {
private final AtomicBoolean isRunning = new AtomicBoolean(false);
@Override
public boolean enter() {
if (isRunning.compareAndSet(false, true)) {
PlatformImpl.runAndWait(() -> {
Toolkit.getToolkit().enterNestedEventLoop(FwSecondaryLoop.this);
});
return true;
}
return false;
}
@Override
public boolean exit() {
if (isRunning.compareAndSet(true, false)) {
PlatformImpl.runAndWait(() -> {
Toolkit.getToolkit().exitNestedEventLoop(FwSecondaryLoop.this, null);
});
return true;
}
return false;
}
}
private static class FXDispatcher extends DispatcherWrapper {
@Override
public boolean isDispatchThread() {
return Platform.isFxApplicationThread();
}
@Override
public void scheduleDispatch(Runnable runnable) {
Platform.runLater(runnable);
}
@Override
public SecondaryLoop createSecondaryLoop() {
return new FwSecondaryLoop();
}
}
public void setFwDispatcher(EventQueue eventQueue) {
DispatcherWrapper.setFwDispatcher(eventQueue, new FXDispatcher());
}
}
