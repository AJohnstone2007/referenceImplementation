package com.sun.marlin;
public final class MarlinUtils {
private static final com.sun.javafx.logging.PlatformLogger LOG;
static {
if (MarlinConst.USE_LOGGER) {
LOG = com.sun.javafx.logging.PlatformLogger.getLogger("prism.marlin");
} else {
LOG = null;
}
}
private MarlinUtils() {
}
public static void logInfo(final String msg) {
if (MarlinConst.USE_LOGGER) {
LOG.info(msg);
} else if (MarlinConst.ENABLE_LOGS) {
System.out.print("INFO: ");
System.out.println(msg);
}
}
public static void logException(final String msg, final Throwable th) {
if (MarlinConst.USE_LOGGER) {
LOG.warning(msg, th);
} else if (MarlinConst.ENABLE_LOGS) {
System.out.print("WARNING: ");
System.out.println(msg);
th.printStackTrace(System.err);
}
}
public static ThreadGroup getRootThreadGroup() {
ThreadGroup currentTG = Thread.currentThread().getThreadGroup();
ThreadGroup parentTG = currentTG.getParent();
while (parentTG != null) {
currentTG = parentTG;
parentTG = currentTG.getParent();
}
return currentTG;
}
private final static java.lang.ref.Cleaner cleaner
= java.lang.ref.Cleaner.create();
static java.lang.ref.Cleaner getCleaner() {
return cleaner;
}
}
