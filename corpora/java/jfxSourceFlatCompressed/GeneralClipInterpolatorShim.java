package com.sun.scenario.animation.shared;
import javafx.animation.KeyFrame;
public class GeneralClipInterpolatorShim extends GeneralClipInterpolator {
public GeneralClipInterpolatorShim(KeyFrame[] keyFrames, long[] keyFrameTicks) {
super(keyFrames, keyFrameTicks);
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
