package test.com.sun.javafx.scene;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.StructureTest.StubNode;
public class StubNodeHelper extends NodeHelper {
private static final StubNodeHelper theInstance;
private static StubNodeAccessor stubNodeAccessor;
static {
theInstance = new StubNodeHelper();
Utils.forceInit(StubNode.class);
}
private static StubNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(StubNode stubNode) {
setHelper(stubNode, getInstance());
}
public static void setStubNodeAccessor(final StubNodeAccessor newAccessor) {
if (stubNodeAccessor != null) {
throw new IllegalStateException();
}
stubNodeAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubNodeAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return stubNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return stubNodeAccessor.doComputeContains(node, localX, localY);
}
public interface StubNodeAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
