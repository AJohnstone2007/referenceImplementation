package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.tk.Toolkit;
public class Shadow extends Effect {
private boolean changeIsLocal;
public Shadow() {}
public Shadow(double radius, Color color) {
setRadius(radius);
setColor(color);
}
public Shadow(BlurType blurType, Color color, double radius) {
setBlurType(blurType);
setColor(color);
setRadius(radius);
}
@Override
com.sun.scenario.effect.GeneralShadow createPeer() {
return new com.sun.scenario.effect.GeneralShadow();
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
double localRadius = getRadius();
if (!changeIsLocal) {
changeIsLocal = true;
updateRadius(localRadius);
changeIsLocal = false;
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
}
@Override
public Object getBean() {
return Shadow.this;
}
@Override
public String getName() {
return "radius";
}
};
}
return radius;
}
private void updateRadius(double value) {
double newdim = (value * 2 + 1);
if (width != null && width.isBound()) {
if (height == null || !height.isBound()) {
setHeight(newdim * 2 - getWidth());
}
} else if (height != null && height.isBound()) {
setWidth(newdim * 2 - getHeight());
} else {
setWidth(newdim);
setHeight(newdim);
}
}
private DoubleProperty width;
public final void setWidth(double value) {
widthProperty().set(value);
}
public final double getWidth() {
return width == null ? 21 : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new DoublePropertyBase(21) {
@Override
public void invalidated() {
double localWidth = getWidth();
if (!changeIsLocal) {
changeIsLocal = true;
updateWidth(localWidth);
changeIsLocal = false;
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
}
@Override
public Object getBean() {
return Shadow.this;
}
@Override
public String getName() {
return "width";
}
};
}
return width;
}
private void updateWidth(double value) {
if (radius == null || !radius.isBound()) {
double newrad = ((value + getHeight()) / 2);
newrad = ((newrad - 1) / 2);
if (newrad < 0) {
newrad = 0;
}
setRadius(newrad);
} else {
if (height == null || !height.isBound()) {
double newdim = (getRadius() * 2 + 1);
setHeight(newdim * 2 - value);
}
}
}
private DoubleProperty height;
public final void setHeight(double value) {
heightProperty().set(value);
}
public final double getHeight() {
return height == null ? 21 : height.get();
}
public final DoubleProperty heightProperty() {
if (height == null) {
height = new DoublePropertyBase(21) {
@Override
public void invalidated() {
double localHeight = getHeight();
if (!changeIsLocal) {
changeIsLocal = true;
updateHeight(localHeight);
changeIsLocal = false;
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
}
@Override
public Object getBean() {
return Shadow.this;
}
@Override
public String getName() {
return "height";
}
};
}
return height;
}
private void updateHeight(double value) {
if (radius == null || !radius.isBound()) {
double newrad = ((getWidth() + value) / 2);
newrad = ((newrad - 1) / 2);
if (newrad < 0) {
newrad = 0;
}
setRadius(newrad);
} else {
if (width == null || !width.isBound()) {
double newdim = (getRadius() * 2 + 1);
setWidth(newdim * 2 - value);
}
}
}
private ObjectProperty<BlurType> blurType;
public final void setBlurType(BlurType value) {
blurTypeProperty().set(value);
}
public final BlurType getBlurType() {
return blurType == null ? BlurType.THREE_PASS_BOX : blurType.get();
}
public final ObjectProperty<BlurType> blurTypeProperty() {
if (blurType == null) {
blurType = new ObjectPropertyBase<BlurType>(BlurType.THREE_PASS_BOX) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return Shadow.this;
}
@Override
public String getName() {
return "blurType";
}
};
}
return blurType;
}
private ObjectProperty<Color> color;
public final void setColor(Color value) {
colorProperty().set(value);
}
public final Color getColor() {
return color == null ? Color.BLACK : color.get();
}
public final ObjectProperty<Color> colorProperty() {
if (color == null) {
color = new ObjectPropertyBase<Color>(Color.BLACK) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Shadow.this;
}
@Override
public String getName() {
return "color";
}
};
}
return color;
}
private float getClampedWidth() {
return (float) Utils.clamp(0, getWidth(), 255);
}
private float getClampedHeight() {
return (float) Utils.clamp(0, getHeight(), 255);
}
private Color getColorInternal() {
Color c = getColor();
return c == null ? Color.BLACK : c;
}
private BlurType getBlurTypeInternal() {
BlurType bt = getBlurType();
return bt == null ? BlurType.THREE_PASS_BOX : bt;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.GeneralShadow peer =
(com.sun.scenario.effect.GeneralShadow) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setGaussianWidth(getClampedWidth());
peer.setGaussianHeight(getClampedHeight());
peer.setShadowMode(Toolkit.getToolkit().toShadowMode(getBlurTypeInternal()));
peer.setColor(Toolkit.getToolkit().toColor4f(getColorInternal()));
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
return getShadowBounds(bounds, tx,
getClampedWidth(),
getClampedHeight(),
getBlurTypeInternal());
}
@Override
Effect copy() {
Shadow shadow = new Shadow(this.getBlurType(), this.getColor(), this.getRadius());
shadow.setInput(this.getInput());
shadow.setHeight(this.getHeight());
shadow.setWidth(this.getWidth());
return shadow;
}
}
