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
public class Bloom extends Effect {
public Bloom() {}
public Bloom(double threshold) {
setThreshold(threshold);
}
@Override
com.sun.scenario.effect.Bloom createPeer() {
return new com.sun.scenario.effect.Bloom();
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
private DoubleProperty threshold;
public final void setThreshold(double value) {
thresholdProperty().set(value);
}
public final double getThreshold() {
return threshold == null ? 0.3 : threshold.get();
}
public final DoubleProperty thresholdProperty() {
if (threshold == null) {
threshold = new DoublePropertyBase(0.3) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Bloom.this;
}
@Override
public String getName() {
return "threshold";
}
};
}
return threshold;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.Bloom peer =
(com.sun.scenario.effect.Bloom) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setThreshold((float)Utils.clamp(0, getThreshold(), 1));
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
return getInputBounds(bounds, tx,node, boundsAccessor, getInput());
}
@Override
Effect copy() {
Bloom b = new Bloom(this.getThreshold());
b.setInput(this.getInput());
return b;
}
}
