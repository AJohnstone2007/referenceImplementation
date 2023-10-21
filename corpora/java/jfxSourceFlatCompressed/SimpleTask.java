package test.javafx.concurrent.mocks;
import test.javafx.concurrent.AbstractTask;
public class SimpleTask extends AbstractTask {
@Override protected String call() throws Exception {
return "Sentinel";
}
}
