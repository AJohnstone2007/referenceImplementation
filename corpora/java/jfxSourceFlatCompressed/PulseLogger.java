package com.sun.javafx.logging;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
public class PulseLogger {
public static final boolean PULSE_LOGGING_ENABLED;
private static final String [] DEFAULT_LOGGERS = {"com.sun.javafx.logging.PrintLogger", "com.sun.javafx.logging.jfr.JFRPulseLogger"};
private static final Logger[] loggers;
static {
List<Logger> list = new ArrayList<>();
for (String loggerClass : DEFAULT_LOGGERS) {
Logger logger = loadLogger(loggerClass);
if (logger != null) {
list.add(logger);
}
}
loggers = list.toArray(new Logger[list.size()]);
PULSE_LOGGING_ENABLED = loggers.length > 0;
}
public static void pulseStart() {
for (Logger logger: loggers) {
logger.pulseStart();
}
}
public static void pulseEnd() {
for (Logger logger: loggers) {
logger.pulseEnd();
}
}
public static void renderStart() {
for (Logger logger: loggers) {
logger.renderStart();
}
}
public static void renderEnd() {
for (Logger logger: loggers) {
logger.renderEnd();
}
}
public static void addMessage(String message) {
for (Logger logger: loggers) {
logger.addMessage(message);
}
}
public static void incrementCounter(String counter) {
for (Logger logger: loggers) {
logger.incrementCounter(counter);
}
}
public static void newPhase(String name) {
for (Logger logger: loggers) {
logger.newPhase(name);
}
}
public static void newInput(String name) {
for (Logger logger: loggers) {
logger.newInput(name);
}
}
@SuppressWarnings("removal")
public static boolean isPulseLoggingRequested() {
return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("javafx.pulseLogger"));
}
private static Logger loadLogger(String className) {
try {
Class<?> klass = Class.forName(className);
if (klass != null) {
Method method = klass.getDeclaredMethod("createInstance");
return (Logger) method.invoke(null);
}
} catch (NoClassDefFoundError | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
}
return null;
}
}
