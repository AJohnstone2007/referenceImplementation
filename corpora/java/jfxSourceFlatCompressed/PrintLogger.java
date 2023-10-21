package com.sun.javafx.logging;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
class PrintLogger extends Logger {
@SuppressWarnings("removal")
private static long THRESHOLD = (long)
AccessController.doPrivileged((PrivilegedAction<Integer>) () -> Integer.getInteger("javafx.pulseLogger.threshold", 17));
@SuppressWarnings("removal")
private static final int EXIT_ON_PULSE =
AccessController.doPrivileged((PrivilegedAction<Integer>) () -> Integer.getInteger("javafx.pulseLogger.exitOnPulse", 0));
private int pulseCount = 1;
private static final int INTER_PULSE_DATA = -1;
private volatile int wrapCount = 0;
private volatile PulseData fxData, renderData;
private long lastPulseStartTime;
class ThreadLocalData {
String phaseName;
long phaseStart;
}
private Thread fxThread;
private final ThreadLocal<ThreadLocalData> phaseData =
new ThreadLocal<>() {
@Override
public ThreadLocalData initialValue() {
return new ThreadLocalData();
}
};
private PulseData head;
private PulseData tail;
private AtomicInteger active;
private static final int AVAILABLE = 0;
private static final int INCOMPLETE = 1;
private static final int COMPLETE = 2;
private PrintLogger() {
head = new PulseData();
tail = new PulseData();
head.next = tail;
active = new AtomicInteger(0);
}
public static Logger createInstance() {
boolean enabled = PulseLogger.isPulseLoggingRequested();
if (enabled) {
return new PrintLogger();
}
return null;
}
private PulseData allocate(int n) {
PulseData res;
if (head != tail && head.state == AVAILABLE) {
res = head;
head = head.next;
res.next = null;
}
else {
res = new PulseData();
}
tail.next = res;
tail = res;
res.init(n);
return res;
}
@Override
public void pulseStart() {
if (fxThread == null) {
fxThread = Thread.currentThread();
}
if (fxData != null) {
fxData.state = COMPLETE;
if (active.incrementAndGet() == 1) {
fxData.printAndReset();
active.decrementAndGet();
}
}
fxData = allocate(pulseCount++);
if (lastPulseStartTime > 0) {
fxData.interval = (fxData.startTime - lastPulseStartTime)/1000000L;
}
lastPulseStartTime = fxData.startTime;
}
@Override
public void renderStart() {
newPhase(null);
fxData.pushedRender = true;
renderData = fxData;
active.incrementAndGet();
}
@Override
public void pulseEnd() {
if (fxData != null && !fxData.pushedRender) {
fxData.state = COMPLETE;
if (active.incrementAndGet() == 1) {
fxData.printAndReset();
active.decrementAndGet();
}
}
fxData = null;
}
@Override
public void renderEnd() {
newPhase(null);
renderData.state = COMPLETE;
for (;;) {
renderData.printAndReset();
if (active.decrementAndGet() == 0) {
break;
}
renderData = renderData.next;
}
renderData = null;
}
@Override
public void addMessage(String message) {
PulseData pulseData;
if (fxThread == null || Thread.currentThread() == fxThread) {
if (fxData == null) {
fxData = allocate(INTER_PULSE_DATA);
}
pulseData = fxData;
}
else {
pulseData = renderData;
}
if (pulseData == null) {
return;
}
pulseData.message
.append("T")
.append(Thread.currentThread().getId())
.append(" : ")
.append(message)
.append("\n");
}
@Override
public void incrementCounter(String counter) {
PulseData pulseData;
if (fxThread == null || Thread.currentThread() == fxThread) {
if (fxData == null) {
fxData = allocate(INTER_PULSE_DATA);
}
pulseData = fxData;
}
else {
pulseData = renderData;
}
if (pulseData == null) {
return;
}
Map<String,Counter> counters = pulseData.counters;
Counter cval = counters.get(counter);
if (cval == null) {
cval = new Counter();
counters.put(counter, cval);
}
cval.value += 1;
}
@Override
public void newPhase(String name) {
long curTime = System.nanoTime();
ThreadLocalData curPhase = phaseData.get();
if (curPhase.phaseName != null) {
PulseData pulseData = Thread.currentThread() == fxThread ? fxData : renderData;
if (pulseData != null) {
pulseData.message
.append("T")
.append(Thread.currentThread().getId())
.append(" (").append((curPhase.phaseStart-pulseData.startTime)/1000000L)
.append(" +").append((curTime - curPhase.phaseStart)/1000000L).append("ms): ")
.append(curPhase.phaseName)
.append("\n");
}
}
curPhase.phaseName = name;
curPhase.phaseStart = curTime;
}
private static class Counter {
int value;
}
private final class PulseData {
PulseData next;
volatile int state = AVAILABLE;
long startTime;
long interval;
int pulseCount;
boolean pushedRender;
StringBuffer message = new StringBuffer();
Map<String,Counter> counters = new ConcurrentHashMap<>();
void init(int n) {
state = INCOMPLETE;
pulseCount = n;
startTime = System.nanoTime();
interval = 0;
pushedRender = false;
}
void printAndReset() {
long endTime = System.nanoTime();
long totalTime = (endTime - startTime)/1000000L;
if (state != COMPLETE) {
System.err.println("\nWARNING: logging incomplete state");
}
if (totalTime <= THRESHOLD) {
if (pulseCount != INTER_PULSE_DATA) {
System.err.print((wrapCount++ % 10 == 0 ? "\n[" : "[") + pulseCount+ " " + interval + "ms:" + totalTime + "ms]");
}
}
else {
if (pulseCount == INTER_PULSE_DATA) {
System.err.println("\n\nINTER PULSE LOG DATA");
}
else {
System.err.print("\n\nPULSE: " + pulseCount +
" [" + interval + "ms:" + totalTime + "ms]");
if (!pushedRender) {
System.err.print(" Required No Rendering");
}
System.err.println();
}
System.err.print(message);
if (!counters.isEmpty()) {
System.err.println("Counters:");
List<Map.Entry<String,Counter>> entries = new ArrayList<>(counters.entrySet());
Collections.sort(entries, (a, b) -> a.getKey().compareTo(b.getKey()));
for (Map.Entry<String, Counter> entry : entries) {
System.err.println("\t" + entry.getKey() + ": " + entry.getValue().value);
}
}
wrapCount = 0;
}
message.setLength(0);
counters.clear();
state = AVAILABLE;
if (EXIT_ON_PULSE > 0 && pulseCount >= EXIT_ON_PULSE) {
System.err.println("Exiting after pulse #" + pulseCount);
System.exit(0);
}
}
}
}
