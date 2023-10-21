package test.com.sun.javafx.pgstub;
import com.sun.javafx.perf.PerformanceTracker;
public class StubPerformanceTracker extends PerformanceTracker {
public StubPerformanceTracker() {
}
@Override public void doLogEvent(String s) {
}
@Override public void doOutputLog() {
}
@Override public long nanoTime() {
return 0;
}
}
