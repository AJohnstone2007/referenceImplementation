package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
class MonocleSettings {
@SuppressWarnings("removal")
static final MonocleSettings settings = AccessController.doPrivileged(
(PrivilegedAction<MonocleSettings>) () -> new MonocleSettings());
final boolean traceEvents;
final boolean traceEventsVerbose;
final boolean tracePlatformConfig;
private MonocleSettings() {
traceEventsVerbose = Boolean.getBoolean("monocle.input.traceEvents.verbose");
traceEvents = traceEventsVerbose || Boolean.getBoolean("monocle.input.traceEvents");
tracePlatformConfig = Boolean.getBoolean("monocle.platform.traceConfig");
}
}
