package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.ArcHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import test.javafx.scene.shape.ArcTest;
public class StubArcHelper extends ArcHelper {
private static final StubArcHelper theInstance;
private static StubArcAccessor stubArcAccessor;
static {
theInstance = new StubArcHelper();
Utils.forceInit(ArcTest.StubArc.class);
}
private static StubArcHelper getInstance() {
return theInstance;
}
public static void initHelper(Arc arc) {
setHelper(arc, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubArcAccessor.doCreatePeer(node);
}
public static void setStubArcAccessor(final StubArcAccessor newAccessor) {
if (stubArcAccessor != null) {
throw new IllegalStateException();
}
stubArcAccessor = newAccessor;
}
public interface StubArcAccessor {
NGNode doCreatePeer(Node node);
}
}
