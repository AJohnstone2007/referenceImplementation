package test.com.sun.javafx.scene.bounds;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.bounds.PerfNode;
public class PerfNodeHelper extends NodeHelper {
private static final PerfNodeHelper theInstance;
private static PerfNodeAccessor perfNodeAccessor;
static {
theInstance = new PerfNodeHelper();
Utils.forceInit(PerfNode.class);
}
private static PerfNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(PerfNode perfNode) {
setHelper(perfNode, getInstance());
}
public static void setPerfNodeAccessor(final PerfNodeAccessor newAccessor) {
if (perfNodeAccessor != null) {
throw new IllegalStateException();
}
perfNodeAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return perfNodeAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return perfNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return perfNodeAccessor.doComputeContains(node, localX, localY);
}
public interface PerfNodeAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
