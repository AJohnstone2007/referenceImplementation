package test.javafx.scene.bounds;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGRectangle;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.Node;
import com.sun.javafx.scene.NodeHelper;
import test.com.sun.javafx.scene.bounds.PerfNodeHelper;
public class PerfNode extends Node {
static {
PerfNodeHelper.setPerfNodeAccessor(new PerfNodeHelper.PerfNodeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((PerfNode) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((PerfNode) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((PerfNode) node).doComputeContains(localX, localY);
}
});
}
{
PerfNodeHelper.initHelper(this);
}
public PerfNode() {
}
public PerfNode(float x, float y, float width, float height) {
setX(x);
setY(y);
setWidth(width);
setHeight(height);
}
private FloatProperty x;
public final void setX(float value) {
xProperty().set(value);
}
public final float getX() {
return x == null ? 0 : x.get();
}
public FloatProperty xProperty() {
if (x == null) {
x = new SimpleFloatProperty() {
@Override
protected void invalidated() {
NodeHelper.geomChanged(PerfNode.this);
}
};
}
return x;
}
private FloatProperty y;
public final void setY(float value) {
yProperty().set(value);
}
public final float getY() {
return y == null ? 0 : y.get();
}
public FloatProperty yProperty() {
if (y == null) {
y = new SimpleFloatProperty() {
@Override
protected void invalidated() {
NodeHelper.geomChanged(PerfNode.this);
}
};
}
return y;
}
private FloatProperty width;
public final void setWidth(float value) {
widthProperty().set(value);
}
public final float getWidth() {
return width == null ? 100 : width.get();
}
public FloatProperty widthProperty() {
if (width == null) {
width = new SimpleFloatProperty() {
@Override
protected void invalidated() {
impl_storeWidth(width, get());
}
};
}
return width;
}
protected void impl_storeWidth(FloatProperty model, float value) {
NodeHelper.geomChanged(this);
}
private FloatProperty height;
public final void setHeight(float value) {
heightProperty().set(value);
}
public final float getHeight() {
return height == null ? 100 : height.get();
}
public FloatProperty heightProperty() {
if (height == null) {
height = new SimpleFloatProperty() {
@Override
protected void invalidated() {
impl_storeHeight(height, get());
}
};
}
return height;
}
protected void impl_storeHeight(FloatProperty model, float value) {
NodeHelper.geomChanged(this);
}
int geomComputeCount = 0;
private BaseBounds doComputeGeomBounds(com.sun.javafx.geom.BaseBounds bounds, com.sun.javafx.geom.transform.BaseTransform tx) {
geomComputeCount++;
bounds = bounds.deriveWithNewBounds(0, 0, 0, 100, 100, 0);
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
private NGNode doCreatePeer() {
return new NGRectangle();
}
}
