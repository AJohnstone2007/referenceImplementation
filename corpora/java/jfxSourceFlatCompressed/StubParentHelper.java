package test.com.sun.javafx.scene;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import test.javafx.scene.StructureTest;
public class StubParentHelper extends ParentHelper {
private static final StubParentHelper theInstance;
private static StubParentAccessor stubParentAccessor;
static {
theInstance = new StubParentHelper();
Utils.forceInit(StructureTest.StubParent.class);
}
private static StubParentHelper getInstance() {
return theInstance;
}
public static void initHelper(StructureTest.StubParent stubParent) {
setHelper(stubParent, getInstance());
}
public static void setStubParentAccessor(final StubParentAccessor newAccessor) {
if (stubParentAccessor != null) {
throw new IllegalStateException();
}
stubParentAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubParentAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return stubParentAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return stubParentAccessor.doComputeContains(node, localX, localY);
}
public interface StubParentAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}