package com.sun.scenario.animation.shared;
import javafx.animation.Animation;
abstract class MultiLoopClipEnvelope extends ClipEnvelope {
protected boolean autoReverse;
protected long cyclePos;
protected MultiLoopClipEnvelope(Animation animation) {
super(animation);
}
protected boolean isAutoReverse() {
return autoReverse;
}
@Override
public void setAutoReverse(boolean autoReverse) {
this.autoReverse = autoReverse;
}
protected long ticksRateChange(double newRate) {
return Math.round((ticks - deltaTicks) * Math.abs(newRate / rate));
}
protected boolean isDirectionChanged(double newRate) {
return newRate * rate < 0;
}
protected boolean isDuringEvenCycle() {
return ticks % (2 * cycleTicks) < cycleTicks;
}
}
