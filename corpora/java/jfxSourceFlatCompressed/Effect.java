package javafx.scene.effect;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.scenario.effect.EffectHelper;
public abstract class Effect {
static {
EffectHelper.setEffectAccessor(new EffectHelper.EffectAccessor() {
@Override
public com.sun.scenario.effect.Effect getPeer(Effect effect) {
return effect.getPeer();
}
@Override
public void sync(Effect effect) {
effect.sync();
}
@Override
public IntegerProperty effectDirtyProperty(Effect effect) {
return effect.effectDirtyProperty();
}
@Override
public boolean isEffectDirty(Effect effect) {
return effect.isEffectDirty();
}
@Override
public BaseBounds getBounds(Effect effect, BaseBounds bounds,
BaseTransform tx, Node node, BoundsAccessor boundsAccessor) {
return effect.getBounds(bounds, tx, node, boundsAccessor);
}
@Override
public Effect copy(Effect effect) {
return effect.copy();
}
@Override
public com.sun.scenario.effect.Blend.Mode getToolkitBlendMode(BlendMode mode) {
return Blend.getToolkitMode(mode);
}
});
}
protected Effect() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
void effectBoundsChanged() {
toggleDirty(EffectDirtyBits.BOUNDS_CHANGED);
}
private com.sun.scenario.effect.Effect peer;
abstract com.sun.scenario.effect.Effect createPeer();
com.sun.scenario.effect.Effect getPeer() {
if (peer == null) {
peer = createPeer();
}
return peer;
}
private IntegerProperty effectDirty =
new SimpleIntegerProperty(this, "effectDirty");
private void setEffectDirty(int value) {
effectDirtyProperty().set(value);
}
private final IntegerProperty effectDirtyProperty() {
return effectDirty;
}
private final boolean isEffectDirty() {
return isEffectDirty(EffectDirtyBits.EFFECT_DIRTY);
}
final void markDirty(EffectDirtyBits dirtyBit) {
setEffectDirty(effectDirty.get() | dirtyBit.getMask());
}
private void toggleDirty(EffectDirtyBits dirtyBit) {
setEffectDirty(effectDirty.get() ^ dirtyBit.getMask());
}
private boolean isEffectDirty(EffectDirtyBits dirtyBit) {
return ((effectDirty.get() & dirtyBit.getMask()) != 0);
}
private void clearEffectDirty(EffectDirtyBits dirtyBit) {
setEffectDirty(effectDirty.get() & ~dirtyBit.getMask());
}
final void sync() {
if (isEffectDirty(EffectDirtyBits.EFFECT_DIRTY)) {
update();
clearEffectDirty(EffectDirtyBits.EFFECT_DIRTY);
}
}
abstract void update();
abstract boolean checkChainContains(Effect e);
boolean containsCycles(Effect value) {
if (value != null
&& (value == this || value.checkChainContains(this))) {
return true;
}
return false;
}
class EffectInputChangeListener extends EffectChangeListener {
private int oldBits;
public void register(Effect value) {
super.register(value == null? null: value.effectDirtyProperty());
if (value != null) {
oldBits = value.effectDirtyProperty().get();
}
}
@Override
public void invalidated(Observable valueModel) {
int newBits = ((IntegerProperty)valueModel).get();
int dirtyBits = newBits ^ oldBits;
oldBits = newBits;
if (EffectDirtyBits.isSet(dirtyBits, EffectDirtyBits.EFFECT_DIRTY)
&& EffectDirtyBits.isSet(newBits, EffectDirtyBits.EFFECT_DIRTY)) {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
if (EffectDirtyBits.isSet(dirtyBits, EffectDirtyBits.BOUNDS_CHANGED)) {
toggleDirty(EffectDirtyBits.BOUNDS_CHANGED);
}
}
}
class EffectInputProperty extends ObjectPropertyBase<Effect> {
private final String propertyName;
private Effect validInput = null;
private final EffectInputChangeListener effectChangeListener =
new EffectInputChangeListener();
public EffectInputProperty(final String propertyName) {
this.propertyName = propertyName;
}
@Override
public void invalidated() {
final Effect newInput = super.get();
if (containsCycles(newInput)) {
if (isBound()) {
unbind();
set(validInput);
throw new IllegalArgumentException("Cycle in effect chain "
+ "detected, binding was set to incorrect value, "
+ "unbinding the input property");
} else {
set(validInput);
throw new IllegalArgumentException("Cycle in effect chain detected");
}
}
validInput = newInput;
effectChangeListener.register(newInput);
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return Effect.this;
}
@Override
public String getName() {
return propertyName;
}
}
abstract BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor);
abstract Effect copy();
static BaseBounds transformBounds(BaseTransform tx, BaseBounds r) {
if (tx == null || tx.isIdentity()) {
return r;
}
BaseBounds ret = new RectBounds();
ret = tx.transform(r, ret);
return ret;
}
static int getKernelSize(float fsize, int iterations) {
int ksize = (int) Math.ceil(fsize);
if (ksize < 1) ksize = 1;
ksize = (ksize-1) * iterations + 1;
ksize |= 1;
return ksize / 2;
}
static BaseBounds getShadowBounds(BaseBounds bounds,
BaseTransform tx,
float width,
float height,
BlurType blurType) {
int hgrow = 0;
int vgrow = 0;
switch (blurType) {
case GAUSSIAN:
float hradius = width < 1.0f ? 0.0f : ((width - 1.0f) / 2.0f);
float vradius = height < 1.0f ? 0.0f : ((height - 1.0f) / 2.0f);
hgrow = (int) Math.ceil(hradius);
vgrow = (int) Math.ceil(vradius);
break;
case ONE_PASS_BOX:
hgrow = getKernelSize(Math.round(width/3.0f), 1);
vgrow = getKernelSize(Math.round(height/3.0f), 1);
break;
case TWO_PASS_BOX:
hgrow = getKernelSize(Math.round(width/3.0f), 2);
vgrow = getKernelSize(Math.round(height/3.0f), 2);
break;
case THREE_PASS_BOX:
hgrow = getKernelSize(Math.round(width/3.0f), 3);
vgrow = getKernelSize(Math.round(height/3.0f), 3);
break;
}
bounds = bounds.deriveWithPadding(hgrow, vgrow, 0);
return transformBounds(tx, bounds);
}
static BaseBounds getInputBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor,
Effect input) {
if (input != null) {
bounds = input.getBounds(bounds, tx, node, boundsAccessor);
} else {
bounds = boundsAccessor.getGeomBounds(bounds, tx, node);
}
return bounds;
}
}
