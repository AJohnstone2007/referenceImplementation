package test.javafx.concurrent;
import test.javafx.concurrent.mocks.EpicFailTask;
import test.javafx.concurrent.mocks.SimpleTask;
import javafx.util.Callback;
import javafx.util.Duration;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.ScheduledServiceShim;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class ScheduledServiceTest extends ServiceTestBase {
private static final Callback<Void, AbstractTask> EPIC_FAIL_FACTORY = param -> new EpicFailTask();
private ScheduledServiceMock s;
private Callback<Void,AbstractTask> taskFactory = null;
private long wallClock;
@Override protected TestServiceFactory setupServiceFactory() {
return new TestServiceFactory() {
@Override public AbstractTask createTestTask() {
return taskFactory == null ? new SimpleTask() : taskFactory.call(null);
}
@Override public Service<String> createService() {
return new ScheduledServiceMock(this);
}
};
}
@Override public void setup() {
super.setup();
s = (ScheduledServiceMock) service;
wallClock = 0;
}
@Test public void setCumulativePeriod_MaxIsInfinity_TwoSeconds() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.seconds(2), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsInfinity_Negative() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsInfinity_NegativeInfinity() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsInfinity_NaN() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsInfinity_PositiveInfinity() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsInfinity_MAX_VALUE() {
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.millis(Double.MAX_VALUE), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_TwoSeconds() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.seconds(2), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_Negative() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_NegativeInfinity() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_NaN() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_PositiveInfinity() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNaN_MAX_VALUE() {
s.setMaximumCumulativePeriod(Duration.UNKNOWN);
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.millis(Double.MAX_VALUE), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_TwoSeconds() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.seconds(2), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_Negative() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_NegativeInfinity() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_NaN() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_PositiveInfinity() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNull_MAX_VALUE() {
s.setMaximumCumulativePeriod(null);
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.millis(Double.MAX_VALUE), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_TwoSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.seconds(2), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_TenSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(10));
assertEquals(Duration.seconds(10), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_TwelveSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(12));
assertEquals(Duration.seconds(10), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_Negative() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_NegativeInfinity() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_NaN() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_PositiveInfinity() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.seconds(10), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs10_MAX_VALUE() {
s.setMaximumCumulativePeriod(Duration.seconds(10));
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.seconds(10), s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_TwoSeconds() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_TenSeconds() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(10));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_TwelveSeconds() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(12));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_Negative() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_NegativeInfinity() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_NaN() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_PositiveInfinity() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIs0_MAX_VALUE() {
s.setMaximumCumulativePeriod(Duration.ZERO);
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_TwoSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_TenSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(10));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_TwelveSeconds() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(12));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_Negative() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(-2));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_NegativeInfinity() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NEGATIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_NaN() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.NaN));
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_PositiveInfinity() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.seconds(Double.POSITIVE_INFINITY));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void setCumulativePeriod_MaxIsNegative_MAX_VALUE() {
s.setMaximumCumulativePeriod(Duration.seconds(-1));
ScheduledServiceShim.setCumulativePeriod(s, Duration.millis(Double.MAX_VALUE));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
}
@Test public void delayIsHonored_Positive() throws InterruptedException {
s.setDelay(Duration.seconds(1));
s.start();
assertEquals(1000, wallClock);
}
@Test public void delayIsHonored_Unknown() throws InterruptedException {
s.setDelay(Duration.UNKNOWN);
s.start();
assertEquals(0, wallClock);
}
@Test public void delayIsHonored_Infinite() throws InterruptedException {
s.setDelay(Duration.INDEFINITE);
s.start();
assertEquals(Long.MAX_VALUE, wallClock);
}
@Test public void delayIsHonored_ZERO() throws InterruptedException {
s.setDelay(Duration.ZERO);
s.start();
assertEquals(0, wallClock);
}
@Test public void delayIsNotUsedOnSubsequentIteration() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
s.iterate();
assertEquals(4000, wallClock);
}
@Test public void delayIsUsedOnRestart() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
s.iterate();
s.cancel();
wallClock = 0;
s.restart();
assertEquals(1000, wallClock);
}
@Test public void delayIsUsedOnStartFollowingReset() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
s.iterate();
s.cancel();
wallClock = 0;
s.reset();
s.start();
assertEquals(1000, wallClock);
}
@Test public void periodDoesNotContributeToDelay() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
assertEquals(1000, wallClock);
}
@Test public void executionTimeLessThanPeriod() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
s.iterate();
assertEquals(4000, wallClock);
}
@Test public void executionTimeEqualsPeriod() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
wallClock += 3000;
s.iterate();
assertEquals(4000, wallClock);
}
@Test public void executionTimeExceedsPeriod() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
wallClock += 6000;
s.iterate();
assertEquals(7000, wallClock);
}
@Test public void startOfPeriodIsResetAfterReset() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
wallClock += 6000;
s.iterate();
s.cancel();
wallClock = 0;
s.reset();
s.start();
s.iterate();
assertEquals(4000, wallClock);
}
@Test public void startOfPeriodIsResetAfterRestart() {
s.setDelay(Duration.seconds(1));
s.setPeriod(Duration.seconds(3));
s.start();
wallClock += 6000;
s.iterate();
s.cancel();
wallClock = 0;
s.reset();
s.start();
s.iterate();
assertEquals(4000, wallClock);
}
@Test public void onFailureCumulativePeriodIsIncreased_EXPONENTIAL_BACKOFF_zero() {
s.setBackoffStrategy(ScheduledService.EXPONENTIAL_BACKOFF_STRATEGY);
s.setPeriod(Duration.ZERO);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(Math.exp(1)), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_EXPONENTIAL_BACKOFF_one() {
s.setBackoffStrategy(ScheduledService.EXPONENTIAL_BACKOFF_STRATEGY);
s.setPeriod(Duration.seconds(1));
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(1000 + (1000 * Math.exp(1))), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_EXPONENTIAL_BACKOFF_indefinite() {
s.setBackoffStrategy(ScheduledService.EXPONENTIAL_BACKOFF_STRATEGY);
s.setPeriod(Duration.INDEFINITE);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_EXPONENTIAL_BACKOFF_unknown() {
s.setBackoffStrategy(ScheduledService.EXPONENTIAL_BACKOFF_STRATEGY);
s.setPeriod(Duration.UNKNOWN);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LOGARITHMIC_BACKOFF_zero() {
s.setBackoffStrategy(ScheduledService.LOGARITHMIC_BACKOFF_STRATEGY);
s.setPeriod(Duration.ZERO);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(Math.log1p(1)), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LOGARITHMIC_BACKOFF_one() {
s.setBackoffStrategy(ScheduledService.LOGARITHMIC_BACKOFF_STRATEGY);
s.setPeriod(Duration.seconds(1));
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(1000 + (1000 * Math.log1p(1))), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LOGARITHMIC_BACKOFF_indefinite() {
s.setBackoffStrategy(ScheduledService.LOGARITHMIC_BACKOFF_STRATEGY);
s.setPeriod(Duration.INDEFINITE);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LOGARITHMIC_BACKOFF_unknown() {
s.setBackoffStrategy(ScheduledService.LOGARITHMIC_BACKOFF_STRATEGY);
s.setPeriod(Duration.UNKNOWN);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LINEAR_BACKOFF_zero() {
s.setBackoffStrategy(ScheduledService.LINEAR_BACKOFF_STRATEGY);
s.setPeriod(Duration.ZERO);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(1), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LINEAR_BACKOFF_one() {
s.setBackoffStrategy(ScheduledService.LINEAR_BACKOFF_STRATEGY);
s.setPeriod(Duration.seconds(1));
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.millis(1000 + (1000 * 1)), s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LINEAR_BACKOFF_indefinite() {
s.setBackoffStrategy(ScheduledService.LINEAR_BACKOFF_STRATEGY);
s.setPeriod(Duration.INDEFINITE);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.INDEFINITE, s.getCumulativePeriod());
}
@Test public void onFailureCumulativePeriodIsIncreased_LINEAR_BACKOFF_unknown() {
s.setBackoffStrategy(ScheduledService.LINEAR_BACKOFF_STRATEGY);
s.setPeriod(Duration.UNKNOWN);
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertEquals(Duration.UNKNOWN, s.getCumulativePeriod());
}
@Test public void cumulativePeriodSetWhenScheduled() {
assertEquals(Duration.ZERO, s.getCumulativePeriod());
s.setPeriod(Duration.seconds(1));
assertEquals(Duration.ZERO, s.getCumulativePeriod());
s.start();
assertEquals(Duration.seconds(1), s.getCumulativePeriod());
}
@Test public void cumulativePeriodDoesNotChangeOnSuccessfulRun() {
s.setPeriod(Duration.seconds(1));
s.start();
s.iterate();
assertEquals(Duration.seconds(1), s.getCumulativePeriod());
}
@Test public void cumulativePeriodResetOnSuccessfulRun() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = param -> new AbstractTask() {
@Override protected String call() throws Exception {
int c = counter.incrementAndGet();
if (c < 10) throw new Exception("Kaboom!");
return "Success";
}
};
s.setPeriod(Duration.seconds(1));
s.start();
for (int i=0; i<8; i++) s.iterate();
assertTrue(Duration.seconds(1).lessThan(s.getCumulativePeriod()));
s.iterate();
assertEquals(Duration.seconds(1), s.getCumulativePeriod());
}
@Test public void cumulativePeriodDoesNotChangeOnCancelRun() {
s.setPeriod(Duration.seconds(1));
s.start();
s.iterate();
s.cancel();
assertEquals(Duration.seconds(1), s.getCumulativePeriod());
}
@Test public void restartOnFailure_True() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = new Callback<Void, AbstractTask>() {
@Override public AbstractTask call(Void param) {
return new EpicFailTask() {
@Override protected String call() throws Exception {
counter.incrementAndGet();
return super.call();
}
};
}
};
s.start();
assertEquals(Worker.State.SCHEDULED, s.getState());
s.iterate();
assertEquals(2, counter.get());
}
@Test public void restartOnFailure_False() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = new Callback<Void, AbstractTask>() {
@Override public AbstractTask call(Void param) {
return new EpicFailTask() {
@Override protected String call() throws Exception {
counter.incrementAndGet();
return super.call();
}
};
}
};
s.setRestartOnFailure(false);
s.start();
assertEquals(Worker.State.FAILED, s.getState());
assertEquals(1, counter.get());
}
@Test public void serviceIteratesWhile_CurrentFailureCount_IsLessThan_MaximumFailureCount() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = new Callback<Void, AbstractTask>() {
@Override public AbstractTask call(Void param) {
return new EpicFailTask() {
@Override protected String call() throws Exception {
counter.incrementAndGet();
return super.call();
}
};
}
};
s.setMaximumFailureCount(10);
s.start();
while (s.getState() != Worker.State.FAILED) {
assertEquals(counter.get(), s.getCurrentFailureCount());
s.iterate();
}
assertEquals(10, counter.get());
assertEquals(counter.get(), s.getCurrentFailureCount());
}
@Test public void currentFailureCountIsResetOnRestart() {
taskFactory = EPIC_FAIL_FACTORY;
s.start();
for (int i=0; i<10; i++) s.iterate();
taskFactory = null;
s.restart();
assertEquals(0, s.getCurrentFailureCount());
}
@Test public void currentFailureCountIsResetOnReset() {
taskFactory = EPIC_FAIL_FACTORY;
s.start();
for (int i=0; i<10; i++) s.iterate();
s.cancel();
s.reset();
assertEquals(0, s.getCurrentFailureCount());
}
@Test public void currentFailureCountIsNotResetOnCancel() {
taskFactory = EPIC_FAIL_FACTORY;
s.start();
for (int i=0; i<10; i++) s.iterate();
s.cancel();
assertEquals(11, s.getCurrentFailureCount());
}
@Test public void lastValueIsInitiallyNull() {
assertNull(s.getLastValue());
}
@Test public void lastValueIsNullAfterFailedFirstIteration() {
taskFactory = EPIC_FAIL_FACTORY;
s.start();
assertNull(s.getLastValue());
}
@Test public void lastValueIsSetAfterSuccessfulFirstIteration() {
s.start();
assertEquals("Sentinel", s.getLastValue());
assertNull(s.getValue());
}
@Test public void lastValueIsSetAfterFailedFirstIterationAndSuccessfulSecondIteration() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = param -> new AbstractTask() {
@Override protected String call() throws Exception {
int c = counter.incrementAndGet();
if (c == 1) throw new Exception("Bombed out!");
return "Success";
}
};
s.start();
assertNull(s.getLastValue());
assertNull(s.getValue());
s.iterate();
assertEquals("Success", s.getLastValue());
assertNull(s.getValue());
}
@Test public void lastValueIsUnchangedAfterSuccessfulFirstIterationAndFailedSecondIteration() {
final AtomicInteger counter = new AtomicInteger();
taskFactory = param -> new AbstractTask() {
@Override protected String call() throws Exception {
int c = counter.incrementAndGet();
if (c == 1) return "Success";
throw new Exception("Bombed out!");
}
};
s.start();
assertEquals("Success", s.getLastValue());
assertNull(s.getValue());
s.iterate();
assertEquals("Success", s.getLastValue());
assertNull(s.getValue());
}
@Test public void lastValueIsClearedOnReset() {
s.start();
assertEquals("Sentinel", s.getLastValue());
s.cancel();
assertEquals("Sentinel", s.getLastValue());
s.reset();
assertNull(s.getLastValue());
}
@Test public void callingCancelFromOnSucceededEventHandlerShouldStopScheduledService() {
AtomicBoolean onReadyCalled = new AtomicBoolean();
AtomicBoolean onScheduledCalled = new AtomicBoolean();
AtomicBoolean onCancelledCalled = new AtomicBoolean();
s.setOnSucceeded(event -> {
s.cancel();
onReadyCalled.set(false);
onScheduledCalled.set(false);
onCancelledCalled.set(false);
});
s.setOnReady(event -> onReadyCalled.set(true));
s.setOnScheduled(event -> onScheduledCalled.set(true));
s.setOnCancelled(event -> onCancelledCalled.set(true));
s.start();
assertFalse(s.isRunning());
assertEquals(Worker.State.CANCELLED, s.getState());
assertTrue(onReadyCalled.get());
assertTrue(onScheduledCalled.get());
assertTrue(onCancelledCalled.get());
}
private final class ScheduledServiceMock extends ScheduledServiceShim<String> {
private TestServiceFactory factory;
private Task<String> nextTask = null;
ScheduledServiceMock(TestServiceFactory f) {
this.factory = f;
}
@Override protected Task<String> createTask() {
factory.currentTask = factory.createTestTask();
factory.currentTask.set_test(factory.test);
return factory.currentTask;
}
@Override public void checkThread() { }
@Override public void schedule(TimerTask task, long delay) {
wallClock += delay;
task.run();
}
@Override public void executeTask(Task<String> task) {
nextTask = task;
if (isFreshStart()) iterate();
}
@Override public long clock() {
return wallClock;
}
@Override public boolean isFxApplicationThread() {
return Thread.currentThread() == factory.appThread;
}
void iterate() {
assert nextTask != null;
Task<String> task = nextTask;
nextTask = null;
super.executeTask(task);
handleEvents();
}
}
}
