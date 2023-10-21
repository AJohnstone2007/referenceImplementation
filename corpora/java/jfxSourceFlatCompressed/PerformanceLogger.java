package com.sun.javafx.tk.quantum;
import java.util.Vector;
import java.io.FileWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
public class PerformanceLogger {
private static final int START_INDEX = 0;
private static final int LAST_RESERVED = START_INDEX;
private static boolean perfLoggingOn = false;
private static boolean useNanoTime = false;
private static Vector<TimeData> times;
private static String logFileName = null;
private static Writer logWriter = null;
private static long baseTime;
static {
@SuppressWarnings("removal")
String perfLoggingProp =
java.security.AccessController.doPrivileged(
new java.security.PrivilegedAction<String>() {
public String run() {
return System.getProperty("sun.perflog");
}
});
if (perfLoggingProp != null) {
perfLoggingOn = true;
@SuppressWarnings("removal")
String perfNanoProp =
java.security.AccessController.doPrivileged(
new java.security.PrivilegedAction<String>() {
public String run() {
return System.getProperty("sun.perflog.nano");
}
});
if (perfNanoProp != null) {
useNanoTime = true;
}
if (perfLoggingProp.regionMatches(true, 0, "file:", 0, 5)) {
logFileName = perfLoggingProp.substring(5);
}
if (logFileName != null) {
if (logWriter == null) {
@SuppressWarnings("removal")
var dummy = java.security.AccessController.doPrivileged(
new java.security.PrivilegedAction<Void>() {
public Void run() {
try {
File logFile = new File(logFileName);
logFile.createNewFile();
logWriter = new FileWriter(logFile);
} catch (Exception e) {
System.out.println(e + ": Creating logfile " +
logFileName +
".  Log to console");
}
return null;
}
});
}
}
if (logWriter == null) {
logWriter = new OutputStreamWriter(System.out);
}
}
times = new Vector<TimeData>(10);
for (int i = 0; i <= LAST_RESERVED; ++i) {
times.add(new TimeData("Time " + i + " not set", 0));
}
}
public static boolean loggingEnabled() {
return perfLoggingOn;
}
static class TimeData {
String message;
long time;
TimeData(String message, long time) {
this.message = message;
this.time = time;
}
String getMessage() {
return message;
}
long getTime() {
return time;
}
}
private static long getCurrentTime() {
if (useNanoTime) {
return System.nanoTime();
} else {
return System.currentTimeMillis();
}
}
public static void setStartTime(String message) {
if (loggingEnabled()) {
long nowTime = getCurrentTime();
setStartTime(message, nowTime);
}
}
public static void setBaseTime(long time) {
if (loggingEnabled()) {
baseTime = time;
}
}
public static void setStartTime(String message, long time) {
if (loggingEnabled()) {
times.set(START_INDEX, new TimeData(message, time));
}
}
public static long getStartTime() {
if (loggingEnabled()) {
return times.get(START_INDEX).getTime();
} else {
return 0;
}
}
public static int setTime(String message) {
if (loggingEnabled()) {
long nowTime = getCurrentTime();
return setTime(message, nowTime);
} else {
return 0;
}
}
public static int setTime(String message, long time) {
if (loggingEnabled()) {
synchronized (times) {
times.add(new TimeData(message, time));
return (times.size() - 1);
}
} else {
return 0;
}
}
public static long getTimeAtIndex(int index) {
if (loggingEnabled()) {
return times.get(index).getTime();
} else {
return 0;
}
}
public static String getMessageAtIndex(int index) {
if (loggingEnabled()) {
return times.get(index).getMessage();
} else {
return null;
}
}
public static void outputLog(Writer writer) {
if (loggingEnabled()) {
try {
synchronized(times) {
for (int i = 0; i < times.size(); ++i) {
TimeData td = times.get(i);
if (td != null) {
writer.write(i + " " + td.getMessage() + ": " +
(td.getTime() - baseTime) + "\n");
}
}
}
writer.flush();
} catch (Exception e) {
System.out.println(e + ": Writing performance log to " +
writer);
}
}
}
public static void outputLog() {
outputLog(logWriter);
}
}
