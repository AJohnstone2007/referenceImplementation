package com.sun.javafx.tk.quantum;
import com.sun.javafx.perf.PerformanceTracker;
final class PerformanceTrackerImpl extends PerformanceTracker {
final PerformanceTrackerHelper helper =
PerformanceTrackerHelper.getInstance();
public PerformanceTrackerImpl() {
setPerfLoggingEnabled(helper.isPerfLoggingEnabled());
}
@Override public void doLogEvent(String s) {
helper.logEvent(s);
}
@Override public void doOutputLog() {
helper.outputLog();
}
@Override public long nanoTime() {
return helper.nanoTime();
}
}
