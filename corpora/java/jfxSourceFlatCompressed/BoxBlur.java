package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
public class BoxBlur extends Effect {
public BoxBlur() {}
public BoxBlur(double width, double height, int iterations) {
setWidth(width);
setHeight(height);
setIterations(iterations);
}
@Override
com.sun.scenario.effect.BoxBlur createPeer() {
return new com.sun.scenario.effect.BoxBlur();
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
private DoubleProperty width;
public final void setWidth(double value) {
widthProperty().set(value);
}
public final double getWidth() {
return width == null ? 5 : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new DoublePropertyBase(5) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return BoxBlur.this;
}
@Override
public String getName() {
return "width";
}
};
}
return width;
}
private DoubleProperty height;
public final void setHeight(double value) {
heightProperty().set(value);
}
public final double getHeight() {
return height == null ? 5 : height.get();
}
public final DoubleProperty heightProperty() {
if (height == null) {
height = new DoublePropertyBase(5) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return BoxBlur.this;
}
@Override
public String getName() {
return "height";
}
};
}
return height;
}
private IntegerProperty iterations;
public final void setIterations(int value) {
iterationsProperty().set(value);
}
public final int getIterations() {
return iterations == null ? 1 : iterations.get();
}
public final IntegerProperty iterationsProperty() {
if (iterations == null) {
iterations = new IntegerPropertyBase(1) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return BoxBlur.this;
}
@Override
public String getName() {
return "iterations";
}
};
}
return iterations;
}
private int getClampedWidth() {
return Utils.clamp(0, (int) getWidth(), 255);
}
private int getClampedHeight() {
return Utils.clamp(0, (int) getHeight(), 255);
}
private int getClampedIterations() {
return Utils.clamp(0, getIterations(), 3);
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.BoxBlur peer =
(com.sun.scenario.effect.BoxBlur) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setHorizontalSize(getClampedWidth());
peer.setVerticalSize(getClampedHeight());
peer.setPasses(getClampedIterations());
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
int localIterations = getClampedIterations();
int hgrow = getKernelSize(getClampedWidth(), localIterations);
int vgrow = getKernelSize(getClampedHeight(), localIterations);
bounds = bounds.deriveWithPadding(hgrow, vgrow, 0);
return transformBounds(tx, bounds);
}
@Override
Effect copy() {
BoxBlur bb = new BoxBlur(this.getWidth(), this.getHeight(), this.getIterations());
bb.setInput(this.getInput());
return bb;
}
}
