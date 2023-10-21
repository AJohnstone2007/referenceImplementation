package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.PolygonHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import test.javafx.scene.shape.PolygonTest;
public class StubPolygonHelper extends PolygonHelper {
private static final StubPolygonHelper theInstance;
private static StubPolygonAccessor stubPolygonAccessor;
static {
theInstance = new StubPolygonHelper();
Utils.forceInit(PolygonTest.StubPolygon.class);
}
private static StubPolygonHelper getInstance() {
return theInstance;
}
public static void initHelper(Polygon polygon) {
setHelper(polygon, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubPolygonAccessor.doCreatePeer(node);
}
public static void setStubPolygonAccessor(final StubPolygonAccessor newAccessor) {
if (stubPolygonAccessor != null) {
throw new IllegalStateException();
}
stubPolygonAccessor = newAccessor;
}
public interface StubPolygonAccessor {
NGNode doCreatePeer(Node node);
}
}
