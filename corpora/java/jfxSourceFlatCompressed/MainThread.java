package com.sun.webkit;
final class MainThread {
private static void fwkScheduleDispatchFunctions() {
Invoker.getInvoker().postOnEventThread(() -> {
twkScheduleDispatchFunctions();
});
}
private static native void twkScheduleDispatchFunctions();
}
