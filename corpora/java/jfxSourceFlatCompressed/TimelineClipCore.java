package com.sun.scenario.animation.shared;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import com.sun.javafx.animation.TickCalculation;
public class TimelineClipCore {
private static final int UNDEFINED_KEYFRAME = -1;
private static final Comparator<KeyFrame> KEY_FRAME_COMPARATOR = (kf1, kf2) -> kf1.getTime().compareTo(kf2.getTime());
Timeline timeline;
private KeyFrame[] keyFrames = new KeyFrame[0];
private long[] keyFrameTicks = new long[0];
private boolean canSkipFrames = true;
private ClipInterpolator clipInterpolator;
public TimelineClipCore(Timeline timeline) {
this.timeline = timeline;
this.clipInterpolator = ClipInterpolator.create(keyFrames, keyFrameTicks);
}
public Duration setKeyFrames(Collection<KeyFrame> keyFrames) {
final int n = keyFrames.size();
final KeyFrame[] sortedKeyFrames = new KeyFrame[n];
keyFrames.toArray(sortedKeyFrames);
Arrays.sort(sortedKeyFrames, KEY_FRAME_COMPARATOR);
canSkipFrames = true;
this.keyFrames = sortedKeyFrames;
keyFrameTicks = new long[n];
for (int i = 0; i < n; ++i) {
keyFrameTicks[i] = TickCalculation.fromDuration(this.keyFrames[i].getTime());
if (canSkipFrames && this.keyFrames[i].getOnFinished() != null) {
canSkipFrames = false;
}
}
clipInterpolator = clipInterpolator.setKeyFrames(sortedKeyFrames, keyFrameTicks);
return (n == 0) ? Duration.ZERO
: sortedKeyFrames[n-1].getTime();
}
public void notifyCurrentRateChanged() {
if (timeline.getStatus() != Status.RUNNING) {
clearLastKeyFrame();
}
}
public void abort() {
aborted = true;
}
private boolean aborted = false;
private int lastKF = UNDEFINED_KEYFRAME;
private long curTicks = 0;
private void clearLastKeyFrame() {
lastKF = UNDEFINED_KEYFRAME;
}
public void jumpTo(long ticks, boolean forceJump) {
lastKF = UNDEFINED_KEYFRAME;
curTicks = ticks;
if (timeline.getStatus() != Status.STOPPED || forceJump) {
if (forceJump) {
clipInterpolator.validate(false);
}
clipInterpolator.interpolate(ticks);
}
}
public void start(boolean forceSync) {
clearLastKeyFrame();
clipInterpolator.validate(forceSync);
if (curTicks > 0) {
clipInterpolator.interpolate(curTicks);
}
}
public void playTo(long ticks) {
if (canSkipFrames) {
clearLastKeyFrame();
setTime(ticks);
clipInterpolator.interpolate(ticks);
return;
}
aborted = false;
final boolean forward = curTicks <= ticks;
if (forward) {
final int fromKF = (lastKF == UNDEFINED_KEYFRAME) ? 0
: (keyFrameTicks[lastKF] <= curTicks) ? lastKF + 1
: lastKF;
final int toKF = keyFrames.length;
for (int fi = fromKF; fi < toKF; fi++) {
final long kfTicks = keyFrameTicks[fi];
if (kfTicks > ticks) {
lastKF = fi - 1;
break;
}
if (kfTicks >= curTicks) {
visitKeyFrame(fi, kfTicks);
if (aborted) {
break;
}
}
}
} else {
final int fromKF = (lastKF == UNDEFINED_KEYFRAME) ? keyFrames.length - 1
: (keyFrameTicks[lastKF] >= curTicks) ? lastKF - 1
: lastKF;
for (int fi = fromKF; fi >= 0; fi--) {
final long kfTicks = keyFrameTicks[fi];
if (kfTicks < ticks) {
lastKF = fi + 1;
break;
}
if (kfTicks <= curTicks) {
visitKeyFrame(fi, kfTicks);
if (aborted) {
break;
}
}
}
}
if (!aborted
&& ((lastKF == UNDEFINED_KEYFRAME)
|| keyFrameTicks[lastKF] != ticks || (keyFrames[lastKF]
.getOnFinished() == null))) {
setTime(ticks);
clipInterpolator.interpolate(ticks);
}
}
private void setTime(long ticks) {
curTicks = ticks;
AnimationAccessor.getDefault().setCurrentTicks(timeline, ticks);
}
private void visitKeyFrame(int kfIndex, long kfTicks) {
if (kfIndex != lastKF) {
lastKF = kfIndex;
final KeyFrame kf = keyFrames[kfIndex];
final EventHandler<ActionEvent> onFinished = kf.getOnFinished();
if (onFinished != null) {
setTime(kfTicks);
clipInterpolator.interpolate(kfTicks);
try {
onFinished.handle(new ActionEvent(kf, null));
} catch (Throwable ex) {
Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
}
}
}
}
}
