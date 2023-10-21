package test.javafx.concurrent;
import javafx.concurrent.Task;
import javafx.concurrent.TaskShim;
import test.javafx.concurrent.mocks.ProgressingTask;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TaskProgressTest {
private Task task;
@Before public void setup() {
task = new ProgressingTask();
}
@Test public void afterRunningWorkDoneShouldBe_20() {
task.run();
assertEquals(20, task.getWorkDone(), 0);
}
@Test public void afterRunningTotalWorkShouldBe_20() {
task.run();
assertEquals(20, task.getTotalWork(), 0);
}
@Test public void afterRunningProgressShouldBe_1() {
task.run();
assertEquals(1, task.getProgress(), 0);
}
@Test public void updateProgress_Long_0_100() {
TaskShim.updateProgress(task, 0, 100);
assertEquals(0, task.getProgress(), 0);
assertEquals(0, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_n1_100() {
TaskShim.updateProgress(task, -1, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_n10_100() {
TaskShim.updateProgress(task, -10, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_MIN_VALUE_100() {
TaskShim.updateProgress(task, Long.MIN_VALUE, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_10_100() {
TaskShim.updateProgress(task, 10, 100);
assertEquals(.1, task.getProgress(), 0);
assertEquals(10, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_100_100() {
TaskShim.updateProgress(task, 100, 100);
assertEquals(1, task.getProgress(), 0);
assertEquals(100, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_110_100() {
TaskShim.updateProgress(task, 110, 100);
assertEquals(1, task.getProgress(), 0);
assertEquals(100, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_MAX_VALUE_100() {
TaskShim.updateProgress(task, Long.MAX_VALUE, 100);
assertEquals(1, task.getProgress(), 0);
assertEquals(100, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_0_n1() {
TaskShim.updateProgress(task, 0, -1);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_0_n10() {
TaskShim.updateProgress(task, 0, -10);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_0_MIN_VALUE() {
TaskShim.updateProgress(task, 0, Long.MIN_VALUE);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_100_10() {
TaskShim.updateProgress(task, 100, 10);
assertEquals(1, task.getProgress(), 0);
assertEquals(10, task.getWorkDone(), 0);
assertEquals(10, task.getTotalWork(), 0);
}
@Test public void updateProgress_Long_100_MAX_VALUE() {
TaskShim.updateProgress(task, 100, Long.MAX_VALUE);
assertEquals(100.0 / Long.MAX_VALUE, task.getProgress(), 0);
assertEquals(100, task.getWorkDone(), 0);
assertEquals(Long.MAX_VALUE, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_Infinity_100() {
TaskShim.updateProgress(task, Double.POSITIVE_INFINITY, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NInfinity_100() {
TaskShim.updateProgress(task, Double.NEGATIVE_INFINITY, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NaN_100() {
TaskShim.updateProgress(task, Double.NaN, 100);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(100, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_0_Infinity() {
TaskShim.updateProgress(task, 0, Double.POSITIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_0_NInfinity() {
TaskShim.updateProgress(task, 0, Double.NEGATIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_0_NaN() {
TaskShim.updateProgress(task, 0, Double.NaN);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_Infinity_Infinity() {
TaskShim.updateProgress(task, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NInfinity_Infinity() {
TaskShim.updateProgress(task, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_Infinity_NInfinity() {
TaskShim.updateProgress(task, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NInfinity_NInfinity() {
TaskShim.updateProgress(task, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_Infinity_NaN() {
TaskShim.updateProgress(task, Double.POSITIVE_INFINITY, Double.NaN);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NInfinity_NaN() {
TaskShim.updateProgress(task, Double.NEGATIVE_INFINITY, Double.NaN);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NaN_Infinity() {
TaskShim.updateProgress(task, Double.NaN, Double.POSITIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NaN_NInfinity() {
TaskShim.updateProgress(task, Double.NaN, Double.NEGATIVE_INFINITY);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void updateProgress_Double_NaN_NaN() {
TaskShim.updateProgress(task, Double.NaN, Double.NaN);
assertEquals(-1, task.getProgress(), 0);
assertEquals(-1, task.getWorkDone(), 0);
assertEquals(-1, task.getTotalWork(), 0);
}
}
