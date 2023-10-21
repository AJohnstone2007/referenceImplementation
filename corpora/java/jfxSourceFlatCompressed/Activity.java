package com.sun.glass.ui.android;
public class Activity {
public static void shutdown() {
_shutdown();
};
private static native void _shutdown();
}
