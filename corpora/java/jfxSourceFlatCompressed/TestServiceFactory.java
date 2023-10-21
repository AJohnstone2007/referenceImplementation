package test.javafx.concurrent;
import javafx.concurrent.Service;
import javafx.concurrent.ServiceShim;
import javafx.concurrent.Task;
public abstract class TestServiceFactory {
public final Thread appThread = Thread.currentThread();
public ServiceTestBase test;
public AbstractTask currentTask;
public abstract AbstractTask createTestTask();
public Service<String> createService() {
return new ServiceShim<String>() {
@Override protected Task<String> createTask() {
currentTask = createTestTask();
currentTask.set_test(test);
currentTask.appThread = appThread;
return currentTask;
}
@Override public boolean isFxApplicationThread() {
return Thread.currentThread() == appThread;
}
@Override public void runLater(Runnable r) {
if (test != null) {
test.eventQueue.add(r);
} else {
r.run();
}
}
};
}
public final AbstractTask getCurrentTask() { return currentTask; }
}
