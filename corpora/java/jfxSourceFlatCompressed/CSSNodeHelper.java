package test.com.sun.javafx.scene;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.CSSNode;
public class CSSNodeHelper extends NodeHelper {
private static final CSSNodeHelper theInstance;
private static CSSNodeAccessor cssNodeAccessor;
static {
theInstance = new CSSNodeHelper();
Utils.forceInit(CSSNode.class);
}
private static CSSNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(CSSNode cssNode) {
setHelper(cssNode, getInstance());
}
public static void setCSSNodeAccessor(final CSSNodeAccessor newAccessor) {
if (cssNodeAccessor != null) {
throw new IllegalStateException();
}
cssNodeAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return cssNodeAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return cssNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return cssNodeAccessor.doComputeContains(node, localX, localY); }
public interface CSSNodeAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
