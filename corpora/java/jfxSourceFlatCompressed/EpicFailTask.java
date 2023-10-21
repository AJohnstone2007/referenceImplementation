package test.javafx.concurrent.mocks;
import test.javafx.concurrent.AbstractTask;
import javafx.concurrent.TaskShim;
public class EpicFailTask extends AbstractTask {
private Exception exception;
public EpicFailTask() {
this(new Exception("Failure"));
}
public EpicFailTask(Exception exception) {
this.exception = exception;
}
@Override protected String call() throws Exception {
updateProgress(10, 20);
updateMessage("About to fail");
updateTitle("Epic Fail");
throw exception;
}
}
