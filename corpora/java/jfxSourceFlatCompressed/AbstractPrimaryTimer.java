package com.sun.scenario.animation;
import java.util.Arrays;
import javafx.util.Callback;
import com.sun.javafx.animation.TickCalculation;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.Settings;
import com.sun.scenario.animation.shared.PulseReceiver;
import com.sun.scenario.animation.shared.TimerReceiver;
public abstract class AbstractPrimaryTimer {
protected final static String FULLSPEED_PROP = "javafx.animation.fullspeed";
private static boolean fullspeed = Settings.getBoolean(FULLSPEED_PROP);
protected final static String ADAPTIVE_PULSE_PROP = "com.sun.scenario.animation.adaptivepulse";
private static boolean useAdaptivePulse = Settings.getBoolean(ADAPTIVE_PULSE_PROP);
protected final static String PULSE_PROP = "javafx.animation.pulse";
protected final static String FRAMERATE_PROP = "javafx.animation.framerate";
protected final static String FIXED_PULSE_LENGTH_PROP = "com.sun.scenario.animation.fixed.pulse.length";
protected final static String ANIMATION_MBEAN_ENABLED = "com.sun.scenario.animation.AnimationMBean.enabled";
protected final static boolean enableAnimationMBean = false;
private final int PULSE_DURATION_NS = getPulseDuration(1000000000);
private final int PULSE_DURATION_TICKS = getPulseDuration((int)TickCalculation.fromMillis(1000));
private static Callback<String, Void> pcl = key -> {
switch (key) {
case FULLSPEED_PROP:
fullspeed = Settings.getBoolean(FULLSPEED_PROP);
break;
case ADAPTIVE_PULSE_PROP:
useAdaptivePulse = Settings.getBoolean(ADAPTIVE_PULSE_PROP);
break;
case ANIMATION_MBEAN_ENABLED:
AnimationPulse.getDefaultBean()
.setEnabled(Settings.getBoolean(ANIMATION_MBEAN_ENABLED));
break;
}
return null;
};
private boolean paused = false;
private long totalPausedTime;
private long startPauseTime;
boolean isPaused() { return paused; }
long getTotalPausedTime() { return totalPausedTime; }
long getStartPauseTime() { return startPauseTime; }
private PulseReceiver receivers[] = new PulseReceiver[2];
private int receiversLength;
private boolean receiversLocked;
private TimerReceiver animationTimers[] = new TimerReceiver[2];
private int animationTimersLength;
private boolean animationTimersLocked;
private final long fixedPulseLength = Boolean.getBoolean(FIXED_PULSE_LENGTH_PROP) ? PULSE_DURATION_NS : 0;
private long debugNanos = 0;
private final MainLoop theMainLoop = new MainLoop();
static {
Settings.addPropertyChangeListener(pcl);
int pulse = Settings.getInt(PULSE_PROP, -1);
if (pulse != -1) {
System.err.println("Setting PULSE_DURATION to " + pulse + " hz");
}
}
public int getDefaultResolution() {
return PULSE_DURATION_TICKS;
}
public void pause() {
if (!paused) {
startPauseTime = nanos();
paused = true;
}
}
public void resume() {
if (paused) {
paused = false;
totalPausedTime += nanos() - startPauseTime;
}
}
public long nanos() {
if (fixedPulseLength > 0) {
return debugNanos;
}
return paused ? startPauseTime :
System.nanoTime() - totalPausedTime;
}
public boolean isFullspeed() {
return fullspeed;
}
protected AbstractPrimaryTimer() {
}
public void addPulseReceiver(PulseReceiver target) {
boolean needMoreSize = receiversLength == receivers.length;
if (receiversLocked || needMoreSize) {
receivers = Arrays.copyOf(receivers, needMoreSize ? receivers.length * 3 / 2 + 1 : receivers.length);
receiversLocked = false;
}
receivers[receiversLength++] = target;
if (receiversLength == 1) {
theMainLoop.updateAnimationRunnable();
}
}
public void removePulseReceiver(PulseReceiver target) {
if (receiversLocked) {
receivers = receivers.clone();
receiversLocked = false;
}
for (int i = 0; i < receiversLength; ++i) {
if (target == receivers[i]) {
if (i == receiversLength - 1) {
receivers[i] = null;
} else {
System.arraycopy(receivers, i + 1, receivers, i, receiversLength - i - 1);
receivers[receiversLength - 1] = null;
}
--receiversLength;
break;
}
}
if (receiversLength == 0) {
theMainLoop.updateAnimationRunnable();
}
}
public void addAnimationTimer(TimerReceiver timer) {
boolean needMoreSize = animationTimersLength == animationTimers.length;
if (animationTimersLocked || needMoreSize) {
animationTimers = Arrays.copyOf(animationTimers, needMoreSize ? animationTimers.length * 3 / 2 + 1 : animationTimers.length);
animationTimersLocked = false;
}
animationTimers[animationTimersLength++] = timer;
if (animationTimersLength == 1) {
theMainLoop.updateAnimationRunnable();
}
}
public void removeAnimationTimer(TimerReceiver timer) {
if (animationTimersLocked) {
animationTimers = animationTimers.clone();
animationTimersLocked = false;
}
for (int i = 0; i < animationTimersLength; ++i) {
if (timer == animationTimers[i]) {
if (i == animationTimersLength - 1) {
animationTimers[i] = null;
} else {
System.arraycopy(animationTimers, i + 1, animationTimers, i, animationTimersLength - i - 1);
animationTimers[animationTimersLength - 1] = null;
}
--animationTimersLength;
break;
}
}
if (animationTimersLength == 0) {
theMainLoop.updateAnimationRunnable();
}
}
protected void recordStart(long shiftMillis) {
}
protected void recordEnd() {
}
protected void recordAnimationEnd() {
}
private final class MainLoop implements DelayedRunnable {
private boolean inactive = true;
private long nextPulseTime = nanos();
private long lastPulseDuration = Integer.MIN_VALUE;
@Override
public void run() {
if (paused) {
return;
}
final long now = nanos();
recordStart((nextPulseTime - now) / 1000000);
timePulseImpl(now);
recordEnd();
updateNextPulseTime(now);
updateAnimationRunnable();
}
@Override
public long getDelay() {
final long now = nanos();
final long timeUntilPulse = (nextPulseTime - now) / 1000000;
return Math.max(0, timeUntilPulse);
}
private void updateNextPulseTime(long pulseStarted) {
final long now = nanos();
if (fullspeed) {
nextPulseTime = now;
} else {
if (useAdaptivePulse) {
nextPulseTime += PULSE_DURATION_NS;
long pulseDuration = now - pulseStarted;
if (pulseDuration - lastPulseDuration > 500000) {
pulseDuration /= 2;
}
if (pulseDuration < 2000000) {
pulseDuration = 2000000;
}
if (pulseDuration >= PULSE_DURATION_NS) {
pulseDuration = 3 * PULSE_DURATION_NS / 4;
}
lastPulseDuration = pulseDuration;
nextPulseTime = nextPulseTime - pulseDuration;
} else {
nextPulseTime = ((nextPulseTime + PULSE_DURATION_NS) / PULSE_DURATION_NS)
* PULSE_DURATION_NS;
}
}
}
private void updateAnimationRunnable() {
final boolean newInactive = (animationTimersLength == 0 && receiversLength == 0);
if (inactive != newInactive) {
inactive = newInactive;
final DelayedRunnable animationRunnable = inactive? null : this;
postUpdateAnimationRunnable(animationRunnable);
}
}
}
protected abstract void postUpdateAnimationRunnable(
DelayedRunnable animationRunnable);
protected abstract int getPulseDuration(int precision);
protected void timePulseImpl(long now) {
if (fixedPulseLength > 0) {
debugNanos += fixedPulseLength;
now = debugNanos;
}
final PulseReceiver receiversSnapshot[] = receivers;
final int rLength = receiversLength;
try {
receiversLocked = true;
for (int i = 0; i < rLength; i++) {
receiversSnapshot[i].timePulse(TickCalculation.fromNano(now));
}
} finally {
receiversLocked = false;
}
recordAnimationEnd();
final TimerReceiver animationTimersSnapshot[] = animationTimers;
final int aTLength = animationTimersLength;
try {
animationTimersLocked = true;
for (int i = 0; i < aTLength; i++) {
animationTimersSnapshot[i].handle(now);
}
} finally {
animationTimersLocked = false;
}
}
}
