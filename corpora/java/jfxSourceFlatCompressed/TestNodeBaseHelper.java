package test.com.sun.javafx.css;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
public class TestNodeBaseHelper extends NodeHelper {
private static final TestNodeBaseHelper theInstance;
private static TestNodeBaseAccessor testNodeBaseAccessor;
static {
theInstance = new TestNodeBaseHelper();
Utils.forceInit(TestNodeBase.class);
}
private static TestNodeBaseHelper getInstance() {
return theInstance;
}
public static void initHelper(TestNodeBase testNodeBase) {
setHelper(testNodeBase, getInstance());
}
public static void setTestNodeBaseAccessor(final TestNodeBaseAccessor newAccessor) {
if (testNodeBaseAccessor != null) {
throw new IllegalStateException();
}
testNodeBaseAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return testNodeBaseAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return testNodeBaseAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return testNodeBaseAccessor.doComputeContains(node, localX, localY);
}
public interface TestNodeBaseAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
