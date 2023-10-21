package javafx.concurrent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import javafx.util.Duration;
import java.util.Timer;
import java.util.TimerTask;
public abstract class ScheduledService<V> extends Service<V> {
public ScheduledService() {
}
public static final Callback<ScheduledService<?>, Duration> EXPONENTIAL_BACKOFF_STRATEGY
= new Callback<ScheduledService<?>, Duration>() {
@Override public Duration call(ScheduledService<?> service) {
if (service == null) return Duration.ZERO;
final double period = service.getPeriod() == null ? 0 : service.getPeriod().toMillis();
final double x = service.getCurrentFailureCount();
return Duration.millis(period == 0 ? Math.exp(x) : period + (period * Math.exp(x)));
}
};
public static final Callback<ScheduledService<?>, Duration> LOGARITHMIC_BACKOFF_STRATEGY
= new Callback<ScheduledService<?>, Duration>() {
@Override public Duration call(ScheduledService<?> service) {
if (service == null) return Duration.ZERO;
final double period = service.getPeriod() == null ? 0 : service.getPeriod().toMillis();
final double x = service.getCurrentFailureCount();
return Duration.millis(period == 0 ? Math.log1p(x) : period + (period * Math.log1p(x)));
}
};
public static final Callback<ScheduledService<?>, Duration> LINEAR_BACKOFF_STRATEGY
= new Callback<ScheduledService<?>, Duration>() {
@Override public Duration call(ScheduledService<?> service) {
if (service == null) return Duration.ZERO;
final double period = service.getPeriod() == null ? 0 : service.getPeriod().toMillis();
final double x = service.getCurrentFailureCount();
return Duration.millis(period == 0 ? x : period + (period * x));
}
};
private static final Timer DELAY_TIMER = new Timer("ScheduledService Delay Timer", true);
private ObjectProperty<Duration> delay = new SimpleObjectProperty<>(this, "delay", Duration.ZERO);
public final Duration getDelay() { return delay.get(); }
public final void setDelay(Duration value) { delay.set(value); }
public final ObjectProperty<Duration> delayProperty() { return delay; }
private ObjectProperty<Duration> period = new SimpleObjectProperty<>(this, "period", Duration.ZERO);
public final Duration getPeriod() { return period.get(); }
public final void setPeriod(Duration value) { period.set(value); }
public final ObjectProperty<Duration> periodProperty() { return period; }
private ObjectProperty<Callback<ScheduledService<?>,Duration>> backoffStrategy =
new SimpleObjectProperty<>(this, "backoffStrategy", LOGARITHMIC_BACKOFF_STRATEGY);
public final Callback<ScheduledService<?>,Duration> getBackoffStrategy() { return backoffStrategy.get(); }
public final void setBackoffStrategy(Callback<ScheduledService<?>, Duration> value) { backoffStrategy.set(value); }
public final ObjectProperty<Callback<ScheduledService<?>,Duration>> backoffStrategyProperty() { return backoffStrategy; }
private BooleanProperty restartOnFailure = new SimpleBooleanProperty(this, "restartOnFailure", true);
public final boolean getRestartOnFailure() { return restartOnFailure.get(); }
public final void setRestartOnFailure(boolean value) { restartOnFailure.set(value); }
public final BooleanProperty restartOnFailureProperty() { return restartOnFailure; }
private IntegerProperty maximumFailureCount = new SimpleIntegerProperty(this, "maximumFailureCount", Integer.MAX_VALUE);
public final int getMaximumFailureCount() { return maximumFailureCount.get(); }
public final void setMaximumFailureCount(int value) { maximumFailureCount.set(value); }
public final IntegerProperty maximumFailureCountProperty() { return maximumFailureCount; }
private ReadOnlyIntegerWrapper currentFailureCount = new ReadOnlyIntegerWrapper(this, "currentFailureCount", 0);
public final int getCurrentFailureCount() { return currentFailureCount.get(); }
public final ReadOnlyIntegerProperty currentFailureCountProperty() { return currentFailureCount.getReadOnlyProperty(); }
private void setCurrentFailureCount(int value) {
currentFailureCount.set(value);
}
private ReadOnlyObjectWrapper<Duration> cumulativePeriod = new ReadOnlyObjectWrapper<>(this, "cumulativePeriod", Duration.ZERO);
public final Duration getCumulativePeriod() { return cumulativePeriod.get(); }
public final ReadOnlyObjectProperty<Duration> cumulativePeriodProperty() { return cumulativePeriod.getReadOnlyProperty(); }
void setCumulativePeriod(Duration value) {
Duration newValue = value == null || value.toMillis() < 0 ? Duration.ZERO : value;
Duration maxPeriod = maximumCumulativePeriod.get();
if (maxPeriod != null && !maxPeriod.isUnknown() && !newValue.isUnknown()) {
if (maxPeriod.toMillis() < 0) {
newValue = Duration.ZERO;
} else if (!maxPeriod.isIndefinite() && newValue.greaterThan(maxPeriod)) {
newValue = maxPeriod;
}
}
cumulativePeriod.set(newValue);
}
private ObjectProperty<Duration> maximumCumulativePeriod = new SimpleObjectProperty<>(this, "maximumCumulativePeriod", Duration.INDEFINITE);
public final Duration getMaximumCumulativePeriod() { return maximumCumulativePeriod.get(); }
public final void setMaximumCumulativePeriod(Duration value) { maximumCumulativePeriod.set(value); }
public final ObjectProperty<Duration> maximumCumulativePeriodProperty() { return maximumCumulativePeriod; }
private ReadOnlyObjectWrapper<V> lastValue = new ReadOnlyObjectWrapper<>(this, "lastValue", null);
public final V getLastValue() { return lastValue.get(); }
public final ReadOnlyObjectProperty<V> lastValueProperty() { return lastValue.getReadOnlyProperty(); }
private long lastRunTime = 0L;
private boolean freshStart = true;
private TimerTask delayTask = null;
private boolean stop = false;
@Override protected void executeTask(final Task<V> task) {
assert task != null;
checkThread();
if (freshStart) {
assert delayTask == null;
setCumulativePeriod(getPeriod());
final long d = (long) normalize(getDelay());
if (d == 0) {
executeTaskNow(task);
} else {
schedule(delayTask = createTimerTask(task), d);
}
} else {
double cumulative = normalize(getCumulativePeriod());
double runPeriod = clock() - lastRunTime;
if (runPeriod < cumulative) {
assert delayTask == null;
schedule(delayTask = createTimerTask(task), (long) (cumulative - runPeriod));
} else {
executeTaskNow(task);
}
}
}
@Override protected void succeeded() {
super.succeeded();
lastValue.set(getValue());
Duration d = getPeriod();
setCumulativePeriod(d);
final boolean wasCancelled = stop;
superReset();
assert freshStart == false;
if (wasCancelled) {
cancelFromReadyState();
} else {
start();
}
}
@Override protected void failed() {
super.failed();
assert delayTask == null;
setCurrentFailureCount(getCurrentFailureCount() + 1);
if (getRestartOnFailure() && getMaximumFailureCount() > getCurrentFailureCount()) {
Callback<ScheduledService<?>,Duration> func = getBackoffStrategy();
if (func != null) {
Duration d = func.call(this);
setCumulativePeriod(d);
}
superReset();
assert freshStart == false;
start();
} else {
}
}
@Override public void reset() {
super.reset();
stop = false;
setCumulativePeriod(getPeriod());
lastValue.set(null);
setCurrentFailureCount(0);
lastRunTime = 0L;
freshStart = true;
}
@Override public boolean cancel() {
boolean ret = super.cancel();
stop = true;
if (delayTask != null) {
delayTask.cancel();
delayTask = null;
}
return ret;
}
void schedule(TimerTask task, long delay) {
DELAY_TIMER.schedule(task, delay);
}
boolean isFreshStart() { return freshStart; }
long clock() {
return System.currentTimeMillis();
}
private void superReset() {
super.reset();
}
private TimerTask createTimerTask(final Task<V> task) {
assert task != null;
return new TimerTask() {
@Override public void run() {
Runnable r = () -> {
executeTaskNow(task);
delayTask = null;
};
if (isFxApplicationThread()) {
r.run();
} else {
runLater(r);
}
}
};
}
private void executeTaskNow(Task<V> task) {
assert task != null;
lastRunTime = clock();
freshStart = false;
super.executeTask(task);
}
private static double normalize(Duration d) {
if (d == null || d.isUnknown()) return 0;
if (d.isIndefinite()) return Double.MAX_VALUE;
return d.toMillis();
}
}
