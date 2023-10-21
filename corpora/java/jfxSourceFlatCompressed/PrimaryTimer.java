package com.sun.javafx.tk.quantum;
import java.util.Map;
import javafx.animation.Timeline;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.Settings;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.AnimationPulse;
public final class PrimaryTimer extends AbstractPrimaryTimer {
private PrimaryTimer() {
}
private static final Object PRIMARY_TIMER_KEY = new StringBuilder(
"PrimaryTimerKey");
public static synchronized PrimaryTimer getInstance() {
Map<Object, Object> contextMap = Toolkit.getToolkit().getContextMap();
PrimaryTimer instance = (PrimaryTimer) contextMap.get(PRIMARY_TIMER_KEY);
if (instance == null) {
instance = new PrimaryTimer();
contextMap.put(PRIMARY_TIMER_KEY, instance);
if (Settings.getBoolean(ANIMATION_MBEAN_ENABLED,
enableAnimationMBean)) {
AnimationPulse.getDefaultBean().setEnabled(true);
}
}
return instance;
}
protected int getPulseDuration(int precision) {
int retVal = precision / 60;
if (Settings.get(FRAMERATE_PROP) != null) {
int overrideHz = Settings.getInt(FRAMERATE_PROP, 60);
if (overrideHz > 0) {
retVal = precision / overrideHz;
}
} else if (Settings.get(PULSE_PROP) != null) {
int overrideHz = Settings.getInt(PULSE_PROP, 60);
if (overrideHz > 0) {
retVal = precision / overrideHz;
}
} else {
int rate = Toolkit.getToolkit().getRefreshRate();
if (rate > 0) {
retVal = precision / rate;
}
}
return retVal;
}
protected void postUpdateAnimationRunnable(DelayedRunnable animationRunnable) {
Toolkit.getToolkit().setAnimationRunnable(animationRunnable);
}
@Override
protected void recordStart(long shiftMillis) {
AnimationPulse.getDefaultBean().recordStart(shiftMillis);
}
@Override
protected void recordEnd() {
AnimationPulse.getDefaultBean().recordEnd();
}
@Override
protected void recordAnimationEnd() {
AnimationPulse.getDefaultBean().recordAnimationEnd();
}
}
