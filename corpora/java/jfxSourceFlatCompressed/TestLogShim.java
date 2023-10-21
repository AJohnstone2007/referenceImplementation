package com.sun.glass.ui.monocle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
public class TestLogShim {
private static final boolean verbose = Boolean.getBoolean("verbose");
private static final double timeScale = Double.parseDouble(
System.getProperty("timeScale", "1"));
private static final long DEFAULT_TIMEOUT = 3000l;
private static final List<String> log = new ArrayList<>();
private static final Object lock = new Object();
private static long startTime = System.currentTimeMillis();
public static class TestLogAssertion extends Exception {
public TestLogAssertion(String message) {
super(message);
}
}
public static void log(String s) {
synchronized (lock) {
if (verbose) {
System.out.println(timestamp() + " TestLog: " + s);
}
log.add(s);
lock.notifyAll();
}
}
public static void format(String format, Object... args) {
log(new Formatter().format(format, args).toString());
}
public static List<String> getLog() {
return new ArrayList<>(log);
}
public static void clear() {
synchronized (lock) {
log.clear();
}
}
public static void reset() {
synchronized (lock) {
log.clear();
startTime = System.currentTimeMillis();
}
}
public static String timestamp() {
long time = System.currentTimeMillis() - startTime;
StringBuffer sb = new StringBuffer().append(time);
while (sb.length() < 4) {
sb.insert(0, "0");
}
while (sb.length() < 8) {
sb.insert(0, " ");
}
sb.insert(sb.length() - 3, ".");
return sb.toString();
}
public static Object getLock() {
return lock;
}
public static int countLog(String s, int startIndex, boolean exact) {
int count = 0;
for (int i = startIndex; i < log.size(); i++) {
String line = log.get(i);
if (exact) {
if (line.equals(s)) {
count ++;
}
} else {
if (line.indexOf(s) >= 0) {
count ++;
}
}
}
return count;
}
public static int countLog(String s) {
return countLog(s, 0, true);
}
public static int countLogContaining(String s) {
return countLog(s, 0, false);
}
private static String checkLog(String[] matches, int startIndex, boolean exact) {
for (int i = startIndex; i < log.size(); i++) {
String line = log.get(i);
if (matches.length == 1) {
if (exact) {
if (line.equals(matches[0])) {
return line;
}
} else {
if (line.indexOf(matches[0]) >= 0) {
return line;
}
}
} else {
boolean isMatch = true;
for (String match : matches) {
if (line.indexOf(match) < 0) {
isMatch = false;
break;
}
}
if (isMatch) {
return line;
}
}
}
return null;
}
public static boolean checkLog(String s) {
return checkLog(new String[] {s}, 0, true) != null;
}
public static boolean checkLogContaining(String s) {
return checkLog(new String[] {s}, 0, false) != null;
}
public static void assertLog(String s) throws TestLogAssertion {
synchronized (lock) {
if (!checkLog(s)) {
String err = "No line '" + s + "' in log";
if (verbose) {
System.out.println(err);
}
throw new TestLogAssertion(err);
}
}
}
public static void assertLogContaining(String s) throws TestLogAssertion {
synchronized (lock) {
if (!checkLogContaining(s)) {
String err = "No line containing '" + s + "' in log";
if (verbose) {
System.out.println(err);
}
throw new TestLogAssertion(err);
}
}
}
private static String waitForLog(String[] s, long timeout, boolean exact) throws InterruptedException, TestLogAssertion {
long startTime = System.currentTimeMillis();
long timeNow = startTime;
long endTime = timeNow + (long) (timeout * timeScale);
String line;
String logString = Arrays.toString(s).substring(1, Arrays.toString(s).length() - 1);
synchronized (lock) {
int index = 0;
while ((line = checkLog(s, index, exact)) == null) {
index = log.size();
if (endTime - timeNow > 0) {
lock.wait(endTime - timeNow);
}
timeNow = System.currentTimeMillis();
if (timeNow >= endTime) {
String message = "Timed out after " + (timeNow - startTime)
+ "ms waiting for '" + logString + "'";
if (!verbose) {
System.out.flush();
System.err.flush();
for (String logLine: log) {
System.out.println(logLine);
}
}
System.out.println(message);
throw new TestLogAssertion(message);
}
}
}
long matchTime = System.currentTimeMillis() - startTime;
if (verbose) {
if (exact) {
System.out.println("TestLog matched '"
+ logString + "' in "
+ matchTime + "ms");
} else {
System.out.println("TestLog matched '"
+ logString + "' with '"
+ line + "' in "
+ matchTime + "ms");
}
}
return line;
}
public static String waitForLog(String s, long timeout) throws InterruptedException, TestLogAssertion {
return waitForLog(new String [] {s}, timeout, true);
}
public static String waitForLogContaining(String s, long timeout) throws InterruptedException, TestLogAssertion {
return waitForLog(new String [] {s}, timeout, false);
}
public static String waitForLog(String format, Object... args) throws InterruptedException, TestLogAssertion {
return waitForLog(new Formatter().format(format, args).toString(),
DEFAULT_TIMEOUT);
}
public static String waitForLogContaining(String format, Object... args) throws InterruptedException, TestLogAssertion {
return waitForLogContaining(new Formatter().format(format, args).toString(),
DEFAULT_TIMEOUT);
}
public static String waitForLogContainingSubstrings(String... s) throws InterruptedException, TestLogAssertion {
return waitForLog(s, DEFAULT_TIMEOUT, false);
}
}
