package test.javafx.concurrent.mocks;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import test.javafx.concurrent.AbstractTask;
public abstract class RunAwayTask extends AbstractTask {
public AtomicBoolean stopLooping = new AtomicBoolean(false);
private boolean loopHasHappened = false;
@Override protected String call() throws Exception {
int count = 0;
while (!loopHasHappened || !stopLooping.get()) {
count++;
loop(count);
loopHasHappened = true;
}
return "" + count;
}
protected abstract void loop(int count) throws Exception;
}
