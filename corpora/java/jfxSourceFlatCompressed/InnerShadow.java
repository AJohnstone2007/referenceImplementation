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
public class InnerShadow extends Effect {
private boolean changeIsLocal;
public InnerShadow() {}
public InnerShadow(double radius, Color color) {
setRadius(radius);
setColor(color);
}
public InnerShadow(double radius, double offsetX, double offsetY, Color color) {
setRadius(radius);
setOffsetX(offsetX);
setOffsetY(offsetY);
setColor(color);
}
public InnerShadow(BlurType blurType, Color color, double radius, double choke,
double offsetX, double offsetY) {
setBlurType(blurType);
setColor(color);
setRadius(radius);
setChoke(choke);
setOffsetX(offsetX);
setOffsetY(offsetY);
}
@Override
com.sun.scenario.effect.InnerShadow createPeer() {
return new com.sun.scenario.effect.InnerShadow();
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
return InnerShadow.this;
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
return InnerShadow.this;
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
return InnerShadow.this;
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
}
@Override
public Object getBean() {
return InnerShadow.this;
}
@Override
public String getName() {
return "blurType";
}
};
}
return blurType;
}
private DoubleProperty choke;
public final void setChoke(double value) {
chokeProperty().set(value);
}
public final double getChoke() {
return choke == null ? 0 : choke.get();
}
public final DoubleProperty chokeProperty() {
if (choke == null) {
choke = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return InnerShadow.this;
}
@Override
public String getName() {
return "choke";
}
};
}
return choke;
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
return InnerShadow.this;
}
@Override
public String getName() {
return "color";
}
};
}
return color;
}
private DoubleProperty offsetX;
public final void setOffsetX(double value) {
offsetXProperty().set(value);
}
public final double getOffsetX() {
return offsetX == null ? 0 : offsetX.get();
}
public final DoubleProperty offsetXProperty() {
if (offsetX == null) {
offsetX = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return InnerShadow.this;
}
@Override
public String getName() {
return "offsetX";
}
};
}
return offsetX;
}
private DoubleProperty offsetY;
public final void setOffsetY(double value) {
offsetYProperty().set(value);
}
public final double getOffsetY() {
return offsetY == null ? 0 : offsetY.get();
}
public final DoubleProperty offsetYProperty() {
if (offsetY == null) {
offsetY = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return InnerShadow.this;
}
@Override
public String getName() {
return "offsetY";
}
};
}
return offsetY;
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
com.sun.scenario.effect.InnerShadow peer =
(com.sun.scenario.effect.InnerShadow) getPeer();
peer.setShadowSourceInput(localInput == null ? null : localInput.getPeer());
peer.setContentInput(localInput == null ? null : localInput.getPeer());
peer.setGaussianWidth((float)Utils.clamp(0, getWidth(), 255));
peer.setGaussianHeight((float)Utils.clamp(0, getHeight(), 255));
peer.setShadowMode(Toolkit.getToolkit().toShadowMode(getBlurTypeInternal()));
peer.setColor(Toolkit.getToolkit().toColor4f(getColorInternal()));
peer.setChoke((float)Utils.clamp(0, getChoke(), 1));
peer.setOffsetX((int) getOffsetX());
peer.setOffsetY((int) getOffsetY());
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
InnerShadow is = new InnerShadow(this.getBlurType(), this.getColor(),
this.getRadius(), this.getChoke(), this.getOffsetX(),
this.getOffsetY());
is.setInput(this.getInput());
is.setWidth(this.getWidth());
is.setHeight(this.getHeight());
return is;
}
}
