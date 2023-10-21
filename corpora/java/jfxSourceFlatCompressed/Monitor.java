package com.sun.marlin.stats;
public final class Monitor extends StatLong {
private static final long INVALID = -1L;
private long start = INVALID;
public Monitor(final String name) {
super(name);
}
public void start() {
start = System.nanoTime();
}
public void stop() {
final long elapsed = System.nanoTime() - start;
if (start != INVALID && elapsed > 0l) {
add(elapsed);
}
start = INVALID;
}
}
