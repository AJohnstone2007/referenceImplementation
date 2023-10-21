package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.QuadCurveHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.QuadCurve;
import test.javafx.scene.shape.QuadCurveTest;
public class StubQuadCurveHelper extends QuadCurveHelper {
private static final StubQuadCurveHelper theInstance;
private static StubQuadCurveAccessor stubQuadCurveAccessor;
static {
theInstance = new StubQuadCurveHelper();
Utils.forceInit(QuadCurveTest.StubQuadCurve.class);
}
private static StubQuadCurveHelper getInstance() {
return theInstance;
}
public static void initHelper(QuadCurve quadCurve) {
setHelper(quadCurve, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubQuadCurveAccessor.doCreatePeer(node);
}
public static void setStubQuadCurveAccessor(final StubQuadCurveAccessor newAccessor) {
if (stubQuadCurveAccessor != null) {
throw new IllegalStateException();
}
stubQuadCurveAccessor = newAccessor;
}
public interface StubQuadCurveAccessor {
NGNode doCreatePeer(Node node);
}
}
