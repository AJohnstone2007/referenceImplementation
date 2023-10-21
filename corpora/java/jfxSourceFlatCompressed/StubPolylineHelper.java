package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.PolylineHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Polyline;
import test.javafx.scene.shape.PolylineTest;
public class StubPolylineHelper extends PolylineHelper {
private static final StubPolylineHelper theInstance;
private static StubPolylineAccessor stubPolylineAccessor;
static {
theInstance = new StubPolylineHelper();
Utils.forceInit(PolylineTest.StubPolyline.class);
}
private static StubPolylineHelper getInstance() {
return theInstance;
}
public static void initHelper(Polyline polyline) {
setHelper(polyline, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubPolylineAccessor.doCreatePeer(node);
}
public static void setStubPolylineAccessor(final StubPolylineAccessor newAccessor) {
if (stubPolylineAccessor != null) {
throw new IllegalStateException();
}
stubPolylineAccessor = newAccessor;
}
public interface StubPolylineAccessor {
NGNode doCreatePeer(Node node);
}
}
