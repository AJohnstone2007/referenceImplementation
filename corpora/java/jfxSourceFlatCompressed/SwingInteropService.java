package ensemble.samples.language.swing;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
public class SwingInteropService extends Service<Process> {
SimpleBooleanProperty bp = new SimpleBooleanProperty(false);
@Override
protected Task createTask() {
return new SwingInteropTask();
}
@Override
protected void scheduled() {
bp.set(true);
}
@Override
protected void cancelled() {
bp.set(false);
}
@Override
protected void succeeded() {
bp.set(false);
}
@Override
protected void failed() {
bp.set(false);
}
}
