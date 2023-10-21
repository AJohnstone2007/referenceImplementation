package com.sun.scenario.animation.shared;
import javafx.animation.KeyFrame;
public class SimpleClipInterpolatorShim extends SimpleClipInterpolator {
public SimpleClipInterpolatorShim(KeyFrame startKeyFrame, KeyFrame endKeyFrame, long ticks) {
super(startKeyFrame, endKeyFrame, ticks);
}
public SimpleClipInterpolatorShim(KeyFrame endKeyFrame, long ticks) {
super(endKeyFrame, ticks);
}
@Override
public ClipInterpolator setKeyFrames(
KeyFrame[] keyFrames, long[] keyFrameTicks) {
return super.setKeyFrames(keyFrames, keyFrameTicks);
}
@Override
public void validate(boolean forceSync) {
super.validate(forceSync);
}
@Override
public void interpolate(long ticks) {
super.interpolate(ticks);
}
}
