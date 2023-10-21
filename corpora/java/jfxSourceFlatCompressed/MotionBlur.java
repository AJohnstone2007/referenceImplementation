package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
public class MotionBlur extends Effect {
public MotionBlur() {}
public MotionBlur(double angle, double radius) {
setAngle(angle);
setRadius(radius);
}
@Override
com.sun.scenario.effect.MotionBlur createPeer() {
return new com.sun.scenario.effect.MotionBlur();
};
private ObjectProperty<Effect> input;
public final void setInput(Effect value) {
inputProperty().set(value);
}
public final Effect getInput() {
return input == null ? null : input.get();
}
public final ObjectProperty<Effect> inputProperty() {
if (input == null) {
input = new EffectInputProperty("input");
}
return input;
}
@Override
boolean checkChainContains(Effect e) {
Effect localInput = getInput();
if (localInput == null)
return false;
if (localInput == e)
return true;
return localInput.checkChainContains(e);
}
private DoubleProperty radius;
public final void setRadius(double value) {
radiusProperty().set(value);
}
public final double getRadius() {
return radius == null ? 10 : radius.get();
}
public final DoubleProperty radiusProperty() {
if (radius == null) {
radius = new DoublePropertyBase(10) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return MotionBlur.this;
}
@Override
public String getName() {
return "radius";
}
};
}
return radius;
}
private DoubleProperty angle;
public final void setAngle(double value) {
angleProperty().set(value);
}
public final double getAngle() {
return angle == null ? 0 : angle.get();
}
public final DoubleProperty angleProperty() {
if (angle == null) {
angle = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return MotionBlur.this;
}
@Override
public String getName() {
return "angle";
}
};
}
return angle;
}
private float getClampedRadius() {
return (float)Utils.clamp(0, getRadius(), 63);
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.MotionBlur peer =
(com.sun.scenario.effect.MotionBlur) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setRadius(getClampedRadius());
peer.setAngle((float)Math.toRadians(getAngle()));
}
private int getHPad() {
return (int) Math.ceil(Math.abs(Math.cos(Math.toRadians(getAngle())))
* getClampedRadius());
}
private int getVPad() {
return (int) Math.ceil(Math.abs(Math.sin(Math.toRadians(getAngle())))
* getClampedRadius());
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
bounds = getInputBounds(bounds,
BaseTransform.IDENTITY_TRANSFORM,
node, boundsAccessor,
getInput());
int hpad = getHPad();
int vpad = getVPad();
bounds = bounds.deriveWithPadding(hpad, vpad, 0);
return transformBounds(tx, bounds);
}
@Override
Effect copy() {
MotionBlur mb = new MotionBlur(this.getAngle(), this.getRadius());
mb.setInput(mb.getInput());
return mb;
}
}
