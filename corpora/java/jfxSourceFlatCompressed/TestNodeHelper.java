package test.com.sun.javafx.scene.input;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.input.TestNode;
public class TestNodeHelper extends NodeHelper {
private static final TestNodeHelper theInstance;
private static TestNodeAccessor testNodeAccessor;
static {
theInstance = new TestNodeHelper();
Utils.forceInit(TestNode.class);
}
private static TestNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(TestNode testNode) {
setHelper(testNode, getInstance());
}
public static void setTestNodeAccessor(final TestNodeAccessor newAccessor) {
if (testNodeAccessor != null) {
throw new IllegalStateException();
}
testNodeAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return testNodeAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return testNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return testNodeAccessor.doComputeContains(node, localX, localY);
}
public interface TestNodeAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
