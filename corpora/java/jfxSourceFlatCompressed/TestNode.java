package test.javafx.scene.input;
import test.com.sun.javafx.scene.input.TestNodeHelper;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
public class TestNode extends Node {
static {
TestNodeHelper.setTestNodeAccessor(new TestNodeHelper.TestNodeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((TestNode) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((TestNode) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((TestNode) node).doComputeContains(localX, localY);
}
});
}
private float offsetInScene;
{
TestNodeHelper.initHelper(this);
}
public TestNode() {
}
public TestNode(float offsetInScene) {
this.offsetInScene = offsetInScene;
}
@Override
public Point2D sceneToLocal(double x, double y) {
return new Point2D(x - offsetInScene, y - offsetInScene);
}
@Override
public Point2D localToScene(double x, double y) {
return new Point2D(x + offsetInScene, y + offsetInScene);
}
@Override
public Point3D sceneToLocal(double x, double y, double z) {
return new Point3D(x - offsetInScene, y - offsetInScene, z);
}
@Override
public Point3D localToScene(double x, double y, double z) {
return new Point3D(x + offsetInScene, y + offsetInScene, z);
}
private boolean doComputeContains(double f, double f1) {
return false;
}
private BaseBounds doComputeGeomBounds(BaseBounds bd, BaseTransform bt) {
return null;
}
private NGNode doCreatePeer() {
return new NGGroup();
}
}
