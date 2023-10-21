package test.javafx.concurrent;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import javafx.concurrent.Service;
import org.junit.Before;
public abstract class ServiceTestBase {
protected final ConcurrentLinkedQueue<Runnable> eventQueue =
new ConcurrentLinkedQueue<Runnable>();
protected TestServiceFactory factory;
protected Service<String> service;
protected abstract TestServiceFactory setupServiceFactory();
protected Executor createExecutor() {
return command -> {
if (command == null) Thread.dumpStack();
Thread th = new Thread() {
@Override public void run() {
try {
command.run();
} catch (Exception e) {
e.printStackTrace();
} finally {
eventQueue.add(new Sentinel());
}
}
};
th.setDaemon(true);
th.start();
};
}
@Before public void setup() {
factory = setupServiceFactory();
factory.test = this;
service = factory.createService();
service.setExecutor(createExecutor());
}
public void handleEvents() {
Runnable r;
do {
r = eventQueue.poll();
if (r != null) r.run();
} while (r == null || !(r instanceof Sentinel));
}
public static final class Sentinel implements Runnable {
@Override public void run() { }
}
}
