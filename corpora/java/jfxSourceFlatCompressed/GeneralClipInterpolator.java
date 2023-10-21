package com.sun.scenario.animation.shared;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableValue;
class GeneralClipInterpolator extends ClipInterpolator {
private KeyFrame[] keyFrames;
private long[] keyFrameTicks;
private InterpolationInterval[][] interval = new InterpolationInterval[0][];
private int[] undefinedStartValues = new int[0];
private boolean invalid = true;
GeneralClipInterpolator(KeyFrame[] keyFrames, long[] keyFrameTicks) {
this.keyFrames = keyFrames;
this.keyFrameTicks = keyFrameTicks;
}
@Override
ClipInterpolator setKeyFrames(KeyFrame[] keyFrames, long[] keyFrameTicks) {
if (ClipInterpolator.getRealKeyFrameCount(keyFrames) == 2) {
return ClipInterpolator.create(keyFrames, keyFrameTicks);
}
this.keyFrames = keyFrames;
this.keyFrameTicks = keyFrameTicks;
invalid = true;
return this;
}
@Override
void validate(boolean forceSync) {
if (invalid) {
final Map<WritableValue<?>, KeyValue> lastKeyValues = new HashMap<>();
final int n = keyFrames.length;
int index;
for (index = 0; index < n; index++) {
final KeyFrame keyFrame = keyFrames[index];
if (keyFrameTicks[index] == 0) {
for (final KeyValue keyValue : keyFrame.getValues()) {
lastKeyValues.put(keyValue.getTarget(), keyValue);
}
} else {
break;
}
}
final Map<WritableValue<?>, List<InterpolationInterval>> map = new HashMap<>();
final Set<WritableValue<?>> undefinedValues = new HashSet<>();
for (; index < n; index++) {
final KeyFrame keyFrame = keyFrames[index];
final long ticks = keyFrameTicks[index];
for (final KeyValue rightKeyValue : keyFrame.getValues()) {
final WritableValue<?> target = rightKeyValue.getTarget();
List<InterpolationInterval> list = map.get(target);
final KeyValue leftKeyValue = lastKeyValues.get(target);
if (list == null) {
list = new ArrayList<>();
map.put(target, list);
if (leftKeyValue == null) {
list.add(InterpolationInterval.create(
rightKeyValue, ticks));
undefinedValues.add(target);
} else {
list.add(InterpolationInterval
.create(rightKeyValue, ticks,
leftKeyValue, ticks));
}
} else {
assert leftKeyValue != null;
list.add(InterpolationInterval.create(rightKeyValue,
ticks, leftKeyValue,
ticks - list.get(list.size() - 1).ticks));
}
lastKeyValues.put(target, rightKeyValue);
}
}
final int targetCount = map.size();
if (interval.length != targetCount) {
interval = new InterpolationInterval[targetCount][];
}
final int undefinedStartValuesCount = undefinedValues.size();
if (undefinedStartValues.length != undefinedStartValuesCount) {
undefinedStartValues = new int[undefinedStartValuesCount];
}
int undefinedStartValuesIndex = 0;
final Iterator<Map.Entry<WritableValue<?>, List<InterpolationInterval>>> iterator = map
.entrySet().iterator();
for (int i = 0; i < targetCount; i++) {
final Map.Entry<WritableValue<?>, List<InterpolationInterval>> entry = iterator
.next();
interval[i] = new InterpolationInterval[entry.getValue().size()];
entry.getValue().toArray(interval[i]);
if (undefinedValues.contains(entry.getKey())) {
undefinedStartValues[undefinedStartValuesIndex++] = i;
}
}
invalid = false;
} else if (forceSync) {
final int n = undefinedStartValues.length;
for (int i = 0; i < n; i++) {
final int index = undefinedStartValues[i];
interval[index][0].recalculateStartValue();
}
}
}
@Override
void interpolate(long ticks) {
final int targetCount = interval.length;
targetLoop: for (int targetIndex = 0; targetIndex < targetCount; targetIndex++) {
InterpolationInterval[] intervalList = interval[targetIndex];
final int intervalCount = intervalList.length;
long leftTicks = 0;
for (int intervalIndex = 0; intervalIndex < intervalCount - 1; intervalIndex++) {
final InterpolationInterval i = intervalList[intervalIndex];
final long rightTicks = i.ticks;
if (ticks <= rightTicks) {
final double frac = (double)(ticks - leftTicks)
/ (rightTicks - leftTicks);
i.interpolate(frac);
continue targetLoop;
}
leftTicks = rightTicks;
}
final InterpolationInterval i = intervalList[intervalCount - 1];
final double frac = Math.min(1.0, (double)(ticks - leftTicks)
/ (i.ticks - leftTicks));
i.interpolate(frac);
}
}
}
