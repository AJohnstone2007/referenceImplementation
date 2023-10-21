package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.CubicCurveHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CubicCurve;
import test.javafx.scene.shape.CubicCurveTest;
public class StubCubicCurveHelper extends CubicCurveHelper {
private static final StubCubicCurveHelper theInstance;
private static StubCubicCurveAccessor stubCubicCurveAccessor;
static {
theInstance = new StubCubicCurveHelper();
Utils.forceInit(CubicCurveTest.StubCubicCurve.class);
}
private static StubCubicCurveHelper getInstance() {
return theInstance;
}
public static void initHelper(CubicCurve cubicCurve) {
setHelper(cubicCurve, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubCubicCurveAccessor.doCreatePeer(node);
}
public static void setStubCubicCurveAccessor(final StubCubicCurveAccessor newAccessor) {
if (stubCubicCurveAccessor != null) {
throw new IllegalStateException();
}
stubCubicCurveAccessor = newAccessor;
}
public interface StubCubicCurveAccessor {
NGNode doCreatePeer(Node node);
}
}
