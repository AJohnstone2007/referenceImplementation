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
public class GaussianBlur extends Effect {
public GaussianBlur() {}
public GaussianBlur(double radius) {
setRadius(radius);
}
@Override
com.sun.scenario.effect.GaussianBlur createPeer() {
return new com.sun.scenario.effect.GaussianBlur();
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
return GaussianBlur.this;
}
@Override
public String getName() {
return "radius";
}
};
}
return radius;
}
private float getClampedRadius() {
return (float) Utils.clamp(0, getRadius(), 63);
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.GaussianBlur peer =
(com.sun.scenario.effect.GaussianBlur) getPeer();
peer.setRadius(getClampedRadius());
peer.setInput(localInput == null ? null : localInput.getPeer());
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
float r = getClampedRadius();
bounds = bounds.deriveWithPadding(r, r, 0);
return transformBounds(tx, bounds);
}
@Override
Effect copy() {
return new GaussianBlur(this.getRadius());
}
}
