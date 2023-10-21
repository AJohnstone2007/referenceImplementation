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
public class Reflection extends Effect {
public Reflection() {}
public Reflection(double topOffset, double fraction,
double topOpacity, double bottomOpacity) {
setBottomOpacity(bottomOpacity);
setTopOffset(topOffset);
setTopOpacity(topOpacity);
setFraction(fraction);
}
@Override
com.sun.scenario.effect.Reflection createPeer() {
return new com.sun.scenario.effect.Reflection();
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
private DoubleProperty topOffset;
public final void setTopOffset(double value) {
topOffsetProperty().set(value);
}
public final double getTopOffset() {
return topOffset == null ? 0 : topOffset.get();
}
public final DoubleProperty topOffsetProperty() {
if (topOffset == null) {
topOffset = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return Reflection.this;
}
@Override
public String getName() {
return "topOffset";
}
};
}
return topOffset;
}
private DoubleProperty topOpacity;
public final void setTopOpacity(double value) {
topOpacityProperty().set(value);
}
public final double getTopOpacity() {
return topOpacity == null ? 0.5 : topOpacity.get();
}
public final DoubleProperty topOpacityProperty() {
if (topOpacity == null) {
topOpacity = new DoublePropertyBase(0.5) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Reflection.this;
}
@Override
public String getName() {
return "topOpacity";
}
};
}
return topOpacity;
}
private DoubleProperty bottomOpacity;
public final void setBottomOpacity(double value) {
bottomOpacityProperty().set(value);
}
public final double getBottomOpacity() {
return bottomOpacity == null ? 0 : bottomOpacity.get();
}
public final DoubleProperty bottomOpacityProperty() {
if (bottomOpacity == null) {
bottomOpacity = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Reflection.this;
}
@Override
public String getName() {
return "bottomOpacity";
}
};
}
return bottomOpacity;
}
private DoubleProperty fraction;
public final void setFraction(double value) {
fractionProperty().set(value);
}
public final double getFraction() {
return fraction == null ? 0.75 : fraction.get();
}
public final DoubleProperty fractionProperty() {
if (fraction == null) {
fraction = new DoublePropertyBase(0.75) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return Reflection.this;
}
@Override
public String getName() {
return "fraction";
}
};
}
return fraction;
}
private float getClampedFraction() {
return (float)Utils.clamp(0, getFraction(), 1);
}
private float getClampedBottomOpacity() {
return (float)Utils.clamp(0, getBottomOpacity(), 1);
}
private float getClampedTopOpacity() {
return (float)Utils.clamp(0, getTopOpacity(), 1);
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.Reflection peer =
(com.sun.scenario.effect.Reflection) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setFraction(getClampedFraction());
peer.setTopOffset((float)getTopOffset());
peer.setBottomOpacity(getClampedBottomOpacity());
peer.setTopOpacity(getClampedTopOpacity());
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
bounds.roundOut();
float x1 = bounds.getMinX();
float y1 = bounds.getMaxY() + (float)getTopOffset();
float z1 = bounds.getMinZ();
float x2 = bounds.getMaxX();
float y2 = y1 + (getClampedFraction() * bounds.getHeight());
float z2 = bounds.getMaxZ();
BaseBounds ret = BaseBounds.getInstance(x1, y1, z1, x2, y2, z2);
ret = ret.deriveWithUnion(bounds);
return transformBounds(tx, ret);
}
@Override
Effect copy() {
Reflection ref = new Reflection(this.getTopOffset(), this.getFraction(),
this.getTopOpacity(), this.getBottomOpacity());
ref.setInput(ref.getInput());
return ref;
}
}
