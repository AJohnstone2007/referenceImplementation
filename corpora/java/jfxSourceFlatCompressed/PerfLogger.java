package com.sun.webkit.perf;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
public final class PerfLogger {
private static Thread shutdownHook;
private static Map<PlatformLogger, PerfLogger> loggers;
private final HashMap<String, ProbeStat> probes =
new HashMap<String, ProbeStat>();
private final PlatformLogger log;
private final boolean isEnabled;
public synchronized static PerfLogger getLogger(PlatformLogger log) {
if (loggers == null) {
loggers = new HashMap<PlatformLogger, PerfLogger>();
}
PerfLogger l = loggers.get(log);
if (l == null) {
l = new PerfLogger(log);
loggers.put(log, l);
}
if (l.isEnabled() && shutdownHook == null) {
shutdownHook = new Thread() {
@Override
public void run() {
for (PerfLogger l: loggers.values()) {
if (!l.isEnabled()) continue;
l.log(false);
}
}
};
Runtime.getRuntime().addShutdownHook(shutdownHook);
}
return l;
}
public synchronized static PerfLogger getLogger(String name) {
return getLogger(PlatformLogger.getLogger("com.sun.webkit.perf." + name));
}
private PerfLogger(PlatformLogger log) {
this.log = log;
this.isEnabled = log.isLoggable(Level.FINE);
startCount("TOTALTIME");
}
public static final class ProbeStat {
private final String probe;
private int count;
private long totalTime;
private long startTime;
private boolean isRunning = false;
private ProbeStat(String probe) {
this.probe = probe;
}
public String getProbe() {
return probe;
}
public int getCount() {
return count;
}
public long getTotalTime() {
return totalTime;
}
private void reset() {
count = 0;
totalTime = startTime = 0;
}
private void suspend() {
if (isRunning) {
totalTime += System.currentTimeMillis() - startTime;
isRunning = false;
}
}
private void resume() {
isRunning = true;
count++;
startTime = System.currentTimeMillis();
}
private void snapshot() {
if (isRunning) {
totalTime += System.currentTimeMillis() - startTime;
startTime = System.currentTimeMillis();
}
}
@Override
public String toString() {
return super.toString() + "[count=" + count + ", time=" + totalTime + "]";
}
}
public boolean isEnabled() {
return isEnabled;
}
private synchronized String fullName(String probe) {
return log.getName() + "." + probe;
}
private final Comparator timeComparator = (arg0, arg1) -> {
long t0 = probes.get((String)arg0).totalTime;
long t1 = probes.get((String)arg1).totalTime;
if (t0 > t1) {
return 1;
} else if (t0 < t1) {
return -1;
}
return 0;
};
private final Comparator countComparator = (arg0, arg1) -> {
long c0 = probes.get((String)arg0).count;
long c1 = probes.get((String)arg1).count;
if (c0 > c1) {
return 1;
} else if (c0 < c1) {
return -1;
}
return 0;
};
public synchronized void reset() {
for (Map.Entry<String, ProbeStat> entry: probes.entrySet()) {
entry.getValue().reset();
}
startCount("TOTALTIME");
}
public synchronized static void resetAll() {
for (PerfLogger l: loggers.values()) {
l.reset();
}
}
private synchronized ProbeStat registerProbe(String probe) {
String p = probe.intern();
if (probes.containsKey(p)) {
log.fine("Warning: \"" + fullName(p) + "\" probe already exists");
} else {
log.fine("Registering \"" + fullName(p) + "\" probe");
}
ProbeStat stat = new ProbeStat(p);
probes.put(p, stat);
return stat;
}
public synchronized ProbeStat getProbeStat(String probe) {
String p = probe.intern();
ProbeStat s = probes.get(p);
if (s != null) {
s.snapshot();
}
return s;
}
public synchronized void startCount(String probe) {
if (!isEnabled()) {
return;
}
String p = probe.intern();
ProbeStat stat = probes.get(p);
if (stat == null) {
stat = registerProbe(p);
}
stat.reset();
stat.resume();
}
public synchronized void suspendCount(String probe) {
if (!isEnabled()) {
return;
}
String p = probe.intern();
ProbeStat stat = probes.get(p);
if (stat != null) {
stat.suspend();
} else {
log.fine("Warning: \"" + fullName(p) + "\" probe is not registered");
}
}
public synchronized void resumeCount(String probe) {
if (!isEnabled()) {
return;
}
String p = probe.intern();
ProbeStat stat = probes.get(p);
if (stat == null) {
stat = registerProbe(p);
}
stat.resume();
}
public synchronized void log(StringBuffer buf) {
if (!isEnabled()) {
return;
}
buf.append("=========== Performance Statistics =============\n");
ProbeStat total = getProbeStat("TOTALTIME");
ArrayList<String> list = new ArrayList<String>();
list.addAll(probes.keySet());
buf.append("\nTime:\n");
Collections.sort(list, timeComparator);
for (String p: list) {
ProbeStat s = getProbeStat(p);
buf.append(String.format("%s: %dms", fullName(p), s.totalTime));
if (total.totalTime > 0){
buf.append(String.format(", %.2f%%%n", (float)100*s.totalTime/total.totalTime));
} else {
buf.append("\n");
}
}
buf.append("\nInvocations count:\n");
Collections.sort(list, countComparator);
for (String p: list) {
buf.append(String.format("%s: %d%n", fullName(p), getProbeStat(p).count));
}
buf.append("================================================\n");
}
public synchronized void log() {
log(true);
}
private synchronized void log(boolean useLogger) {
StringBuffer buf = new StringBuffer();
log(buf);
if (useLogger) {
log.fine(buf.toString());
} else {
System.out.println(buf.toString());
System.out.flush();
}
}
public synchronized static void logAll() {
for (PerfLogger l: loggers.values()) {
l.log();
}
}
}
