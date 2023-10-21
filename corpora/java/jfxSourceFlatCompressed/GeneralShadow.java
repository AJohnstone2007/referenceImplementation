package com.sun.scenario.effect;
public class GeneralShadow extends DelegateEffect {
private AbstractShadow shadow;
public GeneralShadow() {
this(DefaultInput);
}
public GeneralShadow(Effect input) {
super(input);
this.shadow = new GaussianShadow(10f, Color4f.BLACK, input);
}
public AbstractShadow.ShadowMode getShadowMode() {
return shadow.getMode();
}
public void setShadowMode(AbstractShadow.ShadowMode mode) {
AbstractShadow.ShadowMode old = shadow.getMode();
this.shadow = shadow.implFor(mode);
}
protected Effect getDelegate() {
return shadow;
}
public final Effect getInput() {
return shadow.getInput();
}
public void setInput(Effect input) {
shadow.setInput(input);
}
public float getRadius() {
return shadow.getGaussianRadius();
}
public void setRadius(float radius) {
float old = shadow.getGaussianRadius();
shadow.setGaussianRadius(radius);
}
public float getGaussianRadius() {
return shadow.getGaussianRadius();
}
public float getGaussianWidth() {
return shadow.getGaussianWidth();
}
public float getGaussianHeight() {
return shadow.getGaussianHeight();
}
public void setGaussianRadius(float r) {
setRadius(r);
}
public void setGaussianWidth(float w) {
float old = shadow.getGaussianWidth();
shadow.setGaussianWidth(w);
}
public void setGaussianHeight(float h) {
float old = shadow.getGaussianHeight();
shadow.setGaussianHeight(h);
}
public float getSpread() {
return shadow.getSpread();
}
public void setSpread(float spread) {
float old = shadow.getSpread();
shadow.setSpread(spread);
}
public Color4f getColor() {
return shadow.getColor();
}
public void setColor(Color4f color) {
Color4f old = shadow.getColor();
shadow.setColor(color);
}
}
