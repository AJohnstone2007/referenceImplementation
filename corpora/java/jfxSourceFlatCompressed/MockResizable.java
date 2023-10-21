package test.javafx.scene.layout;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import javafx.scene.Node;
import test.com.sun.javafx.scene.layout.MockResizableHelper;
public class MockResizable extends Parent {
static {
MockResizableHelper.setMockResizableAccessor(new MockResizableHelper.MockResizableAccessor() {
@Override
public Bounds doComputeLayoutBounds(Node node) {
return ((MockResizable) node).doComputeLayoutBounds();
}
@Override
public void doNotifyLayoutBoundsChanged(Node node) {
((MockResizable) node).doNotifyLayoutBoundsChanged();
}
});
}
private double minWidth = 0;
private double minHeight = 0;
private double prefWidth;
private double prefHeight;
private double maxWidth = 5000;
private double maxHeight = 5000;
private double width;
private double height;
{
MockResizableHelper.initHelper(this);
}
public MockResizable(double prefWidth, double prefHeight) {
this.prefWidth = prefWidth;
this.prefHeight = prefHeight;
}
public MockResizable(double minWidth, double minHeight, double prefWidth, double prefHeight, double maxWidth, double maxHeight) {
this.minWidth = minWidth;
this.minHeight = minHeight;
this.prefWidth = prefWidth;
this.prefHeight = prefHeight;
this.maxWidth = maxWidth;
this.maxHeight = maxHeight;
}
@Override public boolean isResizable() {
return true;
}
public double getWidth() {
return width;
}
public double getHeight() {
return height;
}
@Override public void resize(double width, double height) {
this.width = width;
this.height = height;
NodeHelper.layoutBoundsChanged(this);
NodeHelper.geomChanged(this);
NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
requestLayout();
}
@Override public double getBaselineOffset() {
return Math.max(0, prefHeight - 10);
}
private Bounds doComputeLayoutBounds() {
return new BoundingBox(0, 0, 0, width, height, 0);
}
private void doNotifyLayoutBoundsChanged() {
}
@Override public double minWidth(double height) {
return minWidth;
}
@Override public double minHeight(double width) {
return minHeight;
}
@Override public double prefWidth(double height) {
return prefWidth;
}
@Override public double prefHeight(double width) {
return prefHeight;
}
@Override public double maxWidth(double height) {
return maxWidth;
}
@Override public double maxHeight(double width) {
return maxHeight;
}
}
