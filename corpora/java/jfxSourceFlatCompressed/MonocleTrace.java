package com.sun.glass.ui.monocle;
class MonocleTrace {
static void traceEvent(String format, Object... args) {
trace("traceEvent", format, args);
}
static void traceConfig(String format, Object... args) {
trace("traceConfig", format, args);
}
private static void trace(String prefix, String format, Object[] args) {
synchronized (System.out) {
System.out.print(prefix);
System.out.print(": ");
System.out.format(format, args);
System.out.println();
}
}
}
