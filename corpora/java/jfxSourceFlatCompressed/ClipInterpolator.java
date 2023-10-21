package com.sun.scenario.animation.shared;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
public abstract class ClipInterpolator {
static ClipInterpolator create(KeyFrame[] keyFrames, long[] keyFrameTicks) {
return (ClipInterpolator.getRealKeyFrameCount(keyFrames) == 2) ? (keyFrames.length == 1) ? new SimpleClipInterpolator(
keyFrames[0], keyFrameTicks[0]) : new SimpleClipInterpolator(keyFrames[0],
keyFrames[1], keyFrameTicks[1])
: new GeneralClipInterpolator(keyFrames, keyFrameTicks);
}
static int getRealKeyFrameCount(KeyFrame[] keyFrames) {
final int length = keyFrames.length;
return (length == 0) ? 0 : (keyFrames[0].getTime()
.greaterThan(Duration.ZERO)) ? length + 1 : length;
}
abstract ClipInterpolator setKeyFrames(KeyFrame[] keyFrames, long[] keyFrameTicks);
abstract void interpolate(long ticks);
abstract void validate(boolean forceSync);
}
