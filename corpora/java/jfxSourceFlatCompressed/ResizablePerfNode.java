package test.javafx.scene.bounds;
import javafx.beans.property.FloatProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.com.sun.javafx.scene.bounds.ResizablePerfNodeHelper;
public class ResizablePerfNode extends PerfNode {
static {
ResizablePerfNodeHelper.setResizablePerfNodeAccessor(
new ResizablePerfNodeHelper.ResizablePerfNodeAccessor() {
@Override
public Bounds doComputeLayoutBounds(Node node) {
return ((ResizablePerfNode) node).doComputeLayoutBounds();
}
@Override
public void doNotifyLayoutBoundsChanged(Node node) {
((ResizablePerfNode) node).doNotifyLayoutBoundsChanged();
}
});
}
{
ResizablePerfNodeHelper.initHelper(this);
}
ResizablePerfNode() {
}
private Bounds doComputeLayoutBounds() {
return new BoundingBox(0, 0, getWidth(), getHeight());
}
@Override public boolean isResizable() {
return true;
}
@Override public double prefWidth(double height) {
return getWidth();
}
@Override public double prefHeight(double width) {
return getHeight();
}
private void doNotifyLayoutBoundsChanged() { }
@Override
protected void impl_storeWidth(FloatProperty model, float value) {
super.impl_storeWidth(model, value);
ResizablePerfNodeHelper.superNotifyLayoutBoundsChanged(this);
}
@Override
protected void impl_storeHeight(FloatProperty model, float value) {
super.impl_storeHeight(model, value);
ResizablePerfNodeHelper.superNotifyLayoutBoundsChanged(this);
}
@Override public double minWidth(double height) {
return 0;
}
@Override public double minHeight(double width) {
return 0;
}
@Override public double maxWidth(double height) {
return 0;
}
@Override public double maxHeight(double width) {
return 0;
}
}
