package com.sun.media.jfxmedia.logging;
import java.lang.annotation.Native;
public class Logger {
@Native public static final int OFF = Integer.MAX_VALUE;
@Native public static final int ERROR = 4;
@Native public static final int WARNING = 3;
@Native public static final int INFO = 2;
@Native public static final int DEBUG = 1;
private static int currentLevel = OFF;
private static long startTime = 0;
private static final Object lock = new Object();
static {
startLogger();
}
private static void startLogger() {
try {
Integer logLevel;
String level = System.getProperty("jfxmedia.loglevel", "off").toLowerCase();
if (level.equals("debug")) {
logLevel = Integer.valueOf(Logger.DEBUG);
} else if (level.equals("warning")) {
logLevel = Integer.valueOf(Logger.WARNING);
} else if (level.equals("error")) {
logLevel = Integer.valueOf(Logger.ERROR);
} else if (level.equals("info")) {
logLevel = Integer.valueOf(Logger.INFO);
} else {
logLevel = Integer.valueOf(Logger.OFF);
}
setLevel(logLevel.intValue());
startTime = System.currentTimeMillis();
} catch (Exception e) {}
if (Logger.canLog(Logger.DEBUG))
Logger.logMsg(Logger.DEBUG, "Logger initialized");
}
private Logger() {
}
public static boolean initNative() {
if (nativeInit()) {
nativeSetNativeLevel(currentLevel);
return true;
} else {
return false;
}
}
private static native boolean nativeInit();
public static void setLevel(int level) {
currentLevel = level;
try {
nativeSetNativeLevel(level);
} catch(UnsatisfiedLinkError e) {}
}
private static native void nativeSetNativeLevel(int level);
public static boolean canLog(int level) {
if (level < currentLevel) {
return false;
} else {
return true;
}
}
public static void logMsg(int level, String msg) {
synchronized (lock) {
if (level < currentLevel) {
return;
}
if (level == ERROR) {
System.err.println("Error (" + getTimestamp() + "): " + msg);
} else if (level == WARNING) {
System.err.println("Warning (" + getTimestamp() + "): " + msg);
} else if (level == INFO) {
System.out.println("Info (" + getTimestamp() + "): " + msg);
} else if (level == DEBUG) {
System.out.println("Debug (" + getTimestamp() + "): " + msg);
}
}
}
public static void logMsg(int level, String sourceClass, String sourceMethod, String msg) {
synchronized (lock) {
if (level < currentLevel) {
return;
}
logMsg(level, sourceClass + ":" + sourceMethod + "() " + msg);
}
}
private static String getTimestamp() {
long elapsed = System.currentTimeMillis() - startTime;
long elapsedHours = elapsed / (60 * 60 * 1000);
long elapsedMinutes = (elapsed - elapsedHours * 60 * 60 * 1000) / (60 * 1000);
long elapsedSeconds = (elapsed - elapsedHours * 60 * 60 * 1000 - elapsedMinutes * 60 * 1000) / 1000;
long elapsedMillis = elapsed - elapsedHours * 60 * 60 * 1000 - elapsedMinutes * 60 * 1000 - elapsedSeconds * 1000;
return String.format("%d:%02d:%02d:%03d", elapsedHours, elapsedMinutes, elapsedSeconds, elapsedMillis);
}
}
