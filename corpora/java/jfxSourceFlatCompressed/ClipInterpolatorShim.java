package com.sun.scenario.animation.shared;
import javafx.animation.KeyFrame;
public class ClipInterpolatorShim {
public static ClipInterpolator setKeyFrames(ClipInterpolator clip,
KeyFrame[] keyFrames, long[] keyFrameTicks) {
return clip.setKeyFrames(keyFrames, keyFrameTicks);
}
public static void validate(ClipInterpolator clip, boolean forceSync) {
clip.validate(forceSync);
}
public static void interpolate(ClipInterpolator clip,long ticks) {
clip.interpolate(ticks);
}
}
