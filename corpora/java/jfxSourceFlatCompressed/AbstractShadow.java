package com.sun.scenario.effect;
public abstract class AbstractShadow extends LinearConvolveCoreEffect {
public AbstractShadow(Effect input) {
super(input);
}
public enum ShadowMode {
ONE_PASS_BOX,
TWO_PASS_BOX,
THREE_PASS_BOX,
GAUSSIAN,
}
public abstract ShadowMode getMode();
public abstract AbstractShadow implFor(ShadowMode m);
public abstract float getGaussianRadius();
public abstract void setGaussianRadius(float r);
public abstract float getGaussianWidth();
public abstract void setGaussianWidth(float w);
public abstract float getGaussianHeight();
public abstract void setGaussianHeight(float h);
public abstract float getSpread();
public abstract void setSpread(float spread);
public abstract Color4f getColor();
public abstract void setColor(Color4f c);
public abstract Effect getInput();
public abstract void setInput(Effect input);
}
