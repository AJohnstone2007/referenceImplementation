package com.sun.javafx.perf;
import javafx.scene.Scene;
import com.sun.javafx.tk.Toolkit;
public abstract class PerformanceTracker {
private static SceneAccessor sceneAccessor;
public static boolean isLoggingEnabled() {
return Toolkit.getToolkit().getPerformanceTracker().perfLoggingEnabled;
}
public static abstract class SceneAccessor {
public abstract void setPerfTracker(Scene scene, PerformanceTracker tracker);
public abstract PerformanceTracker getPerfTracker(Scene scene);
}
public static PerformanceTracker getSceneTracker(Scene scene) {
PerformanceTracker tracker = null;
if (sceneAccessor != null) {
tracker = sceneAccessor.getPerfTracker(scene);
if (tracker == null) {
tracker = Toolkit.getToolkit().createPerformanceTracker();
}
sceneAccessor.setPerfTracker(scene, tracker);
}
return tracker;
}
public static void releaseSceneTracker(Scene scene) {
if (sceneAccessor != null) {
sceneAccessor.setPerfTracker(scene, null);
}
}
public static void setSceneAccessor(SceneAccessor accessor) {
sceneAccessor = accessor;
}
public static void logEvent(String desc) {
Toolkit.getToolkit().getPerformanceTracker().doLogEvent(desc);
}
public static void outputLog() {
Toolkit.getToolkit().getPerformanceTracker().doOutputLog();
}
private boolean perfLoggingEnabled;
protected boolean isPerfLoggingEnabled() { return perfLoggingEnabled; }
protected void setPerfLoggingEnabled(boolean value) { perfLoggingEnabled = value; }
private boolean firstPulse = true;
private float instantFPS;
private int instantFPSFrames;
private long instantFPSStartTime;
private long avgStartTime;
private int avgFramesTotal;
private float instantPulses;
private int instantPulsesFrames;
private long instantPulsesStartTime;
private long avgPulsesStartTime;
private int avgPulsesTotal;
protected abstract long nanoTime();
public abstract void doOutputLog();
public abstract void doLogEvent(String s);
public synchronized float getInstantFPS() { return instantFPS; }
public synchronized float getAverageFPS() {
long nsseconds = nanoTime() - avgStartTime;
if (nsseconds > 0) {
return ((avgFramesTotal * 1000000000f) / nsseconds);
}
return getInstantFPS();
}
public synchronized void resetAverageFPS() {
avgStartTime = nanoTime();
avgFramesTotal = 0;
}
public float getInstantPulses() { return instantPulses; }
public float getAveragePulses() {
long nsseconds = nanoTime() - avgPulsesStartTime;
if (nsseconds > 0) {
return ((avgPulsesTotal * 1000000000f) / nsseconds);
}
return getInstantPulses();
}
public void resetAveragePulses() {
avgPulsesStartTime = nanoTime();
avgPulsesTotal = 0;
}
public void pulse() {
calcPulses();
updateInstantFps();
if (firstPulse) {
doLogEvent("first repaint");
firstPulse = false;
resetAverageFPS();
resetAveragePulses();
if (onFirstPulse != null) {
onFirstPulse.run();
}
}
if (onPulse != null) onPulse.run();
}
public void frameRendered() {
calcFPS();
if (onRenderedFrameTask != null) {
onRenderedFrameTask.run();
}
}
private void calcPulses() {
avgPulsesTotal++;
instantPulsesFrames++;
updateInstantPulses();
}
private synchronized void calcFPS() {
avgFramesTotal++;
instantFPSFrames++;
updateInstantFps();
}
private synchronized void updateInstantFps() {
long timeSince = nanoTime() - instantFPSStartTime;
if (timeSince > 1000000000) {
instantFPS = ((1000000000f * instantFPSFrames) / timeSince);
instantFPSFrames = 0;
instantFPSStartTime = nanoTime();
}
}
private void updateInstantPulses() {
long timeSince = nanoTime() - instantPulsesStartTime;
if (timeSince > 1000000000) {
instantPulses = ((1000000000f * instantPulsesFrames) / timeSince);
instantPulsesFrames = 0;
instantPulsesStartTime = nanoTime();
}
}
private Runnable onPulse;
public void setOnPulse(Runnable value) { onPulse = value; }
public Runnable getOnPulse() { return onPulse; }
private Runnable onFirstPulse;
public void setOnFirstPulse(Runnable value) { onFirstPulse = value; }
public Runnable getOnFirstPulse() { return onFirstPulse; }
private Runnable onRenderedFrameTask;
public void setOnRenderedFrameTask(Runnable value) { onRenderedFrameTask = value; }
public Runnable getOnRenderedFrameTask() { return onRenderedFrameTask; }
}
