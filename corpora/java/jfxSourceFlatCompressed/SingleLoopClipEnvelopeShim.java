package com.sun.scenario.animation.shared;
import javafx.animation.Animation;
public class SingleLoopClipEnvelopeShim extends SingleLoopClipEnvelope {
public SingleLoopClipEnvelopeShim(Animation animation) {
super(animation);
}
public long getTicks() {
return ticks;
}
}
