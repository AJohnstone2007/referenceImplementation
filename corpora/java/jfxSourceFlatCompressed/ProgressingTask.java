package test.javafx.concurrent.mocks;
import test.javafx.concurrent.AbstractTask;
public class ProgressingTask extends AbstractTask {
@Override protected String call() throws Exception {
for (int i=0; i<=20; i++) {
updateProgress(i, 20);
}
return "Sentinel";
}
}
