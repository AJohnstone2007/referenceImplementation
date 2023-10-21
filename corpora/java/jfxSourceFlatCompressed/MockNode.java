package test.javafx.scene.layout;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import test.com.sun.javafx.scene.layout.MockNodeHelper;
public class MockNode extends Node {
static {
MockNodeHelper.setMockNodeAccessor(new MockNodeHelper.MockNodeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((MockNode) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((MockNode) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((MockNode) node).doComputeContains(localX, localY);
}
});
}
{
MockNodeHelper.initHelper(this);
}
public MockNode() {
}
private NGNode doCreatePeer() { return null; }
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) { return null; }
private boolean doComputeContains(double localX, double localY) { return false; }
}
