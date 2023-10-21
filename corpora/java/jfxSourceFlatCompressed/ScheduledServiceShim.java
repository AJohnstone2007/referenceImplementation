package javafx.concurrent;
import java.util.TimerTask;
import javafx.util.Duration;
public abstract class ScheduledServiceShim<V> extends ScheduledService<V> {
public static void setCumulativePeriod(ScheduledService ss,
Duration value) {
ss.setCumulativePeriod(value);
}
@Override
public void checkThread() {
super.checkThread();
}
@Override
public void schedule(TimerTask task, long delay) {
super.schedule(task, delay);
}
@Override public void executeTask(final Task<V> task) {
super.executeTask(task);
}
@Override
public long clock() {
return super.clock();
}
@Override
public boolean isFxApplicationThread() {
return super.isFxApplicationThread();
}
@Override
public boolean isFreshStart() {
return super.isFreshStart();
}
}
