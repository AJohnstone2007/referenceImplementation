package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.tk.Toolkit;
public class ColorInput extends Effect {
public ColorInput() {}
public ColorInput(double x,
double y,
double width,
double height,
Paint paint) {
setX(x);
setY(y);
setWidth(width);
setHeight(height);
setPaint(paint);
}
@Override
com.sun.scenario.effect.Flood createPeer() {
return new com.sun.scenario.effect.Flood(
Toolkit.getPaintAccessor().getPlatformPaint(Color.RED));
};
private ObjectProperty<Paint> paint;
public final void setPaint(Paint value) {
paintProperty().set(value);
}
public final Paint getPaint() {
return paint == null ? Color.RED : paint.get();
}
public final ObjectProperty<Paint> paintProperty() {
if (paint == null) {
paint = new ObjectPropertyBase<Paint>(Color.RED) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return ColorInput.this;
}
@Override
public String getName() {
return "paint";
}
};
}
return paint;
}
private DoubleProperty x;
public final void setX(double value) {
xProperty().set(value);
}
public final double getX() {
return x == null ? 0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorInput.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
yProperty().set(value);
}
public final double getY() {
return y == null ? 0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorInput.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private DoubleProperty width;
public final void setWidth(double value) {
widthProperty().set(value);
}
public final double getWidth() {
return width == null ? 0 : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorInput.this;
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
return height == null ? 0 : height.get();
}
public final DoubleProperty heightProperty() {
if (height == null) {
height = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorInput.this;
}
@Override
public String getName() {
return "height";
}
};
}
return height;
}
private Paint getPaintInternal() {
Paint p = getPaint();
return p == null ? Color.RED : p;
}
@Override
void update() {
com.sun.scenario.effect.Flood peer =
(com.sun.scenario.effect.Flood) getPeer();
peer.setPaint(Toolkit.getPaintAccessor().getPlatformPaint(getPaintInternal()));
peer.setFloodBounds(new RectBounds(
(float)getX(), (float)getY(),
(float)(getX() + getWidth()),
(float)(getY() + getHeight())));
}
@Override
boolean checkChainContains(Effect e) {
return false;
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
RectBounds ret = new RectBounds(
(float)getX(), (float)getY(),
(float)(getX() + getWidth()),
(float)(getY() + getHeight()));
return transformBounds(tx, ret);
}
@Override
Effect copy() {
return new ColorInput(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getPaint());
}
}
