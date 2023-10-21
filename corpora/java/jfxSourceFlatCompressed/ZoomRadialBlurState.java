package com.sun.scenario.effect.impl.state;
import com.sun.scenario.effect.ZoomRadialBlur;
public class ZoomRadialBlurState {
private float dx = -1f;
private float dy = -1f;
private final ZoomRadialBlur effect;
public ZoomRadialBlurState(ZoomRadialBlur effect) {
this.effect = effect;
}
public int getRadius() {
return effect.getRadius();
}
public void updateDeltas(float dx, float dy) {
this.dx = dx;
this.dy = dy;
}
public void invalidateDeltas() {
this.dx = -1f;
this.dy = -1f;
}
public float getDx() {
return dx;
}
public float getDy() {
return dy;
}
public int getNumSteps() {
int r = getRadius();
return r * 2 + 1;
}
public float getAlpha() {
float r = getRadius();
return 1.0f/(2.0f*r + 1.0f);
}
}
