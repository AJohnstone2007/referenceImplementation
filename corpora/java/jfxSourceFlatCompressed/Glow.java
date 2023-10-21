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
public class Glow extends Effect {
public Glow() {}
public Glow(double level) {
setLevel(level);
}
@Override
com.sun.scenario.effect.Glow createPeer() {
return new com.sun.scenario.effect.Glow();
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
private DoubleProperty level;
public final void setLevel(double value) {
levelProperty().set(value);
}
public final double getLevel() {
return level == null ? 0.3 : level.get();
}
public final DoubleProperty levelProperty() {
if (level == null) {
level = new DoublePropertyBase(0.3) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Glow.this;
}
@Override
public String getName() {
return "level";
}
};
}
return level;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.Glow peer =
(com.sun.scenario.effect.Glow) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setLevel((float)Utils.clamp(0, getLevel(), 1));
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
return getInputBounds(bounds, tx, node, boundsAccessor, getInput());
}
@Override
Effect copy() {
return new Glow(this.getLevel());
}
}
