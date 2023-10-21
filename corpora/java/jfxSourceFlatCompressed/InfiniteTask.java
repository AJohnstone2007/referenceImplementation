package test.javafx.concurrent.mocks;
import test.javafx.concurrent.AbstractTask;
public class InfiniteTask extends AbstractTask {
@SuppressWarnings("InfiniteLoopStatement")
@Override protected String call() throws Exception {
while (true) {
Thread.sleep(1);
}
}
}
