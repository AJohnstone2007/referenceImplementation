package test.javafx.scene;
import com.sun.javafx.scene.shape.RectangleHelper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class Node_layoutSizes_Test {
@Test public void Node_prefWidth_BasedOnLayoutBounds() {
Rectangle node = new Rectangle(10, 10, 100, 100);
assertEquals(100, node.prefWidth(-1), 0);
assertEquals(100, node.prefWidth(5), 0);
}
@Test public void Node_prefWidth_BasedOnLayoutBounds2() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, 50, 50);
assertEquals(50, node.prefWidth(-1), 0);
assertEquals(50, node.prefWidth(5), 0);
}
@Test public void Node_prefWidth_BasedOnLayoutBounds_CleansUpAfterBadBounds() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, Double.NaN, 50);
assertEquals(0, node.prefWidth(-1), 0);
assertEquals(0, node.prefWidth(5), 0);
}
@Test public void Node_prefWidth_BasedOnLayoutBounds_CleansUpAfterBadBounds2() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, -10, 50);
assertEquals(0, node.prefWidth(-1), 0);
assertEquals(0, node.prefWidth(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds() {
Rectangle node = new Rectangle(10, 10, 100, 100);
assertEquals(100, node.prefHeight(-1), 0);
assertEquals(100, node.prefHeight(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds2() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, 50, 50);
assertEquals(50, node.prefHeight(-1), 0);
assertEquals(50, node.prefHeight(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds_CleansUpAfterBadBounds() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, 50, Double.NaN);
assertEquals(0, node.prefHeight(-1), 0);
assertEquals(0, node.prefHeight(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds_CleansUpAfterBadBounds2() {
SpecialRect node = new SpecialRect(10, 10, 100, 100);
node.setTestBB(0, 0, 50, -10);
assertEquals(0, node.prefHeight(-1), 0);
assertEquals(0, node.prefHeight(5), 0);
}
@Test public void Node_minWidth_SameAsPrefWidth() {
Rectangle node = new Rectangle(10, 10, 100, 100) {
@Override public double prefWidth(double height) {
return 500;
}
};
assertEquals(500, node.minWidth(-1), 0);
assertEquals(500, node.minWidth(5), 0);
}
@Test public void Node_minHeight_SameAsPrefHeight() {
Rectangle node = new Rectangle(10, 10, 100, 100) {
@Override public double prefHeight(double height) {
return 500;
}
};
assertEquals(500, node.minHeight(-1), 0);
assertEquals(500, node.minHeight(5), 0);
}
@Test public void Node_maxWidth_SameAsPrefWidth() {
Rectangle node = new Rectangle(10, 10, 100, 100) {
@Override public double prefWidth(double height) {
return 500;
}
};
assertEquals(500, node.maxWidth(-1), 0);
assertEquals(500, node.maxWidth(5), 0);
}
@Test public void Node_maxHeight_SameAsPrefHeight() {
Rectangle node = new Rectangle(10, 10, 100, 100) {
@Override public double prefHeight(double height) {
return 500;
}
};
assertEquals(500, node.maxHeight(-1), 0);
assertEquals(500, node.maxHeight(5), 0);
}
protected class SpecialRect extends Rectangle {
BoundingBox testBB;
{
SpecialRectHelper.initHelper(this);
}
public SpecialRect(double x, double y, double width, double height) {
}
void setTestBB(double x, double y, double width, double height) {
testBB = new BoundingBox(x, y, width, height);
}
private Bounds doComputeLayoutBounds() {
return testBB;
}
}
static final class SpecialRectHelper extends RectangleHelper {
private static final SpecialRectHelper theInstance;
static {
theInstance = new SpecialRectHelper();
}
private static SpecialRectHelper getInstance() {
return theInstance;
}
public static void initHelper(SpecialRect specialRect) {
setHelper(specialRect, getInstance());
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return ((SpecialRect) node).doComputeLayoutBounds();
}
}
}
