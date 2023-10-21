package test.com.sun.javafx.scene.layout;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.layout.MockNode;
public class MockNodeHelper extends NodeHelper {
private static final MockNodeHelper theInstance;
private static MockNodeAccessor mockNodeAccessor;
static {
theInstance = new MockNodeHelper();
Utils.forceInit(MockNode.class);
}
private static MockNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(MockNode mockNode) {
setHelper(mockNode, getInstance());
}
public static void setMockNodeAccessor(final MockNodeAccessor newAccessor) {
if (mockNodeAccessor != null) {
throw new IllegalStateException();
}
mockNodeAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return mockNodeAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return mockNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return mockNodeAccessor.doComputeContains(node, localX, localY);
}
public interface MockNodeAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
